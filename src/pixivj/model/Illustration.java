package pixivj.model;

import com.google.gson.annotations.SerializedName;
import pixivj.util.JsonUtils;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

public class Illustration {

    @SerializedName("id")
    private Long id = null;

    @SerializedName("title")
    private String title = null;

    @SerializedName("type")
    private IllustType type = null;

    @SerializedName("image_urls")
    private IllustImageUrls imageUrls = null;

    @SerializedName("caption")
    private String caption = null;

    @SerializedName("restrict")
    private Integer restrict = null;

    @SerializedName("user")
    private Author user = null;

    @SerializedName("tags")
    private List<Tag> tags = null;

    @SerializedName("tools")
    private List<String> tools = null;

    @SerializedName("create_date")
    private OffsetDateTime createDate = null;

    @SerializedName("page_count")
    private Integer pageCount = null;

    @SerializedName("width")
    private Integer width = null;

    @SerializedName("height")
    private Integer height = null;

    @SerializedName("sanity_level")
    private Integer sanityLevel = null;

    @SerializedName("x_restrict")
    private Integer xRestrict = null;

    @SerializedName("series")
    private Series series = null;

    @SerializedName("meta_single_page")
    private MetaSinglePage metaSinglePage = null;

    @SerializedName("meta_pages")
    private List<MetaPage> metaPages = null;

    @SerializedName("total_view")
    private Integer totalView = null;

    @SerializedName("total_bookmarks")
    private Integer totalBookmarks = null;

    @SerializedName("is_bookmarked")
    private Boolean bookmarked = null;

    @SerializedName("visible")
    private Boolean visible = null;

    @SerializedName("is_muted")
    private Boolean muted = null;

    @SerializedName("illust_ai_type")
    private Integer illustAIType = null;

    /**
     * Get id
     *
     * @return id
     **/
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Get title
     *
     * @return title
     **/
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Get type
     *
     * @return type
     **/
    public IllustType getType() {
        return type;
    }

    public void setType(IllustType type) {
        this.type = type;
    }

    /**
     * Get imageUrls
     *
     * @return imageUrls
     **/
    public IllustImageUrls getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(IllustImageUrls imageUrls) {
        this.imageUrls = imageUrls;
    }

    /**
     * Get caption
     *
     * @return caption
     **/
    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    /**
     * Get restrict
     *
     * @return restrict
     **/
    public Integer getRestrict() {
        return restrict;
    }

    public void setRestrict(Integer restrict) {
        this.restrict = restrict;
    }

    /**
     * Get user
     *
     * @return user
     **/
    public Author getUser() {
        return user;
    }

    public void setUser(Author user) {
        this.user = user;
    }

    public Illustration tags(List<Tag> tags) {
        this.tags = tags;
        return this;
    }

    /**
     * Get tags
     *
     * @return tags
     **/
    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    /**
     * Get tools
     *
     * @return tools
     **/
    public List<String> getTools() {
        return tools;
    }

    public void setTools(List<String> tools) {
        this.tools = tools;
    }

    /**
     * Get createDate
     *
     * @return createDate
     **/
    public OffsetDateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(OffsetDateTime createDate) {
        this.createDate = createDate;
    }

    /**
     * Total number of pages for the illustration.
     *
     * @return pageCount
     **/
    public Integer getPageCount() {
        return pageCount;
    }

    public void setPageCount(Integer pageCount) {
        this.pageCount = pageCount;
    }

    /**
     * Get width
     *
     * @return width
     **/
    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    /**
     * Get height
     *
     * @return height
     **/
    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    /**
     * Get sanityLevel
     *
     * @return sanityLevel
     **/
    public Integer getSanityLevel() {
        return sanityLevel;
    }

    public void setSanityLevel(Integer sanityLevel) {
        this.sanityLevel = sanityLevel;
    }

    /**
     * Get xRestrict
     *
     * @return xRestrict
     **/
    public Integer getXRestrict() {
        return xRestrict;
    }

    public void setXRestrict(Integer xRestrict) {
        this.xRestrict = xRestrict;
    }

