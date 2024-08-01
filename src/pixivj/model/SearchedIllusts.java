package pixivj.model;

import com.google.gson.annotations.SerializedName;
import pixivj.util.JsonUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * The illustrations found by search.
 */
public class SearchedIllusts {

    @SerializedName("illusts")
    private List<pixivj.model.Illustration> illusts = new ArrayList<>();

    @SerializedName("next_url")
    private String nextUrl = null;

    @SerializedName("search_span_limit")
    private Integer searchSpanLimit;

    /**
     * Get illustrations.
     *
     * @return Illustrations.
     **/
    public List<pixivj.model.Illustration> getIllusts() {
        return illusts;
    }

    /**
     * Sets illustrations.
     *
     * @param illusts Illustrations.
     */
    public void setIllusts(List<Illustration> illusts) {
        this.illusts = illusts;
    }

    /**
     * The next URL that can be used for querying the next page, if any.
     *
     * @return Next URL.
     **/
    public String getNextUrl() {
        return nextUrl;
    }

    /**
     * Sets next URL that can be used for querying the next page, if any.
     *
     * @param nextUrl Next URL.
     */
    public void setNextUrl(String nextUrl) {
        this.nextUrl = nextUrl;
    }

    public Integer getSearchSpanLimit() {
        return searchSpanLimit;
    }

    public void setSearchSpanLimit(Integer searchSpanLimit) {
        this.searchSpanLimit = searchSpanLimit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SearchedIllusts that = (SearchedIllusts) o;
        return Objects.equals(illusts, that.illusts) &&
                Objects.equals(nextUrl, that.nextUrl) &&
                Objects.equals(searchSpanLimit, that.searchSpanLimit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                illusts,
                nextUrl,
                searchSpanLimit);
    }

    @Override
    public String toString() {
        return JsonUtils.GSON.toJson(this);
    }
}