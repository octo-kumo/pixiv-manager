package me.kumo.ui.control.filter;

import com.github.hanshsieh.pixivj.model.Illustration;
import com.github.weisj.darklaf.LafManager;
import com.github.weisj.darklaf.components.OverlayScrollPane;
import com.github.weisj.darklaf.iconset.AllIcons;
import me.kumo.ui.Refreshable;
import me.kumo.ui.control.ControlPane;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static me.kumo.io.Loader.getTags;

public class TagFilter extends JPanel implements IllustrationFilter, Refreshable<Illustration[]> {
    private final Set<String> selectedTags = new HashSet<>();
    private final JComboBox<String> combobox;
    private final JPanel filtersPanel;
    private final ControlPane controlPane;

    public TagFilter(ControlPane controlPane) {
        super(new BorderLayout());
        this.controlPane = controlPane;
        add(combobox = new JComboBox<>() {{
            setPreferredSize(new Dimension(200, getPreferredSize().height));
            setEditable(true);
            setSelectedIndex(-1);
        }}, BorderLayout.WEST);
        add(new OverlayScrollPane(filtersPanel = new JPanel(new FlowLayout(FlowLayout.LEADING)), JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED), BorderLayout.CENTER);
        AutoCompleteDecorator.decorate(combobox);
        combobox.addActionListener(e -> {
            if (e.getActionCommand().equals("comboBoxEdited")) {
                Object item = combobox.getSelectedItem();
                if (item == null || item.toString().isBlank()) return;
                selectedTags.add(String.valueOf(item));
                combobox.setSelectedIndex(-1);
                SwingUtilities.invokeLater(controlPane::applyAll);
            }
        });
    }

    @Override
    public void refresh(Illustration[] illustrations) {
        filtersPanel.removeAll();
        selectedTags.forEach(tag -> filtersPanel.add(new TagLabel(tag, e -> {
            selectedTags.remove(tag);
            SwingUtilities.invokeLater(controlPane::applyAll);
        })));
        combobox.setModel(new DefaultComboBoxModel<>(getTags(filter(Arrays.stream(illustrations)))
                .map(t -> Objects.requireNonNullElse(t.getTranslatedName(), t.getName()))
                .filter(tag -> !selectedTags.contains(tag))
                .toArray(String[]::new)));
    }

    @Override
    public boolean test(Illustration illustration) {
        return selectedTags.stream().allMatch(tag ->
                illustration.getTags().stream().anyMatch(t ->
                        Objects.equals(t.getTranslatedName(), tag) || Objects.equals(t.getName(), tag)));
    }

    public static class TagLabel extends JButton implements MouseListener {

        public TagLabel(String tag, ActionListener listener) {
            super(tag, AllIcons.Window.Close.get());
            addMouseListener(this);
            addActionListener(listener);
            setBorder(new LineBorder(getForeground(), 1));
        }

        @Override
        public void mouseClicked(MouseEvent e) {
        }

        @Override
        public void mousePressed(MouseEvent e) {
        }

        @Override
        public void mouseReleased(MouseEvent e) {
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            setForeground(LafManager.getTheme().getAccentColorRule().getAccentColor());
            setBorder(new LineBorder(getForeground(), 1));
        }

        @Override
        public void mouseExited(MouseEvent e) {
            setForeground(UIManager.getColor("Label.foreground"));
            setBorder(new LineBorder(getForeground(), 1));
        }
    }
}
