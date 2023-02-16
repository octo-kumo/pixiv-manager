package me.kumo.ui.utils;

public class Nullity {
    @SafeVarargs
    public static <T> T coalesce(T... items) {
        for (T i : items) if (i != null) return i;
        return null;
    }

    public static <T> T coalesce(T a, T b) {
        return a == null ? b : a;
    }

    public static <T> T coalesce(T a, T b, T c) {
        return a != null ? a : (b != null ? b : c);
    }

    public static <T> T coalesce(T a, T b, T c, T d) {
        return a != null ? a : (b != null ? b : (c != null ? c : d));
    }
}
