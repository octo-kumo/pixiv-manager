package pixivj.model;

import com.google.gson.annotations.SerializedName;

public class UserQuery {
    @SerializedName("user_id")
    private long userID;

    public UserQuery(long userID) {
        this.userID = userID;
    }

    public long getUserID() {
        return userID;
    }

    public void setUserID(long userID) {
        this.userID = userID;
    }
}
