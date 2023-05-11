package me.kumo.components.utils;

public class MathUtils {
    public static int DistLoopAround(int a, int b, int max) {
        return Math.min(Math.min(Math.abs(b - a), Math.abs(b - (a + max))), Math.abs((b + max) - a));
    }

    public static class Curves {
        public static double InOutQuadBlend(double t) {
            if (t <= 0.5) return 2.0 * t * t;
            t -= 0.5;
            return 2.0 * t * (1.0 - t) + 0.5;
        }

        public static double BezierBlend(double t) {
            return t * t * (3.0 - 2.0 * t);
        }
    }
}
