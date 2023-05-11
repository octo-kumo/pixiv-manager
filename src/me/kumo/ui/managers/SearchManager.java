package me.kumo.ui.managers;

import com.github.hanshsieh.pixivj.model.SearchedIllusts;
import com.github.hanshsieh.pixivj.model.SearchedIllustsFilter;
import me.kumo.pixiv.Pixiv;
import me.kumo.ui.control.filter.TagSearchFilter;

import javax.swing.*;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class SearchManager extends GalleryManager {
    public SearchedIllustsFilter lastFilter;

    private final Pixiv pixiv;
    private final TagSearchFilter search;
    private final AtomicInteger more = new AtomicInteger();
    private SwingWorker<Object, Object> worker;
    private String searchNextURL;

    public SearchManager(Pixiv pixiv) {
        this.pixiv = pixiv;
        filter.remove(0);
        filter.add(search = new TagSearchFilter(this), 0);
    }

    public void getMoreSearch() {
        if (worker != null && !worker.isDone()) {
            more.incrementAndGet();
            return;
        }
        worker = new SwingWorker<>() {
            @Override
            protected Object doInBackground() {
                try {
                    SearchedIllusts illusts = searchNextURL == null ? pixiv.searchIllusts(lastFilter = search.getFilter()) :
                            pixiv.requestSender.send(pixiv.createApiReqBuilder().url(searchNextURL).get().build(), SearchedIllusts.class);
                    searchNextURL = illusts.getNextUrl();
                    System.out.println("getMoreSearch :: " + illusts.getIllusts().size());
                    append(illusts.getIllusts());

                    if (more.get() > 0) {
                        more.decrementAndGet();
                        SwingUtilities.invokeLater(() -> getMoreSearch());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
        worker.execute();
    }

    public void reset() {
        searchNextURL = null;
        if (this.illustrations == null) this.illustrations = new ArrayList<>();
        this.illustrations.clear();
        refresh(this.illustrations);
        getMoreSearch();
    }
}
