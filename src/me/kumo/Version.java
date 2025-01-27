package me.kumo;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import org.jetbrains.annotations.NotNull;

import javax.net.ssl.HttpsURLConnection;
import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Comparator;

public class Version implements Comparable<Version> {
    public static final String CHECK_UPDATES = "checkUpdates";
    public static final Gson gson = new Gson();
    public static final Version CURRENT = new Version(Version.class.getPackage().getImplementationVersion() == null ? "in-dev" : Version.class.getPackage().getImplementationVersion());
    private final String version;
    private final boolean string;

    public Version(String version) {
        if (version == null) throw new IllegalArgumentException("Version can not be null");
        this.string = !version.matches("v?[0-9]+(\\.[0-9]+)*(?:-(.+))?");
        this.version = version;
    }

    public final String get() {
        return this.version.replaceFirst("^v", "").replaceAll("-(.+)$", "");
    }

    @Override
    public String toString() {
        return version;
    }

    @Override
    public int compareTo(@NotNull Version that) {
        if (this.string && that.string) return this.get().compareTo(that.get());
        else if (this.string || that.string) return Boolean.compare(this.string, that.string);

        String[] thisParts = this.get().split("\\.");
        String[] thatParts = that.get().split("\\.");
        int length = Math.max(thisParts.length, thatParts.length);
        for (int i = 0; i < length; i++) {
            int thisPart = i < thisParts.length ? Integer.parseInt(thisParts[i]) : 0;
            int thatPart = i < thatParts.length ? Integer.parseInt(thatParts[i]) : 0;
            if (thisPart < thatPart) return -1;
            if (thisPart > thatPart) return 1;
        }
        return 0;
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) return true;
        if (that == null) return false;
        if (this.getClass() != that.getClass()) return false;
        return this.compareTo((Version) that) == 0;
    }

    public static boolean shouldAutoUpdate() {
        return PixivManager.preferences.getBoolean(CHECK_UPDATES, true);
    }

    public static void shouldAutoUpdate(boolean value) {
        PixivManager.preferences.putBoolean(CHECK_UPDATES, value);
    }

    public static void checkForUpdates() {
        if (shouldAutoUpdate()) asyncUpdate();
    }

    public static void asyncUpdate() {
        new Thread(() -> {
            try {
                System.out.println("Checking for updates...");
                Release[] releases = getReleases();
                Arrays.stream(releases).max(Comparator.comparing(r -> new Version(r.tag_name))).ifPresent(Version::notifyLatest);
            } catch (IOException e) {
                System.out.printf("\tChecking failed: %s::%s%n", e.getClass().getSimpleName(), e.getMessage());
            }
        }).start();
    }

    public static Release[] getReleases() throws IOException {
        URL obj = new URL("https://api.github.com/repos/octo-kumo/pixiv-manager/releases");
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        int responseCode = con.getResponseCode();
        if (responseCode == HttpsURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            return gson.fromJson(new JsonReader(in), Release[].class);
        }
        return new Release[0];
    }

    private static void notifyLatest(Release latest) {
        Version version = new Version(latest.tag_name);
        System.out.printf("\tCurrent version = %s%n", CURRENT);
        System.out.printf("\tLatest version = %s%n", version);
        int compare = CURRENT.compareTo(version);
        if (compare == 0) System.out.printf("\tCurrent version %s is latest!%n", CURRENT);
        else if (compare < 0) {
            System.out.printf("\tCurrent version %s is behind!%n", CURRENT);
            if (JOptionPane.showConfirmDialog(null, String.format("Download new version %s?", version), "Outdated Version!", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION && Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE))
                try {
                    Desktop.getDesktop().browse(new URI(latest.html_url));
                } catch (IOException | URISyntaxException e) {
                    e.printStackTrace();
                }
        } else System.out.printf("\tCurrent version %s is in-dev!%n", CURRENT);
    }

    public static class Release {
        public String url, assets_url, upload_url, html_url;
        public int id;
        public String node_id, tag_name, target_commitish, name;
        public boolean draft;
        public User author;
        public boolean prerelease;
        public String created_at, published_at;
        public Asset[] assets;
        public String tarball_url, zipball_url, body;
    }

    public static class User {
        public String login;
        public int id;
        public String node_id, avatar_url, gravatar_id, url, html_url, followers_url, following_url, gists_url, starred_url, subscriptions_url, organizations_url, repos_url, events_url, received_events_url, type;
        public boolean site_admin;
    }

    public static class Asset {
        public String url;
        public int id;
        public String node_id;
        public String name;
        public Object label;
        public User uploader;
        public String content_type;
        public String state;
        public int size;
        public int download_count;
        public String created_at;
        public String updated_at;
        public String browser_download_url;
    }
}
