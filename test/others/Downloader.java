package others;

import com.github.hanshsieh.pixivj.exception.PixivException;
import com.github.hanshsieh.pixivj.model.Illustration;
import com.github.hanshsieh.pixivj.model.MetaPage;
import com.github.hanshsieh.pixivj.util.JsonUtils;
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
        System.getProperties().put("proxySet", "true");
        System.getProperties().put("socksProxyHost", "127.0.0.1");
        System.getProperties().put("socksProxyPort", "1080");
        Pixiv pixiv = new Pixiv();
        pixiv.setToken(PixivManager.getTokenOrNothing(false));
        LocalGallery.setPath(PixivManager.getPathOrNothing(false));
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        ArrayList<Illustration> bookmarks = new ArrayList<>(List.of(JsonUtils.GSON.fromJson(new FileReader(BookmarkManager.PATH, StandardCharsets.UTF_8), Illustration[].class)));

        try (ProgressBar bar = new ProgressBar("Download Bookmarks", bookmarks.size())) {
            for (Illustration illustration : bookmarks) {
                executorService.submit(() -> {
                    try {
                        if (illustration.getPageCount() > 1) {
                            for (MetaPage metaPage : illustration.getMetaPages()) {
                                download(LocalGallery.getBestQuality(metaPage.getImageUrls()));
                            }
                        } else {
                            download(illustration.getMetaSinglePage().getOriginalImageUrl());
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

    public static void download(String url) throws InterruptedException {
        String name = url.substring(url.lastIndexOf('/') + 1);
        File file = LocalGallery.getImage(name);
        if (file == null) throw new AssertionError();
        if (file.exists()) return;
        try {
            NetIO.downloadImage(Pixiv.getInstance(), url, file);
        } catch (PixivException | IOException e) {
            file.delete();
            throw new RuntimeException(e);
        }
    }
}
