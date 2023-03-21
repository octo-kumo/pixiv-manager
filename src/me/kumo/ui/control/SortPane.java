package me.kumo.ui.control;

import com.github.hanshsieh.pixivj.model.Illustration;
import com.github.weisj.darklaf.components.color.QuickColorChooser;
import me.kumo.io.Icons;
import me.kumo.io.ImageUtils;
import me.kumo.io.LocalGallery;
import me.kumo.ui.utils.WrapLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;
import java.util.stream.Stream;

public class SortPane extends JPanel implements ActionListener {
    private final ControlPane controlPane;
    private final ArrayList<SortOption> options = new ArrayList<>();
    private boolean multiSorting = false;

    public SortPane(ControlPane controlPane) {
        super(new WrapLayout(FlowLayout.LEADING));
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
        options.add(new DefaultSortOption("File", SortOption.SortDirection.UNDEFINED, Comparator.comparing(i -> {
            try {
                return Files.size(LocalGallery.getImage(i.getId()).toPath());
            } catch (IOException | NullPointerException e) {
                return 0L;
            }
        }), this));
        options.add(new ColorSortOption("Palette", SortOption.SortDirection.UNDEFINED, this));

        options.forEach(o -> {
            if (o instanceof JComponent c) add(c);
        });
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

    private interface SortOption extends Comparator<Illustration> {

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
    }

    public static class DefaultSortOption extends JButton implements SortOption {
        protected Comparator<Illustration> comparator;
        private SortOption.SortDirection direction;

        public DefaultSortOption(String name, SortOption.SortDirection direction, Comparator<Illustration> comparator, ActionListener actionListener) {
            super(name, SortOption.getIcon(direction));
            this.setDirection(direction);
            this.comparator = comparator;

            addActionListener(e -> {
                setDirection(this.getDirection().next());
                actionListener.actionPerformed(e);
            });
        }

        @Override
        public void setDirection(SortOption.SortDirection direction) {
            this.direction = direction;
            setIcon(SortOption.getIcon(this.direction));
        }


        @Override
        public SortOption.SortDirection getDirection() {
            return direction;
        }

        @Override
        public Comparator<Illustration> getComparator() {
            return comparator;
        }
    }

    public static class ColorSortOption extends JPanel implements SortOption {

        private final Comparator<Illustration> comparator;
        private final DefaultSortOption sort;

        public ColorSortOption(String name, SortDirection direction, ActionListener actionListener) {
            super(new FlowLayout());
            QuickColorChooser cc = new QuickColorChooser("color", Color.WHITE, c -> actionListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "")));
            comparator = Comparator.comparing(i -> ImageUtils.minColorDifference(cc.getColor(), LocalGallery.getPalette(i.getId())));

            add(cc);
            add(sort = new DefaultSortOption(name, direction, null, actionListener));
        }

        @Override
        public SortDirection getDirection() {
            return sort.getDirection();
        }

        @Override
        public void setDirection(SortDirection direction) {
//            sort.setDirection(direction);
        }

        @Override
        public Comparator<Illustration> getComparator() {
            return comparator;
        }
    }
}
