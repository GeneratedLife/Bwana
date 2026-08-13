# client-274

Lost City rev 274 client, from `LostCityRS/Client-Java` branch `274`.

**Status: vendored, building, and reading the client.** `Revision274` supplies the
revision's tables and `State274` implements 12 of the 18 methods across `GameState`
and `WorldQuery`. Nothing calls `Bwana.start` yet, so none of the toolkit *runs*
here — but what it would read is in place and verified null-safe against a real
`Client`.

`State274` sits **outside** `Client.java`, unlike 225's adapter which is the client
itself. Nothing forced 225's arrangement: the client carries no access control at
all, so an adapter can read it from another package entirely. 274 is written the way
[`../bwana-adapter-extraction.md`](../bwana-adapter-extraction.md) concluded 225
should be.

### What State274 answers

| implemented | traced to |
| --- | --- |
| `isLoggedIn` | `ingame` |
| `getSkillLevel` / `BaseLevel` / `Experience` | `statEffectiveLevel` / `statBaseLevel` / `statXP` |
| `getWorldX` / `getWorldY` | `localPlayer.x/z >> 7` + `mapBuildBaseX/Z` |
| `getPlane` | `minusedlevel` |
| `getLocalPlayerName` | `localPlayer.name` |
| `getCamera` | `camX/camY/camZ/camPitch/camYaw` |
| `getPath` | `routeLength` / `routeX` / `routeZ` / `routeRun`, `minimapFlagX/Z` |
| `getGroundItems` | `groundObj[plane][x][z]`, `ClientObj.id/count` |
| `getItemName` | `ObjType.list(id).name` |

The six that are not implemented throw rather than answer, because an empty array
would be a plausible lie — "no npcs", "empty inventory" — and those are the failures
this codebase keeps being rewritten to avoid:

| refuses | needs |
| --- | --- |
| `getNpcs` | `ClientNpc` and `NpcType` field mapping |
| `getPlayer` | `ClientPlayer` field mapping |
| `getInventory` / `getEquipment` / `getInventoryIds` / `getInventoryCounts` | 274's `IfType` container mapping and its tab ids |

Two mappings were checked rather than assumed, and both could have been silently
wrong. `minusedlevel` reads like something other than the plane, but both clients
fill it from a 2-bit field of the same packet. And `getPath` relies on `0` meaning
"no destination" — 274 zeroes `minimapFlagX` on arrival at `Client.java:7576`,
exactly as 225 zeroes `flagSceneTileX`.

## Why this is a port and not a rebase

274 is a different deobfuscation of a different build, not 225's tree with new
constants. What differs before any behaviour is considered:

| | 225 | 274 |
| --- | --- | --- |
| entry point | `deob.client` | `jagex2.client.Client` |
| interfaces | `Component` | `IfType` |
| spot anims | `SpotAnimType` | `SpotType` |
| input | `InputTracking` | `MouseTracking` |
| skills | none in client | `jagex2.client.Skill` |
| source level | 1.6 | 1.8 |
| lines in entry class | 11,105 | 11,270 |

So the 2,659 lines Bwana adds to 225's `client.java` cannot be applied as a diff.
They have to be rewritten against a differently named host class and a different
type vocabulary — mechanical in the main, but line by line rather than by patch.

## What already carries over

Everything in `core`, which is the point of the split. The toolkit does not know
this module exists, and the seven interfaces it needs — `GameState`, `WorldQuery`,
`FrameSource`, `EntityInspector`, `ActionExecutor`, `CollisionSource`,
`WidgetSource` — are the entire contract this module has to satisfy.

`Revision274` is already correct and sourced rather than guessed:

- **skill names and which slots are live** come from `jagex2.client.Skill` in this
  very client, which declares `count = 25`, `names[18] = "slayer"` and
  `used[18] = false` — 274 knows the name but has not enabled it
- **chat type ids** were read off this client's own `addChat` call sites, and
  match 225's numbering one for one

## Remaining work

1. Finish the six methods `State274` refuses, then implement the remaining five
   interfaces against `Client` — `FrameSource`, `EntityInspector`,
   `ActionExecutor`, `CollisionSource`, `WidgetSource`.
2. Call `Bwana.start(new Revision274(), …)` from `Client.main`.
3. Port the revision-specific bodies catalogued in
   [`../bwana-revision-coupling.md`](../bwana-revision-coupling.md) §2 — menu
   opcodes, inventory and equipment component ids, model picking, scene bitsets,
   loc anchoring.

Item 3 is also the point of doing 274 at all. The coupling audit deferred four
abstractions until a second adapter existed, on the grounds that designing against
one implementation just encodes that implementation. This module is that second
implementation.

## Running it

A 274 world is one command:

```
setup-server.cmd 274
```

That clones `Engine-TS` and `Content` from their `274` branches into
`<parent>/Server-274` and pins the engine to port offset 2010 — web 2090, game
45604 — so it can run beside a 225 world without either fighting for a port.

There is still no *launcher* for 274. `play.cmd` and `run-client.cmd` are wired to
`client-225` and pass offset 2000; a 274 launcher has to pass 2010 to match what
`setup-server.cmd` wrote into that server's `.env`.
