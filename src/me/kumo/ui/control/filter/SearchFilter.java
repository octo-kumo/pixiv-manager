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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;

public class SearchFilter extends Box implements IllustrationFilter, ActionListener {
    private final SearchTextFieldWithHistory searchbar;
    private final JCheckBox searchInTags;
    private final JCheckBox ignoreCase;
    private final JCheckBox searchInAuthor;
    private final ControlPane controlPane;
    private String filter = "";

    public SearchFilter(ControlPane controlPane) {
        super(BoxLayout.X_AXIS);
        this.controlPane = controlPane;
        setBorder(new EmptyBorder(5, 5, 0, 5));
        searchbar = new SearchTextFieldWithHistory();
        searchbar.setPreferredSize(new Dimension(200, searchbar.getPreferredSize().height));
        searchbar.setMaximumSize(new Dimension(200, searchbar.getPreferredSize().height));
        searchbar.addActionListener(this);
        add(searchbar);
        add(searchInTags = new JCheckBox("Tags") {{
            addActionListener(SearchFilter.this);
        }});
        add(searchInAuthor = new JCheckBox("Author") {{
            addActionListener(SearchFilter.this);
        }});
        add(ignoreCase = new JCheckBox("Ignore Case") {{
            addActionListener(SearchFilter.this);
        }});
        add(Box.createHorizontalGlue());
        add(new IconButton(Icons.down.get(), e -> {
            controlPane.setAdvancedControls(!controlPane.isAdvancedControls());
            ((IconButton) e.getSource()).setIcon(controlPane.isAdvancedControls() ? Icons.up.get() : Icons.down.get());
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
        searchInAuthor.setSelected(false);
    }

    @Override
    public boolean test(Illustration illustration) {
        if (filter == null || filter.isEmpty() || filter.isBlank()) return true;
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

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == searchbar) {
            if (Objects.equals(filter, filter = searchbar.getText())) return;
        } else if (filter == null || filter.isBlank()) return;
        controlPane.applyAll();
    }
}
