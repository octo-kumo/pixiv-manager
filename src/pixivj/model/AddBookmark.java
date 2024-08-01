package pixivj.model;

import com.google.gson.annotations.SerializedName;

public class AddBookmark {
    @SerializedName("illust_id")
    private Long illustId;
    @SerializedName("restrict")
    private pixivj.model.Restrict restrict;

    public Long getIllustId() {
        return illustId;
    }

    public void setIllustId(Long illustId) {
        this.illustId = illustId;
    }

    public pixivj.model.Restrict getRestrict() {
        return restrict;
    }

    /**
     * Sets who can see the bookmark.
     *
     * @param restrict Restrict string.
     */
    public void setRestrict(Restrict restrict) {
        this.restrict = restrict;
    }
}
