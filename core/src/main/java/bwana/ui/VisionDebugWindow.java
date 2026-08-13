package bwana.ui;

import bwana.vision.ColorRule;
import bwana.vision.Detection;
import bwana.vision.Frame;
import bwana.vision.Histogram;
import bwana.vision.Palette;
import bwana.vision.RuleFitter;
import bwana.vision.RuleStore;
import bwana.vision.SampleSet;
import bwana.vision.Vision;
import bwana.vision.VisionDebug;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * The observable half of the vision layer, and its diagnostic bench.
 * <p>
 * The detection pipeline has three stages and each can fail differently: the
 * colour test can match the wrong pixels, the min-area filter can throw away the
 * right ones, and connectivity can merge or split shapes. Showing only the final
 * boxes makes those indistinguishable, so the view can be switched to each stage:
 * <ul>
 * <li><b>Frame</b> — the raw viewport with result boxes</li>
 * <li><b>Mask</b> — every pixel the colour test passed, before filtering</li>
 * <li><b>Regions</b> — surviving regions, one colour each</li>
 * <li><b>Mask over frame</b> — the mask tinted over the scene, for context</li>
 * </ul>
 * Detection behaviour is unchanged by any of this; the extra stages are only
 * retained while this window is open.
 */
public final class VisionDebugWindow extends JFrame implements Vision.Listener {

	private static final int VIEW_FRAME = 0;
	private static final int VIEW_MASK = 1;
	private static final int VIEW_REGIONS = 2;
	private static final int VIEW_OVERLAY = 3;

	private static final int PALETTE_SIZE = 14;

	private final Vision vision = Vision.getInstance();
	private final Palette palette = new Palette();
	private final ImageView view = new ImageView();
	private final JTextArea list = new JTextArea();
	private final JLabel stats = new JLabel(" ");
	private final JLabel hover = new JLabel(" ");

	private final JComboBox ruleBox = new JComboBox();
	private final JCheckBox ruleEnabled = new JCheckBox("Rule enabled");
	private final JComboBox modeBox = new JComboBox(new String[] { "RGB", "HSV" });
	private final JComboBox viewBox = new JComboBox(new String[] {
		"Frame + boxes", "Mask (pre-filter)", "Regions (post-filter)", "Mask over frame" });
	private final JLabel swatch = new JLabel();
	private final JLabel rangeLabel = new JLabel();
	private final JSlider rgbTol = new JSlider(0, 128, 24);
	private final JSlider hueTol = new JSlider(0, 180, 12);
	private final JSlider satTol = new JSlider(0, 100, 40);
	private final JSlider valTol = new JSlider(0, 100, 40);
	private final JSlider minArea = new JSlider(1, 500, 12);
	private final JSlider gapFill = new JSlider(0, 5, 0);
	private final JLabel gapLabel = new JLabel();
	private final JLabel rgbLabel = new JLabel();
	private final JLabel hueLabel = new JLabel();
	private final JLabel satLabel = new JLabel();
	private final JLabel valLabel = new JLabel();
	private final JLabel areaLabel = new JLabel();
	private final JPanel palettePanel = new JPanel(new GridLayout(0, 7, 2, 2));
	private final JToggleButton eyedropper = new JToggleButton("Eyedropper: hover the game");
	private final JLabel eyedropLabel = new JLabel("Off");
	private final JLabel warnLabel = new JLabel(" ");
	private final JLabel fitLabel = new JLabel("No selection");
	private final JLabel storeLabel = new JLabel(" ");
	private final JLabel sampleLabel = new JLabel(" ");
	private final SampleSet samples = new SampleSet();
	private final HistogramPanel histogramPanel = new HistogramPanel();
	private final JLabel histogramLabel = new JLabel(" ");

	private final float[] hsv = new float[3];
	private boolean syncing;
	private int lastSampled = -1;
	private Palette.Entry[] paletteEntries = new Palette.Entry[0];

