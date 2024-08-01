package pixivj.model;

import com.google.gson.annotations.SerializedName;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import pixivj.util.QueryParamConverter;

import java.util.Objects;

public class SearchedIllustsFilter {
    @SerializedName("filter")
    private FilterType filter;
    @SerializedName("include_translated_tag_results")
    private Boolean includeTranslatedTagResults;
    @SerializedName("merge_plain_keyword_results")
    private Boolean mergePlainKeywordResults;
    @SerializedName("word")
    private String word;
    @SerializedName("sort")
    private pixivj.model.SortOption sort;
    @SerializedName("offset")
    private Integer offset;
    @SerializedName("search_target")
    private SearchTarget searchTarget;

    public SearchedIllustsFilter() {
    }

    public SearchedIllustsFilter(String word) {
        setWord(word);
    }

    @NonNull
    public static SearchedIllustsFilter fromUrl(@NonNull String url) throws IllegalArgumentException {
        return QueryParamConverter.fromQueryParams(url, SearchedIllustsFilter.class);
    }

    public static SearchedIllustsFilter get(String word, @Nullable Boolean r18, @Nullable BookmarkCount bookmarks) {
        SearchedIllustsFilter filter = new SearchedIllustsFilter();
        if (r18 != null) word += " " + (r18 ? "" : "-") + "R-18";
        if (bookmarks != null) word += " " + bookmarks;
        filter.setWord(word);
        return filter;
    }

    public FilterType getFilter() {
        return filter;
    }

    public void setFilter(FilterType filter) {
        this.filter = filter;
    }

    public Boolean getIncludeTranslatedTagResults() {
        return includeTranslatedTagResults;
    }

    public void setIncludeTranslatedTagResults(Boolean includeTranslatedTagResults) {
        this.includeTranslatedTagResults = includeTranslatedTagResults;
    }

    public Boolean getMergePlainKeywordResults() {
        return mergePlainKeywordResults;
    }

    public void setMergePlainKeywordResults(Boolean mergePlainKeywordResults) {
        this.mergePlainKeywordResults = mergePlainKeywordResults;
    }

    public String getWord() {
        return word;
    }

    /**
     * Sets the word to search. It may contain several words separated by spaces.
     *
     * @param word The word to search.
     */
    public void setWord(String word) {
        this.word = word;
    }

    public pixivj.model.SortOption getSort() {
        return sort;
    }

    /**
     * Sets the sorting criteria.
     *
     * @param sort Sorting criteria.
     */
    public void setSort(SortOption sort) {
        this.sort = sort;
    }

    public Integer getOffset() {
        return offset;
    }

    /**
     * Sets the offset. Default 0.
     *
     * @param offset Offset.
     */
    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public SearchTarget getSearchTarget() {
        return searchTarget;
    }

    public void setSearchTarget(SearchTarget searchTarget) {
        this.searchTarget = searchTarget;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SearchedIllustsFilter that = (SearchedIllustsFilter) o;
        return filter == that.filter
                && Objects.equals(includeTranslatedTagResults, that.includeTranslatedTagResults)
                && Objects.equals(mergePlainKeywordResults, that.mergePlainKeywordResults)
                && Objects.equals(word, that.word)
                && sort == that.sort
                && Objects.equals(offset, that.offset)
                && searchTarget == that.searchTarget;
    }

    @Override
    public int hashCode() {
        return Objects.hash(filter, includeTranslatedTagResults, mergePlainKeywordResults, word, sort, offset, searchTarget);
    }

    public enum BookmarkCount {
        B100("100users入り"),
        B1000("1000users入り"),
        B10000("10000users入り"),
        B100000("100000users入り"),
        B3000("3000users入り"),
        B30000("30000users入り"),
        B50("50users入り"),
        B500("500users入り"),
        B5000("5000users入り"),
        B50000("50000users入り");

        private final String s;

        BookmarkCount(String s) {
            this.s = s;
        }

        @Override
        public String toString() {
            return s;
        }
    }
}
