package me.kumo.ui.gallery;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseWheelEvent;

public class HorizontalGallery extends Gallery {
    public HorizontalGallery() {
        getHorizontalScrollBar().setUnitIncrement(32);
        setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        setPreferredSize(new Dimension(720, GalleryImage.GRID_SIZE));
        getVerticalScrollBar().removeAdjustmentListener(this);
        getHorizontalScrollBar().addAdjustmentListener(this);
    }

    public void updateLayout(int count) {
        layoutItemCount = count;
        grid.setLayout(new GridLayout(1, 0, 0, 0));
        grid.revalidate();
        grid.repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
        if (getHorizontalScrollBar().getValue() != Math.round(target)) {
            getHorizontalScrollBar().setValue((int) (getHorizontalScrollBar().getValue() * 0.9 + target * 0.1));
            updateShownStatus();
        }
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        target = Math.max(getHorizontalScrollBar().getMinimum(), Math.min(getHorizontalScrollBar().getMaximum(), target + e.getPreciseWheelRotation() * 100));
    }
}
