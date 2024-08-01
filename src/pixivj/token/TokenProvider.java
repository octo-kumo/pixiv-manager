package pixivj.token;

import org.checkerframework.checker.nullness.qual.NonNull;
import pixivj.exception.AuthException;

import java.io.Closeable;
import java.io.IOException;

/**
 * A provider for obtaining the access token for authentication.
 */
public interface TokenProvider extends Closeable {

    /**
     * Gets and possibly refresh the access token. Whether the access token should be refreshed
     * immediately or by a background is decided by the implementation. If access token is not
     * available or is expired, an exception should be thrown instead of returning null.
     *
     * @return Access token. Always non-null.
     * @throws AuthException         Authentication error.
     * @throws IOException           IO error.
     * @throws IllegalStateException The current state doesn't allow getting an access token. For
     *                               example, the client or the access token is never set into this
     *                               token provider or the token has expired.
     */
    @NonNull
    String getAccessToken() throws AuthException, IOException;
}
