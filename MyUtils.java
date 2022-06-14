package com.example.newoma;

public class MyUtils {
    private MyUtils() {}

    public static String byteArrayToHexStr(byte[] byteArray, int length) {
        if (byteArray == null || length <= 0) {
            return null;
        }
        char[] hexArray = "0123456789ABCDEF".toCharArray();
        char[] hexChars = new char[length * 2];
        for (int j = 0; j < length; j++) {
            int v = byteArray[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return (new String(hexChars));
    }

    public static String byteArrayToHexStr2(byte[] byteArray) {
        final StringBuilder builder = new StringBuilder();
        for (byte b : byteArray) {
            builder.append(String.format("%02x", b));
        }
        return builder.toString();
    }

    public static byte[] hexStrToByteArray(String str)
    {
        int NumberChars = str.length();
        byte[] bytes = new byte[NumberChars / 2];
        for (int i = 0; i < NumberChars; i += 2) {
            bytes[i / 2] = (byte)(Character.digit(str.charAt(i), 16) << 4 |
                    Character.digit(str.charAt(i + 1), 16));
        }
        return bytes;
    }
}

