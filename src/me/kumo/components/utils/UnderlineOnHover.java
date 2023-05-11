package me.kumo.components.utils;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.font.TextAttribute;
import java.util.Map;

public class UnderlineOnHover implements MouseListener {
    private final int mask;

    public UnderlineOnHover() {
        this(0);
    }

    public UnderlineOnHover(int mask) {
        this.mask = mask;
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
        if ((e.getModifiersEx() & mask) == mask) {
            Font font = e.getComponent().getFont();
            Map attributes = font.getAttributes();
            attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
            e.getComponent().setFont(font.deriveFont(attributes));
        }
    }

    @Override
    public void mouseExited(MouseEvent e) {
        Font font = e.getComponent().getFont();
        Map attributes = font.getAttributes();
        attributes.put(TextAttribute.UNDERLINE, -1);
        e.getComponent().setFont(font.deriveFont(attributes));
    }
}
