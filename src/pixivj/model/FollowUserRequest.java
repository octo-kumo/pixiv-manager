package pixivj.model;

import com.google.gson.annotations.SerializedName;

public class FollowUserRequest {

    @SerializedName("user_id")
    private Long userID;

    @SerializedName("restrict")
    private pixivj.model.Restrict restrict = pixivj.model.Restrict.PUBLIC;

    public FollowUserRequest(Long userID) {
        this.userID = userID;
    }

    public Long getUserID() {
        return userID;
    }

    public void setUserID(Long userID) {
        this.userID = userID;
    }

    public pixivj.model.Restrict getRestrict() {
        return restrict;
    }

    public void setRestrict(Restrict restrict) {
        this.restrict = restrict;
    }
}
