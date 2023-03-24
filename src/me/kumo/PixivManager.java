package me.kumo;

import com.github.weisj.darklaf.LafManager;
import me.kumo.io.Icons;
import me.kumo.io.LocalGallery;
import me.kumo.pixiv.Pixiv;
import me.kumo.ui.MainControl;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.io.File;
import java.util.Objects;
import java.util.prefs.Preferences;

public class PixivManager extends JFrame implements WindowFocusListener {
    public static final String PIXIV_TOKEN = "pixiv_token";
    public static final String GALLERY_PATH = "gallery_path";
    public static Preferences preferences = Preferences.userNodeForPackage(PixivManager.class);

    public MainControl getControls() {
        return controls;
    }

    private final MainControl controls;

    public PixivManager() {
        super("Pixiv Manager");

        Pixiv pixiv = new Pixiv();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setContentPane(controls = new MainControl(pixiv));
        setJMenuBar(new PixivMenu(this));
        addWindowFocusListener(controls);
        pack();
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        LafManager.install(LafManager.themeForPreferredStyle(LafManager.getPreferredThemeStyle()));
        Toolkit.getDefaultToolkit().setDynamicLayout(false);
        LocalGallery.setPath(getPathOrNothing(false));

        System.getProperties().put("proxySet", "true");
        System.getProperties().put("socksProxyHost", "127.0.0.1");
        System.getProperties().put("socksProxyPort", "1080");

        PixivManager pixivManager = new PixivManager();
        pixivManager.setVisible(true);
        pixivManager.askForToken(false);
    }

    public void askForToken(boolean force) {
        String pixivToken = getTokenOrNothing(force);
        if (!force && pixivToken == null) {
            JOptionPane.showMessageDialog(null, "Ok");
            System.exit(1);
            System.out.println("1");
            return;
        }
        if (pixivToken != null) {
            preferences.put(PIXIV_TOKEN, pixivToken);
            Pixiv.getInstance().setToken(pixivToken);
        }
    }

    public static String getTokenOrNothing(boolean force) {
        String pixivToken = preferences.get(PIXIV_TOKEN, null);
        if (pixivToken != null && !force) return pixivToken;
        pixivToken = (String) JOptionPane.showInputDialog(null, "You can obtain pixiv token via dev tools in browser.", "Pixiv Refresh Token", JOptionPane.QUESTION_MESSAGE, Icons.pixiv.get(), null, pixivToken);
        return pixivToken;
    }

    public static String getPathOrNothing(boolean force) {
        String galleryPath = preferences.get(GALLERY_PATH, null);
        if (galleryPath != null && !force) return galleryPath;
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setDialogTitle("Select Gallery Path");
        int res = fileChooser.showOpenDialog(null);
        if (res == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (file == null || !file.isDirectory()) {
                JOptionPane.showMessageDialog(null, fileChooser.getSelectedFile(), "Invalid Selection", JOptionPane.ERROR_MESSAGE);
                return null;
            }
            galleryPath = file.getPath();
            JOptionPane.showMessageDialog(null, galleryPath + "\n" + Objects.requireNonNull(file.list()).length + " files", "Selected Gallery Folder", JOptionPane.INFORMATION_MESSAGE);
        }
        return galleryPath;
    }

    @Override
    public void windowGainedFocus(WindowEvent e) {
        LocalGallery.update();
    }

    @Override
    public void windowLostFocus(WindowEvent e) {
    }
}