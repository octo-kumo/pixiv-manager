package pixivj.model;

import com.google.gson.annotations.SerializedName;
import pixivj.util.JsonUtils;

import java.util.Objects;

public class Tag {

    @SerializedName("name")
    private String name = null;

    @SerializedName("translated_name")
    private String translatedName = null;

    /**
     * Get name
     *
     * @return name
     **/
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get translatedName
     *
     * @return translatedName
     **/
    public String getTranslatedName() {
        return translatedName;
    }

    public void setTranslatedName(String translatedName) {
        this.translatedName = translatedName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Tag tag = (Tag) o;
        return Objects.equals(this.name, tag.name) &&
                Objects.equals(this.translatedName, tag.translatedName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, translatedName);
    }


    @Override
    public String toString() {
        return JsonUtils.GSON.toJson(this);
    }

}

