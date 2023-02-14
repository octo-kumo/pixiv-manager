package me.kumo.ui.filter;

import com.github.hanshsieh.pixivj.model.Illustration;
import me.kumo.ui.filter.options.FilterOptions;
import me.kumo.ui.filter.options.SortOptions;
import me.kumo.ui.filter.tags.TagEditor;
import me.kumo.ui.gallery.Gallery;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.stream.Stream;

public class FilterPane extends Box {
    private final Gallery gallery;
    private final TagEditor tagEditor;
    private final SortOptions sorters;
    private final FilterOptions options;

    public FilterPane(Gallery gallery) {
        super(BoxLayout.Y_AXIS);
        this.gallery = gallery;
        add(sorters = new SortOptions(this));
        add(options = new FilterOptions(this));
        add(tagEditor = new TagEditor(this), BorderLayout.SOUTH);
    }

    public void refresh() {
        tagEditor.refreshTags();
        gallery.refresh();
        revalidate();
        repaint();
    }

    public Illustration[] filter(Illustration[] illustrations) {
        Stream<Illustration> stream = Arrays.stream(illustrations);
        stream = options.filter(stream);
        stream = tagEditor.filter(stream);
        stream = sorters.sort(stream);
        return stream.toArray(Illustration[]::new);
    }
}

