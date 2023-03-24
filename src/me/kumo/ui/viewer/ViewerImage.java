package me.kumo.ui.viewer;

import com.github.hanshsieh.pixivj.model.Illustration;
import me.kumo.components.image.RemoteImage;
import me.kumo.components.utils.Formatters;
import me.kumo.io.ImageUtils;
import me.kumo.io.LocalGallery;
import me.kumo.io.ProgressTracker;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;

public class ViewerImage extends RemoteImage implements MouseMotionListener, MouseListener, MouseWheelListener {
    private BufferedImage preload;
    private Color[] colors;

    private long progress;
    private long total;
    private double eta, speed;
    private final Point2D.Double offset = new Point2D.Double(0, 0);
    private double scale = 1;
    private MouseEvent lastDragE;

    public ViewerImage(Illustration illustration, int page, BufferedImage thumb) {
        String url = illustration.getPageCount() == 1 ? illustration.getMetaSinglePage().getOriginalImageUrl() : LocalGallery.getBestQuality(illustration.getMetaPages().get(page).getImageUrls());
        String name = url.substring(url.lastIndexOf('/') + 1);
        File file = LocalGallery.getImage(name);
        setUrl(url);
        setLocalFile(file);
        this.preload = thumb;
        revalidateThumbnail();
        addPropertyChangeListener(THUMBNAIL, evt -> {
            if (evt.getNewValue() != null && evt.getNewValue() instanceof BufferedImage image) {
                LocalGallery.processBigImage(illustration.getId(), page, image);
                colors = LocalGallery.getBigPalette(illustration.getId(), page);
            }
        });

        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);
    }

    public void drawImage(Graphics2D g, BufferedImage image) {
        g.setRenderingHints(ImageUtils.RENDERING_HINTS_FAST);
        AffineTransform transform = g.getTransform();
        g.translate(offset.x, offset.y);
        g.scale(scale, scale);

        double ratio = Math.min(getWidth() * 1d / image.getWidth(), getHeight() * 1d / image.getHeight());
        int w = (int) (image.getWidth() * ratio);
        int h = (int) (image.getHeight() * ratio);
        g.drawImage(image,
                -(w - getWidth()) / 2,
                -(h - getHeight()) / 2,
                w, h, null);
        g.setTransform(transform);

        int s = 32;
        int p = 2;
        if (colors != null) {
            for (int i = 0; i < colors.length; i++) {
                g.setColor(colors[colors.length - i - 1]);
                g.fillRoundRect((w + getWidth()) / 2 - s + p, (h + getHeight()) / 2 - s - s * i + p, s - p - p, s - p - p, p * 4, p * 4);
            }
            g.setColor(Color.WHITE);
//            g.setStroke(new BasicStroke(2));
            for (int i = 0; i < colors.length; i++) {
                g.drawRoundRect((w + getWidth()) / 2 - s + p, (h + getHeight()) / 2 - s - s * i + p, s - p - p, s - p - p, p * 4, p * 4);
            }
        }

        if (scale != 1) {
            Composite composite = g.getComposite();
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
            double shrink = Math.sqrt(40000d / (w * h));
            g.translate(20, getHeight() - 20 - shrink * h);
            g.setColor(new Color(0, 0, 0, 0.3f));
            g.drawImage(preload, 0, 0, (int) (shrink * w), (int) (shrink * h), null);
            Area shape = new Area(new Rectangle2D.Double(
                    shrink * (-offset.x / scale - (getWidth() - w) / 2d),
                    shrink * (-offset.y / scale - (getHeight() - h) / 2d),
                    shrink * getWidth() / scale,
                    shrink * getHeight() / scale));
            shape.exclusiveOr(new Area(new Rectangle2D.Double(0, 0, shrink * w, shrink * h)));
            g.setComposite(composite);
            g.fill(shape);
            g.setTransform(transform);
        }
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
    public boolean shouldMakeThumbnail() {
        return false;
    }

    @Override
    public void update(ProgressTracker tracker) {
        this.progress = tracker.getProgress();
        this.total = tracker.getTotal();
        this.eta = tracker.getEta();
        this.speed = tracker.getSpeed();
    }

    public void dispose() {
        unloadImage();
        preload = null;
        colors = null;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (lastDragE != null) {
            offset.x += e.getX() - lastDragE.getX();
            offset.y += e.getY() - lastDragE.getY();
            clampEdges();
        }
        lastDragE = e;
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
            offset.setLocation(0, 0);
            scale = 1;
            repaint();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        lastDragE = e;
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        Point2D.Double c = unproject(new Point2D.Double(e.getX(), e.getY()));
        scale *= Math.pow(1.1, -e.getPreciseWheelRotation());
        c = project(c);
        offset.setLocation(offset.x - (c.x - e.getX()), offset.y - (c.y - e.getY()));
        clampEdges();
    }

    public Point2D.Double project(Point2D.Double point) {
        return new Point2D.Double(point.x * scale + offset.x, point.y * scale + offset.y);
    }

    public Point2D.Double unproject(Point2D.Double point) {
        return new Point2D.Double((point.x - offset.x) / scale, (point.y - offset.y) / scale);
    }

    public void clampEdges() {
        this.scale = Math.max(1, scale);
        this.offset.x = Math.max(-(scale - 1) * getWidth(), Math.min(0, offset.x));
        this.offset.y = Math.max(-(scale - 1) * getHeight(), Math.min(0, offset.y));
    }
}
