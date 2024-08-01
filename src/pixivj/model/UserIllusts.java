package pixivj.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class UserIllusts {

    @SerializedName("user")
    private User user;
    @SerializedName("illusts")
    private List<Illustration> illusts = new ArrayList<>();
    @SerializedName("next_url")
    private String nextUrl = null;

    public pixivj.model.User getUser() {
        return user;
    }

    public void setUser(pixivj.model.User user) {
        this.user = user;
    }

    public List<Illustration> getIllusts() {
        return illusts;
    }

    public void setIllusts(List<Illustration> illusts) {
        this.illusts = illusts;
    }

    public String getNextUrl() {
        return nextUrl;
    }

    public void setNextUrl(String nextUrl) {
        this.nextUrl = nextUrl;
    }
}
