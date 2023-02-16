package me.kumo.io;

import com.github.hanshsieh.pixivj.exception.PixivException;
import com.github.hanshsieh.pixivj.model.Illustration;
import com.github.hanshsieh.pixivj.model.MetaPageImageUrls;
import me.kumo.io.pixiv.Pixiv;
import me.kumo.ui.utils.Nullity;
import okhttp3.Response;
import okio.BufferedSink;
import okio.Okio;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public class LocalGallery {
    private static final String PATH = "/Users/zy/Documents/Pictures2022";
    private static File[] FILES;

    public static void update() {
        FILES = new File(PATH).listFiles((dir, name) -> name.endsWith(".jpg") || name.endsWith(".png"));
    }

    public static String getImage(long illusID) {
        return getImage(String.valueOf(illusID));
    }

    public static String getImage(String illusID) {
        Optional<File> file = Arrays.stream(FILES).filter(f -> f.getName().startsWith(illusID)).findAny();
        return file.map(File::getAbsolutePath).orElse(null);
    }

    public static boolean downloadIllustration(Pixiv pixiv, Illustration illustration) {
        try {
            Stream.concat(Stream.of(illustration.getMetaSinglePage().getOriginalImageUrl()), illustration.getMetaPages().stream().map(m -> getBestQuality(m.getImageUrls())))
                    .filter(Objects::nonNull).forEach(url -> {
                        String filename = url.substring(url.lastIndexOf('/') + 1);
                        System.out.println("downloading ... " + filename);
                        File target = new File(PATH, filename);
                        if (target.exists()) {
                            System.out.println("File Exists!");
                            return;
                        }
                        try {
                            Response download = pixiv.download(url);
                            if (download.isSuccessful() && download.body() != null) {
                                BufferedSink sink = Okio.buffer(Okio.sink(target));
                                sink.writeAll(download.body().source());
                                sink.close();
                            }
                        } catch (PixivException | IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
            update();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static String getBestQuality(MetaPageImageUrls urls) {
        return Nullity.coalesce(urls.getOriginal(), urls.getLarge(), urls.getMedium());
    }
}
