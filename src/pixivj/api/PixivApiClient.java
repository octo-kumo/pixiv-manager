package pixivj.api;

import okhttp3.*;
import org.apache.commons.lang3.Validate;
import org.checkerframework.checker.nullness.qual.NonNull;
import pixivj.exception.AuthException;
import pixivj.exception.PixivException;
import pixivj.http.Header;
import pixivj.model.*;
import pixivj.token.TokenProvider;
import pixivj.util.FormBodyConverter;
import pixivj.util.IoUtils;
import pixivj.util.QueryParamConverter;

import java.io.Closeable;
import java.io.IOException;


/**
 * Client for Pixiv API.
 */
public class PixivApiClient implements Closeable {

    public final HttpUrl baseUrl;
    public final String userAgent;
    public final OkHttpClient httpClient;
    public final TokenProvider tokenProvider;
    public final pixivj.api.ApiRequestSender requestSender;
    private final Headers headers = new Headers.Builder()
            .add("App-OS", "android")
            .add("Accept-Language", "en-us")
            .add("App-OS-Version", "9.0")
            .add("App-Version", "5.0.234")
            .build();
    public PixivApiClient(@NonNull Builder builder) {
        Validate.notNull(builder.tokenProvider, "Token provider is not set");
        Validate.notNull(builder.baseUrl, "Base URL not set");
        Validate.notNull(builder.userAgent, "User agent not set");
        this.baseUrl = HttpUrl.parse(builder.baseUrl);
        this.userAgent = builder.userAgent;
        this.tokenProvider = builder.tokenProvider;
        this.httpClient = new OkHttpClient.Builder()
                .followRedirects(false)
                .followSslRedirects(false)
                .build();
        this.requestSender = new ApiRequestSender(httpClient);
    }

    /**
     * Grabs a list of ranked illustrations.
     *
     * @param filter the filter to use.
     * @return Ranked Illustrations.
     * @throws PixivException PixivException Error
     * @throws IOException    IOException Error
     */
    @NonNull
    public RankedIllusts getRankedIllusts(@NonNull RankedIllustsFilter filter)
            throws PixivException, IOException {
        return sendGetRequest("v1/illust/ranking", filter, RankedIllusts.class);
    }

    /**
     * Grabs a list of recommended illustrations.
     *
     * @param filter the filter to use.
     * @return Recommended Illustrations.
     * @throws PixivException PixivException Error
     * @throws IOException    IOException Error
     */
    @NonNull
    public RecommendedIllusts getRecommendedIllusts(@NonNull RecommendedIllustsFilter filter)
            throws PixivException, IOException {
        return sendGetRequest("v1/illust/recommended", filter, RecommendedIllusts.class);
    }

    /**
     * Searches illustrations.
     *
     * @param filter The filter to use.
     * @return Search Illustration Results.
     * @throws PixivException PixivException Error
     * @throws IOException    IOException Error
     */
    @NonNull
    public SearchedIllusts searchIllusts(@NonNull SearchedIllustsFilter filter)
            throws PixivException, IOException {
        return sendGetRequest("v1/search/illust", filter, SearchedIllusts.class);
    }

    @NonNull
    public Autocomplete autocomplete(@NonNull SearchedIllustsFilter filter) throws PixivException, IOException {
        return sendGetRequest("v2/search/autocomplete", filter, Autocomplete.class);
    }

    /**
     * Grabs the details of a certain illustration.
     *
     * @param illustId the id of the illustration.
     * @return Illustration Detail.
     * @throws PixivException PixivException Error
     * @throws IOException    IOException Error
     */
    @NonNull
    public IllustDetail getIllustDetail(long illustId) throws PixivException, IOException {
        HttpUrl url = baseUrl.newBuilder()
                .addEncodedPathSegments("v1/illust/detail")
                .addQueryParameter("illust_id", String.valueOf(illustId))
                .build();
        Request request = createApiReqBuilder()
                .url(url)
                .get()
                .build();
        return requestSender.send(request, IllustDetail.class);
    }

    /**
     * Get comments of an illustration. The illustration can be a normal illustration or a manga.
     *
     * @param filter The filter to use.
     * @return Comments.
     * @throws PixivException PixivException Error
     * @throws IOException    IOException Error
     */
    @NonNull
    public Comments getIllustComments(@NonNull IllustCommentsFilter filter)
            throws PixivException, IOException {
        return sendGetRequest("v2/illust/comments", filter, Comments.class);
    }

    @NonNull
    public UserDetail getUserDetails(@NonNull UserQuery userQuery) throws PixivException, IOException {
        return sendGetRequest("v1/user/detail", userQuery, UserDetail.class);
    }

    @NonNull
    public UserIllusts getUserIllusts(@NonNull UserIllustQuery userQuery) throws PixivException, IOException {
        return sendGetRequest("v1/user/illusts", userQuery, UserIllusts.class);
    }

    public void followUser(@NonNull FollowUserRequest request) throws PixivException, IOException {
        sendPostRequest("v1/user/follow/add", request, Void.class);
    }

    public void unfollowUser(@NonNull FollowUserRequest request) throws PixivException, IOException {
        sendPostRequest("v1/user/follow/delete", request, Void.class);
    }

    @NonNull
    public SearchedIllusts bookmarks(@NonNull V2Filter filter) throws PixivException, IOException {
        return sendGetRequest("v1/user/bookmarks/illust", filter, SearchedIllusts.class);
    }

