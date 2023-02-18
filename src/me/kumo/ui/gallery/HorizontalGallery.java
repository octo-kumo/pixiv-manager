package me.kumo.ui.gallery;

import javax.swing.*;
import java.awt.*;

public class HorizontalGallery extends Gallery {
    public HorizontalGallery() {
        getHorizontalScrollBar().setUnitIncrement(32);
        setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        setPreferredSize(new Dimension(720, GalleryImage.GRID_SIZE));
    }

    public void updateLayout(int count) {
        layoutItemCount = count;
        grid.setLayout(new GridLayout(1, 0, 0, 0));
        grid.revalidate();
        grid.repaint();
    }
}
