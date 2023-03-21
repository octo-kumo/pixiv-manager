package me.kumo.ui.piechart;

import javax.swing.*;

public class PieChart extends JComponent {


    public interface PieChartItem {
        String getName();

        double getAmount();

        void onClicked();
    }
}
