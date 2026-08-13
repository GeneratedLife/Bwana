# Bwana revision-coupling audit

**Audited:** 2026-08-11, against Client-Java branch 225.
**Purpose:** dependency map for an eventual 317 adapter. No refactor performed; this
records the state as found.

---

## Headline

**The structural seam is clean.** No file under `bwana/` imports `deob.*` or
`jagex2.*` — the only occurrences of those names are four prose mentions in Javadoc.
Every 225 type (`LocType`, `NpcType`, `NpcEntity`, `PlayerEntity`, `Component`,
`Model`, `World3D`) stops at `client.java`.

So the risk is not structural but **semantic**: places where the types are
toolkit-owned but their *meaning* is 225's. Those are harder to see and are where
the real porting cost sits.

Verification used:

```
rg '^import (deob|jagex2)' Client-Java/src/main/java/bwana   # no matches
rg 'deob\.|jagex2\.'       Client-Java/src/main/java/bwana   # 4 hits, all in comments
```

---

## 1. Core / client-independent

Safe as-is. A 317 adapter reuses these untouched.

| Component | Why it is portable |
| --- | --- |
| `bwana/GameEvents`, `GameEventBus`, `GameEventsAdapter` | Login, logout, tick, xp, chat. No revision concepts in the shapes. |
| `bwana/inspect/NameFilter` | Pure string matching. |
| `bwana/target/*` — `TargetCriteria`, `Candidate`, `Selection`, `TargetTracker` | Acquire-vs-hold, rejection reasons, lost-linger. Pure policy. |
| `bwana/action/ActionState`, and `ActionRunner`'s confirmation model | "Never trust the send, read the world back" is a concept, not a protocol. |
| `bwana/XpTracker`, `SessionStore`, `ChatLog`, `Notepad`, `Screenshot` | Toolkit-side bookkeeping. |
| `bwana/vision/*` — `Frame`, `Detection`, `ConnectedRegions`, `ColorRule`, `RuleFitter`, `Palette`, `Histogram` | **Genuinely revision-agnostic.** Viewport size and canvas origin are constructor *parameters* (`Detection.java:42`, `Frame.java:27`); 512x334 and (8,11) appear only in doc comments. Ports for free. |

---

## 2. 225-specific implementation detail — correctly contained

These are revision-specific and are already sealed inside `client.java`. A 317
adapter rewrites the body; nothing above notices.

- **Inventory/equipment container lookup.** `client.java:11096-11099` —
  `INVENTORY_TAB = 3`, `EQUIPMENT_TAB = 4`, read through `Component.invSlotObjId`
  with the `+1` empty-slot encoding. Widget-backed inventory is deeply 225/317
  shaped, and none of it escapes `GameState`.
- **Model picking.** `Model.pickedBitsets` decoding and bit layout (tileX 0-6,
  tileZ 7-13, id 14-28, kind 29-30); `inspectAtCursor`.
- **Scene bitset lookup.** `locBitsetAt`, and `World3D.getInfo`'s exact-equality
  bitset matching.
- **Loc anchoring.** `tile * 128 + size * 64`, rotation 1/3 swapping width and
  length; `projectFromGround`.
- **Menu dispatch.** `LOC_OPCODES`, `NPC_OPCODES`, scratch slot 499,
  `useMenuOption`.

This category is the architecture working as intended.

---

## 3. Should be behind an abstraction — currently is not, but is not leaking

Revision-specific *values* living in toolkit-owned files. Nothing above them
misbehaves, but a 317 adapter cannot override them because they are static tables
rather than interfaces.

- **`bwana/Skill.java:35-40`** — 225 numbering, with slots 18 and 19 as disabled
  placeholders. **317 fills those with Slayer and Farming.** Concrete, guaranteed
  breakage: `Skill.name(18)` returns `"Stat18"` on a client where it is Slayer.
  Wants to be adapter-supplied.
  *Resolved 2026-08-13: names and enabled-ness now come from `Revision`, supplied
  by the adapter. The ids stay compile-time constants.*
- **`bwana/ChatType.java:13-37`** — type ids read off 225's chat renderer. Not
  stable across revisions.
  *Resolved 2026-08-13: the constants are now the toolkit's own kinds, and
  `Revision.chatKind` translates the client's ids once, in `fireChatMessage`.*
- **`bwana/Levels.java:18-25`** — the XP curve, deliberately duplicated to avoid
  importing the client. Identical in 317, so lowest risk in this group.
- **`WorldQuery.getItemName(int)`** — the interface is fine; the ten-entry
  linear-scan cache warning in its Javadoc is a 225 fact.

---

## 4. Leaking into higher-level tooling

The real findings, ranked by cost.

### 4a. `entityIndex` as identity — the widest leak

`EntityInfo.entityIndex` (`EntityInfo.java:65`) is *the client's array slot*, and
three layers above the adapter now depend on that:

- `target/TargetTracker.java:170-172` — `markHeld` matches on it
- `action/ActionRunner.java:380` — `sameEntity` matches on it
- `action/EntityAction.java:71` — `appliesTo` compares it against `paramA`

