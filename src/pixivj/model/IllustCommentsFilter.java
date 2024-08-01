package pixivj.model;

import com.google.gson.annotations.SerializedName;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import pixivj.util.QueryParamConverter;

import java.util.Objects;

public class IllustCommentsFilter {
    @SerializedName("illust_id")
    private Long illustId;
    @SerializedName("last_comment_id")
    private Long lastCommentId;

    @NonNull
    public static IllustCommentsFilter fromUrl(@NonNull String url) throws IllegalArgumentException {
        return QueryParamConverter
                .fromQueryParams(url, IllustCommentsFilter.class);
    }

    public Long getIllustId() {
        return illustId;
    }

    public void setIllustId(Long illustId) {
        this.illustId = illustId;
    }

    public Long getLastCommentId() {
        return lastCommentId;
    }

    /**
     * Sets the last comment ID of the last page.
     * For the first page, ignore this field.
     *
     * @param lastCommentId Last comment ID.
     */
    public void setLastCommentId(Long lastCommentId) {
        this.lastCommentId = lastCommentId;
    }

    @Override
    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || other.getClass() != getClass()) {
            return false;
        }
        IllustCommentsFilter that = (IllustCommentsFilter) other;
        return Objects.equals(illustId, that.illustId) &&
                Objects.equals(lastCommentId, that.lastCommentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(illustId, lastCommentId);
    }
}
