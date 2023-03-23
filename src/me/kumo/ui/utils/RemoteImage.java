package me.kumo.ui.utils;

import com.github.hanshsieh.pixivj.exception.PixivException;
import me.kumo.io.ImageUtils;
import me.kumo.io.LocalGallery;
import me.kumo.io.ProgressInputStream;
import me.kumo.io.ProgressTracker;
import me.kumo.io.pixiv.Pixiv;
import org.jetbrains.annotations.Nullable;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Objects;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import static me.kumo.io.NetIO.fetchIllustration;

public abstract class RemoteImage extends JComponent implements ProgressTracker.ProgressListener {
    private static final File CACHE = new File("cache");
    public static final String THUMBNAIL = "thumbnail";

    static {
        if (!CACHE.exists() && !CACHE.mkdirs()) throw new RuntimeException("Unable to create cache directories");
    }

    private ImageLoader loader;
    private double spinnerSize = 40;

    private @Nullable String url;
    private @Nullable File localFile;
    private @Nullable BufferedImage thumb;
    private @Nullable File thumbnailFile;

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        if (hasLoaded()) drawImage(g2d, thumb);
        else drawProgress(g2d);
    }

    public void drawProgress(Graphics2D g) {
        ImageUtils.spinner(g, getWidth() / 2d - getSpinnerSize(), getHeight() / 2d - getSpinnerSize(), getSpinnerSize());
    }

    public void drawImage(Graphics2D g, BufferedImage image) {
        double ratio = Math.max(getWidth() * 1d / image.getWidth(), getHeight() * 1d / image.getHeight());
        int w = (int) (image.getWidth() * ratio);
        int h = (int) (image.getHeight() * ratio);
        g.drawImage(image,
                -(w - getWidth()) >> 1,
                -(h - getHeight()) >> 1,
                w, h, null);
    }

    public @Nullable String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
        this.thumbnailFile = new File(CACHE, url.substring(url.lastIndexOf('/') + 1));
    }

    public @Nullable File getLocalFile() {
        return localFile;
    }

    public void setLocalFile(@Nullable File localFile) {
        this.localFile = localFile;
    }

    public abstract boolean shouldSaveToLocal();

    public abstract boolean shouldMakeThumbnail();

    public double getSpinnerSize() {
        return spinnerSize;
    }

    public void setSpinnerSize(double spinnerSize) {
        this.spinnerSize = spinnerSize;
    }

    public final boolean downloaded() {
        return localFile != null && localFile.exists();
    }

    public final boolean hasLoaded() {
        return thumb != null;
    }

    public final boolean isLoading() {
        return loader != null && !loader.isDone() && !loader.isCancelled();
    }

    public void loadImage() {
        if (isLoading()) return;
        if (hasLoaded()) return;
        loader = new ImageLoader();
        loader.execute();
    }

    public void unloadImage() {
        if (isLoading()) loader.cancel(true);
        if (!hasLoaded()) return;
        setThumbnail(null);
        loader = null;
    }

    private void setThumbnail(BufferedImage image) {
        firePropertyChange(THUMBNAIL, thumb, image);
        thumb = image;
    }

    public void revalidateThumbnail() {
        if (hasLoaded()) {
            if (isThumbnailValid(thumb.getWidth(), thumb.getHeight())) return;
            unloadImage();
            loadImage();
        } else loadImage();
    }

    public boolean isThumbnailValid(int w, int h) {
        double ratio = Math.max(getWidth() * 1d / w, getHeight() * 1d / h);
        return !(ratio > 2 || (1 / ratio) > 2);
    }

    public BufferedImage getImage() {
        return thumb;
    }

    @Override
    public void addNotify() {
        super.addNotify();
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        unloadImage();
    }

    private class ImageLoader extends SwingWorker<BufferedImage, Object> {
        private BufferedImage refreshThumbnail() throws IOException, PixivException {
            BufferedImage image = loadThumbnail();
            if (image == null) image = getImage();
            return image;
        }

        private BufferedImage loadImage() throws PixivException, IOException {
            if (localFile != null && localFile.exists()) {
                ProgressInputStream input = new ProgressInputStream(new FileInputStream(localFile), localFile.length());
                input.getTracker().addProgressListener(RemoteImage.this);
                try {
                    return Objects.requireNonNull(ImageIO.read(input));
                } catch (IIOException ignored) {
                    System.out.println("Corrupted Image : " + localFile.getAbsolutePath());
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println(localFile.toString());
                }
            }
            return fetchIllustration(Pixiv.getInstance(), url, RemoteImage.this);
        }

        private @Nullable BufferedImage loadThumbnail() throws IOException {
            if (thumbnailFile != null && thumbnailFile.exists() && shouldMakeThumbnail()) {
                ProgressInputStream input = new ProgressInputStream(new FileInputStream(thumbnailFile), thumbnailFile.length());
                input.getTracker().addProgressListener(RemoteImage.this);
                BufferedImage image = ImageIO.read(input);
                if (!isThumbnailValid(image.getWidth(), image.getHeight())) return null;
                return image;
            }
            return null;
        }

        private @Nullable BufferedImage getImage() throws PixivException, IOException {
            BufferedImage bigImage = loadImage();
            if (bigImage == null) return null;
            BufferedImage thumbnail = shouldMakeThumbnail() ? makeThumbnail(bigImage) : bigImage;
            saveImage(bigImage);
            if (shouldMakeThumbnail()) {
                cacheThumbnail(thumbnail);
                bigImage.flush();
            }
            return thumbnail;
        }

        private void saveImage(BufferedImage image) {
            if (localFile != null && shouldSaveToLocal())
                try (FileOutputStream fs = new FileOutputStream(localFile)) {
                    ImageIO.write(image, LocalGallery.getExtension(localFile.getName()), fs);
                    fs.flush();
                } catch (Exception ignored) {
                }
        }

        private void cacheThumbnail(BufferedImage image) {
            if (thumbnailFile != null && shouldMakeThumbnail())
                try (FileOutputStream fs = new FileOutputStream(thumbnailFile)) {
                    ImageIO.write(image, "png", fs);
                    fs.flush();
                } catch (Exception ignored) {
                }
        }

        @Override
        protected BufferedImage doInBackground() throws Exception {
            return refreshThumbnail();
        }

        @Override
        protected void done() {
            super.done();
            try {
                setThumbnail(get());
            } catch (CancellationException | InterruptedException ignored) {
            } catch (ExecutionException e) {
                if (e.getCause() instanceof SocketTimeoutException) return;
                throw new RuntimeException(e);
            }
        }
    }

    protected BufferedImage makeThumbnail(BufferedImage image) {
        return ImageUtils.downScale(image, getWidth(), getHeight());
    }
}