The *concept* is legitimate and portable: creatures need a stable handle, and
position will not do because they walk. What leaked is the **reuse semantics** —
every consumer must know indices get recycled and must therefore pair the index
with a type id. That rule is currently re-implemented at each of those three
sites, which is the same divergence pattern that previously caused the
overlay/search filter bug (fixed by extracting `NameFilter`).

An adapter whose identity is not an int array slot — a handle, a UID, a
server-assigned id — cannot satisfy this contract.

**Highest-value fix.** Shape: an opaque `EntityHandle` with a single `matches()`,
constructed by the adapter, compared by the toolkit. No behaviour change.

### 4b. `EntityAction.paramA/paramB/paramC` — 225 menu encoding in a "generic" type

`action/EntityAction.java` is documented as generic, but `menuAction` is a raw 225
opcode and the three params are positional menu arguments whose meaning varies by
kind (paramA is a scene bitset for locs, an entity index for creatures). `appliesTo`
then has to *decode* that convention, so the toolkit layer is reading the client's
menu ABI.

A 317 adapter has entirely different opcodes. This type wants an opaque payload
that only the adapter interprets.

`opIndex` being 0-4 additionally bakes in "five ops per definition".

### 4c. `DebugOverlay.Target` — projection internals in a public API

`inspect/DebugOverlay.java:41-45`: `anchorX`/`anchorZ` in 1/128 tile units, plus
`centreHeight`. `EntityInspector.markerFor()` returns this type, so the marker
contract is stated in 225 model-placement terms.
`DebugOverlay.setTargets(list, sceneBaseX, sceneBaseZ)` (line 118) additionally
exposes the 104x104 scene-rebuild origin as a public concept.

Mitigating: only `client.java` constructs and consumes these values; the UI never
reads the anchors. A narrow leak — but it is in an interface signature, which is
where leaks are expensive.

### 4d. Raw client units on model records

- `inspect/EntityInfo.java:31` — `localX`/`localZ` in 1/128 tile units, surfaced to
  the user in `ui/InspectPanel.java:220`
- `inspect/EntityInfo.java:123` and `model/CameraInfo.java:53` — `* 360 / 2048`
  conversions performed in the toolkit
- `model/CameraInfo.java:12-15` — scene-local `x`/`y`/`z` carried alongside
  `worldX`/`worldY`
- `inspect/ProjectionDebug.java` — entirely 225 by design. It is a diagnostic *for*
  the projection, so this one is arguably correct as-is.

Both 1/128 units and 0-2047 angles are RS2-era-wide, so 317 is unaffected. They
would break on anything outside that family.

### 4e. `EntityInfo.kind` mirrors the 225 bitset encoding

`KIND_PLAYER=0, KIND_NPC=1, KIND_LOC=2, KIND_GROUND_ITEM=3` (`EntityInfo.java:13-17`)
are numerically identical to the picked-bitset kind field. Harmless today — 317
matches — but it is an undeclared coincidence rather than a stated mapping, which is
the kind of thing that is silently wrong for one revision in five.

---

## Gap: widgets

**There is no widget abstraction at all.** No `getWidget*` anywhere under `bwana/`.
Interfaces are reached only indirectly, through the inventory and equipment
accessors that read `Component` internally.

That is fine for what exists today, but it matters more than it looks: in this
client family **inventory, equipment, bank, and most dialogue state all live in the
widget tree**. Any future tool needing "is the bank open", "what is in the bank", or
"is a level-up dialogue showing" hits that wall.

Designing this before those tools exist is much cheaper than after. Right now
`GameState` has two container accessors and no callers depending on their
internals.

---

## What a 317 adapter would actually cost

Worth stating plainly: **225 to 317 is the easy port**, and it flatters the current
architecture. Same 104x104 scene, same 1/128 units, same 0-2047 angles, same 512x334
viewport, near-identical model-picking bit layout. Most of what changes is constants.

Would need rewriting:

- menu opcode tables (`LOC_OPCODES`, `NPC_OPCODES`, examine codes)
- `Skill` names for slots 18-20
- `ChatType` ids
- inventory/equipment component ids
- canvas origin offset
- `client.java`'s scene, projection and picking bodies

Comes across untouched:

- all of `bwana/vision/*`
- the whole target and action policy layer
- event bus, XP tracking, chat log, notes, screenshots

The architecture would only be genuinely tested by a client where **entity identity
is not an array index** or **interactions are not menu opcodes** — which is exactly
leaks 4a and 4b.

---

## Recommended order, if acted on

1. **4a — `EntityHandle`.** The only leak currently duplicated across three files.
   Duplicated rules are the ones that drift.
2. **Widget abstraction.** Cheapest now, before any tool depends on widget
   internals.
3. **3 — adapter-supplied `Skill` and `ChatType` tables.** Small, mechanical, and
   `Skill` has a known concrete 317 breakage.
4. **4b — opaque action payload.** Wait until a second adapter actually exists;
   designing the abstraction against one implementation tends to encode that
   implementation.
5. **4c, 4d, 4e.** Cosmetic until the target is outside the RS2 family.
