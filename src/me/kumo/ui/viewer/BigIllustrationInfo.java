package me.kumo.ui.viewer;

import com.github.weisj.darklaf.components.OverlayScrollPane;
import com.github.weisj.darklaf.components.border.DarkBorders;
import me.kumo.components.utils.UnderlineOnHover;
import me.kumo.components.utils.WrapLayout;
import me.kumo.io.Icons;
import me.kumo.io.NetIO;
import me.kumo.pixiv.BookmarkWorker;
import me.kumo.pixiv.FollowWorker;
import me.kumo.pixiv.IllustUpdateListener;
import me.kumo.ui.artist.ArtistManager;
import org.jdesktop.swingx.plaf.basic.core.BasicTransferable;
import pixivj.model.Illustration;
import pixivj.model.Tag;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.HyperlinkEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.format.DateTimeFormatter;

public class BigIllustrationInfo extends JPanel implements IllustUpdateListener {
    private final JLabel id;
    private final JButton title;
    private final JCheckBox author;
    private final JLabel date;
    private final JEditorPane caption;
    private final JPanel tags;
    private final JPanel tools;
    private Illustration illustration;

    public BigIllustrationInfo(Illustration illustration) {
        this.illustration = illustration;
        setBorder(BorderFactory.createCompoundBorder(
                DarkBorders.createLineBorder(1, 1, 1, 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(200, 0));
        add(id = new JLabel() {{
            addMouseListener(new UnderlineOnHover());
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    NetIO.openURL(URI.create("https://pixiv.net/artworks/" + BigIllustrationInfo.this.illustration.getId()));
                }
            });
            setFont(new Font(Font.MONOSPACED, Font.PLAIN, 10));
        }});
        add(title = new JButton() {{
            setAlignmentX(LEFT_ALIGNMENT);
            setBorderPainted(false);
            setOpaque(false);
            setHorizontalAlignment(LEADING);
            setBackground(null);
            setFont(new Font(null, Font.BOLD, 18));
            addActionListener(e -> {
                setEnabled(false);
                new BookmarkWorker(BigIllustrationInfo.this.illustration, b -> {
                    setEnabled(true);
                    setIcon(b ? Icons.heart.get() : Icons.heart_path.get());
                }).execute();
            });
        }});
        add(author = new JCheckBox() {{
            addMouseListener(new UnderlineOnHover(InputEvent.CTRL_DOWN_MASK));
            setAlignmentX(LEFT_ALIGNMENT);
            setBorderPainted(false);
            setOpaque(false);
            setHorizontalAlignment(LEADING);
            setBackground(null);

            addActionListener(e -> {
                if ((e.getModifiers() & ActionEvent.CTRL_MASK) == ActionEvent.CTRL_MASK) {
                    setSelected(BigIllustrationInfo.this.illustration.getUser().isFollowed());
                    ArtistManager.open(SwingUtilities.getWindowAncestor(this), BigIllustrationInfo.this.illustration.getUser().getId());
                } else {
                    setEnabled(false);
                    new FollowWorker(BigIllustrationInfo.this.illustration.getUser(), b -> {
                        setEnabled(true);
                        setSelected(b);
                    }).execute();
                }
            });
        }});
        add(date = new JLabel());
        add(new JSeparator() {{
            setAlignmentX(LEFT_ALIGNMENT);
            setMinimumSize(new Dimension(Short.MAX_VALUE, 10));
        }});
        add(new OverlayScrollPane(caption = new JEditorPane() {{
            setPreferredSize(new Dimension(200, Short.MAX_VALUE));
            setMaximumSize(new Dimension(200, Short.MAX_VALUE));
            putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
            setFont(new Font(null, Font.PLAIN, 12));
            setAlignmentX(LEFT_ALIGNMENT);
            setContentType("text/html");
            setEditable(false);

            addHyperlinkListener(e -> {
                if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) try {
                    NetIO.openURL(e.getURL().toURI());
                } catch (URISyntaxException ex) {
                    ex.printStackTrace();
                }
            });
        }}, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER) {{
            setAlignmentX(LEFT_ALIGNMENT);
            setPreferredSize(new Dimension(200, Short.MAX_VALUE));
            setMaximumSize(new Dimension(200, Short.MAX_VALUE));
        }});

        add(tags = new JPanel(new WrapLayout(WrapLayout.LEADING)) {{
            setAlignmentX(LEFT_ALIGNMENT);
            setBorder(new TitledBorder("Tags"));
        }});
        add(tools = new JPanel(new WrapLayout(WrapLayout.LEADING)) {{
            setAlignmentX(LEFT_ALIGNMENT);
            setBorder(new TitledBorder("Tools"));
        }});
        setIllustration(illustration);
    }

    private void setIllustration(Illustration illustration) {
        this.illustration = illustration;
        id.setText(String.valueOf(illustration.getId()));
        title.setText("<html>" + illustration.getTitle() + "</html>");
        title.setIcon(illustration.isBookmarked() ? Icons.heart.get() : Icons.heart_path.get());
        author.setText(illustration.getUser().getName());
        author.setSelected(illustration.getUser().isFollowed());
        date.setText(illustration.getCreateDate().format(DateTimeFormatter.ofPattern("yyyy MMM dd hh:mm:ss a")));
        caption.setText("<html>" + illustration.getCaption() + "</html>");
        tags.setVisible(!illustration.getTags().isEmpty());
        tags.removeAll();
        tools.setVisible(!illustration.getTools().isEmpty());
        tools.removeAll();
        for (Tag tag : illustration.getTags())
            tags.add(new JLabel("#" + tag.getName()) {{
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        setForeground(Color.BLUE);
                        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new BasicTransferable(tag.getName(), tag.getName()), (clipboard, contents) -> {
                            setForeground(UIManager.getColor("Label.foreground"));
                        });
                    }
                });
            }});
        for (String tool : illustration.getTools()) tools.add(new JLabel(tool));
        revalidate();
    }

    @Override
    public void accept(Illustration illustration) {
        setIllustration(illustration);
    }
}
