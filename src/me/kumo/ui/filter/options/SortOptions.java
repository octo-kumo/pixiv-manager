package me.kumo.ui.filter.options;

import com.github.hanshsieh.pixivj.model.Illustration;
import me.kumo.ui.filter.FilterPane;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Stream;

public class SortOptions extends JPanel implements ActionListener {
    private final FilterPane filterPane;
    private final ArrayList<SortOption> options = new ArrayList<>();

    public SortOptions(FilterPane filterPane) {
        super(new FlowLayout(FlowLayout.LEADING));
        this.filterPane = filterPane;

        options.add(new SortOption("View", SortOption.SortDirection.UNDEFINED, Comparator.comparing(Illustration::getTotalView), this));
        options.add(new SortOption("Bookmark", SortOption.SortDirection.UNDEFINED, Comparator.comparing(Illustration::getTotalBookmarks), this));
        options.add(new SortOption("Create Date", SortOption.SortDirection.UNDEFINED, Comparator.comparing(Illustration::getCreateDate), this));

        options.forEach(this::add);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        SwingUtilities.invokeLater(filterPane::refresh);
    }

    public Stream<Illustration> sort(Stream<Illustration> illustrations) {
        for (SortOption option : options) {
            if (option.enabled()) illustrations = illustrations.sorted(option);
        }
        return illustrations;
    }
}
