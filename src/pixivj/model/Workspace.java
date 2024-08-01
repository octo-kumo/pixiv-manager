package pixivj.model;

import com.google.gson.annotations.SerializedName;

public class Workspace {
    @SerializedName("pc")
    private String pc;

    @SerializedName("monitor")
    private String monitor;

    @SerializedName("tool")
    private String tool;

    @SerializedName("scanner")
    private String scanner;

    @SerializedName("tablet")
    private String tablet;

    @SerializedName("mouse")
    private String mouse;

    @SerializedName("printer")
    private String printer;

    @SerializedName("desktop")
    private String desktop;

    @SerializedName("music")
    private String music;

    @SerializedName("desk")
    private String desk;

    @SerializedName("chair")
    private String chair;

    @SerializedName("comment")
    private String comment;

    @SerializedName("workspace_image_url")
    private String workspaceImageUrl;

    public String getScanner() {
        return scanner;
    }

    public void setScanner(String scanner) {
        this.scanner = scanner;
    }
}
