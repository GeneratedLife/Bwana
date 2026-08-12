# Phase 5 — How gaining XP becomes a changed number in a Java field

Every line number below is real, in your checkout, as of the commit you cloned
(`5a1ba4b`, branch `225`). Client paths are relative to
`Client-Java/src/main/java/`, server paths to `Server/engine/src/`.

---

## 5a. `GameShell` — the skeleton

`jagex2/client/GameShell.java`, 593 lines. This is the whole client's spine, and
it's small enough to hold in your head at once.

`client extends GameShell extends Applet`, and `GameShell implements Runnable`.
That last one is the important part: **the game runs on its own thread**, started
at [GameShell.java:125](Client-Java/src/main/java/jagex2/client/GameShell.java:125)
inside `initApplication`:

```java
this.startThread(this, 1);      // 'this' is Runnable -> run()
```

`run()` ([:140](Client-Java/src/main/java/jagex2/client/GameShell.java:140)) is the
game loop. Stripped of its timing arithmetic it is just:

```java
this.load();                      // one-time init
while (this.state >= 0) {
    Thread.sleep(var5);           // sleep to hit the target framerate
    while (var6 < 256) {
        this.update(437);         // <-- one game tick
        var6 += var4;
    }
    this.draw(false);             // <-- one frame
}
```

Two things are worth understanding here and then never thinking about again:

- **`deltime` is a period, not a rate.** It's `20` by default
  ([:47](Client-Java/src/main/java/jagex2/client/GameShell.java:47)), meaning 20ms
  per tick — 50 ticks/sec. `setFramerate`
  ([:235](Client-Java/src/main/java/jagex2/client/GameShell.java:235)) sets it as
  `1000 / fps`.
- **The `var4`/`var6`/`256` dance is a catch-up mechanism.** `var4` is "how much
  of a tick has elapsed" scaled so 256 == one full tick. If the machine stalls,
  the inner `while` runs `update()` more than once to catch up, while `draw()`
  still happens only once per outer iteration. That's why the game can drop
  frames without dropping game logic.

`update()` and `draw()` are empty stubs here
([:516](Client-Java/src/main/java/jagex2/client/GameShell.java:516),
[:530](Client-Java/src/main/java/jagex2/client/GameShell.java:530)) — `client`
overrides them. `client.update(int)` is at
[client.java:9018](Client-Java/src/main/java/deob/client.java:9018).

`GameShell` is also the sole AWT listener: `mousePressed`, `keyPressed`, etc. all
land here and do nothing but write into plain fields (`mouseX`, `mouseClickButton`,
`actionKey[]`, a 128-entry `keyQueue` ring buffer). **Input is never handled on the
AWT thread** — it's recorded, and the game thread reads it a tick later via
`pollKey` ([:477](Client-Java/src/main/java/jagex2/client/GameShell.java:477)).
That's the same discipline you'll need in reverse for Phase 8.

> The `if (arg0 != 3) throw new NullPointerException()` in `getBaseComponent`
> ([:544](Client-Java/src/main/java/jagex2/client/GameShell.java:544)) and the
> `field4 = !field4` lines are deobfuscation artifacts — opaque predicates the
> original obfuscator inserted to defeat decompilers. They are dead code. Ignore
> them wherever you see them; there are a lot.

---

## 5b. `Packet` — the buffer

`jagex2/io/Packet.java`, 409 lines. A `byte[] data` plus a cursor `pos`
([:39–42](Client-Java/src/main/java/jagex2/io/Packet.java:39)). Every byte in or
out of the game passes through it.

The naming convention is `p` = put, `g` = get, number = bytes:

| write | read | bytes | notes |
|---|---|---|---|
| `p1` | `g1` / `g1b` | 1 | `g1` unsigned, `g1b` signed |
| `p2` | `g2` / `g2b` | 2 | big-endian; `ip2` writes little-endian |
| `p3` | `g3` | 3 | |
| `p4` | `g4` | 4 | `ip4` little-endian |
| `p8` | `g8` | 8 | |
| `pjstr` | `gjstr` | var | newline-terminated (`\n` = 10), *not* length-prefixed |

Read `g4` ([:279](Client-Java/src/main/java/jagex2/io/Packet.java:279)) once and the
whole file makes sense:

```java
return ((data[pos-4] & 0xFF) << 24) + ((data[pos-3] & 0xFF) << 16)
     + ((data[pos-2] & 0xFF) << 8)  +  (data[pos-1] & 0xFF);
```

`& 0xFF` because Java bytes are **signed** — without the mask, any byte ≥ 128
sign-extends to a negative int and corrupts the result. This is the single most
common bug when hand-writing this kind of code.

Three special members:

