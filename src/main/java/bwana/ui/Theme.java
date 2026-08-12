package bwana.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GradientPaint;
import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.plaf.metal.DefaultMetalTheme;
import javax.swing.plaf.metal.MetalLookAndFeel;

/**
 * The toolkit's colours: black surfaces, orange text, orange borders.
 * <p>
 * Done through a {@link javax.swing.plaf.metal.MetalTheme} rather than by setting
 * {@code UIManager} keys alone. Metal derives most of what it paints — tab strips,
 * scroll tracks, the space behind an unselected tab, viewport backing — from a
 * handful of theme colours, and those are not reachable key by key. Setting the
 * keys alone left the whole upper half of the window white, which is exactly the
 * part Metal fills from {@code control} rather than from anything nameable.
 * <p>
 * Buttons and checkboxes get a real gradient by way of a small UI delegate.
 * Metal will paint a gradient from {@code Button.gradient}, but only for buttons
 * and only under its own conditions; painting it directly is both more predictable
 * and works for checkboxes, which Metal does not gradient at all.
 */
public final class Theme {

	/** Near-black rather than pure black, so borders and text keep some separation. */
	public static final Color BACKGROUND = new Color(0x0B0B0B);

	/** Slightly lifted, for fields and lists that sit on the background. */
	public static final Color SURFACE = new Color(0x161616);

	public static final Color ORANGE = new Color(0xFF8A1E);

	/** Dimmer orange for rules and inactive edges, so borders do not shout. */
	public static final Color BORDER = new Color(0x8A4A10);

	/** For secondary text that should read as quieter without turning grey. */
	public static final Color MUTED = new Color(0xC08040);

	/** Gradient stops for buttons and checkboxes. */
	private static final Color GRADIENT_TOP = new Color(0xFFA347);
	private static final Color GRADIENT_BOTTOM = new Color(0xC85F00);
	private static final Color PRESSED_TOP = new Color(0xC85F00);
	private static final Color PRESSED_BOTTOM = new Color(0x8A4A10);
	private static final Color DISABLED_TOP = new Color(0x4A2A0A);
	private static final Color DISABLED_BOTTOM = new Color(0x2A1806);

	private static boolean applied;

	private Theme() {
	}

	/** Apply once, before the first window exists. Later calls do nothing. */
	public static synchronized void apply() {
		if (applied) {
			return;
		}
		applied = true;
		try {
			MetalLookAndFeel.setCurrentTheme(new BwanaTheme());
			UIManager.setLookAndFeel(new MetalLookAndFeel());
		} catch (Exception var0) {
			System.err.println("bwana: could not install the theme: " + var0);
			return;
		}
		keys();
		UIManager.put("ButtonUI", GradientButtonUI.class.getName());
		UIManager.put("CheckBoxUI", GradientCheckBoxUI.class.getName());
	}