    /**
     * Get series
     *
     * @return series
     **/
    public Series getSeries() {
        return series;
    }

    public void setSeries(Series series) {
        this.series = series;
    }

    /**
     * Get metaSinglePage
     *
     * @return metaSinglePage
     **/
    public MetaSinglePage getMetaSinglePage() {
        return metaSinglePage;
    }

    public void setMetaSinglePage(MetaSinglePage metaSinglePage) {
        this.metaSinglePage = metaSinglePage;
    }

    /**
     * Get metaPages
     *
     * @return metaPages
     **/
    public List<MetaPage> getMetaPages() {
        return metaPages;
    }

    public void setMetaPages(List<MetaPage> metaPages) {
        this.metaPages = metaPages;
    }

    /**
     * Get totalView
     *
     * @return totalView
     **/
    public Integer getTotalView() {
        return totalView;
    }

    public void setTotalView(Integer totalView) {
        this.totalView = totalView;
    }

    /**
     * Get totalBookmarks
     *
     * @return totalBookmarks
     **/
    public Integer getTotalBookmarks() {
        return totalBookmarks;
    }

    public void setTotalBookmarks(Integer totalBookmarks) {
        this.totalBookmarks = totalBookmarks;
    }

    /**
     * Get isBookmarked
     *
     * @return isBookmarked
     **/
    public Boolean isBookmarked() {
        return bookmarked != null && bookmarked;
    }

    public void setBookmarked(Boolean bookmarked) {
        this.bookmarked = bookmarked;
    }

    /**
     * Get visible
     *
     * @return visible
     **/
    public Boolean isVisible() {
        return visible != null && visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    /**
     * Get isMuted
     *
     * @return isMuted
     **/
    public Boolean isMuted() {
        return muted != null && muted;
    }

    public void setMuted(Boolean muted) {
        this.muted = muted;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Illustration illustration = (Illustration) o;
        return Objects.equals(this.id, illustration.id) &&
                Objects.equals(this.title, illustration.title) &&
                Objects.equals(this.type, illustration.type) &&
                Objects.equals(this.imageUrls, illustration.imageUrls) &&
                Objects.equals(this.caption, illustration.caption) &&
                Objects.equals(this.restrict, illustration.restrict) &&
                Objects.equals(this.user, illustration.user) &&
                Objects.equals(this.tags, illustration.tags) &&
                Objects.equals(this.tools, illustration.tools) &&
                Objects.equals(this.createDate, illustration.createDate) &&
                Objects.equals(this.pageCount, illustration.pageCount) &&
                Objects.equals(this.width, illustration.width) &&
                Objects.equals(this.height, illustration.height) &&
                Objects.equals(this.sanityLevel, illustration.sanityLevel) &&
                Objects.equals(this.xRestrict, illustration.xRestrict) &&
                Objects.equals(this.series, illustration.series) &&
                Objects.equals(this.metaSinglePage, illustration.metaSinglePage) &&
                Objects.equals(this.metaPages, illustration.metaPages) &&
                Objects.equals(this.totalView, illustration.totalView) &&
                Objects.equals(this.totalBookmarks, illustration.totalBookmarks) &&
                Objects.equals(this.bookmarked, illustration.bookmarked) &&
                Objects.equals(this.visible, illustration.visible) &&
                Objects.equals(this.muted, illustration.muted);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id,
                title,
                type,
                imageUrls,
                caption,
                restrict,
                user,
                tags,
                tools,
                createDate,
                pageCount,
                width,
                height,
                sanityLevel,
                xRestrict,
                series,
                metaSinglePage,
                metaPages,
                totalView,
                totalBookmarks,
                bookmarked,
                visible,
                muted);
    }


    @Override
    public String toString() {
        return JsonUtils.GSON.toJson(this);
    }

    public Integer getIllustAIType() {
        return illustAIType;
    }

    public void setIllustAIType(Integer illustAIType) {
        this.illustAIType = illustAIType;
    }

    public boolean isAI() {
        return illustAIType != null && illustAIType == 2;
    }
}

