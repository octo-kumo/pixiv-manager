package me.kumo.ui.artist;

import com.github.hanshsieh.pixivj.model.UserDetail;
import me.kumo.io.ProgressTracker;
import me.kumo.ui.utils.RemoteImage;

import java.awt.*;

public class ArtistDetails extends RemoteImage {
    private UserDetail detail;

    public ArtistDetails() {
        setPreferredSize(new Dimension(Short.MAX_VALUE, 200));
    }

    public void setDetails(UserDetail detail) {
        this.detail = detail;
        System.out.println("setDetails :: " + detail.getUser().getName());
        setUrl(detail.getProfile().getBackgroundImageUrl());
        loadImage();
    }

    @Override
    public void paintComponent(Graphics g1d) {
        Graphics2D g = (Graphics2D) g1d;
        super.paintComponent(g);
//        g.setPaint(new GradientPaint());
    }

    @Override
    public void update(ProgressTracker tracker) {
    }

    @Override
    public boolean shouldSaveToLocal() {
        return false;
    }

    @Override
    public boolean shouldResize() {
        return true;
    }
}
