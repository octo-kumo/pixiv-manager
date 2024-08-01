package pixivj.model;

import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;
import pixivj.util.JsonUtils;

import java.util.Objects;

public class APIErrorDetails {

    @SerializedName("user_message")
    private String userMessage = null;

    @SerializedName("message")
    private String message = null;

    @SerializedName("reason")
    private String reason = null;

    @SerializedName("user_message_details")
    private JsonElement userMessageDetails = null;

    /**
     * Get userMessage
     *
     * @return userMessage
     **/
    public String getUserMessage() {
        return userMessage;
    }

    public void setUserMessage(String userMessage) {
        this.userMessage = userMessage;
    }

    /**
     * Get message
     *
     * @return message
     **/
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Get reason
     *
     * @return reason
     **/
    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    /**
     * Details of the error. The schema depends on the error type.
     *
     * @return userMessageDetails
     **/
    public JsonElement getUserMessageDetails() {
        return userMessageDetails;
    }

    public void setUserMessageDetails(JsonElement userMessageDetails) {
        this.userMessageDetails = userMessageDetails;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        APIErrorDetails apIErrorDetails = (APIErrorDetails) o;
        return Objects.equals(this.userMessage, apIErrorDetails.userMessage) &&
                Objects.equals(this.message, apIErrorDetails.message) &&
                Objects.equals(this.reason, apIErrorDetails.reason) &&
                Objects.equals(this.userMessageDetails, apIErrorDetails.userMessageDetails);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userMessage, message, reason, userMessageDetails);
    }


    @Override
    public String toString() {
        return JsonUtils.GSON.toJson(this);
    }

}

