package me.kumo.ui.gallery;

import com.github.hanshsieh.pixivj.model.Illustration;
import com.github.weisj.darklaf.components.OverlayScrollPane;
import me.kumo.ui.Refreshable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.Arrays;
import java.util.Stack;

public class Gallery extends OverlayScrollPane implements ComponentListener, Refreshable<Illustration[]> {
    private final JPanel grid;
    private final Stack<GalleryItem> usedPool = new Stack<>();
    private final Stack<GalleryItem> freePool = new Stack<>();
    private int colCount;

    public Gallery() {
        this(5);
    }

    public Gallery(int colCount) {
        super(null, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        this.colCount = colCount;
        this.grid = new JPanel();
        getScrollPane().setViewportView(this.grid);
        getVerticalScrollBar().setUnitIncrement(32);

        setPreferredSize(new Dimension(1280, 720));
        getScrollPane().getViewport().addChangeListener(e -> updateShownStatus());
        addComponentListener(this);
    }

    private void updateShownStatus() {
        Rectangle visibleRect = getScrollPane().getViewport().getViewRect();
        Arrays.stream(grid.getComponents())
                .forEach(component -> ((GalleryItem) component)
                        .setShown(component.getBounds().intersects(visibleRect)));
    }

    public void refresh(Illustration[] illustrations) {
        grid.removeAll();
        updateLayout(illustrations.length);
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
        updateShownStatus();
        repaint();
    }

    private void updateLayout(int count) {
        grid.setLayout(new GridLayout(0, Math.max(1, Math.min(colCount, count)), 0, 0));
    }

    @Override
    public void componentResized(ComponentEvent e) {
        if (colCount != (colCount = e.getComponent().getWidth() / GalleryImage.GRID_SIZE))
            updateLayout(grid.getComponentCount());
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
