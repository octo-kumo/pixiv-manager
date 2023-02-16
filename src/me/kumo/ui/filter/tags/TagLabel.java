package me.kumo.ui.filter.tags;

import com.github.weisj.darklaf.LafManager;
import com.github.weisj.darklaf.iconset.AllIcons;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class TagLabel extends JButton implements MouseListener {

    public TagLabel(String tag, ActionListener listener) {
        super(tag, AllIcons.Window.Close.get());
        addMouseListener(this);
        addActionListener(listener);
        setBorder(new LineBorder(getForeground(), 1));
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
        setForeground(LafManager.getTheme().getAccentColorRule().getAccentColor());
        setBorder(new LineBorder(getForeground(), 1));
    }

    @Override
    public void mouseExited(MouseEvent e) {
        setForeground(UIManager.getColor("Label.foreground"));
        setBorder(new LineBorder(getForeground(), 1));
    }
}
