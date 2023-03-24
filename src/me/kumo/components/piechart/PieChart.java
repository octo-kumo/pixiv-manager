package me.kumo.components.piechart;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.util.List;
import java.util.stream.IntStream;

public class PieChart extends JComponent implements MouseListener, MouseMotionListener {
    private final Arc2D.Double[] arcs;
    private final List<? extends PieChartItem> items;
    private final double scale = 0.8;
    private int hoverI = -1;
    private int pressI = -1;

    public PieChart(List<? extends PieChartItem> items) {
        this.items = items;
        long total = items.stream().mapToLong(PieChartItem::getAmount).sum();
        double[] percentage = items.stream().mapToDouble(i -> i.getAmount() * 1d / total).toArray();

        double start = 0;
        arcs = new Arc2D.Double[percentage.length];
        for (int i = 0; i < percentage.length; i++) {
            Arc2D.Double arc = new Arc2D.Double(0, 0, getWidth(), getHeight(), start, 360 * percentage[i], Arc2D.PIE);
            arcs[i] = arc;
            start += 360 * percentage[i];
        }

        addMouseMotionListener(this);
        addMouseListener(this);

        setPreferredSize(new Dimension(256, 256));
    }

    public int getIndex(int x, int y) {
        return IntStream.range(0, arcs.length).filter(i -> arcs[i].contains(x, y)).findFirst().orElse(-1);
    }


    @Override
    protected void paintComponent(Graphics g1d) {
        super.paintComponent(g1d);
        Graphics2D g = (Graphics2D) g1d;
        double x = getWidth() * (1 - scale) / 2;
        double y = getHeight() * (1 - scale) / 2;
        g.setColor(Color.GRAY);
        g.fill(new Ellipse2D.Double(x, y, getWidth() * scale, getHeight() * scale));
        for (int i = 0; i < arcs.length; i++) {
            g.setColor(i == pressI ? Color.PINK : i == hoverI ? Color.WHITE : Color.LIGHT_GRAY);
            Arc2D.Double arc = arcs[i];
            arc.setFrame(x, y, getWidth() * scale, getHeight() * scale);
            g.draw(arc);
            if (i == pressI) {
                g.setColor(Color.WHITE);
                g.fill(arc);
            }
            double r = Math.toRadians(arc.getAngleStart() + arc.getAngleExtent() / 2);
            int width = g.getFontMetrics().stringWidth(items.get(i).getName());
            g.drawString(items.get(i).getName(), (float) (getWidth() / 2d + scale * Math.cos(r) * getWidth() / 2d - width / 2), (float) (getHeight() / 2d - scale * Math.sin(r) * getHeight() / 2d));
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        pressI = getIndex(e.getX(), e.getY());
        repaint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        hoverI = getIndex(e.getX(), e.getY());
        repaint();
    }

    public interface PieChartItem {
        String getName();

        long getAmount();

        void onClicked();
    }
}
