package me.kumo.ui.filter.tags;

import com.github.weisj.darklaf.LafManager;
import com.github.weisj.darklaf.iconset.AllIcons;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class TagLabel extends JLabel implements MouseListener {

    private ActionListener actionListener;

    public TagLabel(String tag) {
        super(tag, AllIcons.Window.Close.get(), CENTER);
        addMouseListener(this);

        setBorder(new LineBorder(getForeground(), 1));
    }

    public TagLabel(String tag, ActionListener listener) {
        this(tag);
        setActionListener(listener);
    }

    public void setActionListener(ActionListener actionListener) {
        this.actionListener = actionListener;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        actionListener.actionPerformed(new ActionEvent(e.getSource(), e.getID(), e.paramString()));
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
