package me.kumo.ui.gallery;

import javax.swing.*;
import java.awt.*;

public class RankedInfo extends IllustrationInfo {
    private static final Color GOLD = new Color(0xFFD700);
    private static final Color SILVER = new Color(0xC0C0C0);
    private static final Color BRONZE = new Color(0xAF712D);
    private int rank;
    private JLabel rankLabel;

    public RankedInfo() {
        add(new JPanel(new FlowLayout(FlowLayout.LEADING, 5, 5)) {{
            setOpaque(false);
            add(rankLabel = new JLabel() {
                {
                    setForeground(Color.WHITE);
                    setFont(new Font(null, Font.BOLD, 18));
                    setPreferredSize(new Dimension(45, 45));
                    setHorizontalAlignment(CENTER);
                    setVerticalAlignment(CENTER);
                }

                protected void paintComponent(Graphics g) {
                    g.setColor(rank == 1 ? GOLD : rank == 2 ? SILVER : rank == 3 ? BRONZE : HALF_TRANSPARENT);
                    g.fillOval(0, 0, getWidth(), getHeight());
                    super.paintComponent(g);
                }
            });
        }}, BorderLayout.CENTER);
    }

    public void setRank(int rank) {
        this.rank = rank;
        this.rankLabel.setText("<html><small>#</small>" + rank + "</html>");
        this.rankLabel.setFont(new Font(null, rank > 3 ? Font.PLAIN : Font.BOLD, 18));
    }
}
