package me.kumo.ui.gallery;

import com.github.hanshsieh.pixivj.model.Illustration;
import me.kumo.io.Icons;
import me.kumo.io.ImageUtils;

import javax.swing.*;
import java.awt.*;

public class IllustrationInfo extends JComponent {
    private Illustration illustration;

    public IllustrationInfo() {
        setForeground(Color.WHITE);
    }

    public void setIllustration(Illustration illustration) {
        this.illustration = illustration;
        repaint();
    }

    @Override
    public void paintComponent(Graphics g1d) {
        if (illustration == null) return;
        Graphics2D g = (Graphics2D) g1d;
        g.setRenderingHints(ImageUtils.RENDERING_HINTS);

        g.setColor(new Color(0xaa000000, true));
        g.fillRect(0, 0, getWidth(), 48);

        g.setColor(UIManager.getColor("Label.foreground"));

        g.drawString(String.valueOf(illustration.getId()), 0, 12);

        Icons.Heart.paintIcon(null, g, 0, 16);
        g.drawString(String.valueOf(illustration.getTotalBookmarks()), 16, 28);
        Icons.Eye.paintIcon(null, g, 0, 32);
        g.drawString(String.valueOf(illustration.getTotalView()), 16, 44);

        if (illustration.getXRestrict() != 0) {
            g.setColor(Color.RED);
            g.fillRect(0, getHeight() - 16, g.getFontMetrics().stringWidth("R18"), 16);
            g.setColor(Color.WHITE);
            g.drawString("R18", 0, getHeight() - 4);
        }

        if (illustration.getPageCount() != 1) {
            g.setColor(Color.GRAY);
            String count = String.valueOf(illustration.getPageCount());
            int width = g.getFontMetrics().stringWidth(count);
            g.fillRect(getWidth() - width, getHeight() - 16, width, 16);
            g.setColor(Color.WHITE);
            g.drawString(count, getWidth() - width, getHeight() - 4);
        }
    }
}
