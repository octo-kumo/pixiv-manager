package pixivj.model;

import com.google.gson.annotations.SerializedName;

import java.time.OffsetDateTime;

public class Comment {
    @SerializedName("id")
    private Long id;
    @SerializedName("comment")
    private String comment;
    @SerializedName("date")
    private OffsetDateTime date;
    @SerializedName("has_replies")
    private Boolean hasReplies;
    @SerializedName("user")
    private Author user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public OffsetDateTime getDate() {
        return date;
    }

    public void setDate(OffsetDateTime date) {
        this.date = date;
    }

    public Boolean getHasReplies() {
        return hasReplies;
    }

    public void setHasReplies(Boolean hasReplies) {
        this.hasReplies = hasReplies;
    }

    public Author getUser() {
        return user;
    }

    public void setUser(Author user) {
        this.user = user;
    }
}
