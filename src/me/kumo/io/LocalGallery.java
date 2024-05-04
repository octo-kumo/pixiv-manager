package me.kumo.io;

import com.github.hanshsieh.pixivj.model.Illustration;
import com.github.hanshsieh.pixivj.model.MetaPageImageUrls;
import com.github.weisj.darklaf.iconset.AllIcons;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import me.kumo.components.image.RemoteImage;
import me.kumo.components.utils.Formatters;
import me.kumo.components.utils.Nullity;
import me.kumo.image.colorthief.ProminentColor;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NonNls;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LocalGallery {
    private static final String @NonNls [] OK_FILE_EXTENSIONS = new String[]{"jpg", "jpeg", "png", "gif"};

    private static final File COLOR_MAP_FILE = new File("pixiv_mean_color_map.csv");
    private static final File BIG_COLOR_MAP_FILE = new File("pixiv_mean_color_map_large.csv");
    private static final ConcurrentHashMap<Long, Color[]> COLOR_MAP = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Pair<Long, Integer>, Color[]> BIG_COLOR_MAP = new ConcurrentHashMap<>();
    private static final CSVWriter COLOR_MAP_WRITER;
    private static final CSVWriter BIG_COLOR_MAP_WRITER;
    public static String PATH;

    static {
        try (CSVReader reader_small = new CSVReader(new FileReader(COLOR_MAP_FILE)); CSVReader reader_big = new CSVReader(new FileReader(BIG_COLOR_MAP_FILE))) {
            COLOR_MAP.putAll(reader_small.readAll().stream().collect(Collectors.toMap(s -> Long.parseLong(s[0]), s -> Arrays.stream(s[1].split("\\|")).map(Color::decode).toArray(Color[]::new), (a, b) -> b)));
            BIG_COLOR_MAP.putAll(reader_big.readAll().stream().collect(Collectors.toMap(s -> Pair.of(Long.parseLong(s[0]), Integer.parseInt(s[1])), s -> Arrays.stream(s[2].split("\\|")).map(Color::decode).toArray(Color[]::new), (a, b) -> b)));
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
        int[][] palette = ProminentColor.getPalette(image, 5, 10, true);
        if (palette == null) return;
        Color[] colors = Arrays.stream(palette).map(is -> new Color(is[0], is[1], is[2])).toArray(Color[]::new);
        COLOR_MAP.put(id, colors);
        COLOR_MAP_WRITER.writeNext(new String[]{String.valueOf(id), Arrays.stream(colors).map(i -> String.format("#%06X", 0xFFFFFF & i.getRGB())).collect(Collectors.joining("|"))});
        COLOR_MAP_WRITER.flushQuietly();
    }

    public static void processBigImage(long id, int idx, BufferedImage image) {
        Pair<Long, Integer> key = Pair.of(id, idx);
        if (BIG_COLOR_MAP.containsKey(key)) return;
        int[][] palette = ProminentColor.getPalette(image, 5, 10, true);
        if (palette == null) return;
        Color[] colors = Arrays.stream(palette).map(is -> new Color(is[0], is[1], is[2])).toArray(Color[]::new);
        BIG_COLOR_MAP.put(key, colors);
        BIG_COLOR_MAP_WRITER.writeNext(new String[]{String.valueOf(id), String.valueOf(idx), Arrays.stream(colors).map(i -> String.format("#%06X", 0xFFFFFF & i.getRGB())).collect(Collectors.joining("|"))});
        BIG_COLOR_MAP_WRITER.flushQuietly();
    }

    public static void setPath(String path) {
        PATH = path;
    }

    public static File getImage(long illusID) {
        return getImage(String.valueOf(illusID));
    }

    public static List<File> getImages(Illustration illustration) {
        File[] files = new File[illustration.getPageCount()];
        for (int i = 0; i < illustration.getPageCount(); i++) {
            int finalI = i;
            files[i] = ((Supplier<File>) () -> {
                for (String ext : OK_FILE_EXTENSIONS) {
                    File file = new File(PATH, illustration.getId() + "_p" + finalI + "." + ext);
                    if (file.exists()) return file;
                }
                return null;
            }).get();
        }
        return Stream.of(files).filter(Objects::nonNull).toList();
    }

    synchronized public static File getImage(String illusID) {
        if (illusID.indexOf('.') != -1) return new File(PATH, illusID);
        for (String ext : OK_FILE_EXTENSIONS) {
            File file = new File(PATH, illusID + "_p0." + ext);
            if (file.exists()) return file;
        }
        return null;
    }

    public static String getBestQuality(MetaPageImageUrls urls) {
        return Nullity.coalesce(urls.getOriginal(), urls.getLarge(), urls.getMedium());
    }

    public static String getMidQuality(MetaPageImageUrls urls) {
        return Nullity.coalesce(urls.getMedium(), urls.getLarge(), urls.getOriginal());
    }

    public static @NonNls String getExtension(String name) {
        return switch (name.substring(name.lastIndexOf('.') + 1).toLowerCase()) {
            case "png" -> "png";
            case "jpg", "jpeg" -> "jpg";
            case "gif" -> "gif";
            default -> throw new RuntimeException("Unknown extension: " + name);
        };
    }

    public static class ImageFileFilter implements FilenameFilter {
        @Override
        public boolean accept(File dir, String name) {
            return Arrays.stream(OK_FILE_EXTENSIONS).anyMatch(extension -> name.toLowerCase().endsWith(extension));
        }
    }

    public static void showCache(Window parent) {
        JDialog dialog = new JDialog(parent, "Cache");
        dialog.setContentPane(new CachePopup());

        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }

    public static class CachePopup extends Box {
        private final JPanel top;
        private final JLabel fileCount;
        private final JLabel fileSize;
        private final AtomicLong totalSize = new AtomicLong(0);
        private final JProgressBar prog;
        private SwingWorker<Long, Long> calcWorker;

        public CachePopup() {
            super(BoxLayout.Y_AXIS);
            add(top = new JPanel(new FlowLayout()));
            top.add(fileCount = new JLabel(AllIcons.Files.General.get()));
            top.add(fileSize = new JLabel(AllIcons.Files.Folder.get()));
            add(prog = new JProgressBar());
            add(new JButton("Refresh") {{
                addActionListener(e -> {
                    calcSize();
                    refresh();
                });
            }});
            add(new JButton("Clear") {{
                addActionListener(e -> {
                    Desktop.getDesktop().moveToTrash(RemoteImage.CACHE);
                    RemoteImage.CACHE.mkdir();
                });
            }});

            calcSize();
            refresh();
        }

        private void calcSize() {
            calcWorker = new SwingWorker<>() {
                @Override
                protected Long doInBackground() {
                    totalSize.set(0);
                    processFiles(Objects.requireNonNull(RemoteImage.CACHE.listFiles()));
                    return totalSize.get();
                }

                private void processFiles(File[] files) {
                    for (int i = 0; i < files.length; i++) {
                        File file = files[i];
                        if (file.isDirectory()) processFiles(Objects.requireNonNull(file.listFiles()));
                        else totalSize.addAndGet(file.length());
                        prog.setMaximum(files.length);
                        prog.setValue(i);
                    }
                }

                @Override
                protected void process(List<Long> chunks) {
                    totalSize.set(chunks.get(chunks.size() - 1));
                    fileSize.setText(Formatters.formatBytes(totalSize.get()));
                }

                @Override
                protected void done() {
                    try {
                        totalSize.set(get());
                    } catch (InterruptedException | ExecutionException e) {
                        throw new RuntimeException(e);
                    }
                    fileSize.setText(Formatters.formatBytes(totalSize.get()));
                }
            };
            calcWorker.execute();
        }

        public void refresh() {
            fileCount.setText(String.valueOf(Objects.requireNonNull(RemoteImage.CACHE.list()).length));
        }
    }
}
