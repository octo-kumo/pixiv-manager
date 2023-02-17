package me.kumo.ui.utils;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

public class Formatters {
    public static String formatBytes(long bytes) {
        if (-1000 < bytes && bytes < 1000) return bytes + " B";
        CharacterIterator ci = new StringCharacterIterator("kMGTPE");
        while (bytes <= -999_950 || bytes >= 999_950) {
            bytes /= 1000;
            ci.next();
        }
        return String.format("%.1f %cB", bytes / 1000.0, ci.current());
    }
}
