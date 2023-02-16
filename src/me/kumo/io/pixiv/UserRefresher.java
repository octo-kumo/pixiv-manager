package me.kumo.io.pixiv;

import com.github.hanshsieh.pixivj.exception.AuthException;
import com.github.hanshsieh.pixivj.model.AuthResult;
import com.github.hanshsieh.pixivj.model.Credential;
import com.github.hanshsieh.pixivj.model.GrantType;
import com.github.hanshsieh.pixivj.model.User;
import com.github.hanshsieh.pixivj.oauth.PixivOAuthClient;
import com.github.hanshsieh.pixivj.token.LazyTokenRefresher;
import org.apache.commons.lang3.Validate;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.function.Consumer;

public class UserRefresher extends LazyTokenRefresher {

    private User user;

    private Consumer<User> loaded = user -> {
    };

    public void setOnLoad(Consumer<User> loaded) {
        this.loaded = loaded;
        if (user != null) loaded.accept(user);
    }

    public UserRefresher(@NonNull PixivOAuthClient client) {
        super(client);
    }

    public UserRefresher(@NonNull PixivOAuthClient client, @NonNull Duration expireTolerance) {
        super(client, expireTolerance);
    }

    @Override
    @NonNull
    public String getAccessToken() throws AuthException, IOException, IllegalStateException {
        Validate.notNull(expiryTime, "Expiry time not set. Are you logged in?");
        if (isNearExpire()) {
            Validate.notNull(client, "Client is not set");
            logger.debug("Access token is expired, refreshing it");
            Credential credential = new Credential();
            credential.setRefreshToken(this.refreshToken);
            credential.setGrantType(GrantType.REFRESH_TOKEN);
            AuthResult authResult = client.authenticate(credential);
            if (user == null) {
                user = authResult.getUser();
                loaded.accept(user);
            }
            updateTokens(
                    authResult.getAccessToken(),
                    authResult.getRefreshToken(),
                    Instant.now().plusSeconds(authResult.getExpiresIn()));
        }
        Validate.notNull(accessToken, "Access token not set");
        return this.accessToken;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
