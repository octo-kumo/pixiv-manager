package me.kumo.ui.viewer;

import com.github.hanshsieh.pixivj.model.Illustration;
import com.github.hanshsieh.pixivj.model.MetaPage;
import me.kumo.io.LocalGallery;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

public class IllustrationViewer extends JPanel {
    public final Timer timer;

    public IllustrationViewer(Illustration illustration) {
        super(new BorderLayout());
        Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
        size.setSize(illustration.getWidth() * size.getHeight() * 0.7 / illustration.getHeight(), size.getHeight() * 0.7);
        setPreferredSize(size);
        if (illustration.getPageCount() > 1) {
            add(new JTabbedPane() {{
                List<MetaPage> metaPages = illustration.getMetaPages();
                for (int i = 0; i < metaPages.size(); i++) {
                    addTab(String.valueOf(i + 1), new ViewerImage(LocalGallery.getBestQuality(metaPages.get(i).getImageUrls())));
                }
            }}, BorderLayout.CENTER);
        } else {
            add(new ViewerImage(illustration.getMetaSinglePage().getOriginalImageUrl()), BorderLayout.CENTER);
        }

        timer = new Timer(16, e -> repaint());
    }

    public static void show(Frame owner, Illustration illustration) {
        JDialog dialog = new JDialog(owner, illustration.getTitle(), true);
        IllustrationViewer viewer = new IllustrationViewer(illustration);
        dialog.setContentPane(viewer);
        dialog.addWindowListener(new WindowAdapter() {
            public void windowOpened(WindowEvent e) {
                viewer.timer.start();
            }

            public void windowClosed(WindowEvent e) {
                viewer.timer.stop();
            }
        });
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }
}
