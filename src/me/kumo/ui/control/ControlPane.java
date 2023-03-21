package me.kumo.ui.control;

import com.github.hanshsieh.pixivj.model.Illustration;
import me.kumo.ui.Refreshable;
import me.kumo.ui.control.filter.OptionFilter;
import me.kumo.ui.control.filter.SearchFilter;
import me.kumo.ui.control.filter.TagFilter;
import me.kumo.ui.control.filter.ToolFilter;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class ControlPane extends Box implements Refreshable<List<Illustration>> {
    private final SearchFilter searchFilter;
    private final OptionFilter optionFilter;
    private final TagFilter tagFilter;
    private final ToolFilter toolFilter;
    private final SortPane sorters;
    private final Refreshable<List<Illustration>> parent;
    private final Supplier<ArrayList<Illustration>> supplier;
    private final Box advancedStuff;
    private boolean advancedControls = false;

    public ControlPane(Refreshable<List<Illustration>> parent, Supplier<ArrayList<Illustration>> supplier) {
        super(BoxLayout.Y_AXIS);
        this.parent = parent;
        this.supplier = supplier;
        add(searchFilter = new SearchFilter(this));
        add(advancedStuff = Box.createVerticalBox());
        advancedStuff.setBorder(new TitledBorder("Advanced Options"));
        advancedStuff.add(optionFilter = new OptionFilter(this));
        advancedStuff.add(tagFilter = new TagFilter(this));
        advancedStuff.add(toolFilter = new ToolFilter(this));
        advancedStuff.add(sorters = new SortPane(this));
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
        parent.refresh(filterAndSort(supplier.get()));
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
}

