package me.kumo.ui.filter.options;

import com.github.hanshsieh.pixivj.model.Illustration;
import me.kumo.io.Icons;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.util.Comparator;

public class SortOption extends JButton implements Comparator<Illustration> {
    private SortDirection direction;
    private final Comparator<Illustration> comparator;

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
                return UP;
            }
        }, UP() {
            @Override
            public SortDirection next() {
                return DOWN;
            }
        }, DOWN() {
            @Override
            public SortDirection next() {
                return UNDEFINED;
            }
        };

        public abstract SortDirection next();
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

    public SortOption(String name, SortDirection direction, Comparator<Illustration> comparator, ActionListener actionListener) {
        super(name, getIcon(direction));
        this.direction = direction;
        this.comparator = comparator;

        addActionListener(e -> {
            this.direction = this.direction.next();
            setIcon(getIcon(this.direction));
            actionListener.actionPerformed(null);
        });
    }
}
