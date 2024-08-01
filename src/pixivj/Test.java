package pixivj;

import pixivj.model.RecommendedIllustsFilter;
import pixivj.util.QueryParamConverter;

public class Test {
    public static void main(String[] args) {
        RecommendedIllustsFilter filter = QueryParamConverter.fromQueryParams("https://app-api.pixiv.net/v2/illust/comments?include_ranking_illusts=true&filter=for_ios",
                RecommendedIllustsFilter.class);
        System.out.printf("getIncludeRankingIllusts: %s\n", filter.getIncludeRankingIllusts());
        System.out.printf("getFilter: %s\n", filter.getFilter());
    }
}