	/**
	 * Colours Metal does not derive from the theme.
	 * <p>
	 * {@link ColorUIResource} rather than plain {@link Color} throughout: Swing
	 * treats a bare Color as a deliberate per-component override and will not
	 * replace it, which leaves stale colours behind.
	 */
	private static void keys() {
		ColorUIResource var0 = new ColorUIResource(BACKGROUND);
		ColorUIResource var1 = new ColorUIResource(SURFACE);
		ColorUIResource var2 = new ColorUIResource(ORANGE);
		ColorUIResource var3 = new ColorUIResource(BORDER);
		ColorUIResource var4 = new ColorUIResource(Color.WHITE);

		String[] var5 = new String[] {
			"Panel.background", "OptionPane.background", "TabbedPane.background",
			"TabbedPane.contentAreaColor", "TabbedPane.tabAreaBackground",
			"TabbedPane.unselectedBackground", "ScrollPane.background",
			"Viewport.background", "CheckBox.background", "Label.background",
			"Slider.background", "SplitPane.background", "ToolBar.background",
			"MenuBar.background", "RadioButton.background", "ScrollBar.track",
			"Spinner.background", "RootPane.background", "Separator.background",
			"ScrollBar.background", "Menu.background", "PopupMenu.background",
			"MenuItem.background", "ProgressBar.background", "Desktop.background"
		};
		for (int var6 = 0; var6 < var5.length; var6++) {
			UIManager.put(var5[var6], var0);
		}

		String[] var7 = new String[] {
			"TextField.background", "TextArea.background", "List.background",
			"ComboBox.background", "ToolTip.background", "TableHeader.background",
			"Table.background", "FormattedTextField.background",
			"Spinner.arrowButtonBackground", "ComboBox.buttonBackground"
		};
		for (int var8 = 0; var8 < var7.length; var8++) {
			UIManager.put(var7[var8], var1);
		}

		String[] var9 = new String[] {
			"Label.foreground", "TextField.foreground", "TextArea.foreground",
			"ComboBox.foreground", "List.foreground", "TabbedPane.foreground",
			"TitledBorder.titleColor", "ToolTip.foreground", "Table.foreground",
			"TableHeader.foreground", "Spinner.foreground",
			"OptionPane.messageForeground", "FormattedTextField.foreground",
			"MenuItem.foreground", "Menu.foreground"
		};
		for (int var10 = 0; var10 < var9.length; var10++) {
			UIManager.put(var9[var10], var2);
		}

		// White on the gradient: orange text on orange is unreadable, and these are
		// the only surfaces in the theme that are not dark.
		UIManager.put("Button.foreground", var4);
		UIManager.put("CheckBox.foreground", var4);
		UIManager.put("RadioButton.foreground", var4);
		UIManager.put("Button.select", new ColorUIResource(PRESSED_BOTTOM));
		UIManager.put("Button.disabledText", new ColorUIResource(MUTED));
		UIManager.put("CheckBox.disabledText", new ColorUIResource(MUTED));

		UIManager.put("TabbedPane.selected", var1);
		UIManager.put("TabbedPane.selectHighlight", var2);
		UIManager.put("TabbedPane.borderHightlightColor", var2);
		UIManager.put("TabbedPane.darkShadow", var3);
		UIManager.put("TabbedPane.shadow", var3);
		UIManager.put("TabbedPane.light", var3);
		UIManager.put("TabbedPane.highlight", var3);
		UIManager.put("TabbedPane.focus", var2);

		UIManager.put("ComboBox.selectionBackground", var3);
		UIManager.put("ComboBox.selectionForeground", var4);
		UIManager.put("List.selectionBackground", var3);
		UIManager.put("List.selectionForeground", var4);
		UIManager.put("TextField.caretForeground", var2);
		UIManager.put("TextArea.caretForeground", var2);
		UIManager.put("TextField.selectionBackground", var3);
		UIManager.put("TextArea.selectionBackground", var3);
		UIManager.put("TextField.selectionForeground", var4);
		UIManager.put("TextArea.selectionForeground", var4);
		UIManager.put("TextField.inactiveForeground", new ColorUIResource(MUTED));
		UIManager.put("TextArea.inactiveForeground", new ColorUIResource(MUTED));
		UIManager.put("ScrollBar.thumb", new ColorUIResource(BORDER));
		UIManager.put("ScrollBar.thumbHighlight", var2);
		UIManager.put("ScrollBar.thumbShadow", var0);
		UIManager.put("ToolTip.foregroundInactive", var2);
	}

	/** A thin orange rule, for separating sections without drawing a box. */
	public static javax.swing.border.Border rule(int top, int left, int bottom, int right) {
		return javax.swing.BorderFactory.createMatteBorder(top, left, bottom, right, BORDER);
	}

	/**
	 * Metal's palette, which is where every surface it paints ultimately comes from.
	 * <p>
	 * {@code secondary3} is the important one: Metal calls it "control" and fills
	 * panels, tab strips and the space behind unselected tabs with it. That is the
	 * white the keys could not reach.
	 */
	private static final class BwanaTheme extends DefaultMetalTheme {

		public String getName() {
			return "Bwana";
		}

		protected ColorUIResource getPrimary1() {
			return new ColorUIResource(BORDER);
		}

		protected ColorUIResource getPrimary2() {
			return new ColorUIResource(SURFACE);
		}

