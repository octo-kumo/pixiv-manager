package me.kumo.io;

import org.imgscalr.Scalr;

import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Map;

public class ImageUtils {

    public static final RenderingHints RENDERING_HINTS = new RenderingHints(Map.of(
            RenderingHints.KEY_RENDERING,
            RenderingHints.VALUE_RENDER_QUALITY,
            RenderingHints.KEY_COLOR_RENDERING,
            RenderingHints.VALUE_COLOR_RENDER_QUALITY,
            RenderingHints.KEY_STROKE_CONTROL,
            RenderingHints.VALUE_STROKE_PURE,
            RenderingHints.KEY_ALPHA_INTERPOLATION,
            RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY,
            RenderingHints.KEY_TEXT_ANTIALIASING,
            RenderingHints.VALUE_TEXT_ANTIALIAS_ON,
            RenderingHints.KEY_FRACTIONALMETRICS,
            RenderingHints.VALUE_FRACTIONALMETRICS_ON
    ));

    public static BufferedImage scale(BufferedImage bufferedImage, double scale) {
        BufferedImage n = new BufferedImage((int) (scale * bufferedImage.getWidth()), (int) (scale * bufferedImage.getHeight()), bufferedImage.getType());
        Graphics2D g = n.createGraphics();
        g.setRenderingHints(RENDERING_HINTS);
        g.drawImage(bufferedImage, 0, 0, (int) (scale * bufferedImage.getWidth()), (int) (scale * bufferedImage.getHeight()), null);
        g.dispose();
        return n;
    }

    public static BufferedImage centerFill(BufferedImage image, int width, int height) {
        BufferedImage n = new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);
        Graphics2D g = n.createGraphics();
        double ratio = Math.max(1. * width / image.getWidth(), 1. * height / image.getHeight());

        double w = image.getWidth() * ratio;
        double h = image.getHeight() * ratio;
        g.setPaint(new TexturePaint(image, new Rectangle2D.Double(width / 2d - w / 2, height / 2d - h / 2, w, h)));
        g.fillRect(0, 0, image.getWidth(), image.getHeight());
        g.dispose();
        image.flush();
        return n;
    }

    public static BufferedImage downScale(BufferedImage image, int width, int height) {
        double ratio = Math.max(1. * width / image.getWidth(), 1. * height / image.getHeight());
        return Scalr.resize(image, Scalr.Method.SPEED, Scalr.Mode.AUTOMATIC,
                (int) (image.getWidth() * ratio),
                (int) (image.getHeight() * ratio));
    }

    public static final BasicStroke ROUND_STROKE = new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

    public static void spinner(Graphics2D g, double x, double y, double r) {
        double clock = (System.currentTimeMillis() % 1000) / 1000d;
        Stroke stroke = g.getStroke();
        g.setStroke(ROUND_STROKE);
        g.draw(new Arc2D.Double(x, y, r * 2, r * 2, clock * 360, clock * 720 - 360, Arc2D.OPEN));
        g.setStroke(stroke);
    }
}
