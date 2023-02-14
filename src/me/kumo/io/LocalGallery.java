package me.kumo.io;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

public class LocalGallery {
    private static final String PATH = "/Users/zy/Documents/Pictures2022";
    private static final File[] FILES;

    static {
        FILES = new File(PATH).listFiles((dir, name) -> name.endsWith(".jpg") || name.endsWith(".png"));
    }

    public static String getImage(String illusID) throws IOException {
        Optional<File> file = Arrays.stream(FILES).filter(f -> f.getName().startsWith(illusID)).findAny();
        return file.map(File::getAbsolutePath).orElse(null);
    }
}
