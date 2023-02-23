package me.kumo.io;

import me.kumo.ui.utils.MovingAvgLastN;

import javax.swing.event.EventListenerList;
import java.util.EventListener;

public class ProgressTracker {
    private final MovingAvgLastN size;
    private final MovingAvgLastN time;
    private final MovingAvgLastN eta;
    private final long total;
    private long lastEva;
    private long progress;
    private double speed;
    private boolean done;

    private final EventListenerList list;

    public ProgressTracker(int windowSize, long total) {
        this.total = total;
        this.eta = new MovingAvgLastN(windowSize);
        this.size = new MovingAvgLastN(windowSize);
        this.time = new MovingAvgLastN(windowSize);
        this.list = new EventListenerList();
    }

    public void update(long count) {
        if (lastEva == 0) lastEva = System.nanoTime();
        else {
            double et = -(lastEva - (lastEva = System.nanoTime())) / 1e9;
            size.add(count);
            time.add(et);
            speed = size.getAvg() / time.getAvg();
            progress += count;
            eta.add((total - progress) / speed);
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
        return speed;
    }

    public double getEta() {
        return eta.getAvg();
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
                ((ProgressListener) listeners[i + 1]).update(this);
            }
        }
    }

    public interface ProgressListener extends EventListener {
        void update(ProgressTracker tracker);
    }
}
