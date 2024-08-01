package pixivj.oauth;

import okhttp3.OkHttpClient;
import org.checkerframework.checker.nullness.qual.NonNull;
import pixivj.exception.AuthException;
import pixivj.model.AuthError;
import pixivj.util.JsonUtils;
import pixivj.util.RequestSender;

public class AuthRequestSender extends RequestSender<AuthException> {

    public AuthRequestSender(@NonNull OkHttpClient httpClient) {
        super(httpClient);
    }

    @Override
    protected AuthException createExceptionFromRespBody(@NonNull String respStr) {
        AuthError authError = JsonUtils.GSON.fromJson(respStr, AuthError.class);
        return new AuthException(authError);
    }
}
