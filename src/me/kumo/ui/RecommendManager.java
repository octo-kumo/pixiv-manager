package me.kumo.ui;

import com.github.hanshsieh.pixivj.model.Illustration;
import com.github.hanshsieh.pixivj.model.RecommendedIllusts;
import com.github.hanshsieh.pixivj.model.RecommendedIllustsFilter;
import me.kumo.io.pixiv.Pixiv;

import javax.swing.*;
import java.util.ArrayList;

public class RecommendManager extends GalleryManager {
    private final Pixiv pixiv;
    private final ArrayList<Illustration> recommend;

    private String recommendNextURL = null;
    private SwingWorker<Object, Object> worker;

    public RecommendManager(Pixiv pixiv) {
        this.pixiv = pixiv;
        recommend = new ArrayList<>();
        pixiv.addIllustUpdateListener(this::refresh);
        gallery.getScrollPane().getVerticalScrollBar().addAdjustmentListener(e -> {
            JScrollBar scrollBar = (JScrollBar) e.getAdjustable();
            int extent = scrollBar.getModel().getExtent();
            if (scrollBar.getValue() != 0 && (scrollBar.getValue() + extent) == scrollBar.getMaximum())
                getMoreRecommend();
        });
        SwingUtilities.invokeLater(() -> pixiv.addOnLoadListener(result -> {
            if (result == null) return;
            getMoreRecommend();
        }));
    }


    private void getMoreRecommend() {
        if (worker != null && !worker.isDone()) return;
        worker = new SwingWorker<>() {
            @Override
            protected Object doInBackground() {

                try {
                    RecommendedIllusts illusts = recommendNextURL == null ? pixiv.getRecommendedIllusts(new RecommendedIllustsFilter()) :
                            pixiv.requestSender.send(pixiv.createApiReqBuilder().url(recommendNextURL).get().build(), RecommendedIllusts.class);
                    recommendNextURL = illusts.getNextUrl();
                    System.out.println("getMoreRecommend :: " + illusts.getIllusts().size());
                    recommend.addAll(illusts.getIllusts());
                    setIllustrations(recommend);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
        worker.execute();
    }
}
