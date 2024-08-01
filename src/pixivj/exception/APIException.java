package pixivj.exception;

import org.checkerframework.checker.nullness.qual.NonNull;
import pixivj.model.APIError;

public class APIException extends PixivException {

    private final APIError error;

    public APIException(@NonNull APIError error) {
        super(error.toString());
        this.error = error;
    }

    @NonNull
    public APIError getError() {
        return error;
    }
}
