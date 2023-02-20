package me.kumo.ui.gallery;

import me.kumo.io.ImageUtils;
import me.kumo.io.pixiv.Pixiv;
import me.kumo.ui.utils.Curves;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.concurrent.CancellationException;

import static me.kumo.io.NetIO.fetchIllustration;

public class GalleryImage extends JComponent {
    public static final int GRID_SIZE = 200;
    public static final BasicStroke ROUND_STROKE = new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

    private static final File CACHE = new File("cache");
    private static final double DECAY_RATE = 0.1;
    private static final double DECAY_RATE_I = 1 - DECAY_RATE;
    private static final double PARALLAX_AMOUNT = 20;

    static {
        if (!CACHE.exists()) CACHE.mkdir();
    }

    private SwingWorker<BufferedImage, String> worker;
    private BufferedImage scaledCopy;
    private String file;
    private SrcType type;
    private File cacheFile;
    private boolean shown = false;
    private SwingWorker<Boolean, File> cacheWorker;
    private double ratio;
    private Dimension size;
    private double px = 0, py = 0;
    private double tpx = 0, tpy = 0;
    private double rpx = 0, rpy = 0;
    private boolean hover;

    public GalleryImage() {
        setPreferredSize(new Dimension(GRID_SIZE, GRID_SIZE));
    }

    public boolean downloaded() {
        return found() && type == SrcType.LOCAL;
    }

    public boolean found() {
        return file != null;
    }

    public boolean loaded() {
        return scaledCopy != null;
    }

    public void setFile(String file) {
        if (!Objects.equals(this.file, this.file = file)) {
            this.scaledCopy = null;
            String name;
            try {
                new URL(file);
                type = SrcType.ONLINE;
                name = file.substring(file.lastIndexOf('/') + 1);
            } catch (MalformedURLException e) {
                type = SrcType.LOCAL;
                name = new File(file).getName();
            }
            this.cacheFile = new File(CACHE, name);
            this.cacheFile.deleteOnExit();
            loadImage();
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        if (!shown || !isDisplayable()) return;
        Graphics2D g2d = (Graphics2D) g;
//        g2d.setRenderingHints(ImageUtils.RENDERING_HINTS);
        if (this.scaledCopy == null) {
            double r = 40;
            double clock = (System.currentTimeMillis() % 1000) / 1000d;
            g2d.setStroke(ROUND_STROKE);
            g2d.draw(new Arc2D.Double(getWidth() / 2d - r, getHeight() / 2d - r, r * 2, r * 2, clock * 360, clock * 720 - 360, Arc2D.OPEN));
        } else {
            double enlarge = Curves.BezierBlend(Math.min(1, (System.currentTimeMillis() - hoverChangeTime) / 200d));
            if (!hover) enlarge = 1 - enlarge;
            ratio = Math.max((getWidth() + enlarge * PARALLAX_AMOUNT * 2) / scaledCopy.getWidth(), (getHeight() + enlarge * PARALLAX_AMOUNT * 2) / scaledCopy.getHeight());
            int w = (int) (scaledCopy.getWidth() * ratio);
            int h = (int) (scaledCopy.getHeight() * ratio);
            g2d.drawImage(scaledCopy,
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
            revalidateThumbnail();
        }
    }

    public void setShown(boolean shown) {
        if (shown == this.shown) return;
        this.shown = shown;
        if (shown) {
            loadImage();
        } else {
            if (worker != null && !worker.isDone() && !worker.isCancelled()) {
                worker.cancel(true);
                worker = null;
            }
            unload();
        }
    }

    private void loadImage() {
        if (this.file == null || !shown || scaledCopy != null) return;
        if (worker != null) worker.cancel(true);
        worker = new SwingWorker<>() {
            @Override
            protected BufferedImage doInBackground() throws Exception {
                if (cacheFile.exists()) try {
                    if (Files.size(cacheFile.toPath()) != 0) return ImageIO.read(new FileInputStream(cacheFile));
                } catch (Exception ignored) {
                }
                BufferedImage read;
                if (type == SrcType.LOCAL) {
                    read = ImageIO.read(Files.newInputStream(Path.of(file)));
                    if (read == null) return null;
                } else {
                    read = fetchIllustration(Pixiv.getInstance(), file);
                }
                BufferedImage image = ImageUtils.centerFill(read, getWidth(), getHeight());
                read.flush();
                return image;
            }

            @Override
            protected void done() {
                try {
                    scaledCopy = get();
                } catch (CancellationException ignored) {
                } catch (Exception ignored) {
                    ignored.printStackTrace();
                    System.out.println(file);
                    worker = null;
                }
            }
        };
        worker.execute();
    }

    public void unload() {
        if (!loaded()) return;
        if (cacheWorker != null && !cacheWorker.isDone()) return;
        cacheWorker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() throws IOException {
                FileOutputStream output = new FileOutputStream(cacheFile);
                FileLock lock = output.getChannel().tryLock();
                if (lock == null) return false;
                boolean success = ImageIO.write(scaledCopy, "png", output);
                output.flush();
                output.close();
                lock.close();
                scaledCopy.flush();
                scaledCopy = null;
                return success;
            }
        };
        cacheWorker.execute();
    }

    public void revalidateThumbnail() {
        if (!loaded()) return;
        if (Objects.equals(size, size = getSize())) return;
        if (getHeight() > GRID_SIZE && (ratio > 1.5 || (1 / ratio) > 1.5)) {
            System.out.println("Recalculating cache");
            scaledCopy = null;
            cacheFile.delete();
            loadImage();
        }
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

    private long hoverChangeTime;

    public void setHover(boolean hover) {
        if (this.hover != (this.hover = hover)) hoverChangeTime = System.currentTimeMillis();
    }

    public boolean isHover() {
        return hover;
    }

    public enum SrcType {
        ONLINE, LOCAL
    }
}
