package me.kumo.ui.managers;

import com.github.hanshsieh.pixivj.model.*;
import me.kumo.pixiv.Pixiv;
import me.kumo.ui.gallery.GalleryItem;
import me.kumo.ui.gallery.HorizontalGallery;
import me.kumo.ui.gallery.RankedItem;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class FeedManager extends GalleryManager {
    private final Pixiv pixiv;

    private final ArrayList<Illustration> rank;
    private final HorizontalGallery rankGallery;
    private String followNextURL = null;
    private String rankNextURL = null;
    private SwingWorker<Object, Void> followWorker;
    private SwingWorker<Object, Void> rankWorker;
    private boolean showRanked = true;

    public FeedManager(Pixiv pixiv) {
        this.pixiv = pixiv;
        rank = new ArrayList<>();
        gallery.getScrollPane().getVerticalScrollBar().addAdjustmentListener(e -> {
            JScrollBar scrollBar = (JScrollBar) e.getAdjustable();
            int extent = scrollBar.getModel().getExtent();
            if (scrollBar.getValue() != 0 && (scrollBar.getValue() + extent) == scrollBar.getMaximum())
                getMoreFollows();
        });
        pixiv.addIllustUpdateListener(this::refresh);
        SwingUtilities.invokeLater(() -> pixiv.addOnLoadListener(result -> {
            if (result == null) return;
            getMoreFollows();
            getMoreRank();
        }));
        add(rankGallery = new HorizontalGallery() {
            @Override
            public JScrollBar getSmoothScrollbar() {
                return getHorizontalScrollBar();
            }

            public GalleryItem getRefreshOrCreate(Illustration illustration) {
                GalleryItem holder = holderMap.computeIfAbsent(illustration.getId(), i -> new RankedItem());
                holder.refresh(illustration);
                if (holder instanceof RankedItem)
                    ((RankedItem) holder).setRank(rank.indexOf(illustration) + 1);
                return holder;
            }
        }, BorderLayout.SOUTH);
        rankGallery.getScrollPane().getHorizontalScrollBar().addAdjustmentListener(e -> {
            JScrollBar scrollBar = (JScrollBar) e.getAdjustable();
            int extent = scrollBar.getModel().getExtent();
            if (scrollBar.getValue() != 0 && (scrollBar.getValue() + extent) == scrollBar.getMaximum())
                getMoreRank();
        });
    }

    private void getMoreFollows() {
        if (followWorker != null && !followWorker.isDone()) return;
        followWorker = new SwingWorker<>() {
            @Override
            protected Object doInBackground() {
                try {
                    SearchedIllusts illusts = followNextURL == null ? pixiv.followFeed(new V2Filter()) :
                            pixiv.requestSender.send(pixiv.createApiReqBuilder().url(followNextURL).get().build(), SearchedIllusts.class);
                    followNextURL = illusts.getNextUrl();
                    append(illusts.getIllusts());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
        followWorker.execute();
    }

    private void getMoreRank() {
        if (rankWorker != null && !rankWorker.isDone()) return;
        rankWorker = new SwingWorker<>() {
            @Override
            protected Object doInBackground() {
                try {
                    RankedIllusts illusts = rankNextURL == null ? pixiv.getRankedIllusts(new RankedIllustsFilter()) :
                            pixiv.requestSender.send(pixiv.createApiReqBuilder().url(rankNextURL).get().build(), RankedIllusts.class);
                    rankNextURL = illusts.getNextUrl();
                    rank.addAll(illusts.getIllusts());
                    rankGallery.append(illusts.getIllusts());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
        rankWorker.execute();
    }

    @Override
    public void refresh(Illustration illustration) {
        super.refresh(illustration);
        rankGallery.refresh(illustration);
    }

    @Override
    public void start() {
        gallery.start();
        if (showRanked) rankGallery.start();
    }

    @Override
    public void stop() {
        gallery.stop();
        rankGallery.stop();
    }

    public void setShowRanked(boolean showRanked) {
        this.showRanked = showRanked;
        if (showRanked) rankGallery.start();
        else rankGallery.stop();
        rankGallery.setVisible(showRanked);
    }
}
