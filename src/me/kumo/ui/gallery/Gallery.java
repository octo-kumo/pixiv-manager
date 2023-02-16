package me.kumo.ui.gallery;

import com.github.hanshsieh.pixivj.model.Illustration;
import com.github.weisj.darklaf.components.OverlayScrollPane;
import me.kumo.ui.Refreshable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Gallery extends OverlayScrollPane implements ComponentListener, Refreshable<List<Illustration>>, ActionListener {
    private final JPanel grid;
    private final Stack<GalleryItem> usedPool = new Stack<>();
    private final HashMap<Long, GalleryItem> freePool = new HashMap<>();
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

        Timer timer = new Timer(16, this);
        setPreferredSize(new Dimension(720, 480));
        getScrollPane().getViewport().addChangeListener(e -> updateShownStatus());
        addComponentListener(this);
        timer.start();
    }

    private void updateShownStatus() {
        Rectangle visibleRect = getScrollPane().getViewport().getViewRect();
        Arrays.stream(grid.getComponents()).forEach(component -> ((GalleryItem) component).setShown(component.getBounds().intersects(visibleRect)));
    }

    public void refresh(List<Illustration> illustrations) {
        grid.removeAll();
        updateLayout(illustrations.size());
        freePool.putAll(usedPool.stream().collect(Collectors.toMap((GalleryItem galleryItem) -> galleryItem.getIllustration().getId(), Function.identity(), (a, b) -> a)));
        usedPool.clear();
        for (Illustration illustration : illustrations) {
            GalleryItem holder = freePool.remove(illustration.getId());
            if (holder == null) holder = new GalleryItem();
            holder.refresh(illustration);
            usedPool.push(holder);
            grid.add(holder);
        }
        SwingUtilities.invokeLater(() -> {
            revalidate();
            usedPool.forEach(JComponent::revalidate);
            updateShownStatus();
            repaint();
        });
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

    public void tapGallery() {
        usedPool.forEach(GalleryItem::updateImage);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        usedPool.stream().filter(GalleryItem::isShown).filter(i -> !i.image.loaded()).forEach(Component::repaint);
    }
}
