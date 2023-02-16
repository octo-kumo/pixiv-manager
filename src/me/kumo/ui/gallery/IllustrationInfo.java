package me.kumo.ui.gallery;

import com.github.hanshsieh.pixivj.model.Illustration;
import me.kumo.io.Icons;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class IllustrationInfo extends JPanel {
    public static final Color TRANSPARENT = new Color(0, true);
    public static final Color HALF_TRANSPARENT = new Color(0xaa000000, true);
    private JLabel author;
    private JLabel id;
    private JLabel views;
    private JLabel bookmarks;
    private JLabel r18;
    private JLabel pageNumber;
    private JLabel imageSize;

    public IllustrationInfo() {
        super(new BorderLayout());
        setOpaque(false);
        setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        add(new Box(BoxLayout.Y_AXIS) {
            protected void paintComponent(Graphics g) {
                g.setColor(HALF_TRANSPARENT);
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
        add(new Box(BoxLayout.Y_AXIS) {{
            add(new Box(BoxLayout.X_AXIS) {{
                add(r18 = new JLabel("R-18") {{
                    setForeground(Color.WHITE);
                    setBackground(Color.RED);
                    setFont(getFont().deriveFont(Font.BOLD));
                    setBorder(new EmptyBorder(2, 4, 2, 4));
                    setOpaque(true);
                }});
                add(Box.createHorizontalGlue());
                add(pageNumber = new JLabel() {{
                    setForeground(Color.WHITE);
                    setBackground(Color.GRAY);
                    setBorder(new EmptyBorder(2, 4, 2, 4));
                    setOpaque(true);
                }});
            }});
            add(new Box(BoxLayout.X_AXIS) {{
                add(author = new JLabel() {{
                    setForeground(Color.WHITE);
                    setBackground(Color.GRAY);
                    setFont(getFont().deriveFont(Font.BOLD));
                    setBorder(new EmptyBorder(2, 4, 2, 4));
                    setOpaque(true);
                }});
                add(Box.createHorizontalGlue());
                add(imageSize = new JLabel() {{
                    setForeground(Color.WHITE);
                    setBackground(Color.GRAY);
                    setBorder(new EmptyBorder(2, 4, 2, 4));
                    setOpaque(true);
                }});
            }});
        }}, BorderLayout.SOUTH);
    }

    public void setIllustration(Illustration illustration) {
        id.setText(String.valueOf(illustration.getId()));
        bookmarks.setText(String.valueOf(illustration.getTotalBookmarks()));
        views.setText(String.valueOf(illustration.getTotalView()));

        author.setText(illustration.getUser().getName());
        imageSize.setText(illustration.getWidth() + "\u00D7" + illustration.getHeight());
        r18.setVisible(illustration.getXRestrict() != 0);
        pageNumber.setVisible(illustration.getPageCount() != 1);
        pageNumber.setText("\u00D7" + illustration.getPageCount());
    }
}
