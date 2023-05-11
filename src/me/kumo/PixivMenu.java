package me.kumo;

import com.github.hanshsieh.pixivj.model.Illustration;
import me.kumo.io.LocalGallery;
import me.kumo.ui.managers.GalleryManager;
import me.kumo.ui.stats.AuthorMakeup;

import javax.swing.*;
import java.util.ArrayList;

public class PixivMenu extends JMenuBar {
    public PixivMenu(PixivManager manager) {
        add(new JMenu("View") {{
            add(new JCheckBoxMenuItem("Leaderboard") {{
                addActionListener(e -> manager.getControls().getFeeds().setShowRanked(isSelected()));
            }});
        }});
        add(new JMenu("Pixiv") {{
            add(new JMenuItem("Token") {{
                addActionListener(e -> manager.askForToken(true));
            }});
        }});
        add(new JMenu("Stats") {{
            add(new JMenuItem("Author") {{
                addActionListener(e -> {
                    GalleryManager tab = manager.getControls().getCurrentManager();
                    if (tab != null) {
                        ArrayList<Illustration> illustrations = tab.get();
                        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Author Makeup");
                        JTabbedPane tabbedPane = new JTabbedPane();
                        tabbedPane.addTab("Authors", new AuthorMakeup(illustrations.stream().map(Illustration::getUser).toList()));
                        tabbedPane.addTab("Not Followed", new AuthorMakeup(illustrations.stream().map(Illustration::getUser).filter(u -> !u.isFollowed()).toList()));
                        dialog.setContentPane(tabbedPane);
                        dialog.pack();
                        dialog.setLocationRelativeTo(null);
                        dialog.setVisible(true);
                    }
                });
            }});
        }});
        add(new JMenu("Tools") {{
            add(new JMenuItem("GC") {{
                addActionListener(e -> System.gc());
            }});
            add(new JMenuItem("Cache") {{
                addActionListener(e -> LocalGallery.showCache(SwingUtilities.getWindowAncestor(this)));
            }});
        }});
    }
}