- **`accessBits` / `gBit` / `accessBytes`**
  ([:326](Client-Java/src/main/java/jagex2/io/Packet.java:326)–[:362](Client-Java/src/main/java/jagex2/io/Packet.java:362))
  — bit-level reads for player movement, where a value might be 3 or 5 or 11 bits
  wide. `accessBits` sets `bitPos = pos * 8`; `accessBytes` rounds back up to the
  next whole byte. `BITMASK[n]` is just `2^n - 1`.
- **`p1isaac`** ([:153](Client-Java/src/main/java/jagex2/io/Packet.java:153)) — writes
  an opcode with the ISAAC stream cipher added to it. See below.
- **`rsaenc`** ([:377](Client-Java/src/main/java/jagex2/io/Packet.java:377)) —
  RSA for the login block only, via `BigInteger.modPow`.

Note `alloc`/`release` ([:78](Client-Java/src/main/java/jagex2/io/Packet.java:78),
[:115](Client-Java/src/main/java/jagex2/io/Packet.java:115)): buffers are pooled in
three sizes (100 / 5000 / 30000) and recycled. 2004-era GC avoidance.

---

## 5c. The packet loop

`client.read(boolean)` at
[client.java:9764](Client-Java/src/main/java/deob/client.java:9764). It is a
**state machine that handles at most one packet per call** and returns.

It's driven from `updateGame`
([:7658](Client-Java/src/main/java/deob/client.java:7658)), which pulls up to five
packets per game tick:

```java
for (int var2 = 0; var2 < 5 && this.read(false); var2++) { }
```

So: 50 ticks/sec × up to 5 packets = a soft ceiling of 250 packets/sec, and the
loop naturally stops when `read()` returns `false` (nothing more available).

Inside `read()`, three steps:

**1. Opcode, decrypted** ([:9778–9784](Client-Java/src/main/java/deob/client.java:9778)):

```java
this.stream.read(this.in.data, 0, 1);
this.packetType = this.in.data[0] & 0xFF;
if (this.randomIn != null) {
    this.packetType = this.packetType - this.randomIn.takeNextValue() & 0xFF;
}
this.packetSize = Protocol.SERVERPROT_SIZES[this.packetType];
```

`randomIn` is an ISAAC cipher keyed during login
([:7142](Client-Java/src/main/java/deob/client.java:7142)). Only the **opcode byte**
is enciphered — the payload is plaintext. It's obfuscation against packet
injection, not real crypto.

> **This has a hard consequence for your toolkit.** `takeNextValue()` advances a
> stateful keystream. Read the opcodes out of order, twice, or from a second
> thread and every subsequent packet decodes to garbage. Whatever you build in
> Phases 7–9 must **observe** this loop, never re-enter it, and never touch
> `stream` from the Swing thread.

**2. Length** ([:9787–9803](Client-Java/src/main/java/deob/client.java:9787)). The
size table encodes three cases:

| `SERVERPROT_SIZES[op]` | meaning |
|---|---|
| `≥ 0` | fixed-size payload |
| `-1` | length is the next 1 byte |
| `-2` | length is the next 2 bytes |

**3. Dispatch** — a long `if (this.packetType == N)` chain. Each handler ends with
`this.packetType = -1; return true;`, which is what re-arms step 1 for the next call.

---

## 5d. The XP packet, end to end

### Server: something awards XP

`engine/entity/Player.ts` → `addXp`
([Player.ts:1740](Server/engine/src/engine/entity/Player.ts:1740)):

```ts
const multi = allowMulti ? Environment.NODE_XPRATE : 1;
this.stats[stat] += xp * multi;
```

**`stats[]` is stored as XP × 10.** The comment at
[Player.ts:1754](Server/engine/src/engine/entity/Player.ts:1754) spells out why —
32-bit signed ints with one implied decimal place, so the 200m cap is written as
`2_000_000_000`. `baseLevels[stat]` is recomputed here too, and `levels[stat]`
only follows it if no boost/drain is active
([:1760–1764](Server/engine/src/engine/entity/Player.ts:1760)).

### Server: the change is noticed

`NetworkPlayer.updateStats()`
([NetworkPlayer.ts:321](Server/engine/src/engine/entity/NetworkPlayer.ts:321)),
called once per tick from
[World.ts:1107](Server/engine/src/engine/World.ts:1107):

```ts
for (let i = 0; i < this.stats.length; i++) {
    if (this.stats[i] !== this.lastStats[i] || this.levels[i] !== this.lastLevels[i]) {
        this.write(new UpdateStat(i, this.stats[i], this.levels[i]));
        this.lastStats[i] = this.stats[i];
        this.lastLevels[i] = this.levels[i];
    }
}
```

