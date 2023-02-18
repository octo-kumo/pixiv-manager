package me.kumo.ui.viewer;

import me.kumo.io.ImageUtils;
import me.kumo.io.LocalGallery;
import me.kumo.io.pixiv.Pixiv;
import me.kumo.ui.gallery.GalleryImage;

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

public class ViewerImage extends JComponent {
    public static final BasicStroke ROUND_STROKE = new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    private static final File CACHE = new File("cache.big");

    static {
        if (!CACHE.exists()) CACHE.mkdir();
    }

    private SwingWorker<BufferedImage, String> worker;
    private BufferedImage image;
    private String file;
    private GalleryImage.SrcType type;
    private File cacheFile;
    private SwingWorker<Boolean, File> cacheWorker;

    public ViewerImage(String url) {
        String name = url.substring(url.lastIndexOf('/') + 1);
        File file = LocalGallery.getImage(name);
        if (file != null) setFile(file.getAbsolutePath());
        else setFile(url);
    }

    public boolean downloaded() {
        return found() && type == GalleryImage.SrcType.LOCAL;
    }

    public boolean found() {
        return file != null;
    }

    public boolean loaded() {
        return image != null;
    }

    public void setFile(String file) {
        if (!Objects.equals(this.file, this.file = file)) {
            this.image = null;
            String name;
            try {
                new URL(file);
                type = GalleryImage.SrcType.ONLINE;
                name = file.substring(file.lastIndexOf('/') + 1);
            } catch (MalformedURLException e) {
                type = GalleryImage.SrcType.LOCAL;
                name = new File(file).getName();
            }
            this.cacheFile = new File(CACHE, name);
            this.cacheFile.deleteOnExit();
            loadImage();
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHints(ImageUtils.RENDERING_HINTS);
        if (this.image == null) {
            double r = 40;
            double clock = (System.currentTimeMillis() % 1000) / 1000d;
            g2d.setStroke(ROUND_STROKE);
            g2d.draw(new Arc2D.Double(getWidth() / 2d - r, getHeight() / 2d - r, r * 2, r * 2, clock * 360, clock * 720 - 360, Arc2D.OPEN));
        } else {
            double ratio = Math.min(1. * getWidth() / image.getWidth(), 1. * getHeight() / image.getHeight());
            int w = (int) (image.getWidth() * ratio);
            int h = (int) (image.getHeight() * ratio);
            g2d.drawImage(image, -(w - getWidth()) / 2, -(h - getHeight()) / 2, w, h, null);
        }
    }

    private void loadImage() {
        if (this.file == null || image != null) return;
        if (worker != null) worker.cancel(true);
        worker = new SwingWorker<>() {
            @Override
            protected BufferedImage doInBackground() throws Exception {
                if (cacheFile.exists()) try {
                    if (Files.size(cacheFile.toPath()) != 0) return ImageIO.read(new FileInputStream(cacheFile));
                } catch (Exception ignored) {
                }
                return type == GalleryImage.SrcType.LOCAL ? ImageIO.read(Files.newInputStream(Path.of(file))) : fetchIllustration(Pixiv.getInstance(), file);
            }

            @Override
            protected void done() {
                try {
                    image = get();
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
                boolean success = ImageIO.write(image, "png", output);
                output.flush();
                output.close();
                lock.close();
                image.flush();
                image = null;
                return success;
            }
        };
        cacheWorker.execute();
    }
}
