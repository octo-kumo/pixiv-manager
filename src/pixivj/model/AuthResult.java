package pixivj.model;

import com.google.gson.annotations.SerializedName;
import pixivj.util.JsonUtils;

import java.util.Objects;

public class AuthResult {

    @SerializedName("access_token")
    private String accessToken = null;

    @SerializedName("expires_in")
    private long expiresIn = -1;

    @SerializedName("token_type")
    private String tokenType = null;

    @SerializedName("scope")
    private String scope = null;

    @SerializedName("refresh_token")
    private String refreshToken = null;

    @SerializedName("user")
    private pixivj.model.User user = null;

    @SerializedName("device_token")
    private String deviceToken = null;

    /**
     * The issued access token that can be used to access other APIs.
     *
     * @return accessToken
     **/
    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    /**
     * The number of seconds from now that the access token will expire.
     *
     * @return expiresIn
     **/
    public long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
    }

    /**
     * Get tokenType
     *
     * @return tokenType
     **/
    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    /**
     * Get scope
     *
     * @return scope
     **/
    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    /**
     * The token that can be used to get a refreshed access token within expiry time without doing the
     * authentication again.
     *
     * @return refreshToken
     **/
    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    /**
     * Get user
     *
     * @return user
     **/
    public pixivj.model.User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Get deviceToken
     *
     * @return deviceToken
     **/
    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AuthResult authResult = (AuthResult) o;
        return Objects.equals(this.accessToken, authResult.accessToken) &&
                Objects.equals(this.expiresIn, authResult.expiresIn) &&
                Objects.equals(this.tokenType, authResult.tokenType) &&
                Objects.equals(this.scope, authResult.scope) &&
                Objects.equals(this.refreshToken, authResult.refreshToken) &&
                Objects.equals(this.user, authResult.user) &&
                Objects.equals(this.deviceToken, authResult.deviceToken);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accessToken, expiresIn, tokenType, scope, refreshToken, user, deviceToken);
    }


    @Override
    public String toString() {
        return JsonUtils.GSON.toJson(this);
    }

}
