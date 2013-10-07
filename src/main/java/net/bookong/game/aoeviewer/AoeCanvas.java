package net.bookong.game.aoeviewer;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import net.bookong.tools.archive.slp.Frame;

/**
 * @author jiangxu
 *
 */
public class AoeCanvas extends Canvas {
	private static final long serialVersionUID = 1L;

	private static AoeCanvas instance = new AoeCanvas();

	public static AoeCanvas getInstance() {
		return instance;
	}

	public int currPlayerId;
	public boolean ifShowOutlines;
	private int currIdx;
	private Frame frames[];
	private BufferedImage bufferedImages[];
	private Integer[] palette;
	private Rectangle lastPaintRect;

	public AoeCanvas() {
		currPlayerId = 0;
		ifShowOutlines = true;
		currIdx = 0;
		frames = new Frame[0];
	}

	public void update(Graphics g) {
		paint(g);
	}

	public void paint(Graphics g) {
		g.setColor(Color.GRAY);
		if (lastPaintRect == null) {
			g.fillRect(0, 0, getWidth(), getHeight());
			lastPaintRect = new Rectangle();
		} else {
			g.fillRect(lastPaintRect.x, lastPaintRect.y, lastPaintRect.width, lastPaintRect.height);
		}
		
		
		if (frames.length > 0) {
			Frame f = frames[currIdx];
			BufferedImage img = bufferedImages[currIdx];
			
			lastPaintRect.x = getWidth() / 2 - f.centerX;
			lastPaintRect.y = getHeight() / 2 - f.centerY;
			lastPaintRect.width = f.width;
			lastPaintRect.height = f.height;
			
			g.drawImage(img, lastPaintRect.x, lastPaintRect.y, lastPaintRect.width, lastPaintRect.height, null);
			currIdx = ++currIdx % frames.length;
		}
	}
	
	public void reloadImages() {
		reloadImages(frames, palette);
	}

	public void reloadImages(Frame frames[], Integer[] palette) {
		Daemon.getInstance().isStopping = true;
		this.palette = palette;
		this.frames = frames;
		bufferedImages = new BufferedImage[frames.length];
		currIdx = 0;
		
		lastPaintRect = null;
		
		for (int i = 0; i < frames.length; i++) {
			Frame f = frames[i];
			BufferedImage img = new BufferedImage(f.width, f.height, 2);
			bufferedImages[i] = img;
			for (int j = 0; j < f.height; j++) {
				for (int k = 0; k < f.width; k++) {
					int idx = j * f.width + k;
					int feture = 255 & f.featureBuff.get(idx);
					int img256 = 255 & f.img256Buff.get(idx);
					switch (feture) {
					case 0x00:
						img.setRGB(k, j, 0);
						break;

					case 0xFF:
						img.setRGB(k, j, palette[img256].intValue());
						break;

					case 0xEE:
						if (ifShowOutlines){
							img.setRGB(k, j, -256);
						}
						break;

					default:
						img.setRGB(k, j, palette[currPlayerId * 16 + feture].intValue());
						break;
					}
				}

			}

		}
		Daemon.getInstance().isStopping = false;
	}
}
