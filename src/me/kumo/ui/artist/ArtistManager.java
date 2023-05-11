package me.kumo.ui.artist;

import com.github.hanshsieh.pixivj.model.UserDetail;
import com.github.hanshsieh.pixivj.model.UserIllustQuery;
import com.github.hanshsieh.pixivj.model.UserIllusts;
import com.github.hanshsieh.pixivj.model.UserQuery;
import me.kumo.pixiv.Pixiv;
import me.kumo.ui.managers.GalleryManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ArtistManager extends GalleryManager {
    private final Pixiv pixiv;
    private final ArtistDetails info;
    private SwingWorker<Object, Object> worker;
    private SwingWorker<Object, Object> userWorker;
    private String nextURL;
    private UserDetail detail;
    private Timer timer;

    public ArtistManager(Pixiv pixiv, long userID) {
        this.pixiv = pixiv;
        pixiv.addIllustUpdateListener(this::refresh);
        gallery.getScrollPane().getVerticalScrollBar().addAdjustmentListener(e -> {
            JScrollBar scrollBar = (JScrollBar) e.getAdjustable();
            int extent = scrollBar.getModel().getExtent();
            if (scrollBar.getValue() != 0 && (scrollBar.getValue() + extent) == scrollBar.getMaximum())
                getMoreIllusts(userID);
        });
        SwingUtilities.invokeLater(() -> pixiv.addOnLoadListener(result -> {
            if (result == null) return;
            getMoreIllusts(userID);
            getUserDetails(userID);
        }));
        add(info = new ArtistDetails(), BorderLayout.SOUTH);
        timer = new Timer(16, e -> info.repaint());
    }

    public static void open(Window parent, Long user) {
        JDialog dialog = new JDialog(parent, String.valueOf(user), Dialog.ModalityType.APPLICATION_MODAL);
        ArtistManager manager = new ArtistManager(Pixiv.getInstance(), user);
        manager.setPreferredSize(new Dimension(800, 600));
        dialog.setContentPane(manager);
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                manager.start();
            }

            @Override
            public void windowClosed(WindowEvent e) {
                manager.stop();
            }
        });
        dialog.setVisible(true);
    }

    private void getUserDetails(long userID) {
        if (userWorker != null && !userWorker.isDone()) return;
        userWorker = new SwingWorker<>() {
            @Override
            protected Object doInBackground() {
                try {
                    UserDetail detail = pixiv.getUserDetails(new UserQuery(userID));
                    System.out.println("getUserDetails :: " + detail.getUser().getName());
                    setDetails(detail);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
        userWorker.execute();
    }

    private void getMoreIllusts(long userID) {
        if (worker != null && !worker.isDone()) return;
        worker = new SwingWorker<>() {
            @Override
            protected Object doInBackground() {
                try {
                    UserIllusts illusts = nextURL == null ? pixiv.getUserIllusts(new UserIllustQuery(userID)) : pixiv.requestSender.send(pixiv.createApiReqBuilder().url(nextURL).get().build(), UserIllusts.class);
                    nextURL = illusts.getNextUrl();
                    System.out.println("getMoreIllusts :: " + illusts.getIllusts().size());
                    append(illusts.getIllusts());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
        worker.execute();
    }

    public void setDetails(UserDetail detail) {
        this.detail = detail;
        info.setDetails(detail);
    }

    public UserDetail getDetail() {
        return detail;
    }


    @Override
    public void start() {
        gallery.start();
        timer.start();
    }

    @Override
    public void stop() {
        gallery.stop();
        timer.stop();
    }
}
