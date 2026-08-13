# Bwana adapter-extraction audit

**Audited:** 2026-08-13, this fork against upstream `LostCityRS/Client-Java` branch 225.
**Question:** can the toolkit's code leave `client.java`, and what would that cost?
**Answer:** yes, and the cost is mechanical. There are no access blockers of any kind.

Companion to [`bwana-revision-coupling.md`](bwana-revision-coupling.md), which asks
whether the *design* survives another revision. This one asks whether the *code* can
be moved, which is the question that decides what a second adapter costs.

---

## Headline

`client.java` here is upstream's file plus Bwana, and almost nothing else.

| | lines |
| --- | --- |
| upstream `client.java` | 11,105 |
| this fork's `client.java` | 13,757 |
| **added by Bwana** | **2,659** |
| **upstream lines changed or removed** | **7** |

Seven. The integration is 99.7% additive: Bwana bolts itself on rather than
rewriting the client.

The other three files this fork was believed to modify — `class61.java`,
`ObfuscatedName.java`, `signlink.java` — are **byte-identical to upstream**. The
README's claim that four upstream files are modified overstates the job by 4x; it
is one file.

---

## Why extraction is unblocked

The decisive fact:

```
$ grep -rc '\bprivate\b' upstream/src/main/java --include=*.java
(no matches, in any file)

$ grep -rhoE '^\s+(public|private|protected) ' upstream/src/main/java --include=*.java | sort | uniq -c
   2155 	public
```

**The entire client is public.** All 67 classes, all 2,155 member declarations. No
`private`, no `protected`, anywhere. This is an artifact of how the client was
recovered — obfuscators widen access, and deobfuscation preserved that — but the
consequence is concrete: a class in *any* package can read every field and call
every method the toolkit currently reaches through `this`.

So an adapter is not constrained to live in `deob`. `bwana.adapter.rev225` works
equally well, which keeps the toolkit's packages free of the client's.

The 120 distinct upstream members Bwana's added code touches are therefore all
reachable. Including the awkward-looking ones: the four `super.` uses resolve to
`GameShell.frame`, `GameShell.mouseX` and `GameShell.mouseY`, all `public` on a
`public` class, so they become `client.mouseX` from outside with no shim.

---

## What extraction actually costs

Of the 2,659 added lines:

| | lines | note |
| --- | --- | --- |
| comment, javadoc, blank | 673 | moves verbatim |
| real code | 1,986 | moves verbatim |
| of which reference `this.` | ≤386 | needs `this.` → `this.client.` |
| of which reference `super.` | 4 | needs `super.` → `this.client.` |
| anonymous classes | 0 | nothing captures an enclosing instance |
| inner classes | 0 | nothing to re-home |

The 386 is an **upper bound**: an unknown share of those are Bwana's own added
fields, which move with the adapter and keep `this.` unchanged. Only the references
to upstream members need rewriting.

What stays behind in `client.java`:

- the `implements` clause on the class declaration
- the seven upstream edits
- hook calls at the points where the client already notifies the toolkit
- a field holding the adapter, and its construction

That is the entire permanent delta to a file this fork does not own.

---

## What this does not tell you

- **It measures access, not correctness.** That code *can* move says nothing about
  whether each piece *should*; `bwana-revision-coupling.md` §2 lists what is
  legitimately client-side and belongs in an adapter body regardless.
- **It does not check compilation.** No extraction was attempted. Java has ways to
  surprise you that a line count will not predict — static initialisation order and
  the `GameShell` callback timing being the obvious candidates.
- **It says nothing about other revisions.** 274 and 317 are different files. The
  all-public property is a property of *this* deobfuscation, and a 317 client from
  a different lineage may not share it. Re-run the `grep` above before assuming.

---

## Reproducing

```sh
git clone --depth 1 -b 225 --single-branch \
  https://github.com/LostCityRS/Client-Java upstream

# the delta
diff upstream/src/main/java/deob/client.java src/main/java/deob/client.java \
  | grep -c '^>'                      # added
diff upstream/src/main/java/deob/client.java src/main/java/deob/client.java \
  | grep -c '^<'                      # changed or removed

# the other three files
cmp upstream/src/main/java/deob/class61.java       src/main/java/deob/class61.java
cmp upstream/src/main/java/deob/ObfuscatedName.java src/main/java/deob/ObfuscatedName.java
cmp upstream/src/main/java/sign/signlink.java      src/main/java/sign/signlink.java

# the access finding
grep -rc '\bprivate\b' upstream/src/main/java --include=*.java
```

---

## Implication

The multi-revision question was: does the toolkit have to be copied into every
client fork? It does not have to be, and the reason is measurable rather than
hopeful — 1,986 lines of code with no access constraints and no captured state,
which move as a block.

Doing it while one fork exists is bookkeeping. Doing it once two exist means
reconciling two copies that have already drifted, which is the failure
`EntityHandle` was extracted to prevent, at a larger scale.
