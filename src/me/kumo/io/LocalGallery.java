package me.kumo.io;

import com.github.hanshsieh.pixivj.model.MetaPageImageUrls;
import me.kumo.ui.utils.Nullity;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class LocalGallery {
    public static final String PATH = "/Users/zy/Documents/Pictures";
    private static final ConcurrentHashMap<String, File> ID_FILE_MAP = new ConcurrentHashMap<>();
    private static File[] FILES;

    public static void update() {
        ID_FILE_MAP.clear();
        FILES = new File(PATH).listFiles(new ImageFileFilter());
    }

    public static File getImage(long illusID) {
        return getImage(String.valueOf(illusID));
    }

    synchronized public static File getImage(String illusID) {
        return ID_FILE_MAP.computeIfAbsent(illusID, i -> {
            Optional<File> file = Arrays.stream(FILES).filter(f -> f.getName().startsWith(illusID)).findAny();
            return file.orElse(null);
        });
    }

    public static String getBestQuality(MetaPageImageUrls urls) {
        return Nullity.coalesce(urls.getOriginal(), urls.getLarge(), urls.getMedium());
    }

    public static class ImageFileFilter implements FilenameFilter {
        private final String[] okFileExtensions = new String[]{"jpg", "jpeg", "png", "gif"};

        @Override
        public boolean accept(File dir, String name) {
            return Arrays.stream(okFileExtensions).anyMatch(extension -> name.toLowerCase().endsWith(extension));
        }
    }
}
