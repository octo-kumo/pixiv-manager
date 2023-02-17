package me.kumo.ui.gallery;

import com.github.hanshsieh.pixivj.model.Illustration;
import com.github.weisj.darklaf.components.OverlayScrollPane;
import me.kumo.ui.Refreshable;
import me.kumo.ui.utils.Formatters;

import javax.swing.Timer;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.List;
import java.util.*;

public class Gallery extends OverlayScrollPane implements ComponentListener, Refreshable<List<Illustration>> {
    private final JPanel grid;
    private final Stack<GalleryItem> usedPool = new Stack<>();
    private final HashMap<Long, GalleryItem> holderMap = new HashMap<>();
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
        new Timer(16, e -> repaint()).start();
        getScrollPane().setViewportView(this.grid);
        getVerticalScrollBar().setUnitIncrement(32);
        setPreferredSize(new Dimension(720, 480));
        getScrollPane().getViewport().addChangeListener(e -> updateShownStatus());
        addComponentListener(this);
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
                for (GalleryItem galleryItem : usedPool) galleryItem.setVisible(false);
                usedPool.clear();
                for (Illustration illustration : illustrations) {
                    GalleryItem holder = holderMap.computeIfAbsent(illustration.getId(), i -> new GalleryItem());
                    holder.refresh(illustration);
                    holder.setVisible(true);
                    usedPool.push(holder);
                    RepaintManager.currentManager(grid).markCompletelyClean(holder);
                    grid.add(holder);
                    if (isCancelled()) return null;
                    try {
                        Thread.sleep(0);
                    } catch (InterruptedException ignored) {
                        return null;
                    }
                }
                grid.revalidate();
                SwingUtilities.invokeLater(() -> {
                    updateShownStatus();
                    repaint();
                });
                return null;
            }
        };
        SwingUtilities.invokeLater(() -> worker.execute());
    }


    @Override
    public void paint(Graphics g) {
        if (System.nanoTime() - this.last_frame_nanos < 16e-3) return;
        super.paint(g);
        drawDebug(g);
    }

    private void drawDebug(Graphics g) {
        int lines = 6;
        int LINE_HEIGHT = g.getFontMetrics().getHeight();
        int x = getWidth() - 200, y = getHeight() - LINE_HEIGHT * lines;

        g.setColor(new Color(0xaa000000, true));
        g.fillRect(x, y, 200, LINE_HEIGHT * lines);

        g.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 10));
        g.setColor(Color.WHITE);
        Runtime runtime = Runtime.getRuntime();
        y -= LINE_HEIGHT / 2;
        x += 4;
        y += 4;
        int barPad = g.getFontMetrics().stringWidth("MEM:  ");
        g.setXORMode(Color.BLACK);
        g.drawString(String.format("OS:   %s (%s)", System.getProperty("os.name"), System.getProperty("os.version")), x, y += LINE_HEIGHT);
        g.drawString(String.format("ARCH: %s (%d cores)", System.getProperty("os.arch"), runtime.availableProcessors()), x, y += LINE_HEIGHT);

        long usedMem = runtime.totalMemory() - runtime.freeMemory();

        g.fillRect(x + barPad, y + LINE_HEIGHT / 4, (int) ((200 - barPad) * runtime.totalMemory() / runtime.maxMemory()), LINE_HEIGHT);
        g.setColor(Color.RED);
        g.fillRect(x + barPad, y + LINE_HEIGHT / 4, (int) ((200 - barPad) * usedMem / runtime.maxMemory()), LINE_HEIGHT);
        g.setColor(Color.WHITE);
        g.drawString("MEM:  %8s/%s/%s".formatted(Formatters.formatBytes(usedMem), Formatters.formatBytes(runtime.totalMemory()), Formatters.formatBytes(runtime.maxMemory())), x, y += LINE_HEIGHT);
        g.drawString("FPS:  %5.1f".formatted(1e9d / -(this.last_frame_nanos - (this.last_frame_nanos = System.nanoTime()))), x, y += LINE_HEIGHT);
        g.drawString("Cnt:  %d".formatted(grid.getComponentCount()), x, y += LINE_HEIGHT);
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
}
