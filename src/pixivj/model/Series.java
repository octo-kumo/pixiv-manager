package pixivj.model;

import com.google.gson.annotations.SerializedName;
import pixivj.util.JsonUtils;

import java.util.Objects;

public class Series {

    @SerializedName("id")
    private Long id = null;

    @SerializedName("title")
    private String title = null;

    /**
     * Get id
     *
     * @return id
     **/
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Get title
     *
     * @return title
     **/
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Series series = (Series) o;
        return Objects.equals(this.id, series.id) &&
                Objects.equals(this.title, series.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title);
    }


    @Override
    public String toString() {
        return JsonUtils.GSON.toJson(this);
    }

}