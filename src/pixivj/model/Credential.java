package pixivj.model;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Objects;

public class Credential {
    public static final String CLIENT_ID_MOBILE = "MOBrBDS8blbauoSck0ZfDbtuzpyT";
    public static final String CLIENT_SECRET_MOBILE = "lsACyCD94FhDUtGTXi3QzcFE2uU1hqtDaKeqrdwj";
    public static final String DEFAULT_HASH_SECRET = "28c1fdd170a5204386cb1313c7077b34f83e4aaf4aa829ce78c231e05b0bae2c";
    public static final String REDIRECT_URI_AUTH_CALLBACK = "https://app-api.pixiv.net/web/v1/users/auth/pixiv/callback";
    private String clientId = CLIENT_ID_MOBILE;
    private String clientSecret = CLIENT_SECRET_MOBILE;
    private pixivj.model.GrantType grantType = pixivj.model.GrantType.REFRESH_TOKEN;
    private String refreshToken;
    private String codeVerifier;
    private String code;
    private String redirectUri;
    private Boolean includePolicy;
    private String hashSecret = DEFAULT_HASH_SECRET;

    /**
     * Gets the OAuth client ID.
     *
     * @return Client ID.
     */
    public String getClientId() {
        return clientId;
    }

    /**
     * Sets the OAuth client ID. The default value is for acting as the official mobile app.
     *
     * @param clientId Client ID.
     */
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    /**
     * Gets the code verifier.
     * This is used when the "grantType" is {@link pixivj.model.GrantType#AUTHORIZATION_CODE}.
     *
     * @return Code verifier.
     */
    public String getCodeVerifier() {
        return codeVerifier;
    }

    /**
     * Sets code verifier.
     * This is used when the "grantType" is {@link pixivj.model.GrantType#AUTHORIZATION_CODE}.
     *
     * @param codeVerifier Code verifier.
     */
    public void setCodeVerifier(String codeVerifier) {
        this.codeVerifier = codeVerifier;
    }

    /**
     * Gets authorization code.
     * This is used when the "grantType" is {@link pixivj.model.GrantType#AUTHORIZATION_CODE}.
     *
     * @return Authorization code.
     */
    public String getCode() {
        return code;
    }

    /**
     * Sets authorization code.
     * This is used when the "grantType" is {@link pixivj.model.GrantType#AUTHORIZATION_CODE}.
     *
     * @param code Authorization code.
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * Gets the redirect URI.
     *
     * @return Redirect URI.
     */
    public String getRedirectUri() {
        return redirectUri;
    }

    /**
     * Sets the redirect URI.
     *
     * @param redirectUri Redirect URI.
     */
    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }

    /**
     * Whether to include policy in the response.
     *
     * @return Include policy or not.
     */
    public Boolean isIncludePolicy() {
        return includePolicy;
    }

    /**
     * Sets whether to include policy in the response.
     *
     * @param includePolicy Include policy.
     */
    public void setIncludePolicy(Boolean includePolicy) {
        this.includePolicy = includePolicy;
    }

    /**
     * Gets the OAuth client secret.
     *
     * @return Client secret.
     */
    public String getClientSecret() {
        return clientSecret;
    }

    /***
     * Sets OAuth client secret.
     * The default value is for acting as the official mobile app.
     * @param clientSecret Client secret.
     */
    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    /**
     * Gets grant type.
     *
     * @return Grant type.
     */
    public pixivj.model.GrantType getGrantType() {
        return grantType;
    }

    /**
     * Sets grant type.
     *
     * @param grantType Grant type.
     */
    public void setGrantType(GrantType grantType) {
        this.grantType = grantType;
    }

    /**
     * Gets refresh token.
     *
     * @return Refresh token.
     */
    public String getRefreshToken() {
        return refreshToken;
    }

    /**
     * Sets refresh token. It's required when the grant type is "refresh_token".
     *
     * @param refreshToken Refresh token.
     */
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    /**
     * Gets hash secret used for generating the client hash.
     *
     * @return Hash secret.
     */
    public String getHashSecret() {
        return hashSecret;
    }

    /**
     * Sets the hash secret used for generating the client hash.
     *
     * @param hashSecret Hash secret.
     */
    public void setHashSecret(String hashSecret) {
        this.hashSecret = hashSecret;
    }

    @Override
    public boolean equals(@Nullable Object other) {
        if (other == null || other.getClass() != getClass()) {
            return false;
        }
        Credential that = (Credential) other;
        return Objects.equals(clientId, that.clientId) &&
                Objects.equals(clientSecret, that.clientSecret) &&
                Objects.equals(grantType, that.grantType) &&
                Objects.equals(refreshToken, that.refreshToken) &&
                Objects.equals(hashSecret, that.hashSecret) &&
                Objects.equals(codeVerifier, that.codeVerifier) &&
                Objects.equals(code, that.code) &&
                Objects.equals(redirectUri, that.redirectUri) &&
                Objects.equals(includePolicy, that.includePolicy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                clientId,
                clientSecret,
                grantType,
                refreshToken,
                hashSecret,
                codeVerifier,
                code,
                redirectUri,
                includePolicy
        );
    }
}
