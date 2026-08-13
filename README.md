# Bwana

A tooling and automation layer built on the [Lost City](https://github.com/LostCityRS)
Java client (RuneScape rev 225).

Bwana is a fork of `LostCityRS/Client-Java` branch `225`. The interesting property
is how little of the client it touches: **one upstream file is modified, by seven
lines**, and everything else lives in a module that cannot see the client at all.

```
core/          the toolkit. No dependencies, and no client on its classpath
client-225/    the rev 225 client, implementing core's interfaces
client-274/    the rev 274 client. Vendored and reading; not yet driving
```

`core` compiling without `deob` or `jagex2` available is not a convention — it is
what the build does. Reaching for a client type from the toolkit fails with
*package deob does not exist*. A second revision is a second `client-*` module
beside this one, sharing `core` rather than copying it.

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

Needs a JDK 8, and a Lost City server that `setup-server.cmd` will fetch for you.

```
build.cmd          # gradle build, JDKs come from ~/.gradle/gradle.properties
build-home.cmd     # gradle build, JDKs found on disk
setup-server.cmd   # clones and prepares a server, once per revision
play.cmd           # starts the server if needed, then the client
run-client.cmd     # client only, in the foreground, to read what it prints
```

Each takes a revision, defaulting to 225:

```
setup-server.cmd 274
play.cmd 274
run-client.cmd 274
```

The server is a separate project and is not vendored here. Engine and content are
versioned in branches that have to match the client, so `setup-server.cmd` takes
both from the branch you name and puts them where `play.cmd` looks:

```
<parent>/Server-<rev>/engine     Engine-TS, branch <rev>, runs on Bun
<parent>/Server-<rev>/content    Content,   branch <rev>
```

Each revision gets its own folder and its own port offset, so two worlds can be up
at once. 225 stays at `<parent>/Server` when an install is already there, since it
predates the argument.

Everything that differs between revisions lives in `revision.cmd`, which the three
scripts above all read: the port offset, the jar path, where that revision's server
is, and the client's argument line. Adding a revision is a line in each of its
tables. They are deliberately separate copies of nothing — an offset that agrees in
the launcher but not in the engine's `.env` presents as "the client cannot connect",
which names neither file.

It also writes an `.env` giving the engine the ports the client asks for. The
client is passed a port offset — 2000 for 225, 2010 for 274 — and turns it into
HTTP `80 + offset` and game `43594 + offset`, while the engine defaults to 80 and
43594, so without that file the client never finds the server. Beyond Bun, the
engine needs Java 17+ on `PATH` to pack content.

`build.cmd` relies on `~/.gradle/gradle.properties` pinning `org.gradle.java.home`
and listing both JDKs for toolchain resolution. That file is outside the repo, so
a fresh clone on another machine has neither setting and the build stops at *No
matching toolchains found for Java 8*. `build-home.cmd` needs no setup: it finds a
JDK 8 for the toolchain and a JDK to run Gradle, then passes both on the command
line. It will not pick a JDK newer than 23 to run Gradle, because 8.11.1 predates
Java 24 and fails on it with *Unsupported class file major version 68* while
parsing `build.gradle`. `play.cmd` and `run-client.cmd` search the same roots for
the JDK 8 they launch on, so no script carries a machine-specific path;
`run-client.cmd` then settles for `JAVA_HOME` at any version, since a trace out of
a newer JDK still beats no trace. Name anything the search misses:

```
set BWANA_JDK8=C:\path\to\jdk8       # the JDK 8, read by all three scripts
set BWANA_JDK_DAEMON=C:\path\to\jdk  # what runs Gradle, build-home.cmd only
set BWANA_SERVER=C:\path\to\Server   # the Lost City checkout, play.cmd only
```

## Documentation

- [`lostcity-client-plan.md`](lostcity-client-plan.md) — the original nine-phase plan
- [`phase5-xp-packet-trace.md`](phase5-xp-packet-trace.md) — tracing the experience packet
- [`bwana-revision-coupling.md`](bwana-revision-coupling.md) — what would have to change
  for a 317 adapter, and what would not
- [`bwana-adapter-extraction.md`](bwana-adapter-extraction.md) — whether the toolkit can
  leave `client.java`, and what moving it costs

## Licence

Inherits whatever terms the upstream client carries. Written for private,
educational use against a locally hosted server.
