# Building a Custom Java Client for Lost City (rev 225)

**Goal:** learn how a 2004-era RuneScape client actually works, then build a personal SwiftKit-style toolkit around your own fork of it.

**Starting point:** new to Java, Swing for the toolkit UI, full scope.

---

## Ground truth

Facts confirmed by inspecting the actual repositories, not from memory:

| Thing | Value |
|---|---|
| Client repo | `github.com/LostCityRS/Client-Java` |
| **Branch you need** | **`225`** — *not* the default branch |
| Build system | Gradle (wrapper included, no separate install) |
| Java toolchain | **8** (source/target compatibility 1.6) |
| Main class | `deob.client` |
| Gradle project name | `rs2client` |
| Server repo | `github.com/LostCityRS/Server` |
| Server toolchain | Node 22 + Java 17 |
| Game port | `43594 + portOffset` |

### The branch thing will bite you

`Client-Java` has one branch per game revision:

```
225  244  245.2  254  254-teavm  274  289  377  435  468  500
```

The **default branch is `274`**. Lost City's server is **225**. If you clone without specifying a branch you will get a client that cannot talk to the server, and the failure will look like a mysterious connection or CRC error rather than an obvious version mismatch.

```bash
git clone -b 225 https://github.com/LostCityRS/Client-Java.git
```

### What the source actually looks like

68 files, ~31,500 lines. Organized as:

```
sign/              signlink — threading + socket/file access shim (1 file)
deob/              client.java, the god class (11,105 lines)
jagex2/
  client/          GameShell (593) — applet shell + main loop, InputTracking, ViewBox
  io/              Packet (409), Isaac, Protocol, ClientStream, Jagfile, BZip2 (7 files)
  graphics/        Pix3D (2,494) — the rasterizer. Model (2,143), fonts, sprites (11 files)
  dash3d/          World3D (2,424) — scene graph. World, CollisionMap, Occlude + entity/, type/
  config/          ObjType, LocType, NpcType, SeqType, Component… (9 files)
  datastruct/      linked lists, hashtables, caches (7 files)
  sound/           (3 files)
  wordenc/         WordFilter (1,225) — chat censoring (2 files)
```

Two things to know going in:

1. **It's a *partial* refactor.** Classes and packages are named sensibly, and important fields are too — but plenty of variables are still `field3`, `var5`, `arg0`. Every member carries an `@ObfuscatedName("a.b")` annotation recording its original obfuscated name. Improving names as you understand them is a legitimate and useful way to learn.

2. **`deob/client.java` is 11,105 lines.** That is not a mistake or bad refactoring — the original was one enormous class. Don't try to read it front to back. You'll navigate it by search, and your IDE's "go to symbol" will be your main tool.

---

## Realistic pacing

Being straight with you: new to Java plus an 11k-line class plus a binary network protocol is a genuinely hard combination. It is very doable, but the failure mode is trying to run at Phase 6 in week one, hitting a wall, and concluding you can't do it.

Rough shape, at a few hours a week:

- Phases 0–2: two to four weeks. Mostly Java and getting things to run.
- Phases 3–5: a month or two. This is where the learning actually happens.
- Phases 6–9: a month or two. This is where SwiftKit appears.

Every phase below ends with a **checkpoint** — something concrete that either works or doesn't. Don't advance until the checkpoint passes. That discipline is what keeps a project this size from becoming an unfixable mess.

---

## Phase 0 — Enough Java to not be lost

**Don't skip this and don't over-do it.** You need maybe two weeks of fundamentals, not a full course.

Learn:

- Classes, objects, fields, methods, constructors
- `static` vs instance — the client uses static state heavily and this will confuse you otherwise
- Primitives (`int`, `byte`, `boolean`, `char`) and arrays, especially `int[]` and `byte[]`
- Inheritance and `extends` — `client extends GameShell extends Applet` is the spine of the whole thing
- Interfaces and `implements`
- Bitwise operators: `&`, `|`, `^`, `<<`, `>>`, `>>>`. **The protocol and the rasterizer are built on these.** If they're unfamiliar, spend real time here — it's the single highest-value topic on this list.
- Threads at a basic level: what `Runnable` and `run()` mean, and roughly why a game loop lives on its own thread

Safe to defer: generics, lambdas, streams, annotations, most of the standard library. The client is 2004-era Java and uses almost none of it.

**Checkpoint:** you can write a small program that reads a `byte[]`, extracts individual bits with masks and shifts, and prints them — without copying from a tutorial.

---

## Phase 1 — Environment

Install:

