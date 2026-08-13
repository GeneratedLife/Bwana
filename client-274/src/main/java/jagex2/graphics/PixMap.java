package jagex2.graphics;

import deob.ObfuscatedName;

import java.awt.*;
import java.awt.image.*;

@ObfuscatedName("qb")
public final class PixMap implements ImageProducer, ImageObserver {

	@ObfuscatedName("qb.c")
	public final int[] data;

	@ObfuscatedName("qb.d")
	public final int width;

	@ObfuscatedName("qb.e")
	public final int height;

	@ObfuscatedName("qb.f")
	public final ColorModel colorModel;

	@ObfuscatedName("qb.g")
	public ImageConsumer imageConsumer;

	@ObfuscatedName("qb.h")
	public final Image image;

	public PixMap(int arg0, int arg2, Component arg3) {
		width = arg2;
		height = arg0;
		data = new int[arg2 * arg0];
		colorModel = new DirectColorModel(32, 16711680, 65280, 255);
		image = arg3.createImage(this);
		consumerSetPixels();
		arg3.prepareImage(image, this);
		consumerSetPixels();
		arg3.prepareImage(image, this);
		consumerSetPixels();
		arg3.prepareImage(image, this);
		setPixels();
	}

	@ObfuscatedName("qb.a(Z)V")
	public void setPixels() {
		Pix2D.setPixels(width, height, data);
	}

	@ObfuscatedName("qb.a(ILjava/awt/Graphics;BI)V")
	public void draw(int arg0, Graphics arg1, int arg3) {
		consumerSetPixels();
		arg1.drawImage(image, arg0, arg3, this);
	}

	@Override
	public synchronized void addConsumer(ImageConsumer arg0) {
		imageConsumer = arg0;
		arg0.setDimensions(width, height);
		arg0.setProperties(null);
		arg0.setColorModel(colorModel);
		arg0.setHints(14);
	}

	@Override
	public synchronized boolean isConsumer(ImageConsumer arg0) {
		return imageConsumer == arg0;
	}

	@Override
	public synchronized void removeConsumer(ImageConsumer arg0) {
		if (imageConsumer == arg0) {
			imageConsumer = null;
		}
	}

	@Override
	public void startProduction(ImageConsumer arg0) {
		addConsumer(arg0);
	}

	@Override
	public void requestTopDownLeftRightResend(ImageConsumer arg0) {
		System.out.println("TDLR");
	}

	@ObfuscatedName("qb.a()V")
	public synchronized void consumerSetPixels() {
		if (imageConsumer != null) {
			imageConsumer.setPixels(0, 0, width, height, colorModel, data, 0, width);
			imageConsumer.imageComplete(2);
		}
	}

	@Override
	public boolean imageUpdate(Image arg0, int arg1, int arg2, int arg3, int arg4, int arg5) {
		return true;
	}
}
