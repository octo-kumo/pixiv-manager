package me.kumo.components;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class IconButton extends JButton implements MouseListener {

    private Border border;

    public IconButton() {
        this(null, null);
    }

    public IconButton(Icon icon, ActionListener actionListener) {
        super(icon);
        addActionListener(actionListener);
        setPreferredSize(new Dimension((int) (1.8 * icon.getIconWidth()), (int) (1.8 * icon.getIconHeight())));
        setContentAreaFilled(false);
        addMouseListener(this);
        setBorderPainted(false);
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
        setBorderPainted(true);
    }

    @Override
    public void mouseExited(MouseEvent e) {
        setBorderPainted(false);
    }
}
