package pixivj.model;

import com.google.gson.annotations.SerializedName;
import pixivj.util.JsonUtils;

public enum IllustType {
    @SerializedName("illust")
    ILLUST,
    @SerializedName("ugoira")
    UGOIRA,
    @SerializedName("manga")
    MANGA;

    @Override
    public String toString() {
        return JsonUtils.GSON.toJson(this);
    }
}
