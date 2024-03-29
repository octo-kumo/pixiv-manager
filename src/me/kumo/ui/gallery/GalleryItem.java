package me.kumo.ui.gallery;

import com.github.hanshsieh.pixivj.model.Illustration;
import com.github.weisj.darklaf.components.loading.LoadingIndicator;
import com.github.weisj.darklaf.iconset.AllIcons;
import me.kumo.components.IconButton;
import me.kumo.components.image.RemoteImage;
import me.kumo.components.utils.Formatters;
import me.kumo.io.Icons;
import me.kumo.io.LocalGallery;
import me.kumo.io.NetIO;
import me.kumo.pixiv.BookmarkWorker;
import me.kumo.pixiv.DownloadWorker;
import me.kumo.ui.Refreshable;
import me.kumo.ui.viewer.IllustrationViewer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Objects;
import java.util.stream.Collectors;

public class GalleryItem extends JPanel implements MouseListener, Refreshable<Illustration>, MouseMotionListener {
    public static final int MOUSE_DRAG_TOLERANCE = 25;
    public final GalleryImage image;
    private final ItemToolbar controls;
    protected IllustrationInfo info;
    private Illustration illustration;
    private MouseEvent mouseDownEvent;
    private boolean shown;

    static {
        ToolTipManager.sharedInstance().setInitialDelay(1000);
    }

    public GalleryItem() {
        setLayout(new OverlayLayout(this));

        setPreferredSize(new Dimension(GalleryImage.GRID_SIZE, GalleryImage.GRID_SIZE));
        setTransferHandler(new GalleryItemHandler());
        addMouseListener(this);
        addMouseMotionListener(this);
        add(controls = new ItemToolbar());
        add(info = new IllustrationInfo());
        add(image = new GalleryImage());
        image.addPropertyChangeListener(RemoteImage.THUMBNAIL, evt -> {
            if (evt.getNewValue() != null && illustration != null && evt.getNewValue() instanceof BufferedImage image) {
                LocalGallery.processImage(illustration.getId(), image);
            }
        });
    }

    public Illustration getIllustration() {
        return illustration;
    }

    @Override
    public void refresh(Illustration illustration) {
        if (this.illustration == (this.illustration = illustration)) return;
        if (illustration == null) {
            setVisible(false);
            return;
        }
        setVisible(true);
        updateImage();
        info.setIllustration(illustration);
        controls.refresh(illustration);
        setToolTipText(String.format("<html><body><h3><ruby>%s<rt><code>%d</code></rt></ruby></h3><p>%s</p><p>%s</p><p><b>%s</b></p></body></html>", illustration.getTitle(), illustration.getId(), illustration.getCaption(), illustration.getTags().stream().map(t -> "#" + t.getName()).collect(Collectors.joining(" ")), illustration.getUser().getAccount()));
    }

    public boolean isShown() {
        return shown;
    }

