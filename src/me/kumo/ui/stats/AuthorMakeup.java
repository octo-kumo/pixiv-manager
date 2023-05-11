package me.kumo.ui.stats;

import com.github.hanshsieh.pixivj.model.Author;
import com.github.weisj.darklaf.components.OverlayScrollPane;
import me.kumo.components.piechart.PieChart;
import me.kumo.io.NetIO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AuthorMakeup extends JPanel {

    private final Map<Author, Long> frequency;

    public AuthorMakeup(List<Author> authors) {
        super(new BorderLayout());
        frequency = authors.stream().collect(Collectors.groupingBy(Function.identity(),
                Collectors.counting()));
        authors = authors.stream().distinct()
                .sorted(Comparator.comparing(frequency::get).reversed()).collect(Collectors.toList());
        add(new PieChart(authors.stream().map(a -> new PieChart.PieChartItem() {
            @Override
            public String getName() {
                return a.getName();
            }

            @Override
            public long getAmount() {
                return frequency.get(a);
            }

            @Override
            public void onClicked() {

            }
        }).toList()), BorderLayout.CENTER);
        List<Author> finalAuthors = authors;
        add(new OverlayScrollPane(new JList<>(new Vector<>(authors.stream().map(a -> a.getName() + " (" + frequency.get(a) + ")").toList())) {{
            addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        int selectedIndex = getSelectedIndex();
                        if (selectedIndex != -1)
                            NetIO.open(URI.create("https://pixiv.net/users/" + finalAuthors.get(selectedIndex).getId()));
                    }
                }
            });
        }}, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER), BorderLayout.EAST);
    }
}
