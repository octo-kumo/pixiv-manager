package me.kumo.io;

import me.kumo.components.utils.MovingAvgLastN;

import javax.swing.event.EventListenerList;
import java.util.EventListener;

public class ProgressTracker {
    private final MovingAvgLastN size;
    private final MovingAvgLastN time;
    private final MovingAvgLastN speed;
    private final long total;
    private final EventListenerList list;
    private double eta;
    private long lastEva;
    private long progress;
    private boolean done;

    public ProgressTracker(int windowSize, long total) {
        this.total = total;
        this.eta = 0;
        this.size = new MovingAvgLastN(windowSize);
        this.time = new MovingAvgLastN(windowSize);
        this.speed = new MovingAvgLastN(windowSize / 64);
        this.list = new EventListenerList();
    }

    public void update(long count) {
        if (lastEva == 0) lastEva = System.nanoTime();
        else {
            double et = -(lastEva - (lastEva = System.nanoTime())) / 1e9;
            size.add(count);
            time.add(et);
            speed.add(size.getAvg() / time.getAvg());
            progress += count;
            eta = (total - progress) / speed.getAvg();
        }
        fire();
    }

    public long getProgress() {
        return progress;
    }

    public long getTotal() {
        return total;
    }

    public double getSpeed() {
        return speed.getAvg();
    }

    public double getEta() {
        return eta;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
        fire();
    }

    public void addProgressListener(ProgressListener l) {
        list.add(ProgressListener.class, l);
    }

    public void removeProgressListener(ProgressListener l) {
        list.remove(ProgressListener.class, l);
    }

    protected void fire() {
        Object[] listeners = list.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ProgressListener.class) {
                ((ProgressListener) listeners[i + 1]).onProgress(this);
            }
        }
    }

    public interface ProgressListener extends EventListener {
        void onProgress(ProgressTracker tracker);
    }
}
