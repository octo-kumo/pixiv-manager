package me.kumo.ui.gallery;

import me.kumo.io.ImageUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.util.Objects;

public class GalleryImage extends JComponent implements ComponentListener {
    public static final int GRID_SIZE = 200;

    private SwingWorker<BufferedImage, String> worker;
    private BufferedImage thumbnail;
    private String file;
    private boolean shown;

    public GalleryImage() {
        addComponentListener(this);
        setPreferredSize(new Dimension(GRID_SIZE, GRID_SIZE));
    }

    public void setImage(String file) {
        if (!Objects.equals(this.file, this.file = file)) {
            this.thumbnail = null;
            loadImage();
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHints(ImageUtils.RENDERING_HINTS);
        if (this.thumbnail != null && shown) g2d.drawImage(thumbnail, 0, 0, null);
        else g2d.drawString("Loading...", 0, 10);
    }

    public void setShown(boolean shown) {
        if (shown == this.shown) return;
        this.shown = shown;
        if (shown) {
            loadImage();
        } else {
            if (worker != null) {
                worker.cancel(true);
                worker = null;
            }
        }
    }

    @Override
    public void componentResized(ComponentEvent e) {
        thumbnail = null;
        loadImage();
    }

    private void loadImage() {
        if (!(this.file != null && shown && thumbnail == null)) return;
        if (worker != null) worker.cancel(true);
        final String FILE_PATH = file;
        worker = new SwingWorker<>() {
            @Override
            protected BufferedImage doInBackground() throws Exception {
                return ImageUtils.centerFill(ImageIO.read(new FileInputStream(FILE_PATH)), getWidth(), getHeight());
            }

            @Override
            protected void done() {
                try {
                    thumbnail = get();
                    GalleryImage.this.repaint();
                } catch (Exception ignored) {
                    worker = null;
                }
            }
        };
        worker.execute();
    }

    @Override
    public void componentMoved(ComponentEvent e) {
    }

    @Override
    public void componentShown(ComponentEvent e) {
        shown = true;
    }

    @Override
    public void componentHidden(ComponentEvent e) {
        shown = false;
    }
}