- **JDK 17** (for the server's RuneScript compiler)
- **JDK 8** — Gradle's toolchain support may fetch this automatically; if not, install it. The client needs it.
- **Node 22**
- **IntelliJ IDEA Community** — free, and its navigation is genuinely necessary for a file this size
- **Git**

Then in IntelliJ, learn four shortcuts before anything else. These are not optional nice-to-haves; they're how you read a codebase like this:

- Go to declaration (`Ctrl`/`Cmd` + click)
- Find usages (`Alt+F7`)
- Go to symbol (`Ctrl+Alt+Shift+N`)
- Rename refactor (`Shift+F6`) — safe renaming, which you'll use constantly

**Checkpoint:** `java -version` and `node -v` both report the right versions, and IntelliJ opens.

---

## Phase 2 — Run the stock server and play

Clone `LostCityRS/Server`, run the quickstart script, follow the prompts. It fetches the cache for you. Then open the bundled web client and log in.

Change nothing. The entire point is a known-good baseline.

**Checkpoint:** you're standing in Lumbridge in a browser, connected to a server running on your own machine.

---

## Phase 3 — Build the Java client and connect it

```bash
git clone -b 225 https://github.com/LostCityRS/Client-Java.git
cd Client-Java
./gradlew build
```

The client's `main` takes four arguments:

```
node-id  port-offset  [lowmem|highmem]  [free|members]
```

With the Gradle `application` plugin:

```bash
./gradlew run --args="10 0 highmem members"
```

Port offset `0` means it connects to `43594`, and the code base defaults to `http://127.0.0.1:80` when running standalone, so localhost works without changes.

Expect friction here — this is the most likely place to get stuck, and the causes are usually: wrong branch, wrong JDK, the server not running, or the web-served cache not being where the standalone client looks for it. If the login screen renders but connecting fails, that's a *good* sign: rendering and cache loading already work and you're down to networking.

**Checkpoint:** the Java client window opens, shows the 2004 login screen, and you log into your local server with it.

> This checkpoint is the real milestone of the whole project. Everything after it is incremental.

---

## Phase 4 — One trivial change

Find a string on the login screen, change it, rebuild, see it change.

You are not testing the change. You are testing your edit → build → run loop, and proving you can find things in the source.

**Checkpoint:** your own text on the login screen.

---

## Phase 5 — Read the source, in this order

Now the actual learning. Read with the debugger — set a breakpoint, look at real values. Reading these files statically is far less effective.

**5a. `jagex2/client/GameShell.java` (593 lines).** Start here, always. It's the whole skeleton: applet lifecycle, the game loop, timing, mouse and keyboard listeners, the draw call. Small enough to read completely. Understand this and the client stops being a black box.

**5b. `jagex2/io/Packet.java` (409 lines).** The read/write buffer. Every byte in and out of the game goes through it. Read the getters `g1`, `g2`, `g4`, `g8`, `gjstr` and their write counterparts `p1`, `p2`, `p4`, `pjstr`. Then `accessBits` / `gBit` / `accessBytes` — bit-level packing, used heavily for player movement. Note `p1isaac`, which writes an opcode through the ISAAC cipher, and `rsaenc` for the login block. This is where your Phase 0 bitwise work pays off.

**5c. The cache and network layer — `jagex2/io/`.** Small and high-value: `Jagfile` (archive format), `BZip2` (decompression), `ClientStream` (the socket), `Isaac` (the cipher), `Protocol` (opcode tables).

**5d. The packet loop in `deob/client.java`.** Search for the main incoming-packet switch. Pick **one** packet and trace it end to end — through `Engine-TS` on the server, across the wire, into the client field it mutates. This single exercise teaches more than any amount of general reading.

Good one to start with: the stat update packet, around **line 10739**, which writes `skillExperience`, `skillLevel` and `skillBaseLevel`. It's simple, easy to trigger (gain any XP), and — conveniently — it is exactly the data your toolkit will want.

**5e. `jagex2/config/`.** `ObjType` (items), `LocType` (world objects), `Component` (interfaces). This is how the cache becomes game content.

**5f. Optional, later: `Pix3D.java` and `Model.java`.** The software rasterizer. Genuinely fascinating — triangle filling, texture mapping, and lighting written for 2004 CPUs with no GPU at all. Completely unnecessary for the toolkit. Save it as a reward.

**Checkpoint:** you can explain out loud how gaining XP in game becomes a changed number in a Java field.

---

## Phase 6 — Applet to JFrame

`GameShell extends Applet`, and applets are dead. The class already supports standalone operation (`initApplication`, and the `signlink.mainapp` checks throughout `client.java` that route around applet APIs), so this is less invasive than it sounds — but you own the window now.

Work:

- Host the client's `Canvas` in a `JFrame`
- Replace remaining `getParameter` / `getCodeBase` dependencies with your own config
- Handle resize, focus, and clean shutdown yourself

**Do this before building any toolkit UI.** The window is the container everything else lives in.

**Checkpoint:** the game runs in a `JFrame` you control, with no `Applet` in the path.

---

## Phase 7 — The game-state interface

**The most important design decision in the project.**

Do not let toolkit code reach into `client.java` fields directly. If you do, your toolkit and the client become one tangled thing and you'll never pull upstream changes again.

Instead define a narrow, read-only interface — one small file:

```java
public interface GameState {
    int getSkillLevel(int skill);      // current, boosted
    int getSkillBaseLevel(int skill);  // real level
    int getSkillExperience(int skill);
    int[] getInventoryIds();
    int[] getInventoryCounts();
    int getWorldX();
    int getWorldY();
    boolean isLoggedIn();
}
```

Plus an event stream for things that happen rather than things that are:

```java
public interface GameEvents {
    void onChatMessage(int type, String sender, String text);
    void onExperienceGained(int skill, int oldXp, int newXp);
    void onLogin();
    void onLogout();
}
```

`client.java` implements the getters (trivially — the fields already exist and are already well named) and fires the events from the packet handlers you traced in Phase 5d. Your fork's diff against upstream stays tiny and reviewable.

This is the structural advantage you have over the real SwiftKit and over RuneLite. They had to inject into a client they didn't control, using reflection and deobfuscation pipelines that break on every game update. You just add a getter.

**Checkpoint:** a class outside `deob` prints your Woodcutting XP once a second, touching no client field directly.

---

## Phase 8 — The Swing shell

Now the SwiftKit look: game canvas in the middle, tool panels around it.

- `JFrame` with `BorderLayout`
- Game `Canvas` in `CENTER`
- `JTabbedPane` or a collapsible sidebar in `EAST`
- Status bar in `SOUTH`

**One critical rule:** the game loop runs on its own thread; Swing must only be touched on the Event Dispatch Thread. Any update from game state to UI goes through `SwingUtilities.invokeLater`. Getting this wrong produces intermittent, maddening, hard-to-reproduce bugs. Learn it now, not after.

Also: mixing AWT (the game `Canvas`) with Swing components is the classic "heavyweight vs lightweight" problem. Modern Java mostly handles it, but if panels render behind the canvas or menus vanish, that's what you're looking at.

**Checkpoint:** empty panel docked beside a fully playable game.

---

## Phase 9 — The first real tool

Build **one** tool completely before starting a second. The XP tracker is the right first one — it's the classic, and it only needs data you already exposed in Phase 7.

- XP gained this session, per skill
- XP per hour
- Time to next level at current rate
- Reset button

Then, roughly in order of increasing effort:

- Session stats and a screenshot button (`Robot` or canvas capture, save to disk)
- Notepad, persisted to a local file
- Skill goals with ETAs
- Chat log to disk
- Drop/loot log
- Item search over the cache's `ObjType` data
- Alerts and system-tray notifications

Persist to a local JSON or SQLite file. Now you have the thing a browser tab can't do: history that outlives the session.

**Checkpoint:** you chop a tree and your own panel tells you your Woodcutting XP/hr.

---

## Gotchas, collected

- **Wrong branch.** Say it again: `225`. The default is `274`.
- **Two JDKs.** 17 for the server, 8 for the client. Confusing them produces baffling errors.
- **Cache/CRC mismatch** between client and server presents as vague load failures, not a clear message.
- **Swing threading.** `invokeLater` for every UI update from game state.
- **The god class.** Navigate by search, never by scrolling.
- **Java 1.6 target.** Modern syntax you learn in tutorials may not compile here. No `var`, no lambdas, no switch expressions.
- **Keep the fork thin.** Every line you change in `client.java` is a line you maintain forever.

---

## What to deliberately skip

- The rasterizer, until you want it for fun
- OpenGL/LWJGL — a large rewrite, no toolkit benefit
- The word filter, sound, and music code
- Writing RuneScript, unless you want custom server content (it's a separate track and doesn't touch the client)
- Any thought of distributing this. It's personal. That's the whole premise, and it removes a large category of problems.

---

## The one-line version

Clone branch `225`, get it connecting to a local server, read `GameShell` then `Packet` then trace the XP packet, convert to `JFrame`, put a narrow `GameState` interface between client and toolkit, then build an XP tracker in a Swing panel.
