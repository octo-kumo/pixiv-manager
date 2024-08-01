package pixivj.model;

import com.google.gson.annotations.SerializedName;

public enum FilterMode {
    @SerializedName("day")
    DAY,
    @SerializedName("day_male")
    DAY_MALE,
    @SerializedName("day_female")
    DAY_FEMALE,
    @SerializedName("week_original")
    WEEK_ORIGINAL,
    @SerializedName("week_rookie")
    WEEK_ROOKIE,
    @SerializedName("month_rookie")
    MONTH_ROOKIE,
    @SerializedName("week")
    WEEK,
    @SerializedName("month")
    MONTH,
    @SerializedName("day_r18")
    DAY_R18,
    @SerializedName("week_r18")
    WEEK_R18,
    @SerializedName("week_r18g")
    WEEK_R18G,
    @SerializedName("day_male_r18")
    DAY_MALE_R18,
    @SerializedName("day_female_r18")
    DAY_FEMALE_R18,
    @SerializedName("day_manga")
    DAY_MANGA,
    @SerializedName("week_manga")
    WEEK_MANGA,
    @SerializedName("month_manga")
    MONTH_MANGA
}
