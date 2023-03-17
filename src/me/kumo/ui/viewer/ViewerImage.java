package me.kumo.ui.viewer;

import me.kumo.io.ImageUtils;
import me.kumo.io.LocalGallery;
import me.kumo.io.ProgressTracker;
import me.kumo.ui.utils.Formatters;
import me.kumo.ui.utils.RemoteImage;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;

public class ViewerImage extends RemoteImage {
    private final BufferedImage preload;

    private long progress;
    private long total;
    private double eta, speed;

    public ViewerImage(String url, BufferedImage thumb) {
        String name = url.substring(url.lastIndexOf('/') + 1);
        File file = LocalGallery.getImage(name);
        setUrl(url);
        setLocalFile(file);
        this.preload = thumb;
        revalidateThumbnail();
    }

    public void drawImage(Graphics2D g, BufferedImage image) {
        g.setRenderingHints(ImageUtils.RENDERING_HINTS);
        double ratio = Math.min(getWidth() * 1d / image.getWidth(), getHeight() * 1d / image.getHeight());
        int w = (int) (image.getWidth() * ratio);
        int h = (int) (image.getHeight() * ratio);
        g.drawImage(image,
                -(w - getWidth()) >> 1,
                -(h - getHeight()) >> 1,
                w, h, null);
    }

    public void drawProgress(Graphics2D g) {
        if (preload != null) drawImage(g, preload);
        double v = Math.sqrt(this.progress * 1.0 / this.total);
        g.setColor(Color.BLACK);
        g.setXORMode(Color.WHITE);
        g.draw(new Rectangle2D.Double((1 - v) * getWidth() / 2, (1 - v) * getHeight() / 2, v * getWidth(), v * getHeight()));
        g.drawString(Formatters.formatBytes((long) speed) + "/s", getWidth() / 2, getHeight() / 2);
        g.drawString("%.3fs left".formatted(eta), getWidth() / 2, getHeight() / 2 + 20);
        ImageUtils.spinner(g, getWidth() / 2d - getSpinnerSize(), getHeight() / 2d - getSpinnerSize(), getSpinnerSize());
    }

    @Override
    public boolean shouldSaveToLocal() {
        return true;
    }

    @Override
    public boolean shouldResize() {
        return false;
    }

    @Override
    public void update(ProgressTracker tracker) {
        this.progress = tracker.getProgress();
        this.total = tracker.getTotal();
        this.eta = tracker.getEta();
        this.speed = tracker.getSpeed();
    }
}
