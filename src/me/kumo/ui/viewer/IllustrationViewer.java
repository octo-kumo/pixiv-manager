package me.kumo.ui.viewer;

import com.github.hanshsieh.pixivj.model.Illustration;
import me.kumo.components.utils.MathUtils;
import me.kumo.pixiv.Pixiv;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class IllustrationViewer extends JPanel {
    public final Timer timer;
    private final ArrayList<ViewerImage> images = new ArrayList<>();
    private final BigIllustrationInfo info;
    private final Comments comments;
    private JTabbedPane tabs;

    public IllustrationViewer(JDialog dialog, Illustration illustration, BufferedImage thumb) {
        super(new BorderLayout());
        if (illustration.getPageCount() > 1) {
            add(tabs = new JTabbedPane() {{
                for (int i = 0; i < illustration.getMetaPages().size(); i++)
                    addTab(String.valueOf(i + 1), addImage(new ViewerImage(illustration, i, thumb)));
            }}, BorderLayout.CENTER);
            tabs.addChangeListener(e -> updateImages());
        } else {
            add(addImage(new ViewerImage(illustration, 0, thumb)), BorderLayout.CENTER);
        }
        updateImages();

        add(info = new BigIllustrationInfo(illustration), BorderLayout.EAST);
        add(comments = new Comments(illustration), BorderLayout.WEST);
        dialog.setFocusTraversalPolicy(new DefaultFocusTraversalPolicy() {
            @Override
            public Component getDefaultComponent(Container aContainer) {
                return tabs == null ? images.get(0) : tabs;
            }
        });
        timer = new Timer(16, e -> repaint());
        Pixiv.getInstance().addIllustUpdateListener(info);
        new SwingWorker<>() {
            @Override
            protected Object doInBackground() throws Exception {
                Pixiv.getInstance().getIllustDetail(illustration.getId());
                return null;
            }
        }.execute();
    }

    public static void show(Window owner, Illustration illustration, BufferedImage thumb) {
        String title = "%d :: %s by %s :: \u2661%d / %d :: %s".formatted(illustration.getId(),
                illustration.getTitle(),
                illustration.getUser().getAccount(),
                illustration.getTotalBookmarks(),
                illustration.getTotalView(),
                illustration.getCaption().replaceAll("(?s)<[^>]*>(\\s*<[^>]*>)*", " "));
        JDialog dialog =
                owner instanceof Frame ? new JDialog((Frame) owner, title, true) :
                        owner instanceof Dialog ? new JDialog((Dialog) owner, title, true) :
                                new JDialog(owner, title, Dialog.ModalityType.APPLICATION_MODAL);
        IllustrationViewer viewer = new IllustrationViewer(dialog, illustration, thumb);
        dialog.setContentPane(viewer);
        dialog.addWindowListener(new WindowAdapter() {
            public void windowOpened(WindowEvent e) {
                viewer.timer.start();
            }

            public void windowClosed(WindowEvent e) {
                viewer.dispose();
            }
        });
        dialog.pack();
        dialog.setLocationRelativeTo(owner);
        dialog.setVisible(true);
    }

    private void updateImages() {
        int i = tabs == null ? 0 : tabs.getSelectedIndex();
        for (int j = 0; j < images.size(); j++) {
            boolean shown = MathUtils.DistLoopAround(i, j, images.size()) < 3;
            images.get(j).setShown(shown);
        }
    }

    private ViewerImage addImage(ViewerImage image) {
        images.add(image);
        image.setIllust(this);
        return image;
    }

    public void setFocus(boolean focus) {
        comments.setVisible(!focus);
        info.setVisible(!focus);
//        revalidate();
    }

    private void dispose() {
        Pixiv.getInstance().removeIllustUpdateListener(info);
        images.forEach(ViewerImage::dispose);
        timer.stop();
    }
}
