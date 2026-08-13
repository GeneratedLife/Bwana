package jagex2.graphics;

import deob.ObfuscatedName;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.ImageConsumer;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.util.Hashtable;

@ObfuscatedName("qb")
public class PixMap implements ImageProducer, ImageObserver {

	@ObfuscatedName("qb.a")
	public int field671 = 299;

	@ObfuscatedName("qb.b")
	public int[] pixels;

	@ObfuscatedName("qb.c")
	public int width;

	@ObfuscatedName("qb.d")
	public int height;

	@ObfuscatedName("qb.e")
	public ColorModel colorModel;

	@ObfuscatedName("qb.f")
	public ImageConsumer imageConsumer;

	@ObfuscatedName("qb.g")
	public Image image;

	public PixMap(Component arg0, int arg1, int arg2, int arg3) {
		this.width = arg1;
		this.height = arg3;
		this.pixels = new int[arg1 * arg3];
		this.colorModel = new DirectColorModel(32, 16711680, 65280, 255);
		this.image = arg0.createImage(this);
		this.setPixels();
		arg0.prepareImage(this.image, this);
		this.setPixels();
		arg0.prepareImage(this.image, this);
		this.setPixels();
		int var5 = 96 / arg2;
		arg0.prepareImage(this.image, this);
		this.bind((byte) 62);
	}

	@ObfuscatedName("qb.a(B)V")
	public void bind(byte arg0) {
		if (arg0 != 62) {
			this.field671 = -283;
		}
		Pix2D.bind(this.width, this.pixels, -657, this.height);
	}

	@ObfuscatedName("qb.a(ILjava/awt/Graphics;II)V")
	public void draw(int arg0, Graphics arg1, int arg2, int arg3) {
		if (arg3 == 5193) {
			this.setPixels();
			arg1.drawImage(this.image, arg2, arg0, this);
		}
	}

	public synchronized void addConsumer(ImageConsumer arg0) {
		this.imageConsumer = arg0;
		arg0.setDimensions(this.width, this.height);
		arg0.setProperties(null);
		arg0.setColorModel(this.colorModel);
		arg0.setHints(14);
	}

	public synchronized boolean isConsumer(ImageConsumer arg0) {
		return this.imageConsumer == arg0;
	}

	public synchronized void removeConsumer(ImageConsumer arg0) {
		if (this.imageConsumer == arg0) {
			this.imageConsumer = null;
		}
	}

	public void startProduction(ImageConsumer arg0) {
		this.addConsumer(arg0);
	}

	public void requestTopDownLeftRightResend(ImageConsumer arg0) {
		System.out.println("TDLR");
	}

	@ObfuscatedName("qb.a()V")
	public synchronized void setPixels() {
		if (this.imageConsumer != null) {
			this.imageConsumer.setPixels(0, 0, this.width, this.height, this.colorModel, this.pixels, 0, this.width);
			this.imageConsumer.imageComplete(2);
		}
	}

	public boolean imageUpdate(Image arg0, int arg1, int arg2, int arg3, int arg4, int arg5) {
		return true;
	}
}
