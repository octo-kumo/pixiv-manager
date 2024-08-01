package pixivj.exception;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class PixivException extends Exception {

    public PixivException(@NonNull String message) {
        this(message, null);
    }

    public PixivException(@NonNull String message, @Nullable Throwable cause) {
        super(message, cause);
    }
}
