package me.kumo.io;

import com.github.hanshsieh.pixivj.exception.PixivException;
import com.github.hanshsieh.pixivj.model.Illustration;
import me.kumo.io.pixiv.Pixiv;
import me.kumo.ui.utils.FileTransferable;
import okhttp3.Response;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class NetIO {
    private static final String PATH = "/Users/zy/Documents/Pictures2022";
    private static final HashSet<Long> downloading = new HashSet<>();

    public static int tasks() {
        return downloading.size();
    }

    public static BufferedImage fetchIllustration(Pixiv pixiv, String url) throws PixivException, IOException {
        Response download = pixiv.download(url);
        if (download.isSuccessful() && download.body() != null) {
            BufferedImage read = ImageIO.read(download.body().byteStream());
            download.close();
            return read;
        }
        return null;
    }

    public static boolean downloadIllustration(Pixiv pixiv, Illustration illustration, ProgressListener listener) {
        try {
            if (downloading.contains(illustration.getId())) return false;
            downloading.add(illustration.getId());
            Stream.concat(Stream.of(illustration.getMetaSinglePage().getOriginalImageUrl()), illustration.getMetaPages().stream().map(m -> LocalGallery.getBestQuality(m.getImageUrls())))
                    .filter(Objects::nonNull).forEach(url -> {
                        String filename = url.substring(url.lastIndexOf('/') + 1);
//                        System.out.println("downloading ... " + filename);
                        File target = new File(PATH, filename);
                        if (target.exists()) {
                            System.out.println("File Exists! " + target.getName());
                            return;
                        }
                        try {
                            Response response = pixiv.download(url);
                            if (response.isSuccessful() && response.body() != null) {
                                try (InputStream in = response.body().byteStream();
                                     FileOutputStream fw = new FileOutputStream(target)) {
                                    long targetSize = response.body().contentLength(), downloaded = 0;
                                    byte[] buffer = new byte[1024 * 16];
                                    int len;
                                    while ((len = in.read(buffer)) != -1) {
                                        fw.write(buffer, 0, len);
                                        downloaded += len;
                                        listener.update(downloaded, targetSize, false);
                                    }
                                    fw.flush();
                                    listener.update(downloaded, targetSize, downloaded == targetSize);
                                }
                            }
                        } catch (PixivException | IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
            downloading.remove(illustration.getId());
            LocalGallery.update();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public interface ProgressListener {
        void update(long bytesRead, long contentLength, boolean done);
    }

    public static void open(URI uri) {
        try {
            Desktop.getDesktop().browse(uri);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void open(Illustration illustration) {
        if (illustration == null) return;
        open(URI.create("https://pixiv.net/artworks/" + illustration.getId()));
    }

    public static void openFile(Illustration illustration) {
        if (illustration == null) return;
        try {
            Desktop.getDesktop().open(LocalGallery.getImage(illustration.getId()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void copyFile(Illustration illustration) {
        FileTransferable ft = new FileTransferable(List.of(LocalGallery.getImage(illustration.getId())));
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ft, (clipboard, contents) -> System.out.println("Lost ownership"));
    }
}
