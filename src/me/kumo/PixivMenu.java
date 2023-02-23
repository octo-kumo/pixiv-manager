package me.kumo;

import me.kumo.ui.CorruptedManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class PixivMenu extends JMenuBar {
    public PixivMenu() {
        add(new JMenu("Extra") {{
            add(new AbstractAction("Corrupted Manager") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    new CorruptedManager((Frame) SwingUtilities.getWindowAncestor(PixivMenu.this)).setVisible(true);
                }
            });
        }});
    }
}