    @NonNull
    public SearchedIllusts followFeed(@NonNull V2Filter filter) throws PixivException, IOException {
        return sendGetRequest("v2/illust/follow", filter, SearchedIllusts.class);
    }

    @NonNull
    public AddBookmarkResult addBookmark(@NonNull AddBookmark filter) throws PixivException, IOException {
        return sendPostRequest("v2/illust/bookmark/add", filter, AddBookmarkResult.class);
    }

    @NonNull
    public DeleteBookmarkResult removeBookmark(@NonNull DeleteBookmark filter) throws PixivException, IOException {
        return sendPostRequest("v1/illust/bookmark/delete", filter, DeleteBookmarkResult.class);
    }

    public long connections() {
        return httpClient.connectionPool().connectionCount();
    }

    public long idleConnections() {
        return httpClient.connectionPool().idleConnectionCount();
    }

    /**
     * Fetches the content of the given URL as stream.
     * It is often used for downloading the image of an illustration.
     * It is caller's responsibility to close the returned response object.
     * If the status code is not within the range [200, 300), {@link PixivException} is thrown.
     *
     * @param url - URL.
     * @return Response of downloading the resource.
     */
    @NonNull
    public Response download(@NonNull String url) throws PixivException, IOException {
        Request request = new Request.Builder()
                .header("User-Agent", userAgent)
                .header("Referer", baseUrl.toString())
                .header("Accept-Encoding", "gzip")
                .url(url)
                .get()
                .build();

        Response response = httpClient.newCall(request).execute();
        if (!response.isSuccessful()) {
            int code = response.code();
            response.close();
            throw new PixivException("Fail to download. Status code: " + code);
        }
        return response;
    }

    /**
     * Sends a HTTP GET request.
     *
     * @param path     The relative URL path.
     * @param filter   The filter to used to generate the query parameters.
     * @param respType Type of the response.
     * @param <T>      Type of the serialized response.
     * @param <F>      Type of the filter.
     * @return Serialized response.
     * @throws PixivException The server returns an error.
     * @throws IOException    IO error.
     */
    public <T, F> T sendGetRequest(@NonNull String path, @NonNull F filter, Class<T> respType)
            throws PixivException, IOException {
        HttpUrl.Builder urlBuilder = baseUrl.newBuilder()
                .addEncodedPathSegments(path);
        QueryParamConverter.toQueryParams(filter, urlBuilder);
        HttpUrl url = urlBuilder
                .build();
        Request request = createApiReqBuilder()
                .url(url)
                .get()
                .build();
        return requestSender.send(request, respType);
    }

    public <T, F> T sendPostRequest(@NonNull String path, @NonNull F filter, Class<T> respType)
            throws PixivException, IOException {
        HttpUrl.Builder urlBuilder = baseUrl.newBuilder()
                .addEncodedPathSegments(path);
        FormBody.Builder builder = new FormBody.Builder();
        FormBodyConverter.toQueryParams(filter, builder);
        Request request = createApiReqBuilder()
                .url(urlBuilder.build())
                .post(builder.build())
                .build();
        return requestSender.send(request, respType);
    }

    /**
     * Creates an API request builder.
     *
     * @return Created API request builder.
     * @throws AuthException Authentication error when obtaining the access token.
     * @throws IOException   IO error.
     */
    public Request.@NonNull Builder createApiReqBuilder() throws AuthException, IOException {
        return new Request.Builder()
                .headers(headers)
                .header("User-Agent", userAgent)
                .header("Authorization", "Bearer " + tokenProvider.getAccessToken());
    }

    /**
     * Closes the client and release the resources.
     *
     * @throws IOException IO error.
     */
    @Override
    public void close() throws IOException {
        this.tokenProvider.close();
        IoUtils.close(this.httpClient);
    }

    /**
     * Builder for {@link PixivApiClient}.
     */
    public static class Builder {
        public static final String DEFAULT_BASE_URL = "https://app-api.pixiv.net";
        public String baseUrl = DEFAULT_BASE_URL;
        public String userAgent = Header.USER_AGENT_ANDROID;
        public TokenProvider tokenProvider = null;

        /**
         * Sets the base URL of the service.
         * Default: {@link #DEFAULT_BASE_URL}.
         *
         * @param baseUrl Base URL.
         * @return This instance.
         */
        @NonNull
        public Builder setBaseUrl(@NonNull String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        /**
         * Sets the user agent used when sending requests.
         * Default: {@link Header#USER_AGENT_ANDROID}
         *
         * @param userAgent User agent.
         * @return This instance.
         */
        @NonNull
        public Builder setUserAgent(@NonNull String userAgent) {
            this.userAgent = userAgent;
            return this;
        }

        /**
         * Sets the provider for the access token.
         * The created {@link PixivApiClient} will take ownership of the token provider, which means
         * when the {@link PixivApiClient} is closed, the token provider will also be closed.
         *
         * @param tokenProvider Token provider.
         * @return This instance.
         */
        @NonNull
        public Builder setTokenProvider(@NonNull TokenProvider tokenProvider) {
            this.tokenProvider = tokenProvider;
            return this;
        }

        /**
         * Builds the {@link PixivApiClient} instance.
         *
         * @return Built instance.
         */
        @NonNull
        public PixivApiClient build() {
            return new PixivApiClient(this);
        }
    }
}
