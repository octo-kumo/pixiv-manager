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
        if (ignoreCase.isSelected()) filter = filter.toUpperCase();
        if (searchInTags.isSelected() && illustration.getTags().stream().anyMatch(this::testTag))
            return true;

        if (ignoreCase.isSelected())
            return illustration.getId().toString().toUpperCase().startsWith(filter) ||
                    illustration.getTitle().toUpperCase().contains(filter) ||
                    illustration.getCaption().toUpperCase().contains(filter);
        else
            return illustration.getId().toString().startsWith(filter) ||
                    illustration.getTitle().contains(filter) ||
                    illustration.getCaption().contains(filter);
    }

    private boolean testTag(Tag tag) {
        if (ignoreCase.isSelected())
            return tag.getTranslatedName() != null && tag.getTranslatedName().toUpperCase().contains(filter) ||
                    tag.getName() != null && tag.getName().toUpperCase().contains(filter);
        else
            return tag.getTranslatedName() != null && tag.getTranslatedName().contains(filter) ||
                    tag.getName() != null && tag.getName().contains(filter);
    }
}
