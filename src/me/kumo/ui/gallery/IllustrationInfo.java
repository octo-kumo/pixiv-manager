package me.kumo.ui.gallery;

import com.github.hanshsieh.pixivj.model.Illustration;
import me.kumo.components.utils.Formatters;
import me.kumo.components.utils.UnderlineOnHover;
import me.kumo.io.Icons;
import me.kumo.io.LocalGallery;
import me.kumo.ui.artist.ArtistManager;
import org.ocpsoft.prettytime.PrettyTime;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.nio.file.Files;

public class IllustrationInfo extends JPanel {
    private static final PrettyTime PRETTY = new PrettyTime();

    public static final Color HALF_TRANSPARENT = new Color(0xaa000000, true);
    private JLabel datetime;
    private JLabel fileSize;
    private JLabel sanity;
    private JLabel author;
    private JLabel id;
    private JLabel views;
    private JLabel bookmarks;
    private JLabel r18;
    private JLabel pageNumber;
    private JLabel imageSize;
    private JLabel ai;
    private Illustration illustration;

    public IllustrationInfo() {
        super(new BorderLayout());
        setOpaque(false);
        setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        add(new Box(BoxLayout.Y_AXIS) {
            {
                setBorder(new EmptyBorder(5, 5, 5, 5));
                add(new Box(BoxLayout.X_AXIS) {{
                    add(id = new JLabel(Icons.empty.get()));
                    add(datetime = new JLabel(Icons.empty.get()));
                    add(Box.createHorizontalGlue());
                }});
                add(new Box(BoxLayout.X_AXIS) {{
                    add(views = new JLabel(Icons.eye.get()));
                    add(Box.createHorizontalGlue());
                }});
                add(new Box(BoxLayout.X_AXIS) {{
                    add(bookmarks = new JLabel(Icons.heart.get()));
                    add(Box.createHorizontalGlue());
                }});
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
                add(ai = new JLabel("AI") {{
                    setForeground(Color.WHITE);
                    setBackground(Color.BLUE);
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
                    setFocusable(true);
                    addMouseListener(new UnderlineOnHover());
                    addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            ArtistManager.open(SwingUtilities.getWindowAncestor(IllustrationInfo.this), illustration.getUser().getId());
                        }
                    });
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
        this.illustration = illustration;
        id.setText(String.valueOf(illustration.getId()));
        bookmarks.setText(illustration.getTotalBookmarks() == null ? "" : String.valueOf(illustration.getTotalBookmarks()));
        views.setText(illustration.getTotalView() == null ? "" : String.valueOf(illustration.getTotalView()));
        datetime.setText(illustration.getCreateDate() == null ? "" : PRETTY.format(illustration.getCreateDate()));
        author.setText(illustration.getUser() == null ? "" : illustration.getUser().getName() + (illustration.getUser().isFollowed() ? " \u2713" : ""));
        imageSize.setText(illustration.getWidth() == null || illustration.getHeight() == null ? "" : illustration.getWidth() + "\u00D7" + illustration.getHeight());
        r18.setVisible(illustration.getXRestrict() != null && illustration.getXRestrict() != 0);
        sanity.setText(illustration.getSanityLevel() == null ? "" : String.valueOf(illustration.getSanityLevel()));
        pageNumber.setVisible(illustration.getPageCount() != 1);
        pageNumber.setText("\u00D7" + illustration.getPageCount());

        ai.setVisible(illustration.isAI());

        try {
            long size = Files.size(LocalGallery.getImage(illustration.getId()).toPath());
            fileSize.setText(Formatters.formatBytes(size));
            fileSize.setVisible(true);
        } catch (Exception e) {
            fileSize.setText("NA");
            fileSize.setVisible(false);
        }
    }
}
