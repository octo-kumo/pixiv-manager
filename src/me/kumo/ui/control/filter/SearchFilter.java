package me.kumo.ui.control.filter;

import com.github.hanshsieh.pixivj.model.Illustration;
import com.github.hanshsieh.pixivj.model.Tag;
import com.github.weisj.darklaf.components.text.SearchTextFieldWithHistory;
import com.github.weisj.darklaf.iconset.AllIcons;
import me.kumo.io.Icons;
import me.kumo.ui.control.ControlPane;
import me.kumo.ui.utils.IconButton;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class SearchFilter extends Box implements IllustrationFilter {
    private final SearchTextFieldWithHistory searchbar;
    private final JCheckBox searchInTags;
    private final JCheckBox ignoreCase;
    private final JCheckBox searchInAuthor;
    private String filter;

    public SearchFilter(ControlPane controlPane) {
        super(BoxLayout.X_AXIS);
        setBorder(new EmptyBorder(5, 5, 0, 5));
        searchbar = new SearchTextFieldWithHistory();
        searchbar.setPreferredSize(new Dimension(200, searchbar.getPreferredSize().height));
        searchbar.setMaximumSize(new Dimension(200, searchbar.getPreferredSize().height));
        searchbar.addActionListener(e -> {
            searchbar.addEntry(filter = searchbar.getText());
            controlPane.applyAll();
        });
        add(searchbar);
        add(searchInTags = new JCheckBox("Tags") {{
            addActionListener(e -> controlPane.applyAll());
        }});
        add(searchInAuthor = new JCheckBox("Author") {{
            addActionListener(e -> controlPane.applyAll());
        }});
        add(ignoreCase = new JCheckBox("Ignore Case") {{
            addActionListener(e -> controlPane.applyAll());
        }});
        add(Box.createHorizontalGlue());
        add(new IconButton(Icons.Down, e -> {
            controlPane.setAdvancedControls(!controlPane.isAdvancedControls());
            ((IconButton) e.getSource()).setIcon(controlPane.isAdvancedControls() ? Icons.Up : Icons.Down);
        }));
        add(new IconButton(AllIcons.Action.Delete.get(), e -> {
            controlPane.reset();
            controlPane.applyAll();
        }));
    }

    @Override
    public void reset() {
        searchbar.setText(filter = "");
        searchInTags.setSelected(false);
    }

    @Override
    public boolean test(Illustration illustration) {
        if (filter == null || filter.isEmpty()) return true;
        if (searchInTags.isSelected() && illustration.getTags().stream().anyMatch(this::testTag))
            return true;
        if (searchInAuthor.isSelected() && (startsWith(illustration.getUser().getId().toString(), filter) ||
                contains(illustration.getUser().getAccount(), filter) ||
                contains(illustration.getUser().getName(), filter))) return true;

        return startsWith(illustration.getId().toString(), filter) ||
                contains(illustration.getTitle(), filter) ||
                contains(illustration.getCaption(), filter);
    }

    private boolean testTag(Tag tag) {
        return tag.getTranslatedName() != null && contains(tag.getTranslatedName(), filter) ||
                tag.getName() != null && contains(tag.getName(), filter);
    }

    private boolean startsWith(String a, String b) {
        return ignoreCase.isSelected() ? a.toUpperCase().startsWith(b.toUpperCase()) : a.startsWith(b);
    }

    private boolean contains(String a, String b) {
        return ignoreCase.isSelected() ? a.toUpperCase().contains(b.toUpperCase()) : a.contains(b);
    }
}
