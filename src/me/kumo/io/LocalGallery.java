package me.kumo.io;

import com.github.hanshsieh.pixivj.model.MetaPageImageUrls;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import me.kumo.io.img.colorthief.ColorThief;
import me.kumo.ui.utils.Nullity;
import org.apache.commons.lang3.tuple.Pair;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class LocalGallery {
    public static final String PATH = "/Users/zy/Documents/Pictures";
    private static final ConcurrentHashMap<String, File> ID_FILE_MAP = new ConcurrentHashMap<>();

    private static final File COLOR_MAP_FILE = new File("pixiv_mean_color_map.csv");
    private static final File BIG_COLOR_MAP_FILE = new File("pixiv_mean_color_map_large.csv");
    private static final ConcurrentHashMap<Long, Color[]> COLOR_MAP = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Pair<Long, Integer>, Color[]> BIG_COLOR_MAP = new ConcurrentHashMap<>();
    private static final CSVWriter COLOR_MAP_WRITER;
    private static final CSVWriter BIG_COLOR_MAP_WRITER;

    static {
        try (CSVReader reader_small = new CSVReader(new FileReader(COLOR_MAP_FILE));
             CSVReader reader_big = new CSVReader(new FileReader(BIG_COLOR_MAP_FILE))) {
            COLOR_MAP.putAll(reader_small.readAll().stream().collect(Collectors.toMap(
                    s -> Long.parseLong(s[0]),
                    s -> Arrays.stream(s[1].split("\\|")).map(Color::decode).toArray(Color[]::new),
                    (a, b) -> b)));
            BIG_COLOR_MAP.putAll(reader_big.readAll().stream().collect(Collectors.toMap(
                    s -> Pair.of(Long.parseLong(s[0]), Integer.parseInt(s[1])),
                    s -> Arrays.stream(s[2].split("\\|")).map(Color::decode).toArray(Color[]::new),
                    (a, b) -> b)));
        } catch (IOException ignored) {
        } finally {
            try {
                COLOR_MAP_WRITER = new CSVWriter(new FileWriter(COLOR_MAP_FILE, true));
                BIG_COLOR_MAP_WRITER = new CSVWriter(new FileWriter(BIG_COLOR_MAP_FILE, true));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static Color[] getBigPalette(long id, int idx) {
        return BIG_COLOR_MAP.getOrDefault(Pair.of(id, idx), new Color[]{});
    }

    public static Color[] getPalette(long id) {
        return BIG_COLOR_MAP.getOrDefault(Pair.of(id, 0), COLOR_MAP.getOrDefault(id, new Color[]{}));
    }

    public static void processImage(long id, BufferedImage image) {
        if (COLOR_MAP.containsKey(id)) return;
        int[][] palette = ColorThief.getPalette(image, 5, 10, true);
        if (palette == null) return;
        Color[] colors = Arrays.stream(palette).map(is -> new Color(is[0], is[1], is[2])).toArray(Color[]::new);
        COLOR_MAP.put(id, colors);
        COLOR_MAP_WRITER.writeNext(new String[]{String.valueOf(id), Arrays.stream(colors).map(i -> String.format("#%06X", 0xFFFFFF & i.getRGB())).collect(Collectors.joining("|"))});
        COLOR_MAP_WRITER.flushQuietly();
    }

    public static void processBigImage(long id, int idx, BufferedImage image) {
        Pair<Long, Integer> key = Pair.of(id, idx);
        if (BIG_COLOR_MAP.containsKey(key)) return;
        int[][] palette = ColorThief.getPalette(image, 5, 10, true);
        if (palette == null) return;
        Color[] colors = Arrays.stream(palette).map(is -> new Color(is[0], is[1], is[2])).toArray(Color[]::new);
        BIG_COLOR_MAP.put(key, colors);
        BIG_COLOR_MAP_WRITER.writeNext(new String[]{String.valueOf(id), String.valueOf(idx), Arrays.stream(colors).map(i -> String.format("#%06X", 0xFFFFFF & i.getRGB())).collect(Collectors.joining("|"))});
        BIG_COLOR_MAP_WRITER.flushQuietly();
    }

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

    public static String getMidQuality(MetaPageImageUrls urls) {
        return Nullity.coalesce(urls.getMedium(), urls.getLarge(), urls.getOriginal());
    }

    public static class ImageFileFilter implements FilenameFilter {
        private final String[] okFileExtensions = new String[]{"jpg", "jpeg", "png", "gif"};

        @Override
        public boolean accept(File dir, String name) {
            return Arrays.stream(okFileExtensions).anyMatch(extension -> name.toLowerCase().endsWith(extension));
        }
    }

    public static String getExtension(String name) {
        return switch (name.substring(name.lastIndexOf('.') + 1).toLowerCase()) {
            case "png" -> "png";
            case "jpg", "jpeg" -> "jpg";
            case "gif" -> "gif";
            default -> throw new RuntimeException("Unknown extension: " + name);
        };
    }
}
