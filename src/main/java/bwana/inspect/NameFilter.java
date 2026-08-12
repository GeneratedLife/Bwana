package bwana.inspect;

/**
 * A comma-separated list of name fragments, matched as "any of these".
 * <p>
 * Shared by the search and the live overlay deliberately. When each had its own
 * matching, they could disagree about what counted as a match — the overlay drew
 * creatures the search could not find — and that is a confusing class of bug to
 * chase because both halves look correct on their own.
 * <p>
 * Matching is case-insensitive substring, so "man" finds "Man" and "Woman", and
 * "gob" is enough for "Goblin". An empty filter matches everything.
 */
public final class NameFilter {

	private static final String[] NO_TERMS = new String[0];

	public static final NameFilter EMPTY = new NameFilter(NO_TERMS);

	private final String[] terms;

	private NameFilter(String[] terms) {
		this.terms = terms;
	}

	public static NameFilter parse(String text) {
		if (text == null) {
			return EMPTY;
		}
		String[] var1 = text.split(",");
		int var2 = 0;
		for (int var3 = 0; var3 < var1.length; var3++) {
			String var4 = var1[var3].trim().toLowerCase();
			if (var4.length() > 0) {
				var1[var2++] = var4;
			}
		}
		if (var2 == 0) {
			return EMPTY;
		}
		String[] var5 = new String[var2];
		System.arraycopy(var1, 0, var5, 0, var2);
		return new NameFilter(var5);
	}

	public boolean isEmpty() {
		return this.terms.length == 0;
	}

	public int termCount() {
		return this.terms.length;
	}

	public boolean matches(String name) {
		if (this.terms.length == 0) {
			return true;
		}
		if (name == null) {
			return false;
		}
		String var2 = name.toLowerCase();
		for (int var3 = 0; var3 < this.terms.length; var3++) {
			if (var2.indexOf(this.terms[var3]) >= 0) {
				return true;
			}
		}
		return false;
	}

	public String toString() {
		if (this.terms.length == 0) {
			return "(anything)";
		}
		StringBuilder var1 = new StringBuilder();
		for (int var2 = 0; var2 < this.terms.length; var2++) {
			if (var2 > 0) {
				var1.append(" or ");
			}
			var1.append(this.terms[var2]);
		}
		return var1.toString();
	}
}
