package pixivj.token;

import org.checkerframework.checker.nullness.qual.NonNull;

public class FixedTokenProvider implements TokenProvider {
    private final String accessToken;

    /**
     * Constructs a new instance with the given access token.
     *
     * @param accessToken Access token.
     */
    public FixedTokenProvider(@NonNull String accessToken) {
        this.accessToken = accessToken;
    }

    @Override
    public @NonNull String getAccessToken() {
        return accessToken;
    }

    @Override
    public void close() {
    }
}
