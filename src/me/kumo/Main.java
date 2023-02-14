package me.kumo;

import com.github.weisj.darklaf.LafManager;
import me.kumo.io.Loader;
import me.kumo.ui.gallery.Gallery;

import javax.swing.*;
import java.awt.*;

public class Main extends JFrame {
    public Main() {
        super("Pixiv Manager");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setContentPane(new Gallery());

        pack();
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        LafManager.install(LafManager.themeForPreferredStyle(LafManager.getPreferredThemeStyle()));
        Toolkit.getDefaultToolkit().setDynamicLayout(false);
        System.out.println("Loaded " + Loader.illustrations.length + " artworks");
        Main main = new Main();
        main.setVisible(true);
    }
}