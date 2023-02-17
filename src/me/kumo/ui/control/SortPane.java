package me.kumo.ui.control;

import com.github.hanshsieh.pixivj.model.Illustration;
import me.kumo.io.Icons;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;
import java.util.stream.Stream;

public class SortPane extends JPanel implements ActionListener {
    private final ControlPane controlPane;
    private final ArrayList<SortOption> options = new ArrayList<>();
    private boolean multiSorting = false;

    public SortPane(ControlPane controlPane) {
        super(new FlowLayout(FlowLayout.LEADING));
        add(new JCheckBox("Multi-sorting") {{
            addActionListener(e -> {
                multiSorting = isSelected();
                actionPerformed(e);
            });
        }});
        this.controlPane = controlPane;

        options.add(new SortOption("ID", SortOption.SortDirection.UNDEFINED, Comparator.comparing(Illustration::getId), this));
        options.add(new SortOption("View", SortOption.SortDirection.UNDEFINED, Comparator.comparing(Illustration::getTotalView), this));
        options.add(new SortOption("Bookmark", SortOption.SortDirection.UNDEFINED, Comparator.comparing(Illustration::getTotalBookmarks), this));
        options.add(new SortOption("Bookmark Date", SortOption.SortDirection.UNDEFINED, Comparator.comparing(Illustration::getCreateDate), this));
        options.add(new SortOption("Sanity", SortOption.SortDirection.UNDEFINED, Comparator.comparing(Illustration::getSanityLevel), this));

        options.forEach(this::add);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!multiSorting)
            options.stream().filter(o -> !Objects.equals(o, e.getSource())).forEach(o -> o.setDirection(SortOption.SortDirection.UNDEFINED));
        SwingUtilities.invokeLater(controlPane::applyAll);
    }

    public Stream<Illustration> sort(Stream<Illustration> illustrations) {
        for (SortOption option : options)
            if (option.enabled())
                illustrations = illustrations.sorted(option);
        return illustrations;
    }

    public void reset() {
        for (SortOption option : options) option.setDirection(SortOption.SortDirection.UNDEFINED);
    }

    public static class SortOption extends JButton implements Comparator<Illustration> {
        private final Comparator<Illustration> comparator;
        private SortDirection direction;

        public SortOption(String name, SortDirection direction, Comparator<Illustration> comparator, ActionListener actionListener) {
            super(name, getIcon(direction));
            this.direction = direction;
            this.comparator = comparator;

            addActionListener(e -> {
                setDirection(this.direction.next());
                actionListener.actionPerformed(e);
            });
        }

        private static Icon getIcon(SortDirection direction) {
            switch (direction) {
                case UNDEFINED -> {
                    return Icons.Empty;
                }
                case UP -> {
                    return Icons.Up;
                }
                case DOWN -> {
                    return Icons.Down;
                }
                default -> throw new IllegalStateException("Unexpected value: " + direction);
            }
        }

        public void setDirection(SortDirection direction) {
            this.direction = direction;
            setIcon(getIcon(this.direction));
        }

        @Override
        public int compare(Illustration o1, Illustration o2) {
            return (direction == SortDirection.UP ? 1 : direction == SortDirection.DOWN ? -1 : 0) * comparator.compare(o1, o2);
        }

        public boolean enabled() {
            return direction != SortDirection.UNDEFINED;
        }

        enum SortDirection {
            UNDEFINED() {
                @Override
                public SortDirection next() {
                    return DOWN;
                }
            }, DOWN() {
                @Override
                public SortDirection next() {
                    return UP;
                }
            }, UP() {
                @Override
                public SortDirection next() {
                    return UNDEFINED;
                }
            };

            public abstract SortDirection next();
        }
    }
}
