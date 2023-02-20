package me.kumo.ui.viewer;

import com.github.hanshsieh.pixivj.model.Illustration;
import com.github.hanshsieh.pixivj.model.Tag;
import com.github.weisj.darklaf.components.OverlayScrollPane;
import com.github.weisj.darklaf.components.border.DarkBorders;
import me.kumo.io.NetIO;
import me.kumo.ui.utils.WrapLayout;
import org.jdesktop.swingx.hyperlink.HyperlinkAction;
import org.jdesktop.swingx.plaf.basic.core.BasicTransferable;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.HyperlinkEvent;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.format.DateTimeFormatter;

public class BigIllustrationInfo extends JPanel {
    public BigIllustrationInfo(Illustration illustration) {
        setBorder(BorderFactory.createCompoundBorder(
                DarkBorders.createLineBorder(1, 1, 1, 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(200, 0));
        add(new JLabel(String.valueOf(illustration.getId())) {{
            setFont(new Font(Font.MONOSPACED, Font.PLAIN, 10));
        }});
        add(new JButton() {{
            setAlignmentX(LEFT_ALIGNMENT);
            setBorderPainted(false);
            setOpaque(false);
            setHorizontalAlignment(LEADING);
            setBackground(null);
            setFont(new Font(null, Font.BOLD, 18));
            HyperlinkAction action = HyperlinkAction.createHyperlinkAction(URI.create("https://pixiv.net/artworks/" + illustration.getId()));
            action.setName("<html>" + illustration.getTitle() + "</html>");
            setAction(action);
        }});
        add(new JButton() {{
            setAlignmentX(LEFT_ALIGNMENT);
            setBorderPainted(false);
            setOpaque(false);
            setHorizontalAlignment(LEADING);
            setBackground(null);
            HyperlinkAction action = HyperlinkAction.createHyperlinkAction(URI.create("https://www.pixiv.net/users/" + illustration.getUser().getId()));
            action.setName(illustration.getUser().getName() + (illustration.getUser().isFollowed() ? " \u2713" : ""));
            setAction(action);
        }});
        add(new JLabel(illustration.getCreateDate().format(DateTimeFormatter.ofPattern("yyyy MMM dd hh:mm:ss a"))));
        add(new JSeparator() {{
            setAlignmentX(LEFT_ALIGNMENT);
            setMinimumSize(new Dimension(Short.MAX_VALUE, 10));
        }});
        add(new OverlayScrollPane(new JEditorPane() {{
            putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
            setFont(new Font(null, Font.PLAIN, 12));
            setAlignmentX(LEFT_ALIGNMENT);
            setContentType("text/html");
            setEditable(false);
            setText("<html>" + illustration.getCaption() + "</html>");
            addHyperlinkListener(e -> {
                if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) try {
                    NetIO.open(e.getURL().toURI());
                } catch (URISyntaxException ex) {
                    ex.printStackTrace();
                }
            });
        }}, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER) {{
            setPreferredSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
            setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
        }});

        if (!illustration.getTags().isEmpty()) add(new JPanel(new WrapLayout(WrapLayout.LEADING)) {{
            setAlignmentX(LEFT_ALIGNMENT);
            setBorder(new TitledBorder("Tags"));
            for (Tag tag : illustration.getTags()) {
                add(new JLabel("#" + tag.getName()) {{
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
            }
        }});
        if (!illustration.getTools().isEmpty()) add(new JPanel(new WrapLayout(WrapLayout.LEADING)) {{
            setAlignmentX(LEFT_ALIGNMENT);
            setBorder(new TitledBorder("Tools"));
            for (String tool : illustration.getTools()) {
                add(new JLabel(tool));
            }
        }});
    }
}
