package pixivj.model;

import com.google.gson.annotations.SerializedName;
import org.checkerframework.checker.nullness.qual.NonNull;

public enum Restrict {
    @SerializedName("public")
    PUBLIC("public"),
    @SerializedName("private")
    PRIVATE("private");
    private final String strVal;

    Restrict(@NonNull String strVal) {
        this.strVal = strVal;
    }

    @NonNull
    public String string() {
        return strVal;
    }
}
