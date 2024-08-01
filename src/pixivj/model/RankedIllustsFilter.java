package pixivj.model;

import com.google.gson.annotations.SerializedName;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import pixivj.util.QueryParamConverter;

import java.time.LocalDate;
import java.util.Objects;

public class RankedIllustsFilter {

    @SerializedName("filter")
    private pixivj.model.FilterType filter;
    @SerializedName("mode")
    private pixivj.model.FilterMode mode;
    @SerializedName("date")
    private LocalDate date;
    @SerializedName("offset")
    private Integer offset;

    @NonNull
    public static RankedIllustsFilter fromUrl(@NonNull String url) throws IllegalArgumentException {
        return QueryParamConverter
                .fromQueryParams(url, RankedIllustsFilter.class);
    }

    public pixivj.model.FilterType getFilter() {
        return filter;
    }

    public void setFilter(FilterType filter) {
        this.filter = filter;
    }

    public pixivj.model.FilterMode getMode() {
        return mode;
    }

    public void setMode(FilterMode mode) {
        this.mode = mode;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RankedIllustsFilter that = (RankedIllustsFilter) o;
        return filter == that.filter &&
                mode == that.mode &&
                Objects.equals(date, that.date) &&
                Objects.equals(offset, that.offset);
    }

    @Override
    public int hashCode() {
        return Objects.hash(filter, mode, date, offset);
    }
}
