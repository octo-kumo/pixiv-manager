package me.kumo.ui.managers;

import com.github.hanshsieh.pixivj.model.Illustration;
import com.github.weisj.darklaf.components.tristate.TristateCheckBox;
import com.github.weisj.darklaf.iconset.AllIcons;
import me.kumo.components.utils.StartAndStoppable;
import me.kumo.io.LocalGallery;
import me.kumo.pixiv.Pixiv;
import me.kumo.ui.gallery.Gallery;
import me.kumo.ui.gallery.GalleryItem;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExtraManager extends JPanel implements StartAndStoppable {
    private final BookmarkManager bookmarks;
    private final Gallery gallery;
    private SwingWorker<Object, Object> refresher;
    private TristateCheckBox selector;
    private ArrayList<Illustration> illusts;

    public ExtraManager(BookmarkManager bookmarks, Pixiv pixiv) {
        super(new BorderLayout());
        this.bookmarks = bookmarks;
        add(gallery = new Gallery(), BorderLayout.CENTER);
        add(new JPanel(new FlowLayout()) {{
            add(new JButton("Refresh", AllIcons.Action.Refresh.get()) {{
                addActionListener(e -> refresh());
            }});
            add(selector = new TristateCheckBox("Select"));
            selector.addActionListener(e -> {

            });
        }}, BorderLayout.NORTH);
        pixiv.addIllustUpdateListener(this::refresh);
        refresh();
    }

    private void refresh() {
        if (refresher != null && !refresher.isDone()) return;
        (refresher = new SwingWorker<>() {
            @Override
            protected Object doInBackground() {
                refreshAllExtra();
                return null;
            }
        }).execute();
    }

    public void refreshAllExtra() {
        HashMap<Long, Illustration> map = new HashMap<>();
        HashMap<Long, Long> modified = new HashMap<>();
        Pattern pattern = Pattern.compile("(\\d+)_p(\\d+)\\.(jpg|jpeg|png|gif)");
        for (File f : Objects.requireNonNull(new File(LocalGallery.PATH).listFiles())) {
            Matcher matcher = pattern.matcher(f.getName());
            if (matcher.find()) {
                Long id = Long.parseLong(matcher.group(1));
                if (bookmarks.getBookmarks().stream().anyMatch(i -> Objects.equals(i.getId(), id)))
                    continue;
                Illustration illustration = map.computeIfAbsent(id, _id -> {
                    Illustration i = new Illustration();
                    i.setId(_id);
                    return i;
                });
                modified.compute(id, (_id, date) -> date == null ? f.lastModified() : Math.max(f.lastModified(), date));
                illustration.setPageCount((illustration.getPageCount() == null ? 0 : illustration.getPageCount()) + 1);
            }
        }
        illusts = new ArrayList<>(map.values());
        illusts.sort(Comparator.comparing(il -> -modified.get(il.getId())));
        gallery.refresh(illusts);
    }

    @Override
    public void start() {
        gallery.start();
    }

    @Override
    public void stop() {
        gallery.stop();
    }


    public void refresh(Illustration illustration) {
        if (illustration.isBookmarked() && illusts.stream().anyMatch(b -> Objects.equals(b.getId(), illustration.getId()))) {
            illusts.removeIf(b -> Objects.equals(b.getId(), illustration.getId()));
            GalleryItem holder = gallery.getRefreshOrCreate(illustration);
            gallery.grid.remove(holder);
        }
    }
}
