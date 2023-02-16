package me.kumo.ui.gallery;

import com.github.hanshsieh.pixivj.model.Illustration;
import me.kumo.io.Icons;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;

public class IllustrationInfo extends JPanel {
    public static final Color TRANSPARENT = new Color(0, true);
    private JLabel id;
    private JLabel views;
    private JLabel bookmarks;
    private JLabel r18;
    private JLabel pageNumber;

    public IllustrationInfo() {
        super(new BorderLayout());
        setOpaque(false);
        setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        add(new Box(BoxLayout.Y_AXIS) {
            protected void paintComponent(Graphics g) {
                g.setColor(new Color(0xaa000000, true));
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }

            {
                setBorder(new EmptyBorder(5, 5, 5, 5));
                add(id = new JLabel(Icons.Empty));
                add(views = new JLabel(Icons.Eye));
                add(bookmarks = new JLabel(Icons.Heart));
            }
        }, BorderLayout.NORTH);
        add(new Box(BoxLayout.X_AXIS) {{
            add(r18 = new JLabel("R-18") {{
                setForeground(Color.WHITE);
                setBackground(Color.RED);
                setFont(getFont().deriveFont(Font.BOLD));
                setBorder(new MatteBorder(2, 4, 2, 4, Color.RED));
                setOpaque(true);
            }});
            add(Box.createHorizontalGlue());
            add(pageNumber = new JLabel() {{
                setBackground(Color.GRAY);
                setBorder(new MatteBorder(2, 4, 2, 4, Color.GRAY));
                setOpaque(true);
            }});
        }}, BorderLayout.SOUTH);
    }

    public void setIllustration(Illustration illustration) {
        id.setText(String.valueOf(illustration.getId()));
        bookmarks.setText(String.valueOf(illustration.getTotalBookmarks()));
        views.setText(String.valueOf(illustration.getTotalView()));

        r18.setVisible(illustration.getXRestrict() != 0);
        pageNumber.setVisible(illustration.getPageCount() != 1);
        pageNumber.setText(String.valueOf(illustration.getPageCount()));
    }
}
