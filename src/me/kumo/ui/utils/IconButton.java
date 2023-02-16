package me.kumo.ui.utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class IconButton extends JButton implements MouseListener {
    public IconButton() {
        this(null, null);
    }

    public IconButton(Icon icon, ActionListener actionListener) {
        super(icon);
        addActionListener(actionListener);
        setPreferredSize(new Dimension(2 * icon.getIconWidth(), 2 * icon.getIconHeight()));
        setContentAreaFilled(false);
        addMouseListener(this);
        setBorder(null);
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
        setBorder(UIManager.getBorder("Button.border"));
    }

    @Override
    public void mouseExited(MouseEvent e) {
        setBorder(null);
    }
}
