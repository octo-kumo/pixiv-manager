package pixivj.model;

import com.google.gson.annotations.SerializedName;

public class ProfilePublicity {
    @SerializedName("gender")
    private String gender;
    @SerializedName("region")
    private String region;
    @SerializedName("birth_day")
    private String birthDay;
    @SerializedName("birth_year")
    private String birthYear;
    @SerializedName("job")
    private String job;
    @SerializedName("pawoo")
    private boolean pawoo;

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(String birthDay) {
        this.birthDay = birthDay;
    }

    public String getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(String birthYear) {
        this.birthYear = birthYear;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public boolean isPawoo() {
        return pawoo;
    }

    public void setPawoo(boolean pawoo) {
        this.pawoo = pawoo;
    }

}
