package me.kumo.ui.filter;

import com.github.hanshsieh.pixivj.model.Illustration;
import com.github.weisj.darklaf.components.tristate.TristateCheckBox;
import me.kumo.ui.filter.tags.TagEditor;
import me.kumo.ui.gallery.Gallery;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class FilterPane extends JPanel {
    private final Gallery gallery;
    private final TagEditor tagEditor;

    public FilterPane(Gallery gallery) {
        this.gallery = gallery;
        setLayout(new BorderLayout());
        add(new JPanel(new FlowLayout(FlowLayout.LEADING)) {{
            add(new TristateCheckBox("R-18") {{
                
            }});
        }});
        add(tagEditor = new TagEditor(this), BorderLayout.SOUTH);
    }

    public void refresh() {
        tagEditor.refreshTags();
        gallery.refresh();
        revalidate();
        repaint();
    }

    public Illustration[] filter(Illustration[] illustrations) {
        return tagEditor.filter(Arrays.stream(illustrations)).toArray(Illustration[]::new);
    }
}

