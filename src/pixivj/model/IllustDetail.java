package pixivj.model;

import com.google.gson.annotations.SerializedName;
import pixivj.util.JsonUtils;

import java.util.Objects;

public class IllustDetail {

    @SerializedName("illust")
    private Illustration illust;

    /**
     * Gets illustration.
     *
     * @return Illustration.
     **/
    public Illustration getIllust() {
        return illust;
    }

    /**
     * Sets illustration.
     *
     * @param illust Illustration.
     */
    public void setIllust(Illustration illust) {
        this.illust = illust;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        IllustDetail illustrationDetail = (IllustDetail) o;
        return Objects.equals(this.illust, illustrationDetail.illust);
    }

    @Override
    public int hashCode() {
        return Objects.hash(illust);
    }


    @Override
    public String toString() {
        return JsonUtils.GSON.toJson(this);
    }
}

