package pixivj.model;

import com.google.gson.annotations.SerializedName;

public class Profile {
    @SerializedName("webpage")
    private String webpage;

    @SerializedName("gender")
    private String gender;

    @SerializedName("birth")
    private String birth;

    @SerializedName("birth_day")
    private String birthDay;

    @SerializedName("birth_year")
    private int birthYear;

    @SerializedName("region")
    private String region;

    @SerializedName("address_id")
    private int addressId;

    @SerializedName("country_code")
    private String countryCode;

    @SerializedName("job")
    private String job;

    @SerializedName("job_id")
    private int jobId;

    @SerializedName("total_follow_users")
    private int totalFollowUsers;

    @SerializedName("total_mypixiv_users")
    private int totalMypixivUsers;

    @SerializedName("total_illusts")
    private int totalIllusts;

    @SerializedName("total_manga")
    private int totalManga;

    @SerializedName("total_novels")
    private int totalNovels;

    @SerializedName("total_illust_bookmarks_public")
    private int totalIllustBookmarksPublic;

    @SerializedName("total_illust_series")
    private int totalIllustSeries;

    @SerializedName("total_novel_series")
    private int totalNovelSeries;

    @SerializedName("background_image_url")
    private String backgroundImageUrl;

    @SerializedName("twitter_account")
    private String twitterAccount;

    @SerializedName("twitter_url")
    private String twitterUrl;

    @SerializedName("pawoo_url")
    private String pawooUrl;

    @SerializedName("is_premium")
    private boolean isPremium;

    @SerializedName("is_using_custom_profile_image")
    private boolean isUsingCustomProfileImage;

    public String getWebpage() {
        return webpage;
    }

    public void setWebpage(String webpage) {
        this.webpage = webpage;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public String getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(String birthDay) {
        this.birthDay = birthDay;
    }

    public int getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(int birthYear) {
        this.birthYear = birthYear;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public int getAddressId() {
        return addressId;
    }

    public void setAddressId(int addressId) {
        this.addressId = addressId;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public int getJobId() {
        return jobId;
    }

    public void setJobId(int jobId) {
        this.jobId = jobId;
    }

    public int getTotalFollowUsers() {
        return totalFollowUsers;
    }

    public void setTotalFollowUsers(int totalFollowUsers) {
        this.totalFollowUsers = totalFollowUsers;
    }

    public int getTotalMypixivUsers() {
        return totalMypixivUsers;
    }

    public void setTotalMypixivUsers(int totalMypixivUsers) {
        this.totalMypixivUsers = totalMypixivUsers;
    }

    public int getTotalIllusts() {
        return totalIllusts;
    }

    public void setTotalIllusts(int totalIllusts) {
        this.totalIllusts = totalIllusts;
    }

    public int getTotalManga() {
        return totalManga;
    }

    public void setTotalManga(int totalManga) {
        this.totalManga = totalManga;
    }

    public int getTotalNovels() {
        return totalNovels;
    }

    public void setTotalNovels(int totalNovels) {
        this.totalNovels = totalNovels;
    }

    public int getTotalIllustBookmarksPublic() {
        return totalIllustBookmarksPublic;
    }

    public void setTotalIllustBookmarksPublic(int totalIllustBookmarksPublic) {
        this.totalIllustBookmarksPublic = totalIllustBookmarksPublic;
    }

    public int getTotalIllustSeries() {
        return totalIllustSeries;
    }

    public void setTotalIllustSeries(int totalIllustSeries) {
        this.totalIllustSeries = totalIllustSeries;
    }

    public int getTotalNovelSeries() {
        return totalNovelSeries;
    }

    public void setTotalNovelSeries(int totalNovelSeries) {
        this.totalNovelSeries = totalNovelSeries;
    }

    public String getBackgroundImageUrl() {
        return backgroundImageUrl;
    }

    public void setBackgroundImageUrl(String backgroundImageUrl) {
        this.backgroundImageUrl = backgroundImageUrl;
    }

    public String getTwitterAccount() {
        return twitterAccount;
    }

    public void setTwitterAccount(String twitterAccount) {
        this.twitterAccount = twitterAccount;
    }

    public String getTwitterUrl() {
        return twitterUrl;
    }

    public void setTwitterUrl(String twitterUrl) {
        this.twitterUrl = twitterUrl;
    }

    public String getPawooUrl() {
        return pawooUrl;
    }

    public void setPawooUrl(String pawooUrl) {
        this.pawooUrl = pawooUrl;
    }

    public boolean isPremium() {
        return isPremium;
    }

    public void setPremium(boolean premium) {
        isPremium = premium;
    }

    public boolean isUsingCustomProfileImage() {
        return isUsingCustomProfileImage;
    }

    public void setUsingCustomProfileImage(boolean usingCustomProfileImage) {
        isUsingCustomProfileImage = usingCustomProfileImage;
    }
}
