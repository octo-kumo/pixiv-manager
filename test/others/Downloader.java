package others;

import pixivj.exception.PixivException;
import pixivj.model.Illustration;
import pixivj.model.MetaPage;
import pixivj.util.JsonUtils;
import me.kumo.PixivManager;
import me.kumo.io.LocalGallery;
import me.kumo.io.NetIO;
import me.kumo.pixiv.Pixiv;
import me.kumo.ui.managers.BookmarkManager;
import me.tongfei.progressbar.ProgressBar;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Downloader {
    public static void main(String... args) throws IOException {
        if (PixivManager.getProxy() != null) {
            String[] p = PixivManager.getProxy().split(":");
            if (p.length > 1) {
                System.getProperties().put("proxySet", "true");
                System.getProperties().put("socksProxyHost", p[0]);
                System.getProperties().put("socksProxyPort", p[1]);
            }
        }
        Pixiv pixiv = new Pixiv();
        pixiv.setToken(PixivManager.getTokenOrNothing(false));
        LocalGallery.setPath(PixivManager.getPathOrNothing(false));
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        ArrayList<Illustration> bookmarks = new ArrayList<>(List.of(JsonUtils.GSON.fromJson(new FileReader(BookmarkManager.PATH, StandardCharsets.UTF_8), Illustration[].class)));
        try (ProgressBar bar = new ProgressBar("Download Bookmarks", bookmarks.size());
             ProgressBar dbar = new ProgressBar("Fetch", 0)) {
            for (Illustration illustration : bookmarks) {
                executorService.submit(() -> {
                    try {
                        if (illustration.getPageCount() > 1) {
                            for (MetaPage metaPage : illustration.getMetaPages()) {
                                download(LocalGallery.getBestQuality(metaPage.getImageUrls()), dbar);
                            }
                        } else {
                            download(illustration.getMetaSinglePage().getOriginalImageUrl(), dbar);
                        }
                        bar.step();
                        bar.refresh();
                    } catch (Exception e) {
                        System.out.println(illustration.getId() + " " + e);
                        throw new RuntimeException(e);
                    }
                });
            }
            if (executorService.awaitTermination(10, TimeUnit.SECONDS)) bar.stepTo(bar.getMax());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void download(String url, ProgressBar bar) throws InterruptedException {
        String name = url.substring(url.lastIndexOf('/') + 1);
        File file = LocalGallery.getImage(name);
        if (file == null) throw new AssertionError();
        if (file.exists()) return;
        try {
            NetIO.downloadImage(Pixiv.getInstance(), url, file, e -> {
                bar.stepTo(e.getProgress());
                bar.maxHint(e.getTotal());
                bar.setExtraMessage(name);
            });
        } catch (PixivException | IOException e) {
            if (file.exists() && !file.delete()) throw new RuntimeException(e);
        }
    }
}