It is a **delta**: the packet is sent only when a stat actually changed. Free
event semantics for your tracker — you don't have to poll or diff anything.

### Server: encoded

`UpdateStatEncoder.encode`
([UpdateStatEncoder.ts:9](Server/engine/src/network/game/server/codec/UpdateStatEncoder.ts:9)):

```ts
buf.p1(message.stat);
buf.p4((message.exp / 10) | 0);
buf.p1(message.level);   // not base level
```

Declared as `new ServerGameProt(44, 6)`
([ServerGameProt.ts:58](Server/engine/src/network/game/server/ServerGameProt.ts:58))
— **opcode 44, 6 bytes**. And on the client,
`Protocol.SERVERPROT_SIZES[44]` is `6`. The two agree; that's the CRC-style
version coupling the plan warns about, made concrete.

`(message.exp / 10) | 0` is the fixed-point conversion. `| 0` is JS integer
truncation.

### Client: decoded

[client.java:10734](Client-Java/src/main/java/deob/client.java:10734):

```java
if (this.packetType == 44) {
    this.redrawSidebar = true;
    var26 = this.in.g1();          // skill id
    var4  = this.in.g4();          // experience
    var5  = this.in.g1();          // current (boosted) level
    this.skillExperience[var26] = var4;
    this.skillLevel[var26]      = var5;
    this.skillBaseLevel[var26]  = 1;
    for (var6 = 0; var6 < 98; var6++) {
        if (var4 >= levelExperience[var6]) {
            this.skillBaseLevel[var26] = var6 + 2;
        }
    }
    this.packetType = -1;
    return true;
}
```

`1 + 4 + 1 = 6` bytes, matching the table.

The three destination fields:

| field | declared | meaning |
|---|---|---|
| `skillExperience[50]` | [client.java:506](Client-Java/src/main/java/deob/client.java:506) | whole XP |
| `skillLevel[50]` | [client.java:167](Client-Java/src/main/java/deob/client.java:167) | current, boosted |
| `skillBaseLevel[50]` | [client.java:365](Client-Java/src/main/java/deob/client.java:365) | real level |

**Base level is never transmitted** — the client derives it by linear scan of
`levelExperience`, built at
[client.java:11087](Client-Java/src/main/java/deob/client.java:11087):

```java
var3 = (int)((double) var2 + Math.pow(2.0D, (double) var2 / 7.0D) * 300.0D);
var0 += var3;
levelExperience[var1] = var0 / 4;
```

The server builds the identical table at
[Player.ts:84](Server/engine/src/engine/entity/Player.ts:84) as
`Math.floor(acc / 4) * 10` — same curve, times ten, because of the fixed point.

---

## Answering the checkpoint out loud

> You chop a tree. Server-side `addXp(WOODCUTTING, 25)` adds `25 × NODE_XPRATE`
> to `stats[8]`, which is XP×10. On that tick `updateStats()` sees `stats[8]`
> differs from `lastStats[8]` and queues an `UpdateStat`. The encoder writes
> opcode 44 as six bytes — skill id, XP÷10 as a big-endian int, boosted level.
> The client's game thread, inside `updateGame`, calls `read()`, pulls one byte,
> subtracts the next ISAAC keystream value to recover `44`, looks up size `6`,
> reads exactly six bytes, and the opcode-44 branch writes
> `skillExperience[8]`, `skillLevel[8]`, and a locally-derived
> `skillBaseLevel[8]`. It sets `redrawSidebar = true` and the next `draw()`
> repaints the skill tab.

---

## What this means for Phases 7–9

Four things fall out of the trace that shape the toolkit design:

1. **[client.java:10739](Client-Java/src/main/java/deob/client.java:10739) is your
   event site.** It is the one place XP changes, it already has old value (in the
   array) and new value (`var4`) in scope, and it runs on the game thread. Firing
   `onExperienceGained` from there costs about three lines and is the entire
   Phase 7 hook for XP.
2. **You get change events for free.** The server already deltas, so you never
   poll. But note you also get a packet on *login* for all 50 skills — your
   tracker must treat the first event per skill as a baseline, not as XP gained,
   or it will report your whole lifetime XP as session XP.
3. **Client XP is whole numbers.** The `/10` happens server-side, so 0.1-XP
   precision is invisible to you. XP/hr computed client-side is quantized; over a
   session it's immaterial, but don't be surprised when totals differ from the
   server's own logs by a few tenths.
4. **Threading is not optional.** ISAAC's stateful keystream means the network
   read path is single-threaded by construction. Game thread writes state, EDT
   reads it — `SwingUtilities.invokeLater` in one direction, and nothing at all in
   the other.
