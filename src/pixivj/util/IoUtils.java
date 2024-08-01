package pixivj.util;

import okhttp3.OkHttpClient;
import org.checkerframework.checker.nullness.qual.NonNull;

public class IoUtils {

    /**
     * Closes the given {@link OkHttpClient} and releases the resources.
     * Active connection and requests won't be affected.
     *
     * @param httpClient HTTP client to be closed.
     */
    public static void close(@NonNull OkHttpClient httpClient) {
        httpClient.dispatcher().executorService().shutdown();
        httpClient.connectionPool().evictAll();
    }
}
