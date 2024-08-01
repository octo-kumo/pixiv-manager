package pixivj.util;

import org.checkerframework.checker.nullness.qual.NonNull;

public class HexUtils {

    private static final char[] HEX_ARRAY = "0123456789abcdef".toCharArray();

    @NonNull
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int i = 0; i < bytes.length; i++) {
            int value = bytes[i] & 0xFF;
            hexChars[i * 2] = HEX_ARRAY[value >>> 4];
            hexChars[i * 2 + 1] = HEX_ARRAY[value & 0x0F];
        }
        return new String(hexChars);
    }
}
