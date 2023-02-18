package me.kumo.io.pixiv;

import com.github.hanshsieh.pixivj.api.PixivApiClient;
import com.github.hanshsieh.pixivj.exception.AuthException;
import com.github.hanshsieh.pixivj.exception.PixivException;
import com.github.hanshsieh.pixivj.model.IllustDetail;
import com.github.hanshsieh.pixivj.model.Illustration;
import com.github.hanshsieh.pixivj.model.SearchedIllusts;
import com.github.hanshsieh.pixivj.oauth.PixivOAuthClient;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.checkerframework.checker.nullness.qual.NonNull;

import javax.net.ssl.SSLException;
import javax.swing.*;
import javax.swing.event.EventListenerList;
import java.io.IOException;

public class Pixiv extends PixivApiClient {

    private static Pixiv instance;

    private final EventListenerList list;

    public Pixiv(String token) {
        super(new PixivApiClient.Builder().setTokenProvider(new UserRefresher(new PixivOAuthClient.Builder().build(), token)));
        instance = this;
        list = new EventListenerList();
        SwingUtilities.invokeLater(() -> {
            try {
                System.out.println("Access Token :: " + tokenProvider.getAccessToken());
            } catch (SSLException ignored) {
            } catch (AuthException | IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static Pixiv getInstance() {
        return instance;
    }

    @NonNull
    public SearchedIllusts bookmarks(@NonNull V2Filter filter) throws PixivException, IOException {
        return sendGetRequest("v1/user/bookmarks/illust", filter, SearchedIllusts.class);
    }

    @NonNull
    public SearchedIllusts follow(@NonNull V2Filter filter) throws PixivException, IOException {
        return sendGetRequest("v2/illust/follow", filter, SearchedIllusts.class);
    }

    @NonNull
    public IllustDetail getIllustDetail(long illustId) throws PixivException, IOException {
        IllustDetail detail = super.getIllustDetail(illustId);
        invoke(detail.getIllust());
        return detail;
    }

    public void addOnLoadListener(PixivOnLoadListener listener) {
        ((UserRefresher) tokenProvider).addPixivOnLoadListener(listener);
    }

    @NonNull
    public DeleteBookmarkResult removeBookmark(@NonNull DeleteBookmark bookmark) throws PixivException, IOException {
        HttpUrl url = baseUrl.newBuilder()
                .addEncodedPathSegments("v1/illust/bookmark/delete")
                .build();
        RequestBody formBody = new FormBody.Builder()
                .add("illust_id", String.valueOf(bookmark.getIllustId()))
                .add("restrict", bookmark.getRestrict().string())
                .build();
        Request request = createApiReqBuilder()
                .url(url)
                .post(formBody)
                .build();
        return requestSender.send(request, DeleteBookmarkResult.class);
    }

    public void addIllustUpdateListener(IllustUpdateListener l) {
        list.add(IllustUpdateListener.class, l);
    }

    public void removeIllustUpdateListener(IllustUpdateListener l) {
        list.remove(IllustUpdateListener.class, l);
    }

    protected void invoke(Illustration illust) {
        Object[] listeners = list.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == IllustUpdateListener.class) {
                ((IllustUpdateListener) listeners[i + 1]).accept(illust);
            }
        }
    }
}
