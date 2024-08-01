package me.kumo;

import com.github.weisj.darklaf.LafManager;
import com.github.weisj.darklaf.iconset.AllIcons;
import me.kumo.io.Icons;
import me.kumo.io.LocalGallery;
import me.kumo.pixiv.Pixiv;
import me.kumo.ui.MainControl;
import me.kumo.ui.gallery.GalleryImage;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.Objects;
import java.util.prefs.Preferences;

public class PixivManager extends JFrame {
    public static final String PIXIV_TOKEN = "pixiv_token";
    public static final String GRID_SIZE = "grid_size";
    public static final String GALLERY_PATH = "gallery_path";
    private static final String PROXY = "proxy";
    public static Preferences preferences = Preferences.userNodeForPackage(PixivManager.class);
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
        Version.checkForUpdates();
        GalleryImage.GRID_SIZE = preferences.getInt(GRID_SIZE, 200);
        if (PixivManager.getProxy() != null) {
            String[] p = PixivManager.getProxy().split(":");
            if (p.length > 1) {
                System.getProperties().put("proxySet", "true");
                System.getProperties().put("socksProxyHost", p[0]);
                System.getProperties().put("socksProxyPort", p[1]);
            }
        }
        PixivManager pixivManager = new PixivManager();
        pixivManager.setVisible(true);
        pixivManager.askForToken(false);
    }

    public static String getProxy() {
        return preferences.get(PROXY, null);
    }

    public static void setProxy(String proxy) {
        if (proxy == null) preferences.remove(PROXY);
        else preferences.put(PROXY, proxy);
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
        preferences.put(GALLERY_PATH, galleryPath);
        return galleryPath;
    }

    public static int getSizeOrNothing() {
        String size = preferences.get(GRID_SIZE, String.valueOf(GalleryImage.GRID_SIZE));
        size = (String) JOptionPane.showInputDialog(null, "Size of the gallery grid cells", "Change Grid Size", JOptionPane.QUESTION_MESSAGE, AllIcons.Dialog.Question.get(), null, size);
        return size == null ? GalleryImage.GRID_SIZE : Integer.parseInt(size);
    }

    public MainControl getControls() {
        return controls;
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

    public void askForSize() {
        int size = getSizeOrNothing();

        if (size > 0) {
            preferences.putInt(GRID_SIZE, size);
            GalleryImage.GRID_SIZE = size;
            controls.revalidate();
            System.out.println("changed size to " + size);
        }
    }
}