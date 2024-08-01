package pixivj.model;

import com.google.gson.annotations.SerializedName;
import pixivj.util.JsonUtils;

import java.util.Objects;

public class MetaPage {

    @SerializedName("image_urls")
    private pixivj.model.MetaPageImageUrls imageUrls = null;

    /**
     * Get imageUrls
     *
     * @return imageUrls
     **/
    public pixivj.model.MetaPageImageUrls getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(MetaPageImageUrls imageUrls) {
        this.imageUrls = imageUrls;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MetaPage metaPage = (MetaPage) o;
        return Objects.equals(this.imageUrls, metaPage.imageUrls);
    }

    @Override
    public int hashCode() {
        return Objects.hash(imageUrls);
    }


    @Override
    public String toString() {
        return JsonUtils.GSON.toJson(this);
    }

}
