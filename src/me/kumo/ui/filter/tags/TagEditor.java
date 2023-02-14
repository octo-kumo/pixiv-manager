package me.kumo.ui.filter.tags;

import com.github.hanshsieh.pixivj.model.Illustration;
import me.kumo.io.Loader;
import me.kumo.ui.filter.FilterPane;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

public class TagEditor extends JPanel {
    private final Set<String> selectedTags = new HashSet<>();
    private final JComboBox<String> combobox;
    private final JPanel filtersPanel;
    private final FilterPane filterPane;

    public TagEditor(FilterPane filterPane) {
        super(new BorderLayout());
        this.filterPane = filterPane;
        add(combobox = new JComboBox<>() {{
            setPreferredSize(new Dimension(200, getPreferredSize().height));
            setEditable(true);
            setSelectedIndex(-1);
        }}, BorderLayout.WEST);
        add(filtersPanel = new JPanel(new FlowLayout(FlowLayout.LEADING)), BorderLayout.CENTER);
        AutoCompleteDecorator.decorate(combobox);
        combobox.addActionListener(e -> {
            if (e.getActionCommand().equals("comboBoxEdited")) {
                Object item = combobox.getSelectedItem();
                selectedTags.add(String.valueOf(item));
                combobox.setSelectedIndex(-1);
                filterPane.refresh();
            }
        });

        refreshTags();
    }


    public void refreshTags() {
        filtersPanel.removeAll();
        selectedTags.forEach(tag -> filtersPanel.add(new TagLabel(tag, e -> {
            selectedTags.remove(tag);
            filterPane.refresh();
        })));
        combobox.setModel(new DefaultComboBoxModel<>(Loader.getTags(filter(Arrays.stream(Loader.illustrations)))
                .map(t -> Objects.requireNonNullElse(t.getTranslatedName(), t.getName()))
                .filter(tag -> !selectedTags.contains(tag))
                .toArray(String[]::new)));
    }

    public Stream<Illustration> filter(Stream<Illustration> illustrations) {
        return illustrations.filter(illustration ->
                selectedTags.stream().allMatch(tag ->
                        illustration.getTags().stream().anyMatch(t ->
                                Objects.equals(t.getTranslatedName(), tag) || Objects.equals(t.getName(), tag))));
    }
}
