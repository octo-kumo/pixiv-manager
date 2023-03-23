package me.kumo.io.img;

import java.util.Set;

/**
 * Contains common image formats
 *
 * @author Clyde Velasquez
 * @version 1.0
 */
public enum ImageFormat {

    GIF(new String[]{"gif"}),
    PNG(new String[]{"png"}),
    BMP(new String[]{"bmp"}),
    JPEG(new String[]{"jpg", "jpeg", "jpe", "jif", "jfif", "jfi"});

    public final Set<String> suffix;

    ImageFormat(String[] suffix) {
        // Immutable Set so that modifications can be prevented
        // this.suffix = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(suffix))); // Java 8
        this.suffix = Set.of(suffix); // Java 9 and up
    }

    /**
     * A utility method that returns an {@code ImageFormat} based on the extension of
     * the image filename otherwise null if no file extension is found.
     * If file extension is not in the set then return a default JPEG ImageFormat
     *
     * @param filename The image filename
     * @return {@code ImageFormat} based on the filename
     */
    public static ImageFormat fromFileName(String filename) {
        int lastDot = filename.lastIndexOf('.');
        if (lastDot == -1) {
            return null;
        }

        String ext = filename.substring(lastDot + 1).toLowerCase();
        for (ImageFormat imgFormat : values()) {
            if (imgFormat.suffix.contains(ext)) {
                return imgFormat;
            }
        }

        return JPEG; // Default
    }

    @Override
    public String toString() {
        return this.name().toLowerCase();
    }
}