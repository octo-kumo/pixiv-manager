package me.kumo.ui.gallery;

import me.kumo.io.ProgressTracker;
import me.kumo.io.img.GaussianFilter;
import me.kumo.ui.utils.Curves;
import me.kumo.ui.utils.RemoteImage;
import org.imgscalr.Scalr;

import java.awt.*;
import java.awt.image.BufferedImage;

public class GalleryImage extends RemoteImage {
    public static final int GRID_SIZE = 200;
    private static final double DECAY_RATE = 0.1;
    private static final double DECAY_RATE_I = 1 - DECAY_RATE;
    private static final double PARALLAX_AMOUNT = 20;

    private boolean shown = false;
    private boolean blurred = false;
    private double px = 0, py = 0;
    private double tpx = 0, tpy = 0;
    private double rpx = 0, rpy = 0;

    private boolean hover, pressed;
    private long hoverChangeTime, pressedChangeTime;

    public GalleryImage() {
        setPreferredSize(new Dimension(GRID_SIZE, GRID_SIZE));
    }

    @Override
    public void drawImage(Graphics2D g, BufferedImage image) {
        double enlarge = Curves.BezierBlend(Math.min(1, (System.currentTimeMillis() - hoverChangeTime) / 200d));
        if (!hover) enlarge = 1 - enlarge;
        double pEnlarge = Curves.BezierBlend(Math.min(1, (System.currentTimeMillis() - pressedChangeTime) / 100d));
        if (!pressed) pEnlarge = 1 - pEnlarge;
        enlarge += pEnlarge * 0.2;

        double ratio = Math.max((getWidth() + enlarge * PARALLAX_AMOUNT * 2) / image.getWidth(), (getHeight() + enlarge * PARALLAX_AMOUNT * 2) / image.getHeight());
        int w = (int) (image.getWidth() * ratio);
        int h = (int) (image.getHeight() * ratio);
        g.drawImage(image,
                (int) (-(w - getWidth()) / 2 + px * (w - getWidth()) / 2),
                (int) (-(h - getHeight()) / 2 + py * (h - getHeight()) / 2),
                w, h, null);
        if (!hover) {
            tpx = rpx;
            tpy = rpy;
        }
        if (Math.abs(px - tpx) > 0.005) px = px * DECAY_RATE_I + tpx * DECAY_RATE;
        else px = tpx;
        if (Math.abs(py - tpy) > 0.005) py = py * DECAY_RATE_I + tpy * DECAY_RATE;
        else py = tpy;
    }

    @Override
    public boolean shouldSaveToLocal() {
        return false;
    }

    @Override
    public boolean shouldMakeThumbnail() {
        return true;
    }

    @Override
    public void update(ProgressTracker tracker) {

    }

    public void setShown(boolean shown) {
        if (shown == this.shown) return;
        this.shown = shown;
        if (shown) loadImage();
        else unloadImage();
    }

    public void setParallax(double x, double y) {
        this.tpx = x;
        this.tpy = y;
    }

    public void setRestParallax(double x, double y) {
        if (!hover && this.rpx == this.px && this.rpy == this.py) {
            this.px = x;
            this.py = y;
        }
        this.rpx = x;
        this.rpy = y;
    }

    public void setHover(boolean hover) {
        if (this.hover != (this.hover = hover)) hoverChangeTime = System.currentTimeMillis();
    }

    public void setPressed(boolean pressed) {
        if (this.pressed != (this.pressed = pressed)) pressedChangeTime = System.currentTimeMillis();
    }

    public boolean isBlurred() {
        return blurred;
    }

    public void setBlurred(boolean blurred) {
        this.blurred = blurred;
    }

    protected BufferedImage makeThumbnail(BufferedImage image) {
        if (blurred) return blur(super.makeThumbnail(image));
        else return super.makeThumbnail(image);
    }

    private static BufferedImage blur(BufferedImage image) {
        BufferedImage apply = Scalr.apply(image, new GaussianFilter(Math.max(image.getWidth(), image.getHeight()) / 20f));
        image.flush();
        return apply;
    }
}
