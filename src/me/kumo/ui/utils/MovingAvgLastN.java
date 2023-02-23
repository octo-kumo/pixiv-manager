package me.kumo.ui.utils;

public class MovingAvgLastN {
    private final int maxTotal;
    private final double[] lastN;
    private int total;
    private double avg;
    private int head;

    public MovingAvgLastN(int N) {
        maxTotal = N;
        lastN = new double[N];
        avg = 0;
        head = 0;
        total = 0;
    }

    public void add(double num) {
        double prevSum = total * avg;

        if (total == maxTotal) {
            prevSum -= lastN[head];
            total--;
        }

        head = (head + 1) % maxTotal;
        int emptyPos = (maxTotal + head - 1) % maxTotal;
        lastN[emptyPos] = num;

        double newSum = prevSum + num;
        total++;
        avg = newSum / total;
    }

    public double getAvg() {
        return avg;
    }
}