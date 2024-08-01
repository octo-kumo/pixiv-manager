package pixivj.token;

import org.apache.commons.lang3.Validate;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pixivj.exception.AuthException;
import pixivj.model.AuthResult;
import pixivj.model.Credential;
import pixivj.model.GrantType;
import pixivj.oauth.PixivOAuthClient;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * A token provider that refresh the access token in a background thread.
 * A delay percentage is used decide when to refresh the token. For example,
 * if 0.1 is given, and the remaining life time of the current token is 100 seconds, then it will
 * try to refresh the token after 10 seconds.
 * Notice that the given {@link PixivOAuthClient} won't be closed when the token provider is closed.
 * This class is thread-safe.
 */
public class ThreadedTokenRefresher implements TokenRefresher {

    private static final Logger logger = LoggerFactory.getLogger(ThreadedTokenRefresher.class);
    private final double delayPercentage;
    private final Duration retryDelay;
    private final PixivOAuthClient authClient;
    private final ScheduledExecutorService executor = Executors
            .newSingleThreadScheduledExecutor((Runnable runnable) -> {
                Thread thread = new Thread(runnable);
                thread.setDaemon(true);
                return thread;
            });
    private String accessToken;
    private String refreshToken;
    private Instant expiryTime;
    private ScheduledFuture<?> refreshFuture;

    /**
     * Creates a new instance that will refresh the token when the life of the current token has
     * exceeded the given percentage. For example, if 0.1 is given, at the remaining life time of the
     * current token is 100 seconds, then it will try to refresh the token after 10 seconds. Notice
     * that the given {@link PixivOAuthClient} won't be closed when the token provider is closed.
     *
     * @param builder The builder.
     */
    private ThreadedTokenRefresher(@NonNull Builder builder) {
        Validate.notNull(builder.authClient, "OAuth client not set");
        Validate.isTrue(builder.delayPercentage < 1.0 && builder.delayPercentage > 0.0,
                "Invalid delay percentage");
        Validate.notNull(builder.retryDelay, "Retry delay not set");
        this.authClient = builder.authClient;
        this.delayPercentage = builder.delayPercentage;
        this.retryDelay = builder.retryDelay;
    }

    @Override
    public synchronized void updateTokens(
            @NonNull String accessToken,
            @NonNull String refreshToken,
            @NonNull Instant expiryTime
    ) {
        Validate.notNull(accessToken, "access token cannot be null");
        Validate.notNull(refreshToken, "refresh token cannot be null");
        Validate.notNull(expiryTime, "expiry time cannot be null");
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiryTime = expiryTime;
        long expiryDelayMs = Math.max(0, expiryTime.toEpochMilli() - Instant.now().toEpochMilli());
        long refreshDelayMs = (long) Math.floor(expiryDelayMs * delayPercentage);
        replaceRefreshFuture(
                executor.schedule(this::refreshTokens, refreshDelayMs, TimeUnit.MILLISECONDS));
    }

    @Override
    public synchronized @NonNull String getRefreshToken() {
        if (refreshToken == null) {
            throw new IllegalStateException("Refresh token is not set");
        }
        return refreshToken;
    }

    private void refreshTokens() {
        try {
            Credential credential = new Credential();
            credential.setRefreshToken(this.refreshToken);
            credential.setGrantType(GrantType.REFRESH_TOKEN);
            AuthResult authResult = authClient.authenticate(credential);
            logger.debug("Tokens are refreshed");
            updateTokens(authResult.getAccessToken(), authResult.getRefreshToken(),
                    Instant.now().plusSeconds(authResult.getExpiresIn()));
        } catch (Exception ex) {
            logger.error("Failed to refresh token: ", ex);
            replaceRefreshFuture(
                    executor.schedule(this::refreshTokens, retryDelay.toMillis(), TimeUnit.MILLISECONDS));
        }
    }

    private synchronized void replaceRefreshFuture(@NonNull ScheduledFuture<?> newRefreshFuture) {
        if (refreshFuture != null) {
            refreshFuture.cancel(false);
        }
        refreshFuture = newRefreshFuture;
    }

    @Override
    public void close() {
        executor.shutdownNow();
    }

    @Override
    public synchronized @NonNull String getAccessToken() throws AuthException, IOException {
        if (accessToken == null) {
            throw new IllegalStateException("Access token is not set");
        }
        if (expiryTime.isBefore(Instant.now())) {
            throw new IllegalStateException("Token has been expired at " + expiryTime);
        }
        return accessToken;
    }

    /**
     * A builder for constructing a {@link ThreadedTokenRefresher}.
     */
    public static class Builder {
        public static final double DEFAULT_DELAY_PERCENTAGE = 0.5;
        public static final Duration DEFAULT_RETRY_DELAY = Duration.ofSeconds(3);
        private PixivOAuthClient authClient;
        private double delayPercentage = DEFAULT_DELAY_PERCENTAGE;
        private Duration retryDelay = DEFAULT_RETRY_DELAY;

        @NonNull
        public Builder setAuthClient(@NonNull PixivOAuthClient authClient) {
            this.authClient = authClient;
            return this;
        }

        @NonNull
        public Builder setDelayPercentage(double delayPercentage) {
            this.delayPercentage = delayPercentage;
            return this;
        }

        @NonNull
        public Builder setRetryDelay(@NonNull Duration retryDelay) {
            this.retryDelay = retryDelay;
            return this;
        }

        @NonNull
        public ThreadedTokenRefresher build() {
            return new ThreadedTokenRefresher(this);
        }
    }
}
