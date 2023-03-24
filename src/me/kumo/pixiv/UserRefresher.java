package me.kumo.pixiv;

import com.github.hanshsieh.pixivj.exception.AuthException;
import com.github.hanshsieh.pixivj.model.AuthResult;
import com.github.hanshsieh.pixivj.model.Credential;
import com.github.hanshsieh.pixivj.model.GrantType;
import com.github.hanshsieh.pixivj.oauth.PixivOAuthClient;
import com.github.hanshsieh.pixivj.token.LazyTokenRefresher;
import org.apache.commons.lang3.Validate;
import org.checkerframework.checker.nullness.qual.NonNull;

import javax.swing.event.EventListenerList;
import java.io.IOException;
import java.time.Instant;

public class UserRefresher extends LazyTokenRefresher {
    private final EventListenerList list;
    private boolean loaded;
    private AuthResult result;

    public UserRefresher(@NonNull PixivOAuthClient client) {
        super(client);
        loaded = false;
        list = new EventListenerList();
    }

    public void setToken(String token) {
        updateTokens("", token, Instant.now());
    }

    @Override
    @NonNull
    public String getAccessToken() throws AuthException, IOException {
        try {
            Validate.notNull(expiryTime, "Expiry time not set. Are you logged in?");
            if (isNearExpire()) {
                Validate.notNull(client, "Client is not set");
                logger.debug("Access token is expired, refreshing it");
                Credential credential = new Credential();
                credential.setRefreshToken(this.refreshToken);
                credential.setGrantType(GrantType.REFRESH_TOKEN);
                AuthResult authResult = client.authenticate(credential);
                result = authResult;
                loaded = true;
                invoke();
                updateTokens(
                        authResult.getAccessToken(),
                        authResult.getRefreshToken(),
                        Instant.now().plusSeconds(authResult.getExpiresIn()));
            }
            Validate.notNull(accessToken, "Access token not set");
            return this.accessToken;
        } catch (Exception e) {
            loaded = true;
            invoke();
            throw e;
        }
    }

    private void invoke() {
        Object[] listeners = list.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == PixivOnLoadListener.class) {
                ((PixivOnLoadListener) listeners[i + 1]).done(result);
            }
        }
    }


    public void addPixivOnLoadListener(PixivOnLoadListener l) {
        if (loaded) l.done(result);
        list.add(PixivOnLoadListener.class, l);
    }

    public void removePixivOnLoadListener(PixivOnLoadListener l) {
        list.remove(PixivOnLoadListener.class, l);
    }
}
