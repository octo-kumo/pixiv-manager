package me.kumo.ui.control.filter;

import com.github.weisj.darklaf.components.OverlayScrollPane;
import com.github.weisj.darklaf.components.tristate.TristateCheckBox;
import com.github.weisj.darklaf.components.tristate.TristateState;
import com.github.weisj.darklaf.iconset.AllIcons;
import me.kumo.components.CustomSearchField;
import me.kumo.components.IconButton;
import me.kumo.components.utils.UIUtils;
import me.kumo.components.utils.WrapLayout;
import me.kumo.io.Icons;
import me.kumo.pixiv.AutocompleteWorker;
import me.kumo.ui.control.SortPane;
import me.kumo.ui.managers.SearchManager;
import pixivj.model.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.stream.Stream;

public class TagSearchFilter extends Box implements ActionListener {
    private final CustomSearchField searchbar;
    private final TristateCheckBox searchR18;
    private final SortPane.DefaultSortOption searchSort;
    private final LinkedHashSet<String> selectedTags = new LinkedHashSet<>();
    private final JComboBox<SearchedIllustsFilter.BookmarkCount> searchBookmark;
    private final JPanel filtersPanel;
    private final SearchManager searchManager;
    private final JLabel filter;
    private final JCheckBox searchPartial;
    private final TristateCheckBox searchTranslate;
    private AutocompleteWorker worker;

    public TagSearchFilter(SearchManager searchManager) {
        super(BoxLayout.Y_AXIS);
        this.searchManager = searchManager;
        setBorder(new EmptyBorder(5, 5, 0, 5));
        searchbar = new CustomSearchField();
        searchbar.setPreferredSize(new Dimension(200, searchbar.getPreferredSize().height));
        searchbar.setMaximumSize(new Dimension(200, searchbar.getPreferredSize().height));
        UIUtils.addChangeListener(searchbar, e -> {
            if (worker != null) worker.cancel(true);
            worker = new AutocompleteWorker(searchbar.getText(), a -> searchbar.setSuggestions(a.getTags().stream().map(Tag::getName).toList()));
            worker.execute();
        });
        searchbar.addActionListener(e -> {
            selectedTags.add(searchbar.getText());
            searchbar.setText("");
            searchbar.clearHistory();
            refresh();
        });
        Box boxTop = Box.createHorizontalBox();
        boxTop.add(searchbar);
        boxTop.add(searchR18 = new TristateCheckBox("R18") {{
            setState(TristateState.INDETERMINATE_SEL);
        }});
        boxTop.add(searchSort = new SortPane.DefaultSortOption("Date", SortPane.SortOption.SortDirection.DOWN, Comparator.comparing(Illustration::getCreateDate), TagSearchFilter.this));
        boxTop.add(searchBookmark = new JComboBox<>(Stream.concat(Stream.of((SearchedIllustsFilter.BookmarkCount) null), Arrays.stream(SearchedIllustsFilter.BookmarkCount.values()).sorted((o1, o2) -> {
            if (o1.name().length() != o2.name().length()) return o2.name().length() - o1.name().length();
            return o1.name().compareTo(o2.name());
        })).toArray(SearchedIllustsFilter.BookmarkCount[]::new)));
        boxTop.add(searchPartial = new JCheckBox("Partial"));
        boxTop.add(searchTranslate = new TristateCheckBox("Translate") {{
            setState(TristateState.INDETERMINATE_SEL);
        }});
        boxTop.add(Box.createHorizontalGlue());

        boxTop.add(new JCheckBox("Tools", false) {{
            addActionListener(e -> searchManager.filter.setToolsShown(isSelected()));
        }});
        boxTop.add(new IconButton(Icons.down.get(), e -> {
            searchManager.filter.setAdvancedControls(!searchManager.filter.isAdvancedControls());
            ((IconButton) e.getSource()).setIcon(searchManager.filter.isAdvancedControls() ? Icons.up.get() : Icons.down.get());
        }));
        boxTop.add(new IconButton(AllIcons.Action.Refresh.get(), this));
        add(boxTop);
        add(new OverlayScrollPane(filtersPanel = new JPanel(new WrapLayout(FlowLayout.LEADING)), JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));
        add(filter = new JLabel() {{
            setAlignmentX(CENTER_ALIGNMENT);
            setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        }});
    }

    public void refresh() {
        filtersPanel.removeAll();
        selectedTags.forEach(tag -> filtersPanel.add(new TagFilter.TagLabel(tag, e -> {
            selectedTags.remove(tag);
            refresh();
        })));
        filtersPanel.revalidate();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (Objects.equals(searchManager.lastFilter, getFilter())) searchManager.getMoreSearch();
        else searchManager.reset();
    }

    public SearchedIllustsFilter getFilter() {
        SearchedIllustsFilter filter = SearchedIllustsFilter.get(String.join(" ", selectedTags), searchR18.getState().isIndeterminate() ? null : searchR18.getState().equals(TristateState.SELECTED), (SearchedIllustsFilter.BookmarkCount) searchBookmark.getSelectedItem());
        filter.setSort(searchSort.getDirection() == SortPane.SortOption.SortDirection.UNDEFINED ? null :
                searchSort.getDirection() == SortPane.SortOption.SortDirection.UP ? SortOption.SORT_DATE_ASC : SortOption.SORT_DATE_DESC);
        if (searchPartial.isSelected()) filter.setSearchTarget(SearchTarget.PARTIAL_MATCH_FOR_TAGS);
        if (!searchTranslate.getState().isIndeterminate())
            filter.setIncludeTranslatedTagResults(searchTranslate.isSelected());
        this.filter.setText(filter.getWord());
        return filter;
    }
}
