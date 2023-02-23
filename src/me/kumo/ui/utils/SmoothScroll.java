package me.kumo.ui.utils;

import javax.swing.*;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.function.Supplier;

public class SmoothScroll implements MouseWheelListener, AdjustmentListener {

    private final JScrollBar bar;
    private final double lambda;

    protected double scrollTarget = 0;
    protected double scrollPosition = 0;

    public SmoothScroll(JScrollPane scrollPane, Supplier<JScrollBar> supplier) {
        this(scrollPane, supplier, 0.1);
    }


    public SmoothScroll(JScrollPane scrollPane, Supplier<JScrollBar> supplier, double lambda) {
        bar = supplier.get();
        this.lambda = lambda;
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
        if (e.getValueIsAdjusting()) scrollTarget = scrollPosition;
    }

    public void update() {
        scrollPosition = scrollPosition * (1 - lambda) + scrollTarget * lambda;
        if (Math.round(scrollPosition) != bar.getValue()) bar.setValue((int) Math.round(scrollPosition));
    }
}
