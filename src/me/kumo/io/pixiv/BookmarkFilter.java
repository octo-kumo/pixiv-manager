package me.kumo.io.pixiv;

import com.github.hanshsieh.pixivj.util.QueryParamConverter;
import com.google.gson.annotations.SerializedName;
import org.checkerframework.checker.nullness.qual.NonNull;

public class BookmarkFilter {
    //https://www.pixiv.net/ajax/user/43169692/illusts/bookmarks?tag=&offset=0&limit=48&rest=show&lang=en

    @SerializedName("user_id")
    private String userID;
    @SerializedName("offset")
    private Integer offset;
    @SerializedName("limit")
    private Integer limit;
    @SerializedName("restrict")
    private Restrict restrict = Restrict.PUBLIC;

    public enum Restrict {
        @SerializedName("public")
        PUBLIC,
        @SerializedName("private")
        PRIVATE
    }


    @NonNull
    public static BookmarkFilter fromUrl(@NonNull String url) throws IllegalArgumentException {
        return new QueryParamConverter()
                .fromQueryParams(url, BookmarkFilter.class);
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
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

}
