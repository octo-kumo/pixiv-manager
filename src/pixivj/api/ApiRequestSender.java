package pixivj.api;

import okhttp3.OkHttpClient;
import org.checkerframework.checker.nullness.qual.NonNull;
import pixivj.exception.APIException;
import pixivj.model.APIError;
import pixivj.util.JsonUtils;
import pixivj.util.RequestSender;

public class ApiRequestSender extends RequestSender<APIException> {

    public ApiRequestSender(@NonNull OkHttpClient httpClient) {
        super(httpClient);
    }

    @Override
    protected APIException createExceptionFromRespBody(@NonNull String respStr) {
        APIError error = JsonUtils.GSON.fromJson(respStr, APIError.class);
        return new APIException(error);
    }
}
