package pixivj.model;

import com.google.gson.annotations.SerializedName;
import pixivj.util.JsonUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RecommendedIllusts {

    @SerializedName("illusts")
    private List<Illustration> illusts = new ArrayList<>();

    @SerializedName("ranking_illusts")
    private List<Illustration> rankingIllusts = null;

    @SerializedName("contest_exists")
    private Boolean contestExists = null;

    @SerializedName("privacy_policy")
    private PrivacyPolicy privacyPolicy = null;

    @SerializedName("next_url")
    private String nextUrl = null;

    /**
     * Get illusts
     *
     * @return illusts
     **/
    public List<Illustration> getIllusts() {
        return illusts;
    }

    public void setIllusts(List<Illustration> illusts) {
        this.illusts = illusts;
    }

    /**
     * Get rankingIllusts
     *
     * @return rankingIllusts
     **/
    public List<Illustration> getRankingIllusts() {
        return rankingIllusts;
    }

    public void setRankingIllusts(List<Illustration> rankingIllusts) {
        this.rankingIllusts = rankingIllusts;
    }

    /**
     * Get contestExists
     *
     * @return contestExists
     **/
    public Boolean isContestExists() {
        return contestExists;
    }

    public void setContestExists(Boolean contestExists) {
        this.contestExists = contestExists;
    }

    /**
     * Get privacyPolicy
     *
     * @return privacyPolicy
     **/
    public PrivacyPolicy getPrivacyPolicy() {
        return privacyPolicy;
    }

    public void setPrivacyPolicy(PrivacyPolicy privacyPolicy) {
        this.privacyPolicy = privacyPolicy;
    }

    /**
     * The next URL that can be used for querying the next page, if any.
     *
     * @return nextUrl
     **/
    public String getNextUrl() {
        return nextUrl;
    }

    public void setNextUrl(String nextUrl) {
        this.nextUrl = nextUrl;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RecommendedIllusts recommendIllustrations = (RecommendedIllusts) o;
        return Objects.equals(this.illusts, recommendIllustrations.illusts) &&
                Objects.equals(this.rankingIllusts, recommendIllustrations.rankingIllusts) &&
                Objects.equals(this.contestExists, recommendIllustrations.contestExists) &&
                Objects.equals(this.privacyPolicy, recommendIllustrations.privacyPolicy) &&
                Objects.equals(this.nextUrl, recommendIllustrations.nextUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(illusts, rankingIllusts, contestExists, privacyPolicy, nextUrl);
    }


    @Override
    public String toString() {
        return JsonUtils.GSON.toJson(this);
    }

}