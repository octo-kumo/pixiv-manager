package me.kumo.ui.control;

import com.github.weisj.darklaf.components.color.QuickColorChooser;
import me.kumo.io.Icons;
import me.kumo.io.ImageUtils;
import me.kumo.io.LocalGallery;
import pixivj.model.Illustration;

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

        options.add(new DefaultSortOption("ID", SortOption.SortDirection.UNDEFINED, Comparator.comparing(Illustration::getId), this));
        options.add(new DefaultSortOption("Views", SortOption.SortDirection.UNDEFINED, Comparator.comparing(Illustration::getTotalView), this));
        options.add(new DefaultSortOption("Bookmarks", SortOption.SortDirection.UNDEFINED, Comparator.comparing(Illustration::getTotalBookmarks), this));
        options.add(new DefaultSortOption("BpV", SortOption.SortDirection.UNDEFINED, Comparator.comparing(illustration -> illustration.getTotalBookmarks() * 1.0 / Math.max(1, illustration.getTotalView())), this));
        options.add(new DefaultSortOption("Date", SortOption.SortDirection.UNDEFINED, Comparator.comparing(Illustration::getCreateDate), this));
        options.add(new DefaultSortOption("Sanity", SortOption.SortDirection.UNDEFINED, Comparator.comparing(Illustration::getSanityLevel), this));
        options.add(new DefaultSortOption("Pixel", SortOption.SortDirection.UNDEFINED, Comparator.comparing(i -> i.getWidth() * i.getHeight()), this));
        options.add(new DefaultSortOption("Pages", SortOption.SortDirection.UNDEFINED, Comparator.comparing(Illustration::getPageCount), this));

        options.forEach(o -> {
            if (o instanceof JComponent c) add(c);
        });

        QuickColorChooser cc = new QuickColorChooser("", Color.WHITE, c -> actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "")));
        JComboBox<Integer> level = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5});
        add(cc);
        add(level);
        int start = options.size();
        options.add(new DefaultSortOption("sRGB", SortOption.SortDirection.UNDEFINED, Comparator.comparing(i ->
                ImageUtils.minColorDifference(cc.getColor(),
                        LocalGallery.getPalette(i.getId()), (Integer) level.getSelectedItem())), this));
        options.add(new DefaultSortOption("Hue", SortOption.SortDirection.UNDEFINED, Comparator.comparing(i ->
                ImageUtils.minHueDifference(cc.getColor(),
                        LocalGallery.getPalette(i.getId()), (Integer) level.getSelectedItem())), this));

        for (int i = start; i < options.size(); i++) {
            if (options.get(i) instanceof JComponent c) add(c);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!multiSorting && e.getSource() instanceof SortOption)
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

    public interface SortOption extends Comparator<Illustration> {

        static Icon getIcon(SortOption.SortDirection direction) {
            switch (direction) {
                case UNDEFINED -> {
                    return Icons.empty.get();
                }
                case UP -> {
                    return Icons.up.get();
                }
                case DOWN -> {
                    return Icons.down.get();
                }
                default -> throw new IllegalStateException("Unexpected value: " + direction);
            }
        }

        default int compare(Illustration o1, Illustration o2) {
            return (getDirection() == SortOption.SortDirection.UP ? 1 : getDirection() == SortOption.SortDirection.DOWN ? -1 : 0) * getComparator().compare(o1, o2);
        }

        default boolean enabled() {
            return getDirection() != SortOption.SortDirection.UNDEFINED;
        }

        SortDirection getDirection();

        void setDirection(SortDirection direction);

        Comparator<Illustration> getComparator();

        enum SortDirection {
            UNDEFINED() {
                @Override
                public SortDirection next(boolean nullable) {
                    return DOWN;
                }
            }, DOWN() {
                @Override
                public SortDirection next(boolean nullable) {
                    return UP;
                }
            }, UP() {
                @Override
                public SortDirection next(boolean nullable) {
                    return nullable ? UNDEFINED : DOWN;
                }
            };

            public abstract SortDirection next(boolean nullable);
        }
    }

    public static class DefaultSortOption extends JButton implements SortOption {
        protected Comparator<Illustration> comparator;
        private SortOption.SortDirection direction;

        public DefaultSortOption(String name, SortOption.SortDirection direction, Comparator<Illustration> comparator, ActionListener actionListener) {
            this(name, direction, comparator, true, actionListener);
        }

        public DefaultSortOption(String name, SortDirection direction, Comparator<Illustration> comparator, boolean nullable, ActionListener actionListener) {
            super(name, SortOption.getIcon(direction));
            this.setDirection(direction);
            this.comparator = comparator;

            addActionListener(e -> {
                setDirection(this.getDirection().next(nullable));
                actionListener.actionPerformed(e);
            });
        }

        @Override
        public SortOption.SortDirection getDirection() {
            return direction;
        }

        @Override
        public void setDirection(SortOption.SortDirection direction) {
            this.direction = direction;
            setIcon(SortOption.getIcon(this.direction));
        }

        @Override
        public Comparator<Illustration> getComparator() {
            return comparator;
        }
    }
}
