package me.kumo.ui.gallery;

import com.github.hanshsieh.pixivj.model.Illustration;
import me.kumo.io.Icons;
import me.kumo.io.LocalGallery;
import me.kumo.ui.utils.Formatters;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;

public class IllustrationInfo extends JPanel {
    public static final Color HALF_TRANSPARENT = new Color(0xaa000000, true);
    private JLabel fileSize;
    private JLabel sanity;
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
            {
                setBorder(new EmptyBorder(5, 5, 5, 5));
                add(id = new JLabel(Icons.Empty));
                add(views = new JLabel(Icons.Eye));
                add(bookmarks = new JLabel(Icons.Heart));
            }

            protected void paintComponent(Graphics g) {
                g.setColor(HALF_TRANSPARENT);
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
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
                add(sanity = new JLabel() {{
                    setBorder(new EmptyBorder(2, 4, 2, 4));
                }});
                add(Box.createHorizontalGlue());
                add(pageNumber = new JLabel() {{
                    setForeground(Color.WHITE);
                    setBackground(Color.GRAY);
                    setBorder(new EmptyBorder(2, 4, 2, 4));
                    setOpaque(true);
                }});
                add(fileSize = new JLabel() {{
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
        sanity.setText(String.valueOf(illustration.getSanityLevel()));
        pageNumber.setVisible(illustration.getPageCount() != 1);
        pageNumber.setText("\u00D7" + illustration.getPageCount());

        try {
            long size = Files.size(LocalGallery.getImage(illustration.getId()).toPath());
            fileSize.setText(Formatters.formatBytes(size));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
