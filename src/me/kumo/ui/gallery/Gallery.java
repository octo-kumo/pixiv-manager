package me.kumo.ui.gallery;

import com.github.hanshsieh.pixivj.model.Illustration;
import com.github.weisj.darklaf.components.OverlayScrollPane;
import me.kumo.io.LocalGallery;
import me.kumo.ui.Refreshable;
import me.kumo.ui.utils.Formatters;
import me.tongfei.progressbar.ProgressBar;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;

public class Gallery extends OverlayScrollPane implements ComponentListener, Refreshable<List<Illustration>>, Iterable<GalleryItem>, MouseWheelListener, ActionListener, AdjustmentListener {
    public final JPanel grid;
    protected final ConcurrentHashMap<Long, GalleryItem> holderMap = new ConcurrentHashMap<>();
    private final Stack<GalleryItem> usedPool = new Stack<>();
    protected int layoutItemCount;
    private int colCount;
    private SwingWorker<Object, Object> worker;

    private long last_frame_nanos = 0;

    public Gallery() {
        this(5);
    }

    public Gallery(int colCount) {
        super(null, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        this.colCount = colCount;
        this.grid = new JPanel();
        new Timer(8, this).start();
        getScrollPane().setViewportView(this.grid);
        setPreferredSize(new Dimension(720, 480));
        getScrollPane().setWheelScrollingEnabled(false);
        getScrollPane().addMouseWheelListener(this);
        getSmoothScrollbar().addAdjustmentListener(this);
        addComponentListener(this);
    }

    protected long lastShownUpdate = 0;

    protected void updateShownStatus() {
        if ((System.nanoTime() - lastShownUpdate) / 1e9 < 0.032) return;
        lastShownUpdate = System.nanoTime();
        Rectangle visibleRect = getScrollPane().getViewport().getViewRect();
        for (Component c : grid.getComponents()) {
            if (!(c instanceof GalleryItem item)) continue;
            item.setShown(item.isDisplayable() && visibleRect.intersects(item.getBounds()));
            if (item.isShown()) {
                double px = 2 * (visibleRect.getCenterX() - item.getBounds().getCenterX()) / visibleRect.getWidth();
                double py = 2 * (visibleRect.getCenterY() - item.getBounds().getCenterY()) / visibleRect.getHeight();
                item.image.setRestParallax(px, py);
            }
        }
    }

    public void refresh(Illustration illustration) {
        usedPool.stream().filter(p -> Objects.equals(p.getIllustration().getId(), illustration.getId()))
                .forEach(i -> i.refresh(illustration));
    }

    public void append(List<Illustration> illustrations) {
        if (worker != null) worker.cancel(true);
        worker = new SwingWorker<>() {
            @Override
            protected Object doInBackground() {
                updateLayout(illustrations.size() + layoutItemCount);
                for (Illustration illustration : illustrations) {
                    GalleryItem holder = getRefreshOrCreate(illustration);
                    RepaintManager.currentManager(grid).markCompletelyClean(holder);
                    usedPool.push(holder);
                    grid.add(holder);
                    try {
                        if (isCancelled()) return null;
                        Thread.sleep(0);
                    } catch (InterruptedException ignored) {
                        ignored.printStackTrace();
                        return null;
                    }
                }
                grid.revalidate();
                updateShownStatus();
                return null;
            }
        };
        SwingUtilities.invokeLater(() -> worker.execute());
    }

    public void refresh(List<Illustration> illustrations) {
        if (worker != null) worker.cancel(true);
        System.out.println("gallery.refresh(" + illustrations.size() + ")");
        worker = new SwingWorker<>() {
            @Override
            protected Object doInBackground() {
                int hScroll = getHorizontalScrollBar().getValue();
                int vScroll = getVerticalScrollBar().getValue();
                System.out.println("gallery.doInBackground(" + illustrations.size() + ")");
                grid.removeAll();
                usedPool.clear();
                updateLayout(illustrations.size());
                try (ProgressBar bar = new ProgressBar("Refresh Illustraions", illustrations.size())) {
                    for (Illustration illustration : illustrations) {
                        GalleryItem holder = getRefreshOrCreate(illustration);
                        RepaintManager.currentManager(grid).markCompletelyClean(holder);
                        usedPool.push(holder);
                        grid.add(holder);
                        bar.step();
                        try {
                            if (isCancelled()) return null;
                            Thread.sleep(0);
                        } catch (InterruptedException ignored) {
                            ignored.printStackTrace();
                            return null;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println("gallery.doInBackground.validate(" + grid.getComponentCount() + ")");
                grid.revalidate();
                SwingUtilities.invokeLater(() -> {
                    updateShownStatus();
                    getHorizontalScrollBar().setValue(hScroll);
                    getVerticalScrollBar().setValue(vScroll);
                });
                return null;
            }
        };
        SwingUtilities.invokeLater(() -> worker.execute());
    }

    public GalleryItem getRefreshOrCreate(Illustration illustration) {
        GalleryItem holder = holderMap.computeIfAbsent(illustration.getId(), i -> new GalleryItem());
        holder.refresh(illustration);
        return holder;
    }


    @Override
    public void paint(Graphics g) {
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
        int barWidth = 200 - barPad;
        g.setXORMode(Color.BLACK);
        g.drawString(String.format("OS:   %s (%s)", System.getProperty("os.name"), System.getProperty("os.version")), x, y += LINE_HEIGHT);
        g.drawString(String.format("ARCH: %s (%d cores)", System.getProperty("os.arch"), runtime.availableProcessors()), x, y += LINE_HEIGHT);

        long usedMem = runtime.totalMemory() - runtime.freeMemory();
        g.fillRect(x + barPad, y + LINE_HEIGHT / 4, (int) (barWidth * runtime.totalMemory() / runtime.maxMemory()), LINE_HEIGHT);
        g.setColor(Color.RED);
        g.fillRect(x + barPad, y + LINE_HEIGHT / 4, (int) (barWidth * usedMem / runtime.maxMemory()), LINE_HEIGHT);
        g.setColor(Color.WHITE);
        g.drawString("MEM:  %8s/%s/%s".formatted(Formatters.formatBytes(usedMem), Formatters.formatBytes(runtime.totalMemory()), Formatters.formatBytes(runtime.maxMemory())), x, y += LINE_HEIGHT);
        g.drawString("FPS:  %5.1f".formatted(1e9d / -(this.last_frame_nanos - (this.last_frame_nanos = System.nanoTime()))), x, y += LINE_HEIGHT);

        if (layoutItemCount != 0)
            g.fillRect(x + barPad, y + LINE_HEIGHT / 4, barWidth * grid.getComponentCount() / layoutItemCount, LINE_HEIGHT);
        g.drawString("Cnt:  %d".formatted(grid.getComponentCount()), x, y += LINE_HEIGHT);
        String s = getClass().getPackage().getImplementationVersion();
        g.drawString(String.format("Ver:  %s", s == null ? "dev" : s), x, y += LINE_HEIGHT);
    }

    public void updateLayout(int count) {
        layoutItemCount = count;
        grid.setLayout(new GridLayout(0, Math.max(1, Math.min(colCount, count)), 0, 0));
        grid.revalidate();
        grid.repaint();
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
        LocalGallery.update();
        usedPool.forEach(GalleryItem::updateImage);
    }

    @NotNull
    @Override
    public Iterator<GalleryItem> iterator() {
        return usedPool.stream().iterator();
    }

    protected JScrollBar getSmoothScrollbar() {
        return getVerticalScrollBar();
    }

    protected double scrollTarget = 0;
    protected double scrollPosition = 0;

    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
        scrollPosition = scrollPosition * 0.9 + scrollTarget * 0.1;
        if (Math.round(scrollPosition) != getSmoothScrollbar().getValue()) {
            getSmoothScrollbar().setValue((int) Math.round(scrollPosition));
            updateShownStatus();
        }
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        scrollTarget = Math.max(getSmoothScrollbar().getMinimum(), Math.min(getSmoothScrollbar().getMaximum() - getSmoothScrollbar().getVisibleAmount(), scrollTarget + e.getPreciseWheelRotation() * 100));
    }

    @Override
    public void adjustmentValueChanged(AdjustmentEvent e) {
        if (e.getValueIsAdjusting()) {
            if (scrollPosition != (scrollPosition = e.getValue())) updateShownStatus();
            scrollTarget = scrollPosition;
        }
    }
}
