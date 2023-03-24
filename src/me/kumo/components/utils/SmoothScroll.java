package me.kumo.components.utils;

import javax.swing.*;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class SmoothScroll implements MouseWheelListener, AdjustmentListener {

    private final JScrollBar bar;
    private final double lambda;

    protected double scrollTarget = 0;
    protected double scrollPosition = 0;

    public SmoothScroll(JScrollPane scrollPane, JScrollBar bar) {
        this(scrollPane, bar, 0.1);
    }


    public SmoothScroll(JScrollPane scrollPane, JScrollBar bar, double lambda) {
        this.lambda = lambda;
        this.bar = bar;
        bar.addAdjustmentListener(this);
        scrollPane.setWheelScrollingEnabled(false);
        scrollPane.addMouseWheelListener(this);
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        scrollTarget = Math.max(bar.getMinimum(), Math.min(bar.getMaximum() - bar.getVisibleAmount(), scrollTarget + e.getPreciseWheelRotation() * 100));
    }

    @Override
    public void adjustmentValueChanged(AdjustmentEvent e) {
        if (e.getValueIsAdjusting()) scrollTarget = scrollPosition = e.getValue();
    }

    public void update() {
        scrollPosition = scrollPosition * (1 - lambda) + scrollTarget * lambda;
        if (Math.round(scrollPosition) != bar.getValue()) bar.setValue((int) Math.round(scrollPosition));
    }
}
