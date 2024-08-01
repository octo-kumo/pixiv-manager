package pixivj.oauth;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.apache.commons.lang3.Validate;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import pixivj.exception.AuthException;
import pixivj.http.Header;
import pixivj.model.AuthResult;
import pixivj.model.Credential;
import pixivj.util.HexUtils;
import pixivj.util.IoUtils;

import java.io.Closeable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class PixivOAuthClient implements Closeable {

    private final String userAgent;
    private final HttpUrl baseUrl;
    private final OkHttpClient httpClient;
    private final AuthRequestSender requestSender;
    private PixivOAuthClient(@NonNull Builder builder) {
        Validate.notNull(builder.baseUrl, "Base URL not set");
        Validate.notNull(builder.userAgent, "User agent not set");
        this.userAgent = builder.userAgent;
        this.baseUrl = HttpUrl.parse(builder.baseUrl);
        this.httpClient = new OkHttpClient.Builder()
                .followRedirects(false)
                .followSslRedirects(false)
                .build();
        this.requestSender = new AuthRequestSender(httpClient);
    }

    private void addFormFieldIfNotNull(FormBody.@NonNull Builder form, @NonNull String fieldName, @Nullable Object value) {
        if (value != null) {
            form.add(fieldName, value.toString());
        }
    }

    @NonNull
    public AuthResult authenticate(@NonNull Credential credential) throws AuthException, IOException {
        HttpUrl url = baseUrl.newBuilder()
                .addEncodedPathSegments("auth/token")
                .build();
        FormBody.Builder bodyBuilder = new FormBody.Builder();
        addFormFieldIfNotNull(bodyBuilder, "client_id", credential.getClientId());
        addFormFieldIfNotNull(bodyBuilder, "client_secret", credential.getClientSecret());
        addFormFieldIfNotNull(bodyBuilder, "grant_type", credential.getGrantType());
        addFormFieldIfNotNull(bodyBuilder, "refresh_token", credential.getRefreshToken());
        addFormFieldIfNotNull(bodyBuilder, "code", credential.getCode());
        addFormFieldIfNotNull(bodyBuilder, "code_verifier", credential.getCodeVerifier());
        addFormFieldIfNotNull(bodyBuilder, "redirect_uri", credential.getRedirectUri());
        addFormFieldIfNotNull(bodyBuilder, "include_policy", credential.isIncludePolicy());
        FormBody formBody = bodyBuilder.build();
        ZonedDateTime zonedDateTime = ZonedDateTime
                .ofInstant(Instant.ofEpochSecond(Instant.now().getEpochSecond()), ZoneOffset.UTC);
        String timeStr = DateTimeFormatter.ISO_ZONED_DATE_TIME.format(zonedDateTime);
        String signature = timeStr + credential.getHashSecret();
        String clientHash = md5Hex(signature.getBytes(StandardCharsets.UTF_8));
        Request request = new Request.Builder()
                .url(url)
                .header("User-Agent", userAgent)
                .header("X-Client-Time", timeStr)
                .header("X-Client-Hash", clientHash)
                .post(formBody)
                .build();
        return requestSender.send(request, AuthResult.class);
    }

    @NonNull
    private String md5Hex(byte[] data) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(data);
            return HexUtils.bytesToHex(md.digest());
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void close() {
        IoUtils.close(this.httpClient);
    }

    public static class Builder {

        private String baseUrl = "https://oauth.secure.pixiv.net";
        private String userAgent = Header.USER_AGENT_ANDROID;

        @NonNull
        public Builder setBaseUrl(@NonNull String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        @NonNull
        public Builder setUserAgent(@NonNull String userAgent) {
            this.userAgent = userAgent;
            return this;
        }

        @NonNull
        public PixivOAuthClient build() {
            return new PixivOAuthClient(this);
        }
    }
}
