package pixivj.model;

import com.google.gson.annotations.SerializedName;
import org.checkerframework.checker.nullness.qual.NonNull;
import pixivj.util.QueryParamConverter;

import java.util.Objects;

public class RecommendedIllustsFilter {

    @SerializedName("filter")
    private pixivj.model.FilterType filter;
    @SerializedName("min_bookmark_id_for_recent_illust")
    private String minBookmarkIdForRecentIllust;
    @SerializedName("max_bookmark_id_for_recommend")
    private String maxBookmarkIdForRecommend;
    @SerializedName("offset")
    private Integer offset;
    @SerializedName("include_ranking_illusts")
    private Boolean includeRankingIllusts;
    @SerializedName("include_privacy_policy")
    private Boolean includePrivacyPolicy;

    @NonNull
    public static RecommendedIllustsFilter fromUrl(@NonNull String url) throws IllegalArgumentException {
        return QueryParamConverter
                .fromQueryParams(url, RecommendedIllustsFilter.class);
    }

    public pixivj.model.FilterType getFilter() {
        return filter;
    }

    public void setFilter(FilterType filter) {
        this.filter = filter;
    }

    public String getMinBookmarkIdForRecentIllust() {
        return minBookmarkIdForRecentIllust;
    }

    public void setMinBookmarkIdForRecentIllust(String minBookmarkIdForRecentIllust) {
        this.minBookmarkIdForRecentIllust = minBookmarkIdForRecentIllust;
    }

    public String getMaxBookmarkIdForRecommend() {
        return maxBookmarkIdForRecommend;
    }

    public void setMaxBookmarkIdForRecommend(String maxBookmarkIdForRecommend) {
        this.maxBookmarkIdForRecommend = maxBookmarkIdForRecommend;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public Boolean getIncludeRankingIllusts() {
        return includeRankingIllusts;
    }

    public void setIncludeRankingIllusts(Boolean includeRankingIllusts) {
        this.includeRankingIllusts = includeRankingIllusts;
    }

    public Boolean getIncludePrivacyPolicy() {
        return includePrivacyPolicy;
    }

    public void setIncludePrivacyPolicy(Boolean includePrivacyPolicy) {
        this.includePrivacyPolicy = includePrivacyPolicy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RecommendedIllustsFilter that = (RecommendedIllustsFilter) o;
        return filter == that.filter &&
                Objects.equals(minBookmarkIdForRecentIllust, that.minBookmarkIdForRecentIllust) &&
                Objects.equals(maxBookmarkIdForRecommend, that.maxBookmarkIdForRecommend) &&
                Objects.equals(offset, that.offset) &&
                Objects.equals(includeRankingIllusts, that.includeRankingIllusts) &&
                Objects.equals(includePrivacyPolicy, that.includePrivacyPolicy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(filter, minBookmarkIdForRecentIllust, maxBookmarkIdForRecommend, offset,
                includeRankingIllusts, includePrivacyPolicy);
    }
}