		protected ColorUIResource getPrimary3() {
			return new ColorUIResource(BORDER);
		}

		protected ColorUIResource getSecondary1() {
			return new ColorUIResource(BORDER);
		}

		protected ColorUIResource getSecondary2() {
			return new ColorUIResource(SURFACE);
		}

		/** Metal's "control": panels, tab strips, everything unspecified. */
		protected ColorUIResource getSecondary3() {
			return new ColorUIResource(BACKGROUND);
		}

		protected ColorUIResource getBlack() {
			return new ColorUIResource(BACKGROUND);
		}

		protected ColorUIResource getWhite() {
			return new ColorUIResource(ORANGE);
		}

		public ColorUIResource getControlTextColor() {
			return new ColorUIResource(ORANGE);
		}

		public ColorUIResource getSystemTextColor() {
			return new ColorUIResource(ORANGE);
		}

		public ColorUIResource getUserTextColor() {
			return new ColorUIResource(ORANGE);
		}

		public ColorUIResource getWindowBackground() {
			return new ColorUIResource(BACKGROUND);
		}

		public ColorUIResource getMenuBackground() {
			return new ColorUIResource(BACKGROUND);
		}

		public ColorUIResource getControl() {
			return new ColorUIResource(BACKGROUND);
		}

		public ColorUIResource getFocusColor() {
			return new ColorUIResource(ORANGE);
		}

		public FontUIResource getControlTextFont() {
			return new FontUIResource("Dialog", java.awt.Font.PLAIN, 11);
		}
	}

	/** Paint the orange gradient behind a button or checkbox. */
	private static void paintGradient(Graphics g, JComponent c) {
		AbstractButton var2 = (AbstractButton) c;
		ButtonModel var3 = var2.getModel();
		Color var4;
		Color var5;
		if (!var2.isEnabled()) {
			var4 = DISABLED_TOP;
			var5 = DISABLED_BOTTOM;
		} else if (var3.isPressed() || var3.isSelected()) {
			var4 = PRESSED_TOP;
			var5 = PRESSED_BOTTOM;
		} else {
			var4 = GRADIENT_TOP;
			var5 = GRADIENT_BOTTOM;
		}
		int var6 = c.getWidth();
		int var7 = c.getHeight();
		Graphics2D var8 = (Graphics2D) g.create();
		var8.setPaint(new GradientPaint(0.0F, 0.0F, var4, 0.0F, (float) var7, var5));
		var8.fillRect(0, 0, var6, var7);
		var8.setColor(BORDER);
		var8.drawRect(0, 0, var6 - 1, var7 - 1);
		var8.dispose();
	}

	/** Buttons: orange gradient, white text. */
	public static final class GradientButtonUI extends BasicButtonUI {

		public static ComponentUI createUI(JComponent c) {
			return new GradientButtonUI();
		}

		public void installDefaults(AbstractButton b) {
			super.installDefaults(b);
			b.setOpaque(true);
			b.setForeground(Color.WHITE);
			b.setBorder(javax.swing.BorderFactory.createEmptyBorder(4, 10, 4, 10));
		}

		public void update(Graphics g, JComponent c) {
			paintGradient(g, c);
			this.paint(g, c);
		}
	}

	/**
	 * Checkboxes: the same gradient across the whole row.
	 * <p>
	 * Metal has no gradient for these at all, and its tick icon is drawn from fixed
	 * colours, so the row is painted here and the label kept white on top of it.
	 */
	public static final class GradientCheckBoxUI extends javax.swing.plaf.metal.MetalCheckBoxUI {

		public static ComponentUI createUI(JComponent c) {
			return new GradientCheckBoxUI();
		}

		public void installDefaults(AbstractButton b) {
			super.installDefaults(b);
			b.setOpaque(true);
			b.setForeground(Color.WHITE);
			b.setBorder(javax.swing.BorderFactory.createEmptyBorder(3, 6, 3, 6));
		}

		public void update(Graphics g, JComponent c) {
			paintGradient(g, c);
			this.paint(g, c);
		}
	}
}
