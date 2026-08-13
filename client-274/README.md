# client-274

Lost City rev 274 client, from `LostCityRS/Client-Java` branch `274`.

**Status: vendored and building. The toolkit is not wired in yet.** This module
currently produces a stock 274 client. `Revision274` supplies the revision's tables
to `core`, but nothing calls `Bwana.start`, so none of the toolkit runs here.

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

1. Implement the seven interfaces against `Client`, which is where the 2,659 lines
   go. `GameState` and `WorldQuery` first: they unblock the xp tracker, the chat
   log and the inspector without needing anything else.
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

There is no launcher for 274 yet. `play.cmd` and `run-client.cmd` are wired to
`client-225`, and a 274 world needs `Engine-TS` and `Content` on their own `274`
branches — `setup-server.cmd` hardcodes `225` and would need the revision as an
argument.
