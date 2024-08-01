package pixivj.model;

import com.google.gson.annotations.SerializedName;
import pixivj.util.JsonUtils;

import java.util.Objects;

public class PrivacyPolicy {

    @SerializedName("version")
    private String version = null;

    @SerializedName("message")
    private String message = null;

    @SerializedName("url")
    private String url = null;

    /**
     * Get version
     *
     * @return version
     **/
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * Get message
     *
     * @return message
     **/
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Get url
     *
     * @return url
     **/
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PrivacyPolicy privacyPolicy = (PrivacyPolicy) o;
        return Objects.equals(this.version, privacyPolicy.version) &&
                Objects.equals(this.message, privacyPolicy.message) &&
                Objects.equals(this.url, privacyPolicy.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(version, message, url);
    }


    @Override
    public String toString() {
        return JsonUtils.GSON.toJson(this);
    }

}
