package me.kumo.ui.gallery;

import com.github.hanshsieh.pixivj.model.Illustration;
import com.github.weisj.darklaf.iconset.AllIcons;
import me.kumo.io.Icons;
import me.kumo.io.LocalGallery;
import me.kumo.ui.utils.IconButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class GalleryItem extends JPanel implements MouseListener {
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

    public GalleryItem(Illustration illustration) {
        this();
        setIllustration(illustration);
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
            Desktop.getDesktop().open(new File(LocalGallery.getImage(illustration.getId())));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setIllustration(Illustration illustration) {
        this.illustration = illustration;
        if (illustration == null) {
            setVisible(false);
            return;
        }
        setVisible(true);
        try {
            info.setIllustration(illustration);
            this.image.setImage(LocalGallery.getImage(String.valueOf(illustration.getId())));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isShown() {
        return shown;
    }

    public void setShown(boolean shown) {
        this.shown = shown;
        this.image.setShown(shown);
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

    private class ItemToolbar extends JPanel {
        public ItemToolbar() {
            super(new FlowLayout(FlowLayout.TRAILING));
            setOpaque(false);
            setVisible(false);
            setAlignmentY(Component.BOTTOM_ALIGNMENT);
            setBackground(new Color(0x0, true));
            add(new IconButton(Icons.Pixiv, e -> open(illustration)));
            add(new IconButton(AllIcons.Files.Image.get(), e -> openFile(illustration)));
        }
    }
}
