package me.kumo.io;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;

public class ProgressInputStream extends InputStream {

    private final InputStream in;

    public ProgressTracker getTracker() {
        return tracker;
    }

    private final ProgressTracker tracker;

    public ProgressInputStream(InputStream inputStream, long length) {
        this.in = inputStream;
        this.tracker = new ProgressTracker(64, length);
    }


    @Override
    public int read(byte @NotNull [] b) throws IOException {
        int readCount = in.read(b);
        evaluatePercent(readCount);
        return readCount;
    }


    @Override
    public int read(byte @NotNull [] b, int off, int len) throws IOException {
        int readCount = in.read(b, off, len);
        evaluatePercent(readCount);
        return readCount;
    }

    @Override
    public long skip(long n) throws IOException {
        long skip = in.skip(n);
        evaluatePercent(skip);
        return skip;
    }

    @Override
    public int read() throws IOException {
        int read = in.read();
        evaluatePercent(read != -1 ? 1 : -1);
        return read;
    }

    private void evaluatePercent(long readCount) {
        if (readCount != -1) tracker.update(readCount);
        else tracker.setDone(true);
    }
}