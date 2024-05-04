package me.kumo.ui.viewer;

import com.github.hanshsieh.pixivj.model.Illustration;
import me.kumo.components.image.RemoteImage;
import me.kumo.components.utils.Formatters;
import me.kumo.io.LocalGallery;
import me.kumo.io.NetIO;
import me.kumo.io.ProgressTracker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;

public class ViewerImage extends RemoteImage implements MouseMotionListener, MouseListener, MouseWheelListener {
    private final Point2D.Double offset = new Point2D.Double(0, 0);
    private long size = -1;

    private double scale = 1;

    private BufferedImage preload;
    private Color[] colors;

    private long progress;
    private long total;
    private double eta, speed;
    private MouseEvent lastDragE;
    private boolean shown;
    private IllustrationViewer illust;

    public ViewerImage(Illustration illustration, int page, BufferedImage thumb) {
        String url = illustration.getPageCount() == 1 ?
                illustration.getMetaSinglePage().getOriginalImageUrl() :
                LocalGallery.getBestQuality(illustration.getMetaPages().get(page).getImageUrls());
        if (url.equals("https://s.pximg.net/common/images/limit_unknown_360.png"))
            url = String.valueOf(illustration.getId());
        String name = url.substring(url.lastIndexOf('/') + 1);
        File file = LocalGallery.getImage(name);
        setUrl(url);
        setLocalFile(file == null ? this.thumbnailFile : file);
        this.preload = thumb;
        addPropertyChangeListener(THUMBNAIL, evt -> {
            if (file != null && file.exists()) size = file.length();
            if (evt.getNewValue() != null && evt.getNewValue() instanceof BufferedImage image) {
                new SwingWorker<>() {
                    @Override
                    protected Object doInBackground() {
                        LocalGallery.processBigImage(illustration.getId(), page, image);
                        colors = LocalGallery.getBigPalette(illustration.getId(), page);
                        return colors;
                    }
                }.execute();
            }
        });

        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);
        Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
        size.setSize(illustration.getWidth() * size.getHeight() * 0.7 / illustration.getHeight(),
                size.getHeight() * 0.7);
        setPreferredSize(size);
    }

    public void drawImage(Graphics2D g, BufferedImage image) {
        AffineTransform transform = g.getTransform();
        g.translate(offset.x, offset.y);
        g.scale(getScale(), getScale());

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
        if (colors != null && scale == 1) {
            for (int i = 0; i < colors.length; i++) {
                g.setColor(colors[colors.length - i - 1]);
                g.fillRoundRect((w + getWidth()) / 2 - s + p, (h + getHeight()) / 2 - s - s * i + p, s - p - p, s - p - p, p * 4, p * 4);
            }
            g.setColor(Color.WHITE);
            for (int i = 0; i < colors.length; i++) {
                g.drawRoundRect((w + getWidth()) / 2 - s + p, (h + getHeight()) / 2 - s - s * i + p, s - p - p, s - p - p, p * 4, p * 4);
            }
        }

        if (scale == 1 && size != -1) {
            g.setColor(Color.BLACK);
            Rectangle2D stringBounds = g.getFontMetrics().getStringBounds(Formatters.formatBytes(size), g);
            g.fillRect((int) (0 + stringBounds.getX()), 0, (int) stringBounds.getWidth(), (int) stringBounds.getHeight());

            g.setColor(Color.WHITE);
            g.drawString(Formatters.formatBytes(size), 0f, (float) -stringBounds.getY());
        }

        if (getScale() != 1) {
            Composite composite = g.getComposite();
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
            double shrink = Math.sqrt(40000d / (w * h));
            g.translate(20, getHeight() - 20 - shrink * h);
            g.setColor(new Color(0, 0, 0, 0.3f));
            g.drawImage(preload, 0, 0, (int) (shrink * w), (int) (shrink * h), null);
            Area shape = new Area(new Rectangle2D.Double(
                    shrink * (-offset.x / getScale() - (getWidth() - w) / 2d),
                    shrink * (-offset.y / getScale() - (getHeight() - h) / 2d),
                    shrink * getWidth() / getScale(),
                    shrink * getHeight() / getScale()));
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
        super.drawProgress(g);
    }

    @Override
    protected boolean shouldSaveToLocal() {
        return true;
    }

    @Override
    protected boolean shouldMakeThumbnail() {
        return false;
    }

    @Override
    protected boolean shouldHaveProgress() {
        return true;
    }

    @Override
    public void onProgress(ProgressTracker tracker) {
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
            if (scale != 1) {
                offset.setLocation(0, 0);
                setScale(1);
                repaint();
            } else {
                NetIO.openFile(this.getLocalFile());
            }
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
        setScale(getScale() * Math.pow(1.1, -e.getPreciseWheelRotation()));
        c = project(c);
        offset.setLocation(offset.x - (c.x - e.getX()), offset.y - (c.y - e.getY()));
        clampEdges();
    }

    public Point2D.Double project(Point2D.Double point) {
        return new Point2D.Double(point.x * getScale() + offset.x, point.y * getScale() + offset.y);
    }

    public Point2D.Double unproject(Point2D.Double point) {
        return new Point2D.Double((point.x - offset.x) / getScale(), (point.y - offset.y) / getScale());
    }

    public void clampEdges() {
        this.setScale(Math.max(1, getScale()));
        this.offset.x = Math.max(-(getScale() - 1) * getWidth(), Math.min(0, offset.x));
        this.offset.y = Math.max(-(getScale() - 1) * getHeight(), Math.min(0, offset.y));
    }

    public void setShown(boolean shown) {
        if (this.shown == (this.shown = shown)) return;
        if (!shown) unloadImage();
        else loadImage();
    }

    public void setScale(double scale) {
        this.scale = scale;
        if (this.illust != null) this.illust.setFocus(scale > 1);
    }

    public double getScale() {
        return scale;
    }

    public void setIllust(IllustrationViewer illust) {
        this.illust = illust;
    }
}
