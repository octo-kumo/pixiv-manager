package me.kumo.ui.viewer;

import com.github.hanshsieh.pixivj.exception.PixivException;
import com.github.hanshsieh.pixivj.model.Comment;
import me.kumo.io.ImageUtils;
import me.kumo.io.NetIO;
import me.kumo.io.pixiv.Pixiv;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;

public class CommentListRenderer implements ListCellRenderer<Comment> {

    public static HashMap<Long, CommentItem> items = new HashMap<>();

    @Override
    public Component getListCellRendererComponent(JList<? extends Comment> list, Comment value, int index, boolean isSelected, boolean cellHasFocus) {
        return items.computeIfAbsent(value.getId(), id -> new CommentItem(value));
    }


    public static class CommentItem extends Box {
        public CommentItem(Comment value) {
            super(BoxLayout.Y_AXIS);
            Box author = Box.createHorizontalBox();
            JLabel authorName;
            author.add(authorName = new JLabel(value.getUser().getName(), SwingConstants.LEADING) {{
                setFont(new Font(null, Font.BOLD, 12));
            }});
            author.setAlignmentX(Component.LEFT_ALIGNMENT);
            add(author);
            JEditorPane content = new JEditorPane();
            content.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
            content.setFont(new Font(null, Font.PLAIN, 12));
            content.setAlignmentX(LEFT_ALIGNMENT);
            content.setContentType("text/html");
            content.setEditable(false);
            content.setText("<html>" + value.getComment() + "</html>");

            content.setAlignmentX(Component.LEFT_ALIGNMENT);
            content.setMaximumSize(new Dimension(200, Short.MAX_VALUE));
            add(content);

            SwingUtilities.invokeLater(() -> new SwingWorker<>() {
                @Override
                protected Object doInBackground() throws PixivException, IOException {
                    BufferedImage image = NetIO.fetchIllustrationCached(Pixiv.getInstance(), value.getUser().getProfileImageUrls().getMedium());
                    if (image == null) return null;
                    BufferedImage resize = ImageUtils.centerFill(image, 16, 16);
                    image.flush();
                    authorName.setIcon(new ImageIcon(resize));
                    authorName.revalidate();
                    authorName.updateUI();
                    return null;
                }
            }.execute());
        }

        @Override
        public void addNotify() {
            super.addNotify();

        }

        @Override
        public void removeNotify() {
            super.removeNotify();

        }
    }

    ;
}
