package me.kumo.ui.gallery;

import com.github.hanshsieh.pixivj.model.Illustration;
import com.github.weisj.darklaf.components.OverlayScrollPane;
import me.kumo.ui.Refreshable;
import me.kumo.ui.utils.Formatters;

import javax.swing.Timer;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.List;
import java.util.*;

public class Gallery extends OverlayScrollPane implements ComponentListener, Refreshable<List<Illustration>>, ActionListener {
    private final JPanel grid;
    private final Stack<GalleryItem> usedPool = new Stack<>();
    private final HashMap<Long, GalleryItem> freePool = new HashMap<>();
    private int colCount;
    private SwingWorker<Object, Object> worker;
    private int layoutItemCount;
    private long last_frame_nanos = 0;

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
        Arrays.stream(grid.getComponents()).filter(Objects::nonNull).forEach(component -> ((GalleryItem) component).setShown(component.getBounds().intersects(visibleRect)));
    }

    public void refresh(List<Illustration> illustrations) {
        if (worker != null) worker.cancel(true);
        System.out.println("gallery refresh n(" + illustrations.size() + ")");
        worker = new SwingWorker<>() {
            @Override
            protected Object doInBackground() {
                grid.removeAll();
                updateLayout(illustrations.size());
                grid.revalidate();
                for (GalleryItem galleryItem : usedPool) {
                    galleryItem.setVisible(false);
                    freePool.putIfAbsent(galleryItem.getIllustration().getId(), galleryItem);
                }
                usedPool.clear();
                for (Illustration illustration : illustrations) {
                    GalleryItem holder = freePool.remove(illustration.getId());
                    if (holder == null) holder = new GalleryItem();
                    holder.refresh(illustration);
                    usedPool.push(holder);
                    RepaintManager.currentManager(Gallery.this).markCompletelyClean(grid);
                    RepaintManager.currentManager(grid).markCompletelyClean(holder);
                    holder.setVisible(true);
                    grid.add(holder);
                }
                grid.revalidate();
                usedPool.forEach(JComponent::revalidate);
                SwingUtilities.invokeLater(() -> {
                    updateShownStatus();
                    repaint();
                });
                return null;
            }
        };
        worker.execute();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        drawDebug(g);
    }

    private void drawDebug(Graphics g) {
        int LINE_HEIGHT = g.getFontMetrics().getHeight();
        int x = getWidth() - 200, y = getHeight() - LINE_HEIGHT * 5;

        g.setColor(new Color(0xaa000000, true));
        g.fillRect(x, y, 200, LINE_HEIGHT * 5);

        g.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 10));
        g.setColor(Color.WHITE);
        Runtime runtime = Runtime.getRuntime();
        y -= LINE_HEIGHT / 2;
        x += 4;
        y += 4;
        g.drawString(String.format("OS:   %s (%s)", System.getProperty("os.name"), System.getProperty("os.version")), x, y += LINE_HEIGHT);
        g.drawString(String.format("ARCH: %s (%d cores)", System.getProperty("os.arch"), runtime.availableProcessors()), x, y += LINE_HEIGHT);
        g.drawString(String.format("MEM:  %8s / %s", Formatters.formatBytes(runtime.totalMemory() - runtime.freeMemory()), Formatters.formatBytes(runtime.totalMemory())), x, y += LINE_HEIGHT);
        g.drawString(String.format("FPS:  %5.1f", 1e9d / -(this.last_frame_nanos - (this.last_frame_nanos = System.nanoTime()))), x, y += LINE_HEIGHT);
        String s = getClass().getPackage().getImplementationVersion();
        g.drawString(String.format("Ver:  %s", s == null ? "dev" : s), x, y += LINE_HEIGHT);
    }

    private void updateLayout(int count) {
        layoutItemCount = count;
        grid.setLayout(new GridLayout(0, Math.max(1, Math.min(colCount, count)), 0, 0));
        grid.revalidate();
    }

    @Override
    public void componentResized(ComponentEvent e) {
        if (colCount != (colCount = Math.min(e.getComponent().getWidth() / GalleryImage.GRID_SIZE, 7)))
            updateLayout(layoutItemCount);
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
        try {
            repaint();
        } catch (ConcurrentModificationException | NullPointerException ignored) {
        }
    }
}
