package me.kumo.ui.viewer;

import com.github.hanshsieh.pixivj.model.Illustration;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

public class IllustrationViewer extends JPanel {
    public final Timer timer;

    public IllustrationViewer(JDialog dialog, Illustration illustration, BufferedImage thumb) {
        super(new BorderLayout());
        Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
        size.setSize(illustration.getWidth() * size.getHeight() * 0.7 / illustration.getHeight(), size.getHeight() * 0.7);
        JComponent center;
        if (illustration.getPageCount() > 1) {
            add(center = new JTabbedPane() {{
                for (int i = 0; i < illustration.getMetaPages().size(); i++)
                    addTab(String.valueOf(i + 1), new ViewerImage(illustration, i, thumb));
            }}, BorderLayout.CENTER);
        } else {
            add(center = new ViewerImage(illustration, 0, thumb), BorderLayout.CENTER);
        }
        center.setPreferredSize(size);

        add(new BigIllustrationInfo(illustration), BorderLayout.EAST);
        add(new Comments(illustration), BorderLayout.WEST);
        dialog.setFocusTraversalPolicy(new DefaultFocusTraversalPolicy() {
            @Override
            public Component getDefaultComponent(Container aContainer) {
                return center;
            }
        });
        timer = new Timer(16, e -> repaint());
    }

    public static void show(Frame owner, Illustration illustration, BufferedImage thumb) {
        JDialog dialog = new JDialog(owner, illustration.getTitle(), true);
        IllustrationViewer viewer = new IllustrationViewer(dialog, illustration, thumb);
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
