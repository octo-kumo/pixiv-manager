package me.kumo.ui.gallery;

import com.github.hanshsieh.pixivj.model.Illustration;
import com.github.weisj.darklaf.components.loading.LoadingIndicator;
import com.github.weisj.darklaf.iconset.AllIcons;
import me.kumo.io.Icons;
import me.kumo.io.LocalGallery;
import me.kumo.io.pixiv.Pixiv;
import me.kumo.ui.Refreshable;
import me.kumo.ui.utils.FileTransferable;
import me.kumo.ui.utils.IconButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class GalleryItem extends JPanel implements MouseListener, Refreshable<Illustration> {
    public final GalleryImage image;
    private final IllustrationInfo info;
    private final ItemToolbar controls;
    private Illustration illustration;
    private boolean shown;

    public GalleryItem() {
        setLayout(new OverlayLayout(this));

        setPreferredSize(new Dimension(GalleryImage.GRID_SIZE, GalleryImage.GRID_SIZE));

        addMouseListener(this);
        add(controls = new ItemToolbar());
        add(info = new IllustrationInfo());
        add(image = new GalleryImage());
    }

    public static void open(Illustration illustration) {
        if (illustration == null) return;
        try {
            Desktop.getDesktop().browse(new URI("https://pixiv.net/artworks/" + illustration.getId()));
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static void openFile(Illustration illustration) {
        if (illustration == null) return;
        try {
            Desktop.getDesktop().open(LocalGallery.getImage(illustration.getId()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void copyFile(Illustration illustration) {
        FileTransferable ft = new FileTransferable(List.of(LocalGallery.getImage(illustration.getId())));
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ft, (clipboard, contents) ->
                System.out.println("Lost ownership"));
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
    }

    @Override
    public void revalidate() {
        super.revalidate();
    }

    public boolean isShown() {
        return shown;
    }

    public void setShown(boolean shown) {
        this.shown = shown;
        this.image.setShown(shown);
    }

    public void setVisible(boolean aFlag) {
        super.setVisible(aFlag);
        if (!aFlag) image.unload();
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {
        controls.setVisible(true);
    }

    @Override
    public void mouseExited(MouseEvent e) {
        if (!new Rectangle(getLocationOnScreen(), getSize()).contains(e.getLocationOnScreen()))
            controls.setVisible(false);
    }

    public void updateImage() {
        this.image.setFile(LocalGallery.getImage(String.valueOf(illustration.getId())));
    }

    private class ItemToolbar extends JPanel implements Refreshable<Illustration> {
        private final IconButton refresh;
        private final IconButton file;
        private final IconButton copy;
        private final LoadingIndicator refreshProgress;
        private SwingWorker<Boolean, String> worker;

        public ItemToolbar() {
            super(new FlowLayout(FlowLayout.TRAILING));
            setOpaque(false);
            setVisible(false);
            setAlignmentY(Component.BOTTOM_ALIGNMENT);
            setBackground(new Color(0x0, true));

            add(refreshProgress = new LoadingIndicator(AllIcons.Action.Refresh.get()) {{
                setVisible(false);
            }});
            add(refresh = new IconButton(AllIcons.Action.Refresh.get(), e -> {
                LocalGallery.update();
                updateImage();
                refresh(illustration);
                if (image.found()) return;
                download();
            }));

            add(new IconButton(Icons.Pixiv, e -> open(illustration)));
            add(copy = new IconButton(AllIcons.Action.Copy.get(), e -> copyFile(illustration)));
            add(file = new IconButton(AllIcons.Files.Image.get(), e -> openFile(illustration)));
        }

        private void download() {
            worker = new SwingWorker<>() {
                @Override
                protected Boolean doInBackground() {
                    refreshProgress.setVisible(true);
                    refreshProgress.setRunning(true);
                    refresh.setVisible(false);
                    return LocalGallery.downloadIllustration(Pixiv.getInstance(), illustration);
                }

                @Override
                protected void done() {
                    try {
                        refreshProgress.setVisible(false);
                        if (get()) {
                            updateImage();
                            refresh(illustration);
                        } else refresh.setVisible(true);
                    } catch (InterruptedException | ExecutionException ignored) {
                    }
                }
            };
            worker.execute();
        }

        @Override
        public void refresh(Illustration illustration) {
            refreshProgress.setVisible(false);
            if (!image.found()) download();
            refresh.setVisible(!image.found());
            copy.setVisible(image.found());
            file.setVisible(image.found());
        }
    }
}
