package me.kumo.components.utils;

public class Curves {
    public static double InOutQuadBlend(double t) {
        if (t <= 0.5) return 2.0 * t * t;
        t -= 0.5;
        return 2.0 * t * (1.0 - t) + 0.5;
    }

    public static double BezierBlend(double t) {
        return t * t * (3.0 - 2.0 * t);
    }
}
