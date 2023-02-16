package me.kumo;

import com.github.weisj.darklaf.LafManager;
import me.kumo.io.Loader;
import me.kumo.io.pixiv.Pixiv;
import me.kumo.ui.ControlPanel;

import javax.swing.*;
import java.awt.*;

public class Main extends JFrame {
    private final Pixiv pixiv;

    public Main() {
        super("Pixiv Manager");

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setContentPane(new ControlPanel(Loader.illustrations));


        pack();
        setLocationRelativeTo(null);

        pixiv = new Pixiv();
    }

    public static void main(String[] args) {
        LafManager.install(LafManager.themeForPreferredStyle(LafManager.getPreferredThemeStyle()));
        Toolkit.getDefaultToolkit().setDynamicLayout(false);

        System.getProperties().put("proxySet", "true");
        System.getProperties().put("socksProxyHost", "127.0.0.1");
        System.getProperties().put("socksProxyPort", "1080");

        System.out.println("Loaded " + Loader.illustrations.length + " artworks");
        Main main = new Main();
        main.setVisible(true);
    }
}