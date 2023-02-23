package me.kumo.ui;

import com.github.weisj.darklaf.iconset.AllIcons;
import me.kumo.io.Icons;
import me.kumo.io.pixiv.Pixiv;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

public class MainControl extends JTabbedPane implements WindowFocusListener {

    private final RecommendManager recommended;
    private final BookmarkManager bookmarks;
    private final FeedManager feeds;

    public MainControl(Pixiv pixiv) {
        addTab("Recommended", AllIcons.Navigation.Glue.Vertical.get(), recommended = new RecommendManager(pixiv));
        addTab("Feed", AllIcons.Misc.Palette.get(), feeds = new FeedManager(pixiv));
        addTab("Bookmarks", Icons.heart.get(), bookmarks = new BookmarkManager(pixiv));
//        addTab("Browser", AllIcons.Action.Search.get(), editorPane = new JXEditorPane());

        addChangeListener(e -> updateTabs());
        updateTabs();
    }

    private void updateTabs() {
        for (int i = 0; i < getTabCount(); i++) {
            Component component = getComponentAt(i);
            if (!(component instanceof GalleryManager man)) return;
            System.out.println(man.getClass());
            if (getSelectedIndex() == i) man.start();
            else man.stop();
        }
    }

    @Override
    public void windowGainedFocus(WindowEvent e) {
        recommended.tapGallery();
        bookmarks.tapGallery();
        feeds.tapGallery();
    }

    @Override
    public void windowLostFocus(WindowEvent e) {

    }
}
