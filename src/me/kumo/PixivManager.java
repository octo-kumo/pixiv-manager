package me.kumo;

import com.github.weisj.darklaf.LafManager;
import me.kumo.io.LocalGallery;
import me.kumo.io.pixiv.Pixiv;
import me.kumo.ui.BookmarkManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

public class PixivManager extends JFrame implements WindowFocusListener {

    private final BookmarkManager manager;

    public PixivManager() {
        super("Pixiv Manager");

        Pixiv pixiv = new Pixiv();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setContentPane(manager = new BookmarkManager(pixiv));
        addWindowFocusListener(this);
        pack();
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        LafManager.install(LafManager.themeForPreferredStyle(LafManager.getPreferredThemeStyle()));
        Toolkit.getDefaultToolkit().setDynamicLayout(false);
        LocalGallery.update();

        System.getProperties().put("proxySet", "true");
        System.getProperties().put("socksProxyHost", "127.0.0.1");
        System.getProperties().put("socksProxyPort", "1080");

        PixivManager pixivManager = new PixivManager();
        pixivManager.setVisible(true);
    }

    @Override
    public void windowGainedFocus(WindowEvent e) {
        LocalGallery.update();
        manager.tapGallery();
    }

    @Override
    public void windowLostFocus(WindowEvent e) {
    }
}