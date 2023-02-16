package me.kumo.io.pixiv;

import com.github.hanshsieh.pixivj.api.PixivApiClient;
import com.github.hanshsieh.pixivj.exception.AuthException;
import com.github.hanshsieh.pixivj.exception.PixivException;
import com.github.hanshsieh.pixivj.model.SearchedIllusts;
import com.github.hanshsieh.pixivj.model.User;
import com.github.hanshsieh.pixivj.oauth.PixivOAuthClient;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.IOException;
import java.time.Instant;

public class Pixiv {
    private final PixivApiClient client;

    public Pixiv() {
        UserRefresher tokenProvider = new UserRefresher(new PixivOAuthClient.Builder().build());
        tokenProvider.updateTokens("", System.getenv("PIXIV_TOKEN"), Instant.now());
        this.client = new PixivApiClient.Builder().setTokenProvider(tokenProvider).build();
        tokenProvider.setOnLoad(this::refresh);
        try {
            tokenProvider.getAccessToken();
        } catch (AuthException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @NonNull
    public SearchedIllusts bookmarks(@NonNull BookmarkFilter filter) throws PixivException, IOException {
        return client.sendGetRequest("v1/user/bookmarks/illust", filter, SearchedIllusts.class);
    }

    public void refresh(User user) {
        try {
            System.out.println("Logged in as " + user.getName());
            BookmarkFilter filter = new BookmarkFilter();
            filter.setUserID(user.getId());
            filter.setLimit(30);
            SearchedIllusts bookmarks = bookmarks(filter);
            System.out.println("bookmarks: " + bookmarks.getIllusts().size());
            System.out.println("next: " + bookmarks.getNextUrl());
//            RecommendedIllusts illusts = client.getRecommendedIllusts(new RecommendedIllustsFilter());
//            System.out.println(illusts.getIllusts());
        } catch (PixivException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
