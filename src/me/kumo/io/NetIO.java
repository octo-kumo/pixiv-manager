package me.kumo.io;

import me.kumo.components.utils.FileTransferable;
import me.kumo.pixiv.Pixiv;
import okhttp3.Response;
import pixivj.exception.PixivException;
import pixivj.model.Illustration;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class NetIO {
    private static final HashSet<Long> downloading = new HashSet<>();
    private static final File CACHE = new File("cache");

    static {
        if (!CACHE.exists()) CACHE.mkdir();
    }

    public static int tasks() {
        return downloading.size();
    }

    public static BufferedImage fetchIllustrationCached(Pixiv pixiv, String url) throws PixivException, IOException {
        String extension = url.substring(url.lastIndexOf('.'));
        File cache = new File(CACHE, url.hashCode() + extension);
        try {
            if (cache.exists()) return ImageIO.read(cache);
        } catch (Exception ignored) {
        }
        BufferedImage image = fetchImage(pixiv, url);
        if (image == null) return null;
        try (FileImageOutputStream fw = new FileImageOutputStream(cache)) {
            ImageIO.write(image, extension.substring(1), fw);
            fw.flush();
        }
        return image;
    }


    public static BufferedImage fetchImage(Pixiv pixiv, String url) throws PixivException, IOException {
        Response download = pixiv.download(url);
        if (download.isSuccessful() && download.body() != null) {
            InputStream input = download.body().byteStream();
            BufferedImage read = ImageIO.read(input);
            download.close();
            return read;
        }
        return null;
    }

    public static BufferedImage fetchImage(Pixiv pixiv, String url, ProgressTracker.ProgressListener listener) throws PixivException, IOException {
        Response download = pixiv.download(url);
        if (download.isSuccessful() && download.body() != null) {
            long targetSize = download.body().contentLength();
            BufferedImage read;
            try (InputStream input = download.body().byteStream();
                 ProgressInputStream imageInputStream = new ProgressInputStream(input, targetSize)) {
                imageInputStream.getTracker().addProgressListener(listener);
                read = ImageIO.read(imageInputStream);
            }
            download.close();
            return read;
        }
        return null;
    }

    public static BufferedImage fetchImage(Pixiv pixiv, String url, File path) throws PixivException, IOException {
        Response download = pixiv.download(url);
        if (download.isSuccessful() && download.body() != null) {
            BufferedImage read;
            try (InputStream input = download.body().byteStream();
                 FileOutputStream os = new FileOutputStream(path);
                 ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                input.transferTo(baos);
                read = ImageIO.read(new ByteArrayInputStream(baos.toByteArray()));
                os.write(baos.toByteArray());
                os.flush();
            }
            download.close();
            return read;
        }
        return null;
    }

    public static BufferedImage fetchImage(Pixiv pixiv, String url, ProgressTracker.ProgressListener listener, File path) throws PixivException, IOException {
        Response download = pixiv.download(url);
        if (download.isSuccessful() && download.body() != null) {
            long targetSize = download.body().contentLength();
            BufferedImage read;
            try (InputStream input = download.body().byteStream();
                 FileOutputStream os = new FileOutputStream(path);
                 ByteArrayOutputStream baos = new ByteArrayOutputStream();
                 ProgressInputStream imageInputStream = new ProgressInputStream(input, targetSize)) {
                imageInputStream.getTracker().addProgressListener(listener);
                imageInputStream.transferTo(baos);
                read = ImageIO.read(new ByteArrayInputStream(baos.toByteArray()));
                os.write(baos.toByteArray());
                os.flush();
            }
            download.close();
            return read;
        }
        return null;
    }

    public static void downloadImage(Pixiv pixiv, String url, File path) throws PixivException, IOException {
        Response download = pixiv.download(url);
        if (download.isSuccessful() && download.body() != null) {
            try (InputStream input = download.body().byteStream();
                 FileOutputStream os = new FileOutputStream(path)) {
                input.transferTo(os);
            }
            download.close();
        }
    }

    public static void downloadImage(Pixiv pixiv, String url, File path, ProgressTracker.ProgressListener listener) throws PixivException, IOException {
        Response download = pixiv.download(url);
        if (download.isSuccessful() && download.body() != null) {
            long targetSize = download.body().contentLength();
            try (InputStream in = download.body().byteStream();
                 ProgressInputStream in2 = new ProgressInputStream(in, targetSize);
                 ByteArrayOutputStream os = new ByteArrayOutputStream();
                 FileOutputStream fs = new FileOutputStream(path)) {
                in2.getTracker().addProgressListener(listener);
                in2.transferTo(os);
                fs.write(os.toByteArray());
                fs.flush();
            }
            download.close();
        }
    }

    public static boolean downloadIllustration(Pixiv pixiv, Illustration illustration, ProgressTracker.ProgressListener listener) {
        try {
            if (downloading.contains(illustration.getId())) return false;
            downloading.add(illustration.getId());
            Stream.concat(Stream.of(illustration.getMetaSinglePage().getOriginalImageUrl()), illustration.getMetaPages().stream().map(m -> LocalGallery.getBestQuality(m.getImageUrls())))
                    .filter(Objects::nonNull).forEach(url -> {
                        String filename = url.substring(url.lastIndexOf('/') + 1);
//                        System.out.println("downloading ... " + filename);
                        File target = new File(LocalGallery.PATH, filename);
                        if (target.exists()) {
                            System.out.println("File Exists! " + target.getName());
                            return;
                        }
                        try {
                            Response response = pixiv.download(url);
                            if (response.isSuccessful() && response.body() != null) {
                                long targetSize = response.body().contentLength();
                                try (InputStream in = response.body().byteStream();
                                     ProgressInputStream in2 = new ProgressInputStream(in, targetSize);
                                     FileOutputStream fw = new FileOutputStream(target)) {
                                    in2.getTracker().addProgressListener(listener);
                                    in2.transferTo(fw);
                                }
                            }
                        } catch (SocketTimeoutException e) {
                            target.delete();
                            throw new RuntimeException(e);
                        } catch (PixivException | IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
            downloading.remove(illustration.getId());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static long getFileSize(Pixiv pixiv, String url) throws PixivException, IOException {
        Response download = pixiv.download(url);
        long l = download.isSuccessful() && download.body() != null ? download.body().contentLength() : -1;
        download.close();
        return l;
    }

    public static void openURL(URI uri) {
        try {
            Desktop.getDesktop().browse(uri);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void openURL(Illustration illustration) {
        if (illustration == null) return;
        openURL(URI.create("https://pixiv.net/artworks/" + illustration.getId()));
    }

    public static void openFile(Illustration illustration) {
        if (illustration == null) return;
        try {
            Desktop.getDesktop().open(LocalGallery.getImage(illustration.getId()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void openFile(File file) {
        if (file == null) return;
        try {
            Desktop.getDesktop().open(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void copyFile(Illustration illustration) {
        FileTransferable ft = new FileTransferable(List.of(LocalGallery.getImage(illustration.getId())));
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ft, (clipboard, contents) -> System.out.println("Lost ownership"));
    }

    public static void copyFiles(List<Illustration> illustrations) {
        FileTransferable ft = new FileTransferable(illustrations.stream().map(illustration -> LocalGallery.getImage(illustration.getId())).toList());
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ft, (clipboard, contents) -> System.out.println("Lost ownership"));
    }
}
