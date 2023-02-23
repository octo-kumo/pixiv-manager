package me.kumo.ui.utils;

import me.kumo.io.img.ImageScanner;
import org.apache.commons.lang3.tuple.Pair;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;

public class ImageScanModel extends AbstractTableModel {
    private final ArrayList<Pair<File, ImageScanner.Result>> results;

    public ImageScanModel() {
        this.results = new ArrayList<>();
    }

    @Override
    public int getRowCount() {
        return results.size();
    }

    @Override
    public int getColumnCount() {
        return 4;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return switch (columnIndex) {
            case 0, 2 -> String.class;
            case 1 -> ImageScanner.Result.Type.class;
            default -> Object.class;
        };
    }

    @Override
    public String getColumnName(int column) {
        return switch (column) {
            case 0 -> "File";
            case 1 -> "Type";
            case 2 -> "Message";
            case 3 -> "";
            default -> null;
        };
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Pair<File, ImageScanner.Result> item = results.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> item.getLeft().getName();
            case 1 -> item.getRight().resultType();
            case 2 -> item.getRight().getMessages().toString();
            case 3 -> new AbstractAction("Delete") {
                @Override
                public void actionPerformed(ActionEvent e) {

                }
            };
            default -> null;
        };
    }

    public void add(Pair<File, ImageScanner.Result> item) {
        results.add(item);
        int row = results.indexOf(item);
        fireTableRowsInserted(row, row);
    }

    public void remove(Pair<File, ImageScanner.Result> item) {
        if (results.contains(item)) {
            int row = results.indexOf(item);
            results.remove(row);
            fireTableRowsDeleted(row, row);
        }
    }

    public void remove(int item) {
        if (results.size() > item) {
            results.remove(item);
            fireTableRowsDeleted(item, item);
        }
    }
}
