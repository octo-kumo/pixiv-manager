package pixivj.model;

import org.checkerframework.checker.nullness.qual.NonNull;

public enum GrantType {
    REFRESH_TOKEN("refresh_token"),
    AUTHORIZATION_CODE("authorization_code");
    private final String type;

    GrantType(@NonNull String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type;
    }
}
