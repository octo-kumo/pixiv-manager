package pixivj.model;

import com.google.gson.annotations.SerializedName;

public class AuthErrorDetails {

    @SerializedName("system")
    private pixivj.model.ErrorInfo system;

    public pixivj.model.ErrorInfo getSystem() {
        return system;
    }

    public void setSystem(ErrorInfo system) {
        this.system = system;
    }
}
