package others;

import pixivj.exception.PixivException;
import pixivj.model.Illustration;
import pixivj.model.MetaPage;
import pixivj.util.JsonUtils;
import me.kumo.PixivManager;
import me.kumo.io.CsvMap;
import me.kumo.io.LocalGallery;
import me.kumo.io.NetIO;
import me.kumo.pixiv.Pixiv;
import me.kumo.ui.managers.BookmarkManager;
import me.tongfei.progressbar.ProgressBar;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class FileSizeCheck {
    private static CsvMap map = new CsvMap("file_sizes.csv");

    public static void main(String... args) throws IOException {
        System.getProperties().put("proxySet", "true");
        System.getProperties().put("socksProxyHost", "127.0.0.1");
        System.getProperties().put("socksProxyPort", "1080");
        Pixiv pixiv = new Pixiv();
        pixiv.setToken(PixivManager.getTokenOrNothing(false));
        LocalGallery.setPath(PixivManager.getPathOrNothing(false));
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        ArrayList<Illustration> bookmarks = new ArrayList<>(List.of(JsonUtils.GSON.fromJson(new FileReader(BookmarkManager.PATH, StandardCharsets.UTF_8), Illustration[].class)));

        try (ProgressBar bar = new ProgressBar("Check Bookmarks", bookmarks.size())) {
            for (Illustration illustration : bookmarks) {
                executorService.submit(() -> {
                    try {
                        if (illustration.getPageCount() > 1) {
                            for (MetaPage metaPage : illustration.getMetaPages()) {
                                check(LocalGallery.getBestQuality(metaPage.getImageUrls()), bar);
                            }
                        } else {
                            check(illustration.getMetaSinglePage().getOriginalImageUrl(), bar);
                        }
                        bar.step();
                        bar.refresh();
                    } catch (Exception e) {
                        System.out.println(e);
                        e.printStackTrace();
                    }
                });
            }
            if (executorService.awaitTermination(10, TimeUnit.SECONDS)) bar.stepTo(bar.getMax());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        pixiv.close();
    }

    public static void check(String url, ProgressBar bar) throws InterruptedException {
        String name = url.substring(url.lastIndexOf('/') + 1);
        File file = LocalGallery.getImage(name);
        if (file == null) throw new AssertionError();
        if (!file.exists()) return;
        long fileSize = getFileSize(url);
        bar.setExtraMessage(name);
        if (file.length() != fileSize && fileSize != -1) {
            bar.setExtraMessage(name + " F");
            file.renameTo(new File("huh", file.getName()));
        } else {
//            bar.setExtraMessage(name + " P");
        }
    }

    public static long getFileSize(String url) throws InterruptedException {
        try {
            if (map.containsKey(url) && !Objects.equals(map.get(url), "-1")) return Long.parseLong(map.get(url));
            long fileSize = NetIO.getFileSize(Pixiv.getInstance(), url);
            map.put(url, String.valueOf(fileSize));
            return fileSize;
        } catch (SocketTimeoutException e) {
            System.out.print('.');
            Thread.sleep(1000);
            return getFileSize(url);
        } catch (PixivException e) {
            System.out.println(e + " " + url);
            return -1;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
