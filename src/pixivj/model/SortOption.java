package pixivj.model;

import com.google.gson.annotations.SerializedName;

public enum SortOption {
    @SerializedName("date_asc")
    SORT_DATE_ASC,
    @SerializedName("date_desc")
    SORT_DATE_DESC
}
