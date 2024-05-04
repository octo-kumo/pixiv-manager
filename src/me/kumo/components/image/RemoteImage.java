package me.kumo.components.image;

import com.github.hanshsieh.pixivj.exception.PixivException;
import me.kumo.io.ImageUtils;
import me.kumo.io.NetIO;
import me.kumo.io.ProgressInputStream;
import me.kumo.io.ProgressTracker;
import me.kumo.pixiv.Pixiv;
import org.jetbrains.annotations.Nullable;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Objects;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

public abstract class RemoteImage extends JComponent implements ProgressTracker.ProgressListener {
    public static final String THUMBNAIL = "thumbnail";
    public static final File CACHE = new File("cache");

    static {
        if (!CACHE.exists() && !CACHE.mkdirs()) throw new RuntimeException("Unable to create cache directories");
    }

    @Nullable
    protected File thumbnailFile;
    private ImageLoader loader;
    private double spinnerSize = 40;
    private @Nullable String url;
    private @Nullable File localFile;
    private @Nullable BufferedImage thumb;
    private boolean failedToLoad = false;

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHints(ImageUtils.RENDERING_HINTS_FAST);
        if (hasLoaded()) drawImage(g2d, thumb);
        else drawProgress(g2d);
        if (shouldMakeThumbnail()) revalidateThumbnail();
    }

    public void drawProgress(Graphics2D g) {
        if (failedToLoad)
            g.drawString("Failed", getWidth() / 2f - g.getFontMetrics().stringWidth("Failed") / 2f, getHeight() / 2f);
        else
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
        if (url != null) this.thumbnailFile = new File(CACHE, url.substring(url.lastIndexOf('/') + 1));
    }

    public @Nullable File getLocalFile() {
        return localFile;
    }

    public void setLocalFile(@Nullable File localFile) {
        this.localFile = localFile;
    }

    protected abstract boolean shouldSaveToLocal();

    protected abstract boolean shouldMakeThumbnail();

    protected boolean shouldHaveProgress() {
        return false;
    }

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

    public void loadImage(boolean force) {
        if (isLoading()) return;
        if (hasLoaded() && !force) return;
        loader = new ImageLoader();
        loader.execute();
    }

    public void loadImage() {
        loadImage(false);
    }

    public void unloadImage() {
        if (isLoading()) loader.cancel(true);
        if (!hasLoaded()) return;
        setThumbnail(null);
        loader = null;
    }

    protected void setThumbnail(BufferedImage image) {
        if (thumb == image) return;
        firePropertyChange(THUMBNAIL, thumb, image);
        if (thumb != null) thumb.flush();
        thumb = image;
    }

    public void revalidateThumbnail() {
        if (hasLoaded()) {
            if (isThumbnailValid(thumb.getWidth(), thumb.getHeight())) return;
            loadImage(true);
        }
    }

    public boolean isThumbnailValid(int w, int h) {
        double ratio = Math.max(getWidth() * 2d / w, getHeight() * 2d / h);
        return !(ratio > 1.5 || (1 / ratio) > 1.5);
    }

    public BufferedImage getImage() {
        return thumb;
    }

    @Override
    public void addNotify() {
        super.addNotify();
        revalidateThumbnail();
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        revalidateThumbnail();
    }

    private class ImageLoader extends SwingWorker<BufferedImage, Object> {
        private BufferedImage refreshThumbnail() throws IOException, PixivException {
            BufferedImage image = loadThumbnail();
            if (image == null) image = getImage();
            return image;
        }

        private BufferedImage loadImage() throws PixivException, IOException {
            if (localFile != null && localFile.exists()) {
                InputStream input = new FileInputStream(localFile);
                if (shouldHaveProgress()) {
                    input = new ProgressInputStream(input, localFile.length());
                    ((ProgressInputStream) input).getTracker().addProgressListener(RemoteImage.this);
                }
                try {
                    return Objects.requireNonNull(ImageIO.read(input));
                } catch (IIOException ignored) {
                    System.out.println("Corrupted Image : " + localFile.getAbsolutePath());
                } catch (NullPointerException e) {
                    System.out.println("Empty Image : " + localFile.getAbsolutePath());
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("? Image : " + localFile.getAbsolutePath());
                    System.out.println(localFile.toString());
                }
            }
            if (url == null) return null;
            if (shouldSaveToLocal() && localFile != null) {
                if (shouldHaveProgress())
                    return NetIO.fetchImage(Pixiv.getInstance(), url, RemoteImage.this, localFile);
                else return NetIO.fetchImage(Pixiv.getInstance(), url, localFile);
            } else {
                if (shouldHaveProgress()) return NetIO.fetchImage(Pixiv.getInstance(), url, RemoteImage.this);
                else return NetIO.fetchImage(Pixiv.getInstance(), url);
            }
        }

        private @Nullable BufferedImage loadThumbnail() throws IOException {
            if (thumbnailFile != null && thumbnailFile.exists() && shouldMakeThumbnail()) {
                InputStream input = new FileInputStream(thumbnailFile);
                if (shouldHaveProgress()) {
                    input = new ProgressInputStream(input, thumbnailFile.length());
                    ((ProgressInputStream) input).getTracker().addProgressListener(RemoteImage.this);
                }
                BufferedImage image = ImageIO.read(input);
                if (!isThumbnailValid(image.getWidth(), image.getHeight())) return null;
                return image;
            }
            return null;
        }

        private @Nullable BufferedImage getImage() throws PixivException, IOException {
            BufferedImage bigImage = loadImage();
            if (bigImage == null) return null;
            BufferedImage thumbnail = shouldMakeThumbnail() ? ImageUtils.downScale(bigImage, 2 * getWidth(), 2 * getHeight()) : bigImage;
            if (shouldMakeThumbnail()) {
                cacheThumbnail(thumbnail);
                bigImage.flush();
            }
            return thumbnail;
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
            failedToLoad = false;
            return refreshThumbnail();
        }

        @Override
        protected void done() {
            super.done();
            try {
                setThumbnail(get());
            } catch (CancellationException | InterruptedException ignored) {
            } catch (ExecutionException e) {
                if (e.getCause() instanceof SocketTimeoutException
                        || e.getCause() instanceof SocketException
                        || e.getCause().getCause() instanceof SocketException) failedToLoad = true;
                else if (e.getCause() instanceof IIOException) System.out.println("Corrupt File (IIO) " + localFile);
                else if (e.getCause() instanceof EOFException) System.out.println("Corrupt File (EOF) " + localFile);
                throw new RuntimeException(e);
            }
        }
    }
}
