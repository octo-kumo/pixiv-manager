package pixivj.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * A request sender is a generic class used for sending a HTTP request, and unmarshal the response.
 *
 * @param <E> The type of the exception that should be thrown when a non-2xx response is obtained.
 */
public abstract class RequestSender<E extends Exception> {

    private static final Logger logger = LoggerFactory.getLogger(RequestSender.class);
    private final OkHttpClient httpClient;

    /**
     * Instantiates a new sender with the HTTP client.
     *
     * @param httpClient The HTTP client used for sending requests.
     */
    public RequestSender(@NonNull OkHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    /**
     * Sends a HTTP request, and unmarshal the response. If {@code respType} is {@link Void}, the
     * response body is not unmarshalled, and {@code null} is returned on success.
     *
     * @param request  Request to be sent.
     * @param respType Type of the response to unmarshal on success.
     * @param <T>      Type of the response to unmarshal on success.
     * @return The unmarshalled response.
     * @throws E           A non-2xx response is obtained
     * @throws IOException IO error occurs.
     */
    public <T> T send(@NonNull Request request, @NonNull Class<T> respType) throws E, IOException {
        logger.debug("Sending request {} {}", request.method(), request.url());
        try (Response response = httpClient.newCall(request).execute()) {
            ResponseBody respBody = response.body();
            assert respBody != null;
            String respBodyStr = respBody.string();
            logger.debug("Got response status code {}", response.code());
            // Status code not in [200, 300)
            if (!response.isSuccessful()) {
                throw createExceptionFromRespBody(respBodyStr);
            } else {
                if (respType == Void.class) {
                    return null;
                }
                return JsonUtils.GSON.fromJson(respBodyStr, respType);
            }
        }
    }

    /**
     * Creates an exception to be thrown when a non-2xx response is obtained.
     *
     * @param respStr Response body.
     * @return The built exception.
     */
    protected abstract E createExceptionFromRespBody(@NonNull String respStr);
}
