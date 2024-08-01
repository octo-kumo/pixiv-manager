package pixivj.model;

import com.google.gson.annotations.SerializedName;
import pixivj.util.JsonUtils;

import java.util.Objects;

public class AuthorProfileImageUrls {

    @SerializedName("medium")
    private String medium = null;

    /**
     * Get medium
     *
     * @return medium
     **/
    public String getMedium() {
        return medium;
    }

    public void setMedium(String medium) {
        this.medium = medium;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AuthorProfileImageUrls authorProfileImageUrls = (AuthorProfileImageUrls) o;
        return Objects.equals(this.medium, authorProfileImageUrls.medium);
    }

    @Override
    public int hashCode() {
        return Objects.hash(medium);
    }


    @Override
    public String toString() {
        return JsonUtils.GSON.toJson(this);
    }

}

