package pixivj.model;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

/**
 * FullUser
 */
public class User {

    @SerializedName("profile_image_urls")
    private ProfileImageUrls profileImageUrls = null;

    @SerializedName("id")
    private Long id = null;

    @SerializedName("name")
    private String name = null;

    @SerializedName("account")
    private String account = null;

    @SerializedName("mail_address")
    private String mailAddress = null;

    @SerializedName("is_premium")
    private boolean premium;

    @SerializedName("x_restrict")
    private Integer xRestrict = null;

    @SerializedName("is_mail_authorized")
    private boolean mailAuthorized;

    /**
     * Convert the given object to string with each line indented by 4 spaces (except the first
     * line).
     */
    private static String toIndentedString(Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }

    /**
     * Get profileImageUrls
     *
     * @return profileImageUrls
     **/
    public ProfileImageUrls getProfileImageUrls() {
        return profileImageUrls;
    }

    public void setProfileImageUrls(ProfileImageUrls profileImageUrls) {
        this.profileImageUrls = profileImageUrls;
    }

    /**
     * Internal numerical user ID.
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
     * The name of the user.
     *
     * @return name
     **/
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * The account name used for log in.
     *
     * @return account
     **/
    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    /**
     * Email address of the user.
     *
     * @return mailAddress
     **/
    public String getMailAddress() {
        return mailAddress;
    }

    public void setMailAddress(String mailAddress) {
        this.mailAddress = mailAddress;
    }

    /**
     * Whether the user has bought the premimum subscription.
     *
     * @return isPremium
     **/
    public boolean isPremium() {
        return premium;
    }

    public void setPremium(boolean premium) {
        this.premium = premium;
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
     * Whether the email of the user has been validated.
     *
     * @return isMailAuthorized
     **/
    public boolean isMailAuthorized() {
        return mailAuthorized;
    }

    public void setMailAuthorized(boolean isMailAuthorized) {
        this.mailAuthorized = isMailAuthorized;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        User fullUser = (User) o;
        return Objects.equals(this.profileImageUrls, fullUser.profileImageUrls) &&
                Objects.equals(this.id, fullUser.id) &&
                Objects.equals(this.name, fullUser.name) &&
                Objects.equals(this.account, fullUser.account) &&
                Objects.equals(this.mailAddress, fullUser.mailAddress) &&
                Objects.equals(this.premium, fullUser.premium) &&
                Objects.equals(this.xRestrict, fullUser.xRestrict) &&
                Objects.equals(this.mailAuthorized, fullUser.mailAuthorized);
    }

    @Override
    public int hashCode() {
        return Objects
                .hash(profileImageUrls, id, name, account, mailAddress, premium, xRestrict, mailAuthorized);
    }

    @Override
    public String toString() {

        return "class FullUser {\n" +
                "    profileImageUrls: " + toIndentedString(profileImageUrls) + "\n" +
                "    id: " + toIndentedString(id) + "\n" +
                "    name: " + toIndentedString(name) + "\n" +
                "    account: " + toIndentedString(account) + "\n" +
                "    mailAddress: " + toIndentedString(mailAddress) + "\n" +
                "    isPremium: " + toIndentedString(premium) + "\n" +
                "    xRestrict: " + toIndentedString(xRestrict) + "\n" +
                "    isMailAuthorized: " + toIndentedString(mailAuthorized) + "\n" +
                "}";
    }
}
