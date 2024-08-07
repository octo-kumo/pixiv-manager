package me.kumo.pixiv;

import org.checkerframework.checker.nullness.qual.NonNull;
import pixivj.api.PixivApiClient;
import pixivj.exception.AuthException;
import pixivj.exception.PixivException;
import pixivj.model.IllustDetail;
import pixivj.model.Illustration;
import pixivj.oauth.PixivOAuthClient;

import javax.net.ssl.SSLException;
import javax.swing.*;
import javax.swing.event.EventListenerList;
import java.io.IOException;

public class Pixiv extends PixivApiClient {

    private static Pixiv instance;

    private final EventListenerList list;

    public Pixiv() {
        super(new PixivApiClient.Builder().setTokenProvider(new UserRefresher(new PixivOAuthClient.Builder().build())));
        instance = this;
        list = new EventListenerList();
    }

    public static Pixiv getInstance() {
        return instance;
    }

    public void setToken(String token) {
        ((UserRefresher) tokenProvider).setToken(token);
        SwingUtilities.invokeLater(() -> {
            try {
                System.out.println("Access Token :: " + tokenProvider.getAccessToken());
            } catch (SSLException ignored) {
            } catch (AuthException | IOException e) {
                throw new RuntimeException(e);
            }
        });
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
