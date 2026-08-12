# Bwana

A tooling and automation layer built on the [Lost City](https://github.com/LostCityRS)
Java client (RuneScape rev 225).

Bwana is a fork of `LostCityRS/Client-Java` branch `225`. The interesting property
is how little of the client it touches: **four upstream files are modified**, and
everything else lives under `src/main/java/bwana/`.

```
GameState → World Model → Planner → Behavior → Navigation/Action → Verification
```

## Architecture

The client is reached only through interfaces, so no toolkit code imports
`deob.client` or `jagex2.*`:

| Layer | What it answers |
| --- | --- |
| `bwana.GameState` / `WorldQuery` | What is true right now — skills, inventory, position |
| `bwana.GameEvents` | What just happened — login, tick, experience, chat |
| `bwana.inspect` | What is out there, and which of them is the same thing as before |
| `bwana.target` | Which one am I working on |
| `bwana.action` | What can I do to it, and did it work |
| `bwana.nav` | Where can I walk, and where are things |
| `bwana.widget` | What is on screen, and what does it hold |
| `bwana.plan` | What should happen, in what order |

Two rules hold the whole thing together:

- **Verification comes from game state, never from having sent an input.** An
  action reaching the server proves nothing; experience, inventory, animation and
  the game's own chat replies are what count.
- **Identity is not a slot.** `EntityHandle` owns the rule for deciding whether two
  observations are the same entity, because the client reuses array indices the
  moment something despawns.

## Planning

The Plan tab describes *what* should happen; the systems above decide *how*.
A plan is inert data — target, action, loot scope, carried-item handling, health
threshold, banking, stop conditions — so a new activity is new data rather than a
new script. Woodcutting is simply the first plan anyone wrote.

## Running it

Needs a portable JDK 8 and a Lost City server. The server is a separate project and
is not vendored here; clone it alongside and start it first.

```
build.cmd     # gradle build
play.cmd      # starts the server if needed, then the client
```

The client derives both ports from one offset: HTTP `80 + offset`, game
`43594 + offset`.

## Documentation

- [`lostcity-client-plan.md`](lostcity-client-plan.md) — the original nine-phase plan
- [`phase5-xp-packet-trace.md`](phase5-xp-packet-trace.md) — tracing the experience packet
- [`bwana-revision-coupling.md`](bwana-revision-coupling.md) — what would have to change
  for a 317 adapter, and what would not

## Licence

Inherits whatever terms the upstream client carries. Written for private,
educational use against a locally hosted server.
