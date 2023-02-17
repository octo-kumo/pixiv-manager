package me.kumo.io.pixiv;

import com.github.hanshsieh.pixivj.api.PixivApiClient;
import com.github.hanshsieh.pixivj.exception.AuthException;
import com.github.hanshsieh.pixivj.exception.PixivException;
import com.github.hanshsieh.pixivj.model.SearchedIllusts;
import com.github.hanshsieh.pixivj.model.User;
import com.github.hanshsieh.pixivj.oauth.PixivOAuthClient;
import org.checkerframework.checker.nullness.qual.NonNull;

import javax.net.ssl.SSLHandshakeException;
import javax.swing.*;
import java.io.IOException;
import java.util.function.Consumer;

public class Pixiv extends PixivApiClient {

    private static Pixiv instance;

    public Pixiv() {
        super(new PixivApiClient.Builder().setTokenProvider(new UserRefresher(new PixivOAuthClient.Builder().build(), System.getenv("PIXIV_TOKEN"))));
        instance = this;
        SwingUtilities.invokeLater(() -> {
            try {
                System.out.println("Access Token :: " + tokenProvider.getAccessToken());
            } catch (SSLHandshakeException ignored) {
            } catch (AuthException | IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static Pixiv getInstance() {
        return instance;
    }

    @NonNull
    public SearchedIllusts bookmarks(@NonNull BookmarkFilter filter) throws PixivException, IOException {
        return sendGetRequest("v1/user/bookmarks/illust", filter, SearchedIllusts.class);
    }

    public void onLoad(Consumer<User> consumer) {
        ((UserRefresher) tokenProvider).setOnLoad(consumer);
    }
}
