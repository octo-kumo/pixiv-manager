package ui;

import me.kumo.io.img.GaussianFilter;
import org.imgscalr.Scalr;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

public class BlurImageTest extends JComponent {
    private BufferedImage image;
    private BufferedImage blurred;

    public BlurImageTest(JFrame frame) {
        setPreferredSize(new Dimension(200, 200));
        setDropTarget(new DropTarget() {
            public synchronized void drop(DropTargetDropEvent evt) {
                try {
                    evt.acceptDrop(DnDConstants.ACTION_COPY);
                    List<?> droppedFiles = (List<?>) evt.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                    if (droppedFiles.isEmpty() || !(droppedFiles.get(0) instanceof File file)) return;
                    image = ImageIO.read(file);
                    blurred = blur(image);
                    double ratio = image.getWidth() * 1d / image.getHeight();
                    setPreferredSize(new Dimension((int) (ratio * 2 * 700), 700));
                    frame.pack();
                    frame.setLocationRelativeTo(null);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.drawLine(0, 0, getWidth(), getHeight());
        g.drawImage(image, 0, 0, getWidth() / 2, getHeight(), null);
        g.drawImage(blurred, getWidth() / 2, 0, getWidth() / 2, getHeight(), null);
    }

    public static void main(String... args) {
        JFrame frame = new JFrame("Blur");
        frame.setContentPane(new BlurImageTest(frame));
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static BufferedImage blur(BufferedImage image) {
        return Scalr.apply(image, new GaussianFilter(Math.max(image.getWidth(), image.getHeight()) / 20f));
    }

}
