package me.kumo.io.img;

import me.kumo.io.LocalGallery;
import me.kumo.io.ProgressTracker;
import org.apache.commons.lang3.tuple.Pair;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

public class CorruptDetector extends SwingWorker<ArrayList<Pair<File, ImageScanner.Result>>, File> {
    private final Consumer<ArrayList<Pair<File, ImageScanner.Result>>> listener;
    public final ProgressTracker tracker;
    private final File[] files;

    public CorruptDetector(File folder, Consumer<ArrayList<Pair<File, ImageScanner.Result>>> listener) {
        this.listener = listener;
        files = Objects.requireNonNull(folder.listFiles(new LocalGallery.ImageFileFilter()));
        tracker = new ProgressTracker(32, files.length);
    }

    @Override
    protected ArrayList<Pair<File, ImageScanner.Result>> doInBackground() throws IOException {
        System.out.println("detecting corrupts...");
        ArrayList<Pair<File, ImageScanner.Result>> corrupted = new ArrayList<>();
        for (File file : files) {
            ImageScanner.Result scan = ImageScanner.scan(file.toPath());
            if (scan.isCorrupt()) corrupted.add(Pair.of(file, scan));
            tracker.update(1);
        }
        return corrupted;
    }

    @Override
    protected void done() {
        try {
            listener.accept(get());
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
