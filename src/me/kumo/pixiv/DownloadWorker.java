package me.kumo.pixiv;

import me.kumo.io.ProgressTracker;
import pixivj.model.Illustration;

import javax.swing.*;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

import static me.kumo.io.NetIO.downloadIllustration;

public class DownloadWorker extends SwingWorker<Boolean, ProgressTracker> {
    private final ProgressTracker.ProgressListener listener;
    private final Illustration illustration;
    private final Consumer<Boolean> onDone;

    public DownloadWorker(Illustration illustration, ProgressTracker.ProgressListener listener, Consumer<Boolean> onDone) {
        this.listener = listener;
        this.illustration = illustration;
        this.onDone = onDone;
    }

    @Override
    protected Boolean doInBackground() throws Exception {
        return downloadIllustration(Pixiv.getInstance(), illustration, listener);
    }

    @Override
    protected void done() {
        try {
            onDone.accept(get());
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
