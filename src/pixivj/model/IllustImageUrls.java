package pixivj.model;

import com.google.gson.annotations.SerializedName;
import pixivj.util.JsonUtils;

import java.util.Objects;

public class IllustImageUrls {

    @SerializedName("square_medium")
    private String squareMedium = null;

    @SerializedName("medium")
    private String medium = null;

    @SerializedName("large")
    private String large = null;

    /**
     * Get squareMedium
     *
     * @return squareMedium
     **/
    public String getSquareMedium() {
        return squareMedium;
    }

    public void setSquareMedium(String squareMedium) {
        this.squareMedium = squareMedium;
    }

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

    /**
     * Get large
     *
     * @return large
     **/
    public String getLarge() {
        return large;
    }

    public void setLarge(String large) {
        this.large = large;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        IllustImageUrls illustImageURLs = (IllustImageUrls) o;
        return Objects.equals(this.squareMedium, illustImageURLs.squareMedium) &&
                Objects.equals(this.medium, illustImageURLs.medium) &&
                Objects.equals(this.large, illustImageURLs.large);
    }

    @Override
    public int hashCode() {
        return Objects.hash(squareMedium, medium, large);
    }


    @Override
    public String toString() {
        return JsonUtils.GSON.toJson(this);
    }

}
