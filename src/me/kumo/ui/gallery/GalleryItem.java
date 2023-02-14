package me.kumo.ui.gallery;

import com.github.hanshsieh.pixivj.model.Illustration;
import me.kumo.io.LocalGallery;

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
    private final Box controls;
    private boolean shown;
    private Illustration illustration;

    public GalleryItem() {
        setLayout(new OverlayLayout(this));

        setPreferredSize(new Dimension(GalleryImage.GRID_SIZE, GalleryImage.GRID_SIZE));

        addMouseListener(this);
        add(controls = new Box(BoxLayout.Y_AXIS) {{
            setVisible(false);
            add(new JButton("Open Pixiv") {{
                addActionListener(e -> open(illustration));
            }});
            add(new JButton("Open Image") {{
                addActionListener(e -> openFile(illustration));
            }});
        }});
        add(info = new IllustrationInfo());
        add(image = new GalleryImage());
    }

    public GalleryItem(Illustration illustration) {
        this();
        setIllustration(illustration);
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

    public void setShown(boolean shown) {
        this.shown = shown;
        this.image.setShown(shown);
    }

    public boolean isShown() {
        return shown;
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
}
