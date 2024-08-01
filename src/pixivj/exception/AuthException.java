package pixivj.exception;

import org.checkerframework.checker.nullness.qual.NonNull;
import pixivj.model.AuthError;

public class AuthException extends PixivException {

    private final AuthError error;

    public AuthException(@NonNull AuthError authError) {
        super(authError.toString());
        this.error = authError;
    }

    @NonNull
    public AuthError getError() {
        return error;
    }

}
