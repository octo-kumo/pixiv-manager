package me.kumo.ui.viewer;

import com.github.hanshsieh.pixivj.model.Comment;
import com.github.hanshsieh.pixivj.model.IllustCommentsFilter;
import com.github.hanshsieh.pixivj.model.Illustration;
import com.github.weisj.darklaf.components.OverlayScrollPane;
import com.github.weisj.darklaf.components.border.DarkBorders;
import me.kumo.components.utils.SmoothScroll;
import me.kumo.pixiv.Pixiv;

import javax.swing.*;
import java.awt.*;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class Comments extends OverlayScrollPane {
    private final DefaultListModel<Comment> comments;
    private final SmoothScroll smoothScroll;
    private final IllustCommentsFilter filter;
    private SwingWorker<Object, Object> worker;

    public Comments(Illustration illustration) {
        super(null, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        setBorder(BorderFactory.createCompoundBorder(
                DarkBorders.createLineBorder(1, 1, 1, 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        setPreferredSize(new Dimension(200, 0));
        comments = new DefaultListModel<>();
        filter = new IllustCommentsFilter();
        filter.setIllustId(illustration.getId());

        JList<Comment> list = new JList<>(comments);
        list.setCellRenderer(new CommentListRenderer());
        getScrollPane().setViewportView(list);

        smoothScroll = new SmoothScroll(this.getScrollPane(), getVerticalScrollBar(), 0.4);
        getScrollPane().getVerticalScrollBar().addAdjustmentListener(e -> {
            JScrollBar scrollBar = (JScrollBar) e.getAdjustable();
            int extent = scrollBar.getModel().getExtent();
            if (scrollBar.getValue() != 0 && (scrollBar.getValue() + extent) == scrollBar.getMaximum())
                getMoreComments();
        });
        getMoreComments();
    }

    public void getMoreComments() {
        if (worker != null && !worker.isDone()) return;
        worker = new SwingWorker<>() {
            @Override
            protected Object doInBackground() throws Exception {
                com.github.hanshsieh.pixivj.model.Comments illustComments = Pixiv.getInstance().getIllustComments(filter);
                List<Comment> newComments = illustComments.getComments();
                newComments.sort(Comparator.comparing(Comment::getDate, Comparator.reverseOrder()));
                filter.setIllustId(newComments.get(newComments.size() - 1).getId());
                comments.addAll(newComments);
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                } catch (InterruptedException | ExecutionException e) {
//                    throw new RuntimeException(e);
                }
            }
        };
        worker.execute();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        smoothScroll.update();
    }
}
