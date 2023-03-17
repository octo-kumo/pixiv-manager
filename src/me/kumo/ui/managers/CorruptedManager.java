package me.kumo.ui.managers;

import me.kumo.io.LocalGallery;
import me.kumo.io.img.CorruptDetector;
import me.kumo.io.img.ImageScanner;
import me.kumo.ui.utils.ButtonColumn;
import me.kumo.ui.utils.ImageScanModel;
import org.apache.commons.lang3.tuple.Pair;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.function.Consumer;

public class CorruptedManager extends JDialog implements Consumer<ArrayList<Pair<File, ImageScanner.Result>>> {
    private final CorruptDetector worker;
    private final ImageScanModel model;
    private final JProgressBar progress;

    public CorruptedManager(Frame parent) {
        super(parent, "Corrupted Images", true);
        setLayout(new BorderLayout());
        add(progress = new JProgressBar(), BorderLayout.NORTH);
        model = new ImageScanModel();
        JTable table = new JTable(model);

        new ButtonColumn(table, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JTable table = (JTable) e.getSource();
                int modelRow = Integer.parseInt(e.getActionCommand());
                ((ImageScanModel) table.getModel()).remove(modelRow);
                System.out.println(modelRow);
            }
        }, 3);

        worker = new CorruptDetector(new File(LocalGallery.PATH), this);
        worker.addPropertyChangeListener(evt -> progress.setValue(worker.getProgress()));
        worker.tracker.addProgressListener(tracker -> {
            progress.setToolTipText("%d/%d :: %.2fs".formatted(tracker.getProgress(), tracker.getTotal(), tracker.getEta()));
        });
        add(table, BorderLayout.CENTER);
        setJMenuBar(new JMenuBar() {{
            add(new JMenu("Actions") {{
                add(new JMenuItem("Start") {{
                    addActionListener(e -> worker.execute());
                }});
            }});
        }});
        pack();
        setLocationRelativeTo(null);
    }

    @Override
    public void accept(ArrayList<Pair<File, ImageScanner.Result>> pairs) {
        pairs.forEach(model::add);
    }
}
