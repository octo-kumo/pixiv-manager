package me.kumo.ui.control;

import com.github.weisj.darklaf.components.OverlayScrollPane;
import me.kumo.ui.Refreshable;
import me.kumo.ui.control.filter.OptionFilter;
import me.kumo.ui.control.filter.SearchFilter;
import me.kumo.ui.control.filter.TagFilter;
import me.kumo.ui.control.filter.ToolFilter;
import me.kumo.ui.managers.GalleryManager;
import pixivj.model.Illustration;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.util.List;
import java.util.stream.Stream;

public class ControlPane extends Box implements Refreshable<List<Illustration>> {
    private final SearchFilter searchFilter;
    private final OptionFilter optionFilter;
    private final TagFilter tagFilter;
    private final ToolFilter toolFilter;
    private final SortPane sorters;

    private final GalleryManager galleryManager;
    private final Box advancedStuff;
    private boolean advancedControls = false;

    public ControlPane(GalleryManager galleryManager) {
        super(BoxLayout.Y_AXIS);
        this.galleryManager = galleryManager;
        add(searchFilter = new SearchFilter(this));
        add(advancedStuff = Box.createVerticalBox());
        advancedStuff.setBorder(new TitledBorder("Advanced Options"));
        advancedStuff.add(optionFilter = new OptionFilter(this));
        advancedStuff.add(tagFilter = new TagFilter(this));
        advancedStuff.add(toolFilter = new ToolFilter(this));
        advancedStuff.add(new OverlayScrollPane(sorters = new SortPane(this), JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));
        advancedStuff.setVisible(false);
        toolFilter.setVisible(false);
    }

    public void setToolsShown(boolean shown) {
        toolFilter.setVisible(shown);
    }

    public void refresh(List<Illustration> illustrations) {
        tagFilter.refresh(illustrations);
        toolFilter.refresh(illustrations);
        revalidate();
        repaint();
    }

    public void applyAll() {
        galleryManager.refresh(filterAndSort(galleryManager.get()));
    }

    public void reset() {
        searchFilter.reset();
        optionFilter.reset();
        tagFilter.reset();
        toolFilter.reset();
        sorters.reset();
    }

    public List<Illustration> filterAndSort(List<Illustration> illustrations) {
        Stream<Illustration> stream = illustrations.stream();
        stream = searchFilter.filter(stream);
        stream = optionFilter.filter(stream);
        stream = tagFilter.filter(stream);
        stream = toolFilter.filter(stream);
        stream = sorters.sort(stream);
        return stream.toList();
    }

    public boolean isAdvancedControls() {
        return advancedControls;
    }

    public void setAdvancedControls(boolean advancedControls) {
        this.advancedControls = advancedControls;
        advancedStuff.setVisible(advancedControls);
    }

    public GalleryManager getGalleryManager() {
        return galleryManager;
    }
}

