package eu.codlab.amiiwrite.utils;

import android.text.TextUtils;

/**
 * Created by kevinleperf on 30/10/2015.
 */
public class IO {
    private static String[] HEX = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"};

    public static String ByteArrayToHexString(byte[] bytes) {
        if (bytes == null) return "null";

        String[] res = new String[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            int val = 0xFF & bytes[i];
            int k = 0xF & val >> 4;
            int m = val & 0xF;
            res[i] = "0x" + HEX[k] + HEX[m];
        }
        return "[" + TextUtils.join(",", res) + "]";
    }
}
