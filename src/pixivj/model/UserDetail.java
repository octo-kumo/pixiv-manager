package pixivj.model;

import com.google.gson.annotations.SerializedName;

public class UserDetail {

    @SerializedName("user")
    private Author user = null;
    @SerializedName("profile")
    private Profile profile = null;
    @SerializedName("profile_publicity")
    private ProfilePublicity profilePublicity = null;
    @SerializedName("workspace")
    private Workspace workspace = null;

    public Author getUser() {
        return user;
    }

    public void setUser(Author user) {
        this.user = user;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public pixivj.model.ProfilePublicity getProfilePublicity() {
        return profilePublicity;
    }

    public void setProfilePublicity(pixivj.model.ProfilePublicity profilePublicity) {
        this.profilePublicity = profilePublicity;
    }

    public Workspace getWorkspace() {
        return workspace;
    }

    public void setWorkspace(Workspace workspace) {
        this.workspace = workspace;
    }
}
