package me.kumo.ui.gallery;

import com.github.hanshsieh.pixivj.model.Illustration;
import me.kumo.io.LocalGallery;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class GalleryItem extends JPanel implements MouseListener {
    public final GalleryImage image;
    private final JLabel id;
    private final Box info;
    private boolean shown;
    private Illustration illustration;

    public GalleryItem() {
        setLayout(new OverlayLayout(this));

        setPreferredSize(new Dimension(GalleryImage.GRID_SIZE, GalleryImage.GRID_SIZE));

        id = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                g.setXORMode(Color.white);
                super.paintComponent(g);
            }
        };
        id.setAlignmentX(CENTER_ALIGNMENT);
        addMouseListener(this);
        add(info = new Box(BoxLayout.Y_AXIS) {{
            setVisible(false);
            add(id);
            add(new JButton("Open Pixiv") {{
                addActionListener(e -> {
                    if (illustration != null) {
                        try {
                            Desktop.getDesktop().browse(new URI("https://pixiv.net/en/artworks/" + illustration.getId()));
                        } catch (IOException | URISyntaxException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                });
            }});
//            add(new JButton("Open Image"));
        }});
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
            id.setText(illustration.getId().toString());
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
        info.setVisible(true);
    }

    @Override
    public void mouseExited(MouseEvent e) {
        if (!new Rectangle(getLocationOnScreen(), getSize()).contains(e.getLocationOnScreen()))
            info.setVisible(false);
    }
}
