package pixivj.model;

import com.google.gson.annotations.SerializedName;
import pixivj.util.JsonUtils;

import java.util.Objects;

public class ProfileImageUrls {

    @SerializedName("px_16x16")
    private String px16x16 = null;

    @SerializedName("px_50x50")
    private String px50x50 = null;

    @SerializedName("px_170x170")
    private String px170x170 = null;

    /**
     * URL to the user&#x27;s profile image in 16x16 pixels.
     *
     * @return px16x16
     **/
    public String getPx16x16() {
        return px16x16;
    }

    public void setPx16x16(String px16x16) {
        this.px16x16 = px16x16;
    }

    /**
     * URL to the user&#x27;s profile image in 50x50 pixels.
     *
     * @return px50x50
     **/
    public String getPx50x50() {
        return px50x50;
    }

    public void setPx50x50(String px50x50) {
        this.px50x50 = px50x50;
    }

    /**
     * URL to the user&#x27;s profile image in 170x170 pixels.
     *
     * @return px170x170
     **/
    public String getPx170x170() {
        return px170x170;
    }

    public void setPx170x170(String px170x170) {
        this.px170x170 = px170x170;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ProfileImageUrls profileImageUrls = (ProfileImageUrls) o;
        return Objects.equals(this.px16x16, profileImageUrls.px16x16) &&
                Objects.equals(this.px50x50, profileImageUrls.px50x50) &&
                Objects.equals(this.px170x170, profileImageUrls.px170x170);
    }

    @Override
    public int hashCode() {
        return Objects.hash(px16x16, px50x50, px170x170);
    }


    @Override
    public String toString() {
        return JsonUtils.GSON.toJson(this);
    }
}
