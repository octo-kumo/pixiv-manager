package pixivj.model;

import com.google.gson.annotations.SerializedName;
import org.checkerframework.checker.nullness.qual.NonNull;
import pixivj.util.QueryParamConverter;

public class V2Filter {
    //https://www.pixiv.net/ajax/user/43169692/illusts/bookmarks?tag=&offset=0&limit=48&rest=show&lang=en

    @SerializedName("user_id")
    private Long userID;
    @SerializedName("offset")
    private Integer offset;
    @SerializedName("limit")
    private Integer limit;
    @SerializedName("restrict")
    private Restrict restrict = Restrict.PUBLIC;
    @SerializedName("mode")
    private Mode mode = Mode.ALL;

    public V2Filter() {
    }

    public V2Filter(Long userId) {
        setUserID(userId);
    }

    @NonNull
    public static V2Filter fromUrl(@NonNull String url) throws IllegalArgumentException {
        return QueryParamConverter.fromQueryParams(url, V2Filter.class);
    }

    public Long getUserID() {
        return userID;
    }

    public void setUserID(Long userID) {
        this.userID = userID;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Restrict getRestrict() {
        return restrict;
    }

    public void setRestrict(Restrict restrict) {
        this.restrict = restrict;
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public enum Restrict {
        @SerializedName("public")
        PUBLIC,
        @SerializedName("private")
        PRIVATE,
        @SerializedName("all")
        ALL
    }

    public enum Mode {
        @SerializedName("all")
        ALL
    }

}
