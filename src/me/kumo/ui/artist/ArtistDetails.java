package me.kumo.ui.artist;

import me.kumo.components.image.RemoteImage;
import me.kumo.io.ProgressTracker;
import pixivj.model.UserDetail;

import java.awt.*;

public class ArtistDetails extends RemoteImage {
    private UserDetail detail;

    public ArtistDetails() {
        setPreferredSize(new Dimension(Short.MAX_VALUE, 200));
    }

    public void setDetails(UserDetail detail) {
        this.detail = detail;
        setUrl(detail.getProfile().getBackgroundImageUrl());
        loadImage();
    }

    @Override
    public void paintComponent(Graphics g1d) {
        Graphics2D g = (Graphics2D) g1d;
        super.paintComponent(g);
    }

    @Override
    public void onProgress(ProgressTracker tracker) {
    }

    @Override
    public boolean shouldSaveToLocal() {
        return false;
    }

    @Override
    public boolean shouldMakeThumbnail() {
        return true;
    }
}