    public void setShown(boolean shown) {
        this.shown = shown;
        this.image.setShown(shown);
    }

    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (!visible) image.unloadImage();
    }

    public void downloadIfNotExist() {
        if (!image.downloaded()) controls.download();
    }

    public void updateImage() {
        File file = LocalGallery.getImage(illustration.getId());
        if (!Objects.equals(illustration.getImageUrls().getMedium(), "https://s.pximg.net/common/images/limit_unknown_360.png"))
            this.image.setUrl(illustration.getImageUrls().getMedium());
        this.image.setLocalFile(file);
//        this.image.setBlurred(illustration.getXRestrict() != 0);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        image.setPressed(true);
        mouseDownEvent = e;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        image.setPressed(false);
        if (mouseDownEvent != null && e.getPoint().distance(mouseDownEvent.getPoint()) < MOUSE_DRAG_TOLERANCE)
            IllustrationViewer.show(SwingUtilities.getWindowAncestor(this), illustration, image.getImage());
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        image.setHover(true);
        controls.bar1.setVisible(true);
    }

    @Override
    public void mouseExited(MouseEvent e) {
        if (!new Rectangle(getLocationOnScreen(), getSize()).contains(e.getLocationOnScreen())) {
            image.setHover(false);
            controls.bar1.setVisible(false);
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        image.setParallax(-e.getX() * 2d / getWidth() + 1, -e.getY() * 2d / getHeight() + 1);
        if (mouseDownEvent != null && e.getPoint().distance(mouseDownEvent.getPoint()) > MOUSE_DRAG_TOLERANCE) {
            image.setPressed(false);
            JComponent c = (JComponent) e.getSource();
            TransferHandler handler = c.getTransferHandler();
            handler.setDragImage(image.getImage());
            handler.exportAsDrag(c, mouseDownEvent, TransferHandler.COPY);
            mouseDownEvent = null;
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        image.setParallax(-e.getX() * 2d / getWidth() + 1, -e.getY() * 2d / getHeight() + 1);
    }

    private class ItemToolbar extends JPanel implements Refreshable<Illustration> {
        private final JPanel bar1;
        private final JPanel bar2;
        private IconButton bookmark;
        private IconButton refresh;
        private IconButton file;
        private IconButton copy;
        private LoadingIndicator refreshProgress;
        private DownloadWorker downloadWorker;

        private boolean done;
        private long total;
        private long progress;

        public ItemToolbar() {
            super();
            setOpaque(false);
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            add(bar2 = new JPanel(new FlowLayout(FlowLayout.TRAILING, 0, 0)) {{
                setOpaque(false);

                add(refreshProgress = new LoadingIndicator(Icons.download.get()) {{
                    setVisible(false);
                }});
                add(refresh = new IconButton(Icons.download.get(), e -> {
                    updateImage();
                    refresh(illustration);
                    if (image.downloaded()) return;
                    download();
                }));
                add(bookmark = new IconButton(Icons.heart_path.get(), e -> {
                    bookmark.setEnabled(false);
                    new BookmarkWorker(illustration, b -> {
                        if (b) downloadIfNotExist();
                        bookmark.setEnabled(true);
                    }).execute();
                }));
            }});
            add(bar1 = new JPanel(new FlowLayout(FlowLayout.TRAILING, 0, 0)) {{
                setOpaque(false);
                setVisible(false);
                add(new IconButton(Icons.pixiv.get(), e -> NetIO.open(illustration)));
                add(copy = new IconButton(AllIcons.Action.Copy.get(), e -> NetIO.copyFile(illustration)));
                add(file = new IconButton(AllIcons.Files.Image.get(), e -> NetIO.openFile(illustration)));
            }});
            add(new Box.Filler(new Dimension(0, 0), new Dimension(Short.MAX_VALUE, Short.MAX_VALUE), new Dimension(Short.MAX_VALUE, Short.MAX_VALUE)));
        }

        public void download() {
            if (downloadWorker != null && !downloadWorker.isDone()) return;
            refreshProgress.setVisible(true);
            refreshProgress.setRunning(true);
            refresh.setVisible(false);
            downloadWorker = new DownloadWorker(illustration, (tracker) -> {
                progress = tracker.getProgress();
                total = tracker.getTotal();
                done = tracker.isDone();
            }, b -> {
                refreshProgress.setVisible(false);
                if (b) {
                    updateImage();
                    refresh(illustration);
                    info.setIllustration(illustration);
                } else refresh.setVisible(true);
            });
            downloadWorker.execute();
        }

        @Override
        public void refresh(Illustration illustration) {
            refreshProgress.setVisible(false);
            boolean downloaded = image.downloaded();
            refresh.setVisible(!downloaded);
            copy.setVisible(downloaded);
            file.setVisible(downloaded);
            bookmark.setIcon(illustration.isBookmarked() ? Icons.heart.get() : Icons.heart_path.get());
        }

        @Override
        public void paint(Graphics g) {
            super.paint(g);
            if (downloadWorker != null && !downloadWorker.isDone()) {
                g.setColor(Color.BLACK);
                g.fillRect(0, 0, getWidth(), 12);
                g.setColor(done ? Color.GREEN : Color.WHITE);
                g.fillRect(0, 0, (int) (getWidth() * (1d * progress / total)), 12);
                g.setXORMode(Color.BLACK);
                g.drawString(Formatters.formatBytes(progress) + "/" + Formatters.formatBytes(total), 5, 10);
            }
        }
    }
}
