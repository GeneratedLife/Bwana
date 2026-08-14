# client-274

Lost City rev 274 client, from `LostCityRS/Client-Java` branch `274`.

**Status: the toolkit runs here.** `Revision274` supplies the revision's tables,
`State274` implements `GameState`, `WorldQuery`, `FrameSource`, `WidgetSource` and
`CollisionSource`, and `Client` carries the three event hooks and the `Bwana.start`
call. **Five of the seven interfaces done.** The XP tracker, chat log, vision, the
interface reads and the navigation map all have what they need.

What that cost inside the client is the point of the arrangement:

| | added to the client | changed |
| --- | --- | --- |
| 225 | 2,659 lines | 7 |
| **274** | **12 lines** | **0** |

Same integration, two orders of magnitude less of it in a file this fork does not
own. The difference is entirely that 225's adapter *is* the client and 274's is a
class beside it.

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
| `getNpcs` | `npc[]` / `npcIds[]`, `NpcType.id/name`, `faceEntity`, `primaryAnim` |
| `getPlayer` | `ClientPlayer.name/combatLevel`, `health` / `totalHealth` |
| `getInventory` / `getEquipment` / `getInventoryIds` / `getInventoryCounts` | `IfType.list`, `layerId`, `linkObjType` / `linkObjNumber` |
| `captureViewport` | `areaViewport.data/width/height`, origin (4, 4) |
| `getViewportInterfaceId` / `getChatInterfaceId` | `mainModalId` / `chatComId` |
| `getContainersUnder` / `WithOption` / `getWidgetText` / `isWidgetHidden` | `IfType.list`, `layerId`, `iop`, `text`, `hide` |
| `captureCollision` / `canEnter` | `collision[plane].flags`, masks `0x280120` / `0x280102` / `0x280108` / `0x280180` |

### Mappings that were checked rather than assumed

Four would have been silently wrong if 225's names had simply been reused.

- **`minusedlevel` is the plane.** The name suggests otherwise. Both clients fill it
  from a 2-bit field of the same packet, and 274 indexes `collision[]` with it.
- **`getPath`'s `0` means no destination.** 274 zeroes `minimapFlagX` on arrival at
  `Client.java:7576`, exactly as 225 zeroes `flagSceneTileX`.
- **The viewport origin is (4, 4), not 225's (8, 11).** `PixMap.draw` takes
  `x, graphics, y`, and 274 calls `areaViewport.draw(4, graphics, 4)`. The canvas
  differs too — 765x503 against 225's 532x789. `PixMap` also calls the pixel array
  `data` rather than `pixels`.
- **`linkObjType` carries `id + 1`,** so `0` can mean empty. Confirmed by the
  client's own `ObjType.list(linkObjType[i] - 1)` at `Client.java:6017`.

The one mapping resting on **correspondence rather than a statement in the source**
is the sidebar tab index — 3 for inventory, 4 for equipment. 274 calls the array
`sideOverlayId` where 225 says `tabInterfaceId`, and neither client labels a tab.
Both are `int[15]` of `-1`, both are read at the same literal indices, and both pair
index *i* with `sideicons[i]` in the same redraw and the same click test; only the
pixel coordinates differ. If an inventory read ever returns another tab's contents,
that constant is the first thing to doubt.

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

Two interfaces left — `EntityInspector` and `ActionExecutor`.
`Bwana.start` finds each by `instanceof`, so they light up as they land without the
client changing again; `WidgetSource` was picked up that way with no edit to
`Client.java` at all.

These are the expensive pair, and the coupling audit already says why: model
picking with its bitset layout, and the menu opcode tables. They are catalogued in
[`../bwana-revision-coupling.md`](../bwana-revision-coupling.md) §2 as the parts
that are legitimately revision-specific rather than accidentally so.

Until then, targeting and anything that clicks are absent on 274. Reading and
routing work; acting does not.

`ActionExecutor` is also where `EntityAction`'s `paramA` / `paramB` / `paramC` get
tested. The coupling audit calls them raw 225 menu-ABI values living in a type
documented as generic, and deferred redesigning them until a second adapter
existed. It does now.

Those three are also the point of doing 274 at all. The coupling audit deferred four
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

Then launch it the same way as 225, naming the revision:

```
play.cmd 274         server if needed, then the client
run-client.cmd 274   client only, in the foreground
```

Both read `revision.cmd`, which is where the per-revision facts live — offset,
ports, jar path, server location, and the client's argument line. That last one is
not cosmetic: 274's `main` wants **five** arguments where 225 wants four, the fifth
being `signlink.storeid`, which it clamps to 32-34. A client given the wrong count
prints its usage line and exits, and in `play.cmd`'s minimised window that is
indistinguishable from a crash.

The toolkit starts with the client. What is not there yet is anything that acts —
see Remaining work.