	public VisionDebugWindow() {
		super("Bwana - vision");
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		this.list.setEditable(false);
		this.list.setFont(new Font("Monospaced", Font.PLAIN, 11));
		this.hover.setFont(new Font("Monospaced", Font.PLAIN, 11));
		this.hover.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));

		// click samples one pixel; drag selects an area to fit a rule to
		this.view.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent event) {
				VisionDebugWindow.this.view.beginSelection(event.getX(), event.getY());
			}

			public void mouseReleased(MouseEvent event) {
				if (VisionDebugWindow.this.view.endSelection(event.getX(), event.getY())) {
					VisionDebugWindow.this.describeSelection();
				} else {
					VisionDebugWindow.this.sampleAt(event.getX(), event.getY());
				}
			}
		});
		this.view.addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseMoved(MouseEvent event) {
				VisionDebugWindow.this.describeAt(event.getX(), event.getY());
			}

			public void mouseDragged(MouseEvent event) {
				VisionDebugWindow.this.view.dragSelection(event.getX(), event.getY());
			}
		});

		JScrollPane var0 = new JScrollPane(this.buildSidePanel());
		var0.setPreferredSize(new Dimension(340, 700));
		var0.getVerticalScrollBar().setUnitIncrement(16);

		JSplitPane var1 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
			new JScrollPane(this.view), var0);
		var1.setResizeWeight(1.0D);

		JScrollPane var2 = new JScrollPane(this.list);
		var2.setBorder(BorderFactory.createTitledBorder("Detections (viewport / screen coordinates)"));
		var2.setPreferredSize(new Dimension(100, 130));

		JSplitPane var3 = new JSplitPane(JSplitPane.VERTICAL_SPLIT, var1, var2);
		var3.setResizeWeight(1.0D);

		JPanel var4 = new JPanel(new BorderLayout());
		var4.add(this.hover, BorderLayout.NORTH);
		var4.add(this.stats, BorderLayout.SOUTH);
		this.stats.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));

		this.add(var3, BorderLayout.CENTER);
		this.add(var4, BorderLayout.SOUTH);

		this.rebuildRuleBox();
		this.syncFromRule();
		this.pushDebugRule();

		// poll the desktop pointer so a colour can be sampled from the live game
		// window; MouseInfo works without focus, so the game keeps its input
		Timer var5 = new Timer(100, new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				VisionDebugWindow.this.pollEyedropper();
			}
		});
		var5.start();

		this.vision.addListener(this);
		this.pack();
		this.setLocationByPlatform(true);
	}

	public void dispose() {
		this.vision.removeListener(this);
		// stop paying for mask/label copies once the bench is closed
		this.vision.setDebugRule(null);
		super.dispose();
	}

	// ---- construction ----

	private JPanel buildSidePanel() {
		JPanel var1 = new JPanel(new GridBagLayout());
		var1.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
		// Deliberately no setPreferredSize here. Fixing the container's height
		// makes GridBagLayout squeeze the children into it, and the histogram --
		// the tallest child -- collapses to a few pixels. The scroll pane below
		// sets the width instead, and the column takes whatever height its
		// contents need.
		GridBagConstraints var2 = new GridBagConstraints();
		var2.gridx = 0;
		var2.gridy = 0;
		var2.fill = GridBagConstraints.HORIZONTAL;
		var2.weightx = 1.0D;
		var2.insets = new Insets(2, 0, 2, 0);

		var1.add(section("View"), var2);
		var2.gridy++;
		var1.add(this.viewBox, var2);
		var2.gridy++;

		var1.add(section("Rule"), var2);
		var2.gridy++;
		var1.add(this.ruleBox, var2);
		var2.gridy++;
		var1.add(this.ruleEnabled, var2);
		var2.gridy++;

		JPanel var3 = new JPanel(new BorderLayout(6, 0));
		var3.add(new JLabel("Target"), BorderLayout.WEST);
		this.swatch.setOpaque(true);
		this.swatch.setPreferredSize(new Dimension(60, 18));
		this.swatch.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		var3.add(this.swatch, BorderLayout.CENTER);
		var1.add(var3, var2);
		var2.gridy++;

		var1.add(new JLabel("<html>Click a pixel to sample it.<br>"
			+ "<b>Drag a box over an object to fit the rule to it.</b></html>"), var2);
		var2.gridy++;
		JButton var9 = new JButton("Fit vs background (recommended)");
		var9.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				VisionDebugWindow.this.fitToSelection(true);
			}
		});
		var1.add(var9, var2);
		var2.gridy++;
		JButton var10 = new JButton("Fit to selection only");
		var10.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				VisionDebugWindow.this.fitToSelection(false);
			}
		});
		var1.add(var10, var2);
		var2.gridy++;
		this.fitLabel.setFont(new Font("Monospaced", Font.PLAIN, 11));
		var1.add(this.fitLabel, var2);
		var2.gridy++;
		var1.add(this.modeBox, var2);
		var2.gridy++;

		this.rangeLabel.setFont(new Font("Monospaced", Font.PLAIN, 11));
		var1.add(this.rangeLabel, var2);
		var2.gridy++;
		this.warnLabel.setFont(this.warnLabel.getFont().deriveFont(Font.BOLD, 11.0F));
		this.warnLabel.setForeground(new Color(0xB00020));
		var1.add(this.warnLabel, var2);
		var2.gridy++;

		this.addSlider(var1, var2, this.rgbLabel, this.rgbTol);
		this.addSlider(var1, var2, this.hueLabel, this.hueTol);
		this.addSlider(var1, var2, this.satLabel, this.satTol);
		this.addSlider(var1, var2, this.valLabel, this.valTol);
		this.addSlider(var1, var2, this.areaLabel, this.minArea);
		this.addSlider(var1, var2, this.gapLabel, this.gapFill);

		JCheckBox var4 = new JCheckBox("8-way connectivity", this.vision.isEightWay());
		var4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				VisionDebugWindow.this.vision.setEightWay(((JCheckBox) event.getSource()).isSelected());
			}
		});
		var1.add(var4, var2);
		var2.gridy++;

		var1.add(section("Sample from the game window"), var2);
		var2.gridy++;
		var1.add(this.eyedropper, var2);
		var2.gridy++;
		this.eyedropLabel.setFont(new Font("Monospaced", Font.PLAIN, 11));
		var1.add(this.eyedropLabel, var2);
		var2.gridy++;
		JButton var5 = new JButton("Use sampled colour as target");
		var5.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				VisionDebugWindow.this.applySample();
			}
		});
		var1.add(var5, var2);
		var2.gridy++;

		var1.add(section("Dominant colours (click to target)"), var2);
		var2.gridy++;
		var1.add(this.palettePanel, var2);
		var2.gridy++;

		var1.add(section("Distribution: scene (grey) vs matched"), var2);
		var2.gridy++;
		var1.add(this.histogramPanel, var2);
		var2.gridy++;
		this.histogramLabel.setFont(new Font("Monospaced", Font.PLAIN, 11));
		var1.add(this.histogramLabel, var2);
		var2.gridy++;

		var1.add(section("Train from examples"), var2);
		var2.gridy++;
		var1.add(new JLabel("<html>Drag a box, then mark it. Examples add up<br>"
			+ "across frames, so you can walk to a<br>"
			+ "different tree and add another.</html>"), var2);
		var2.gridy++;
		JButton var14 = new JButton("+ This IS the target");
		var14.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				VisionDebugWindow.this.addSample(true);
			}
		});
		var1.add(var14, var2);
		var2.gridy++;
		JButton var15 = new JButton("- This is NOT the target");
		var15.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				VisionDebugWindow.this.addSample(false);
			}
		});
		var1.add(var15, var2);
		var2.gridy++;
		JButton var16 = new JButton("- Everything else in this frame");
		var16.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				VisionDebugWindow.this.addBackgroundSample();
			}
		});
		var1.add(var16, var2);
		var2.gridy++;
		this.sampleLabel.setFont(new Font("Monospaced", Font.PLAIN, 11));
		var1.add(this.sampleLabel, var2);
		var2.gridy++;
		JButton var17 = new JButton("Train rule from examples");
		var17.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				VisionDebugWindow.this.trainFromSamples();
			}
		});
		var1.add(var17, var2);
		var2.gridy++;
		JButton var18 = new JButton("Clear examples");
		var18.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				VisionDebugWindow.this.samples.clear();
				VisionDebugWindow.this.updateSampleLabel();
			}
		});
		var1.add(var18, var2);
		var2.gridy++;

		var1.add(section("Saved targets"), var2);
		var2.gridy++;
		JButton var6 = new JButton("Add rule from current target");
		var6.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				VisionDebugWindow.this.addRule();
			}
		});
		var1.add(var6, var2);
		var2.gridy++;
		JButton var11 = new JButton("Save targets to disk");
		var11.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				VisionDebugWindow.this.saveRules();
			}
		});
		var1.add(var11, var2);
		var2.gridy++;
		JButton var12 = new JButton("Reload targets from disk");
		var12.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				VisionDebugWindow.this.reloadRules();
			}
		});
		var1.add(var12, var2);
		var2.gridy++;
		this.storeLabel.setFont(new Font("Monospaced", Font.PLAIN, 11));
		var1.add(this.storeLabel, var2);
		var2.gridy++;
		JButton var13 = new JButton("Rename this target");
		var13.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				VisionDebugWindow.this.renameRule();
			}
		});
		var1.add(var13, var2);
		var2.gridy++;

		var2.fill = GridBagConstraints.BOTH;
		var2.weighty = 1.0D;
		var1.add(Box.createVerticalGlue(), var2);

		ActionListener var7 = new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				VisionDebugWindow.this.applyToRule();
			}
		};
		this.ruleEnabled.addActionListener(var7);
		this.modeBox.addActionListener(var7);
		this.ruleBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				VisionDebugWindow.this.syncFromRule();
				VisionDebugWindow.this.pushDebugRule();
			}
		});
		this.viewBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				VisionDebugWindow.this.view.setViewMode(VisionDebugWindow.this.viewBox.getSelectedIndex());
			}
		});

		ChangeListener var8 = new ChangeListener() {
			public void stateChanged(ChangeEvent event) {
				VisionDebugWindow.this.applyToRule();
			}
		};
		this.rgbTol.addChangeListener(var8);
		this.hueTol.addChangeListener(var8);
		this.satTol.addChangeListener(var8);
		this.valTol.addChangeListener(var8);
		this.minArea.addChangeListener(var8);
		this.gapFill.addChangeListener(var8);

		return var1;
	}

	private static JLabel section(String text) {
		JLabel var1 = new JLabel(text);
		var1.setFont(var1.getFont().deriveFont(Font.BOLD));
		var1.setBorder(BorderFactory.createEmptyBorder(8, 0, 2, 0));
		return var1;
	}

	private void addSlider(JPanel panel, GridBagConstraints c, JLabel label, JSlider slider) {
		panel.add(label, c);
		c.gridy++;
		panel.add(slider, c);
		c.gridy++;
	}

	// ---- rules ----

	private void rebuildRuleBox() {
		this.syncing = true;
		Object var1 = this.ruleBox.getSelectedItem();
		this.ruleBox.removeAllItems();
		List<ColorRule> var2 = this.vision.getRules();
		for (int var3 = 0; var3 < var2.size(); var3++) {
			this.ruleBox.addItem(var2.get(var3).name);
		}
		if (var1 != null) {
			this.ruleBox.setSelectedItem(var1);
		}
		this.syncing = false;
	}

	private ColorRule selectedRule() {
		Object var1 = this.ruleBox.getSelectedItem();
		if (var1 == null) {
			return null;
		}
		List<ColorRule> var2 = this.vision.getRules();
		for (int var3 = 0; var3 < var2.size(); var3++) {
			if (var2.get(var3).name.equals(var1)) {
				return var2.get(var3);
			}
		}
		return null;
	}

	/** Tell Vision to retain mask and labels for whichever rule is on the bench. */
	private void pushDebugRule() {
		ColorRule var1 = this.selectedRule();
		this.vision.setDebugRule(var1 == null ? null : var1.name);
	}

	private void syncFromRule() {
		ColorRule var1 = this.selectedRule();
		if (var1 == null) {
			return;
		}
		this.syncing = true;
		this.ruleEnabled.setSelected(var1.enabled);
		this.modeBox.setSelectedIndex(var1.mode);
		this.rgbTol.setValue(var1.rgbTolerance);
		this.hueTol.setValue(var1.hueTolerance);
		this.satTol.setValue(var1.satTolerance);
		this.valTol.setValue(var1.valTolerance);
		this.minArea.setValue(var1.minArea);
		this.gapFill.setValue(var1.gapFill);
		this.swatch.setBackground(new Color(var1.targetRgb));
		this.syncing = false;
		this.updateLabels(var1);
	}

	private void applyToRule() {
		if (this.syncing) {
			return;
		}
		ColorRule var1 = this.selectedRule();
		if (var1 == null) {
			return;
		}
		var1.enabled = this.ruleEnabled.isSelected();
		var1.mode = this.modeBox.getSelectedIndex();
		var1.rgbTolerance = this.rgbTol.getValue();
		var1.hueTolerance = this.hueTol.getValue();
		var1.satTolerance = this.satTol.getValue();
		var1.valTolerance = this.valTol.getValue();
		var1.minArea = this.minArea.getValue();
		var1.gapFill = this.gapFill.getValue();
		this.updateLabels(var1);
	}

	private void updateLabels(ColorRule rule) {
		boolean var2 = rule.mode == ColorRule.MODE_RGB;
		this.rgbLabel.setText("RGB tolerance: " + rule.rgbTolerance + (var2 ? "" : "  (unused in HSV)"));
		this.hueLabel.setText("Hue tolerance: " + rule.hueTolerance + " deg" + (var2 ? "  (unused in RGB)" : ""));
		this.satLabel.setText("Saturation tolerance: " + rule.satTolerance + (var2 ? "  (unused in RGB)" : ""));
		this.valLabel.setText("Value tolerance: " + rule.valTolerance + (var2 ? "  (unused in RGB)" : ""));
		this.areaLabel.setText("Minimum region area: " + rule.minArea + " px");
		this.gapLabel.setText("Gap fill: " + rule.gapFill + " px"
			+ (rule.gapFill == 0 ? "  (off - foliage splits into blobs)" : "  (merges broken canopy)"));
		this.rgbTol.setEnabled(var2);
		this.hueTol.setEnabled(!var2);
		this.satTol.setEnabled(!var2);
		this.valTol.setEnabled(!var2);
		this.rangeLabel.setText(describeRange(rule));
		this.warnLabel.setText(warnFor(rule));
	}

	/**
	 * Hue is derived from (max-min)/max across the channels, so as a colour
	 * approaches black or grey that ratio collapses and tiny channel differences
	 * swing the hue wildly. A target sampled from a dark or washed-out pixel
	 * therefore carries a hue that is close to meaningless, and no hue tolerance
	 * will discriminate with it.
	 */
	private static String warnFor(ColorRule rule) {
		if (rule.mode != ColorRule.MODE_HSV) {
			return " ";
		}
		float[] var1 = new float[3];
		ColorRule.toHsv(rule.targetRgb, var1);
		if (var1[2] < 15.0F) {
			return "<html>Target is very dark (V" + (int) var1[2] + ").<br>"
				+ "Hue is unstable near black - sample a lit pixel.</html>";
		}
		if (var1[1] < 15.0F) {
			return "<html>Target is nearly grey (S" + (int) var1[1] + ").<br>"
				+ "Hue is unstable at low saturation - match on V instead.</html>";
		}
		return " ";
	}

	/** The exact window of colours the rule currently accepts. */
	private static String describeRange(ColorRule rule) {
		int var1 = rule.targetRgb;
		if (rule.mode == ColorRule.MODE_RGB) {
			int var2 = rule.rgbTolerance;
			return "<html>Matching R " + clampLo(var1 >> 16 & 0xFF, var2) + "-" + clampHi(var1 >> 16 & 0xFF, var2)
				+ ", G " + clampLo(var1 >> 8 & 0xFF, var2) + "-" + clampHi(var1 >> 8 & 0xFF, var2)
				+ ", B " + clampLo(var1 & 0xFF, var2) + "-" + clampHi(var1 & 0xFF, var2)
				+ "<br>target " + hex(var1) + "</html>";
		}
		float[] var3 = new float[3];
		ColorRule.toHsv(var1, var3);
		int var4 = (int) var3[0];
		int var5 = (int) var3[1];
		int var6 = (int) var3[2];
		int var7 = (var4 - rule.hueTolerance + 360) % 360;
		int var8 = (var4 + rule.hueTolerance) % 360;
		return "<html>Matching H " + var7 + "-" + var8 + " deg (wraps)"
			+ ", S " + Math.max(0, var5 - rule.satTolerance) + "-" + Math.min(100, var5 + rule.satTolerance)
			+ ", V " + Math.max(0, var6 - rule.valTolerance) + "-" + Math.min(100, var6 + rule.valTolerance)
			+ "<br>target " + hex(var1) + "  H" + var4 + " S" + var5 + " V" + var6 + "</html>";
	}

	private static int clampLo(int v, int t) {
		int var2 = v - t;
		return var2 < 0 ? 0 : var2;
	}

	private static int clampHi(int v, int t) {
		int var2 = v + t;
		return var2 > 255 ? 255 : var2;
	}

	// ---- sampling ----

	private void sampleAt(int x, int y) {
		Frame var3 = this.view.frame;
		if (var3 == null || x < 0 || y < 0 || x >= var3.width || y >= var3.height) {
			return;
		}
		int var4 = var3.getRgb(x, y);
		this.lastSampled = var4;
		ColorRule var5 = this.selectedRule();
		if (var5 != null) {
			var5.setTarget(var4);
			this.swatch.setBackground(new Color(var4));
			this.updateLabels(var5);
		}
		this.describeAt(x, y);
	}

	/** Live readout for the pixel under the pointer. */
	private void describeAt(int x, int y) {
		Frame var3 = this.view.frame;
		if (var3 == null || x < 0 || y < 0 || x >= var3.width || y >= var3.height) {
			this.hover.setText(" ");
			return;
		}
		int var4 = var3.getRgb(x, y);
		ColorRule.toHsv(var4, this.hsv);
		StringBuilder var5 = new StringBuilder();
		var5.append("viewport ").append(x).append(',').append(y);
		var5.append("   screen ").append(x + var3.canvasOriginX + this.vision.getScreenOriginX())
			.append(',').append(y + var3.canvasOriginY + this.vision.getScreenOriginY());
		var5.append("   ").append(hex(var4));
		var5.append(" RGB(").append(var4 >> 16 & 0xFF).append(',').append(var4 >> 8 & 0xFF)
			.append(',').append(var4 & 0xFF).append(')');
		var5.append(" HSV(").append((int) this.hsv[0]).append(',').append((int) this.hsv[1])
			.append(',').append((int) this.hsv[2]).append(')');

		ColorRule var6 = this.selectedRule();
		if (var6 != null) {
			var5.append(var6.matches(var4, new float[3]) ? "   MATCH" : "   no match");
		}
		Detection var7 = this.detectionAt(x, y);
		if (var7 != null) {
			var5.append("   [in ").append(var7.ruleName).append(' ')
				.append(var7.width).append('x').append(var7.height)
				.append(" area ").append(var7.area)
				.append(" box ").append(var7.x).append(',').append(var7.y).append(']');
		}
		this.hover.setText(var5.toString());
	}

	/** Smallest detection containing the point, so overlapping boxes stay useful. */
	private Detection detectionAt(int x, int y) {
		List<Detection> var3 = this.view.detections;
		if (var3 == null) {
			return null;
		}
		Detection var4 = null;
		for (int var5 = 0; var5 < var3.size(); var5++) {
			Detection var6 = var3.get(var5);
			if (x >= var6.x && y >= var6.y && x < var6.x + var6.width && y < var6.y + var6.height
				&& (var4 == null || var6.area < var4.area)) {
				var4 = var6;
			}
		}
		return var4;
	}

	/** Reads the pixel under the desktop pointer when it is over the game view. */
	private void pollEyedropper() {
		if (!this.eyedropper.isSelected()) {
			return;
		}
		Frame var1 = this.vision.getLatestFrame();
		if (var1 == null) {
			this.eyedropLabel.setText("no frame yet");
			return;
		}
		PointerInfo var2 = MouseInfo.getPointerInfo();
		if (var2 == null) {
			return;
		}
		Point var3 = var2.getLocation();
		int var4 = var3.x - this.vision.getScreenOriginX() - var1.canvasOriginX;
		int var5 = var3.y - this.vision.getScreenOriginY() - var1.canvasOriginY;
		if (var4 < 0 || var5 < 0 || var4 >= var1.width || var5 >= var1.height) {
			this.eyedropLabel.setText("pointer is outside the game view");
			return;
		}
		int var6 = var1.getRgb(var4, var5);
		this.lastSampled = var6;
		ColorRule.toHsv(var6, this.hsv);
		this.eyedropLabel.setText("<html>" + hex(var6)
			+ " RGB(" + (var6 >> 16 & 0xFF) + "," + (var6 >> 8 & 0xFF) + "," + (var6 & 0xFF) + ")"
			+ "<br>HSV(" + (int) this.hsv[0] + "," + (int) this.hsv[1] + "," + (int) this.hsv[2] + ")"
			+ " at " + var4 + "," + var5 + "</html>");
		this.swatch.setBackground(new Color(var6));
	}

	private void describeSelection() {
		Rectangle var1 = this.view.getSelection();
		if (var1 == null) {
			this.fitLabel.setText("No selection");
			return;
		}
		this.fitLabel.setText("Selected " + var1.width + "x" + var1.height
			+ " at " + var1.x + "," + var1.y);
	}

	/**
	 * Measure the selected area and set the rule from it.
	 * <p>
	 * Switches the rule to HSV, because that is the space the measurement is made
	 * in — writing HSV-derived tolerances into an RGB rule would silently do
	 * nothing.
	 */
	private void fitToSelection(boolean againstBackground) {
		Rectangle var2 = this.view.getSelection();
		Frame var3 = this.view.frame;
		ColorRule var4 = this.selectedRule();
		if (var2 == null || var3 == null || var4 == null) {
			this.fitLabel.setText("Drag a box over the object first");
			return;
		}
		int var5 = var2.x + var2.width - 1;
		int var6 = var2.y + var2.height - 1;
		RuleFitter.Fit var7 = againstBackground
			? RuleFitter.fitAgainstBackground(var3, var2.x, var2.y, var5, var6)
			: RuleFitter.fit(var3, var2.x, var2.y, var5, var6);
		if (var7 == null) {
			this.fitLabel.setText("Selection was empty");
			return;
		}
		var4.mode = ColorRule.MODE_HSV;
		var4.setTarget(var7.targetRgb);
		var4.hueTolerance = var7.hueTolerance;
		var4.satTolerance = var7.satTolerance;
		var4.valTolerance = var7.valTolerance;
		this.syncFromRule();

		StringBuilder var8 = new StringBuilder("fitted from ").append(var7.pixels).append(" px");
		if (var7.recall >= 0) {
			var8.append("<br>keeps ").append(var7.recall).append("% of selection")
				.append("<br>takes ").append(var7.fallout).append("% of background");
		}
		if (var7.unstableHuePixels > 0) {
			var8.append("<br>").append(var7.unstableHuePixels).append(" px too dark/grey for hue");
		}
		this.fitLabel.setText("<html>" + var8.toString() + "</html>");
	}

	private void applySample() {
		if (this.lastSampled < 0) {
			return;
		}
		ColorRule var1 = this.selectedRule();
		if (var1 == null) {
			return;
		}
		var1.setTarget(this.lastSampled);
		this.swatch.setBackground(new Color(this.lastSampled));
		this.updateLabels(var1);
	}

	private void addRule() {
		ColorRule var1 = this.selectedRule();
		int var2 = this.lastSampled >= 0 ? this.lastSampled : (var1 == null ? 0xFF00FF : var1.targetRgb);
		ColorRule var3 = new ColorRule("Rule " + (this.vision.getRules().size() + 1), var2);
		var3.overlayRgb = 0xFF3399;
		var3.minArea = 20;
		this.vision.addRule(var3);
		this.rebuildRuleBox();
		this.ruleBox.setSelectedItem(var3.name);
	}

	private void addSample(boolean positive) {
		Rectangle var2 = this.view.getSelection();
		Frame var3 = this.view.frame;
		if (var2 == null || var3 == null) {
			this.sampleLabel.setText("Drag a box first");
			return;
		}
		this.samples.add(var3, var2.x, var2.y,
			var2.x + var2.width - 1, var2.y + var2.height - 1, positive);
		this.updateSampleLabel();
	}

	/**
	 * Mark the whole frame except the selection as counter-examples. Useful when
	 * the target is on screen and everything around it is something to reject.
	 */
	private void addBackgroundSample() {
		Rectangle var1 = this.view.getSelection();
		Frame var2 = this.view.frame;
		if (var2 == null) {
			this.sampleLabel.setText("No frame yet");
			return;
		}
		if (var1 == null) {
			// no selection: the entire frame is a counter-example, which is exactly
			// what you want on a screen with none of the target in it
			this.samples.addBackground(var2, -1, -1, -1, -1);
		} else {
			this.samples.addBackground(var2, var1.x, var1.y,
				var1.x + var1.width - 1, var1.y + var1.height - 1);
		}
		this.updateSampleLabel();
	}

	private void updateSampleLabel() {
		this.sampleLabel.setText("<html>" + this.samples.getPositiveBoxes() + " positive box(es), "
			+ this.samples.getPositiveCount() + " px<br>"
			+ this.samples.getNegativeBoxes() + " negative box(es), "
			+ this.samples.getNegativeCount() + " px</html>");
	}

	private void trainFromSamples() {
		ColorRule var1 = this.selectedRule();
		if (var1 == null) {
			return;
		}
		RuleFitter.Fit var2 = RuleFitter.fitFromSamples(this.samples);
		if (var2 == null) {
			this.sampleLabel.setText("Add at least one positive example first");
			return;
		}
		var1.mode = ColorRule.MODE_HSV;
		var1.setTarget(var2.targetRgb);
		var1.hueTolerance = var2.hueTolerance;
		var1.satTolerance = var2.satTolerance;
		var1.valTolerance = var2.valTolerance;
		this.syncFromRule();

		StringBuilder var3 = new StringBuilder("<html>trained on ")
			.append(this.samples.getPositiveCount()).append(" px");
		if (var2.recall >= 0) {
			var3.append("<br>keeps ").append(var2.recall).append("% of positives")
				.append("<br>takes ").append(var2.fallout).append("% of negatives");
		} else {
			var3.append("<br>no negatives yet - add some");
		}
		var3.append("</html>");
		this.sampleLabel.setText(var3.toString());
	}

	private void saveRules() {
		try {
			RuleStore.save(this.vision.getRules());
			this.storeLabel.setText("<html>Saved " + this.vision.getRules().size()
				+ " target(s) to<br>" + RuleStore.file().getName() + "</html>");
		} catch (Exception var2) {
			this.storeLabel.setText("<html>Save failed: " + var2.getMessage() + "</html>");
		}
	}

	private void reloadRules() {
		List<ColorRule> var1 = RuleStore.load();
		if (var1.isEmpty()) {
			this.storeLabel.setText("Nothing saved yet");
			return;
		}
		List<ColorRule> var2 = this.vision.getRules();
		for (int var3 = var2.size() - 1; var3 >= 0; var3--) {
			this.vision.removeRule(var2.get(var3));
		}
		for (int var4 = 0; var4 < var1.size(); var4++) {
			this.vision.addRule(var1.get(var4));
		}
		this.rebuildRuleBox();
		this.syncFromRule();
		this.pushDebugRule();
		this.storeLabel.setText("Loaded " + var1.size() + " target(s)");
	}

	private void renameRule() {
		ColorRule var1 = this.selectedRule();
		if (var1 == null) {
			return;
		}
		String var2 = JOptionPane.showInputDialog(this, "Target name", var1.name);
		if (var2 == null || var2.trim().length() == 0) {
			return;
		}
		var1.name = var2.trim();
		this.rebuildRuleBox();
		this.ruleBox.setSelectedItem(var1.name);
		this.pushDebugRule();
	}

	private void rebuildPalette() {
		this.palettePanel.removeAll();
		for (int var1 = 0; var1 < this.paletteEntries.length; var1++) {
			final Palette.Entry var2 = this.paletteEntries[var1];
			JButton var3 = new JButton();
			var3.setBackground(new Color(var2.rgb));
			var3.setOpaque(true);
			var3.setBorderPainted(false);
			var3.setPreferredSize(new Dimension(28, 22));
			var3.setToolTipText(hex(var2.rgb) + "  " + String.format("%.1f", new Object[] {
				Double.valueOf(var2.percent) }) + "% of frame");
			var3.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			var3.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					VisionDebugWindow.this.lastSampled = var2.rgb;
					VisionDebugWindow.this.applySample();
				}
			});
			this.palettePanel.add(var3);
		}
		this.palettePanel.revalidate();
		this.palettePanel.repaint();
	}

	private static String hex(int rgb) {
		String var1 = Integer.toHexString(rgb & 0xFFFFFF);
		while (var1.length() < 6) {
			var1 = "0" + var1;
		}
		return "#" + var1.toUpperCase();
	}

	// ---- results ----

	/** Worker thread. Palette and histograms are computed here, off the EDT. */
	public void onDetections(final Frame frame, final List<Detection> detections) {
		final Palette.Entry[] var3 = this.palette.dominant(frame, PALETTE_SIZE);
		final ColorRule var4 = this.selectedRule();
		final Histogram var5 = Histogram.compute(frame, var4);
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				VisionDebugWindow.this.paletteEntries = var3;
				VisionDebugWindow.this.rebuildPalette();
				VisionDebugWindow.this.histogramPanel.update(var5, var4);
				VisionDebugWindow.this.histogramLabel.setText(var5.matched + " of " + var5.total
					+ " px matched (" + (var5.total == 0 ? 0 : var5.matched * 100 / var5.total) + "%)");
				VisionDebugWindow.this.view.update(frame, detections, VisionDebugWindow.this.vision.getDebug());

				StringBuilder var1 = new StringBuilder();
				for (int var2 = 0; var2 < detections.size(); var2++) {
					var1.append(detections.get(var2).toString()).append('\n');
				}
				VisionDebugWindow.this.list.setText(var1.toString());
				VisionDebugWindow.this.list.setCaretPosition(0);

				int var3x = frame.width * frame.height;
				Detection var4 = null;
				for (int var5 = 0; var5 < detections.size(); var5++) {
					Detection var6 = detections.get(var5);
					if (var4 == null || var6.area > var4.area) {
						var4 = var6;
					}
				}
				String var7 = "";
				if (var4 != null && var4.area * 100L / (long) var3x >= 40L) {
					var7 = "   [" + var4.ruleName + " covers " + (var4.area * 100L / (long) var3x)
						+ "% of the frame - tolerance is too wide]";
				}
				VisionDebug var8 = VisionDebugWindow.this.vision.getDebug();
				String var9 = "";
				if (var8 != null) {
					// the gap between these two numbers is what min-area removed
					var9 = "   mask " + var8.maskCount + " px -> " + var8.regionCount + " regions";
				}
				VisionDebugWindow.this.stats.setText(detections.size() + " regions   scan "
					+ VisionDebugWindow.this.vision.getLastScanMillis() + " ms   frame "
					+ frame.width + "x" + frame.height + var9 + var7);
			}
		});
	}

	/** Renders whichever pipeline stage is selected. */
	private static final class ImageView extends JPanel {

		Frame frame;
		List<Detection> detections;
		VisionDebug debug;
		private BufferedImage image;
		private int viewMode = VIEW_FRAME;

		private int dragStartX = -1;
		private int dragStartY = -1;
		private Rectangle selection;
		private Rectangle dragging;

		ImageView() {
			this.setBackground(Color.DARK_GRAY);
			this.setPreferredSize(new Dimension(512, 334));
		}

		void beginSelection(int x, int y) {
			this.dragStartX = x;
			this.dragStartY = y;
			this.dragging = null;
		}

		void dragSelection(int x, int y) {
			if (this.dragStartX < 0) {
				return;
			}
			this.dragging = rect(this.dragStartX, this.dragStartY, x, y);
			this.repaint();
		}

		/** @return true if this was a drag rather than a click */
		boolean endSelection(int x, int y) {
			boolean var3 = this.dragStartX >= 0
				&& (Math.abs(x - this.dragStartX) > 3 || Math.abs(y - this.dragStartY) > 3);
			if (var3) {
				this.selection = rect(this.dragStartX, this.dragStartY, x, y);
			}
			this.dragStartX = -1;
			this.dragging = null;
			this.repaint();
			return var3;
		}

		Rectangle getSelection() {
			return this.selection;
		}

		private static Rectangle rect(int x0, int y0, int x1, int y1) {
			int var4 = Math.min(x0, x1);
			int var5 = Math.min(y0, y1);
			return new Rectangle(var4, var5, Math.abs(x1 - x0) + 1, Math.abs(y1 - y0) + 1);
		}

		void setViewMode(int mode) {
			this.viewMode = mode;
			this.rebuild();
			this.repaint();
		}

		void update(Frame frame, List<Detection> detections, VisionDebug debug) {
			this.frame = frame;
			this.detections = detections;
			this.debug = debug;
			if (this.image == null || this.image.getWidth() != frame.width || this.image.getHeight() != frame.height) {
				this.image = new BufferedImage(frame.width, frame.height, BufferedImage.TYPE_INT_RGB);
				this.setPreferredSize(new Dimension(frame.width, frame.height));
				this.revalidate();
			}
			this.rebuild();
			this.repaint();
		}

		private void rebuild() {
			if (this.frame == null || this.image == null) {
				return;
			}
			int var1 = this.frame.width * this.frame.height;
			if (this.viewMode == VIEW_FRAME) {
				this.image.setRGB(0, 0, this.frame.width, this.frame.height, this.frame.pixels, 0, this.frame.width);
				return;
			}
			if (this.debug == null || this.debug.mask.length < var1) {
				this.image.setRGB(0, 0, this.frame.width, this.frame.height, this.frame.pixels, 0, this.frame.width);
				return;
			}
			int[] var2 = new int[var1];
			if (this.viewMode == VIEW_MASK) {
				for (int var3 = 0; var3 < var1; var3++) {
					var2[var3] = this.debug.mask[var3] != 0 ? 0xFFFFFF : 0x101010;
				}
			} else if (this.viewMode == VIEW_REGIONS) {
				for (int var4 = 0; var4 < var1; var4++) {
					int var5 = this.debug.labels[var4];
					// distinct hue per region so touching regions read as separate
					var2[var4] = var5 == 0 ? 0x101010 : Color.HSBtoRGB((var5 * 0.137F) % 1.0F, 0.85F, 1.0F);
				}
			} else {
				for (int var6 = 0; var6 < var1; var6++) {
					int var7 = this.frame.pixels[var6];
					if (this.debug.mask[var6] != 0) {
						var2[var6] = 0xFF0000 | (var7 & 0x00FFFF);
					} else {
						// dim the misses so the matches stand out in context
						var2[var6] = ((var7 >> 1) & 0x7F7F7F);
					}
				}
			}
			this.image.setRGB(0, 0, this.frame.width, this.frame.height, var2, 0, this.frame.width);
		}

		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			if (this.image == null) {
				g.setColor(Color.WHITE);
				g.drawString("Waiting for a frame - enable vision and log in", 12, 24);
				return;
			}
			Graphics2D var2 = (Graphics2D) g;
			var2.drawImage(this.image, 0, 0, null);

			Rectangle var8 = this.dragging != null ? this.dragging : this.selection;
			if (var8 != null) {
				var2.setColor(Color.WHITE);
				var2.setStroke(new BasicStroke(1.0F, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER,
					10.0F, new float[] { 4.0F, 4.0F }, 0.0F));
				var2.drawRect(var8.x, var8.y, var8.width - 1, var8.height - 1);
				var2.setStroke(new BasicStroke(1.0F));
			}

			if (this.detections == null || this.viewMode == VIEW_MASK) {
				return;
			}
			var2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			var2.setFont(var2.getFont().deriveFont(Font.BOLD, 10.0F));
			for (int var3 = 0; var3 < this.detections.size(); var3++) {
				Detection var4 = this.detections.get(var3);
				Color var5 = colorFor(var4.ruleName);
				var2.setColor(var5);
				var2.drawRect(var4.x, var4.y, var4.width - 1, var4.height - 1);
				var2.drawLine(var4.centroidX - 3, var4.centroidY, var4.centroidX + 3, var4.centroidY);
				var2.drawLine(var4.centroidX, var4.centroidY - 3, var4.centroidX, var4.centroidY + 3);
				String var6 = var4.ruleName + " " + var4.area;
				int var7 = var4.y > 12 ? var4.y - 2 : var4.y + var4.height + 10;
				var2.setColor(Color.BLACK);
				var2.drawString(var6, var4.x + 1, var7 + 1);
				var2.setColor(var5);
				var2.drawString(var6, var4.x, var7);
			}
		}

		private static Color colorFor(String ruleName) {
			List<ColorRule> var1 = Vision.getInstance().getRules();
			for (int var2 = 0; var2 < var1.size(); var2++) {
				if (var1.get(var2).name.equals(ruleName)) {
					return new Color(var1.get(var2).overlayRgb);
				}
			}
			return Color.GREEN;
		}
	}
}
