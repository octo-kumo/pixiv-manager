package me.kumo.ui.control;

import com.github.hanshsieh.pixivj.model.Illustration;
import me.kumo.ui.Refreshable;
import me.kumo.ui.control.filter.OptionFilter;
import me.kumo.ui.control.filter.TagFilter;
import me.kumo.ui.control.filter.ToolFilter;

import javax.swing.*;
import java.util.Arrays;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class ControlPane extends Box implements Refreshable<Illustration[]> {
    private final TagFilter tagFilter;
    private final SortPane sorters;
    private final OptionFilter options;
    private final ToolFilter toolFilter;
    private final Refreshable<Illustration[]> parent;
    private final Supplier<Illustration[]> supplier;

    public ControlPane(Refreshable<Illustration[]> parent, Supplier<Illustration[]> supplier) {
        super(BoxLayout.Y_AXIS);
        this.parent = parent;
        this.supplier = supplier;
        add(sorters = new SortPane(this));
        add(options = new OptionFilter(this));
        add(tagFilter = new TagFilter(this));
        add(toolFilter = new ToolFilter(this));
    }

    public void refresh(Illustration[] illustrations) {
        tagFilter.refresh(illustrations);
        toolFilter.refresh(illustrations);
        revalidate();
        repaint();
    }

    public void applyAll() {
        parent.refresh(filter(supplier.get()));
    }

    public Illustration[] filter(Illustration[] illustrations) {
        Stream<Illustration> stream = Arrays.stream(illustrations);
        stream = options.filter(stream);
        stream = tagFilter.filter(stream);
        stream = toolFilter.filter(stream);
        stream = sorters.sort(stream);
        return stream.toArray(Illustration[]::new);
    }
}

