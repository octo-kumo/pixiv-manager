package me.kumo.ui.gallery;

import com.github.hanshsieh.pixivj.model.Illustration;
import me.kumo.io.Loader;
import me.kumo.ui.filter.FilterPane;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.Arrays;
import java.util.Stack;

public class Gallery extends JPanel implements ComponentListener {
    private final JPanel grid;
    private final FilterPane filter;
    private int colCount = 5;

    private Stack<GalleryItem> usedPool = new Stack<>();
    private Stack<GalleryItem> freePool = new Stack<>();

    public Gallery() {
        super(new BorderLayout());
        setPreferredSize(new Dimension(1280, 720));
        add(filter = new FilterPane(this), BorderLayout.NORTH);
        add(new JScrollPane(grid = new JPanel() {{
            setLayout(new GridLayout(0, colCount, 10, 10));
        }}) {{
            getVerticalScrollBar().setUnitIncrement(16);
            getViewport().addChangeListener(e -> {
                Rectangle visibleRect = getViewport().getViewRect();
                Arrays.stream(grid.getComponents())
                        .forEach(component -> ((GalleryItem) component)
                                .setShown(component.getBounds().intersects(visibleRect)));
            });
        }}, BorderLayout.CENTER);
        addComponentListener(this);
        refresh();
    }

    public void refresh() {
        Illustration[] illustrations = filter.filter(Loader.illustrations);
        grid.removeAll();
        grid.setLayout(new GridLayout(0, Math.min(colCount, illustrations.length), 10, 10));
        freePool.addAll(usedPool);
        usedPool.clear();
        for (Illustration illustration : illustrations) {
            if (freePool.empty()) freePool.add(new GalleryItem());
            GalleryItem holder = freePool.pop();
            usedPool.push(holder);
            holder.setIllustration(illustration);
            grid.add(holder);
        }
        revalidate();
        repaint();
    }

    @Override
    public void componentResized(ComponentEvent e) {
        if (colCount != (colCount = e.getComponent().getWidth() / GalleryImage.GRID_SIZE)) {
            grid.setLayout(new GridLayout(0, Math.max(1, colCount), 10, 10));
        }
    }

    @Override
    public void componentMoved(ComponentEvent e) {
    }

    @Override
    public void componentShown(ComponentEvent e) {
    }

    @Override
    public void componentHidden(ComponentEvent e) {
    }
}
