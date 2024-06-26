package me.kumo.ui;

import com.github.weisj.darklaf.iconset.AllIcons;
import me.kumo.components.utils.StartAndStoppable;
import me.kumo.io.Icons;
import me.kumo.pixiv.Pixiv;
import me.kumo.ui.managers.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

public class MainControl extends JTabbedPane implements WindowFocusListener {

    private final RecommendManager recommended;
    private final BookmarkManager bookmarks;
    private final FeedManager feeds;
    private final SearchManager search;
    private final ExtraManager extra;

    public MainControl(Pixiv pixiv) {
        addTab("Recommended", AllIcons.Navigation.Glue.Vertical.get(), recommended = new RecommendManager(pixiv));
        addTab("Feed", AllIcons.Misc.Palette.get(), feeds = new FeedManager(pixiv));
        addTab("Bookmarks", Icons.heart.get(), bookmarks = new BookmarkManager(pixiv));
        addTab("Search", AllIcons.Action.Search.get(), search = new SearchManager(pixiv));
        addTab("Extra", AllIcons.Action.Delete.get(), extra = new ExtraManager(bookmarks, pixiv));
//        addTab("Browser", AllIcons.Action.Search.get(), editorPane = new JXEditorPane());

        addChangeListener(e -> updateTabs());
        updateTabs();
    }

    public RecommendManager getRecommended() {
        return recommended;
    }

    public BookmarkManager getBookmarks() {
        return bookmarks;
    }

    public FeedManager getFeeds() {
        return feeds;
    }

    public GalleryManager getCurrentManager() {
        Component component = getSelectedComponent();
        if (component instanceof GalleryManager manager) return manager;
        return null;
    }

    private void updateTabs() {
        for (int i = 0; i < getTabCount(); i++) {
            Component component = getComponentAt(i);
            if (!(component instanceof StartAndStoppable man)) return;
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
