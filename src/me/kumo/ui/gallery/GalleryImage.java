package me.kumo.ui.gallery;

import me.kumo.io.ImageUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.util.Objects;
import java.util.concurrent.CancellationException;

public class GalleryImage extends JComponent {
    public static final int GRID_SIZE = 200;
    public static final BasicStroke ROUND_STROKE = new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

    private static final File CACHE = new File("cache");

    static {
        if (!CACHE.exists()) CACHE.mkdir();
    }

    private SwingWorker<BufferedImage, String> worker;
    private BufferedImage scaledCopy;
    private File file;
    private File cacheFile;
    private boolean shown = false;
    private SwingWorker<Boolean, File> cacheWorker;
    private double ratio;
    private Dimension size;

    public GalleryImage() {
        setPreferredSize(new Dimension(GRID_SIZE, GRID_SIZE));
    }

    public boolean found() {
        return file != null;
    }

    public boolean loaded() {
        return scaledCopy != null;
    }

    public void setFile(File file) {
        if (!Objects.equals(this.file, this.file = file)) {
            this.scaledCopy = null;
            try {
                this.cacheFile = File.createTempFile(file.getName(), null, CACHE);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            loadImage();
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
//        g2d.setRenderingHints(ImageUtils.RENDERING_HINTS);
        if (this.scaledCopy == null || !shown) {
            double r = 40;
            double clock = (System.currentTimeMillis() % 1000) / 1000d;
            g2d.setStroke(ROUND_STROKE);
            g2d.draw(new Arc2D.Double(getWidth() / 2d - r, getHeight() / 2d - r, r * 2, r * 2, clock * 360, clock * 720 - 360, Arc2D.OPEN));
        } else {
            ratio = Math.max(1. * getWidth() / scaledCopy.getWidth(), 1. * getHeight() / scaledCopy.getHeight());
            int w = (int) (scaledCopy.getWidth() * ratio);
            int h = (int) (scaledCopy.getHeight() * ratio);
            g2d.drawImage(scaledCopy, -(w - getWidth()) / 2, -(h - getHeight()) / 2, w, h, null);
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
        final File FILE_PATH = file;
        worker = new SwingWorker<>() {
            @Override
            protected BufferedImage doInBackground() throws Exception {
                if (cacheFile.exists()) try {
                    if (Files.size(cacheFile.toPath()) != 0) return ImageIO.read(new FileInputStream(cacheFile));
                } catch (Exception ignored) {
                }
                BufferedImage read = ImageIO.read(new FileInputStream(FILE_PATH));
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
                    System.out.println(ignored);
                    System.out.println(FILE_PATH);
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
                boolean success = ImageIO.write(scaledCopy, "jpg", output);
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
}
