package pixivj.model;

import com.google.gson.annotations.SerializedName;
import pixivj.util.JsonUtils;

import java.util.Objects;

public class APIError {

    @SerializedName("error")
    private APIErrorDetails error = null;

    /**
     * Get error
     *
     * @return error
     **/
    public APIErrorDetails getError() {
        return error;
    }

    public void setError(APIErrorDetails error) {
        this.error = error;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        APIError apIError = (APIError) o;
        return Objects.equals(this.error, apIError.error);
    }

    @Override
    public int hashCode() {
        return Objects.hash(error);
    }


    @Override
    public String toString() {
        return JsonUtils.GSON.toJson(this);
    }

}

