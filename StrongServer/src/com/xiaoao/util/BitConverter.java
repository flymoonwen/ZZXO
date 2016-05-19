package com.xiaoao.util;

import java.nio.charset.Charset;

/**
 * 基本类型到bytes的相互转换，java与c#通用
 */
public class BitConverter {

    public static byte[] getBytes(short data) {
        byte[] bytes = new byte[2];
        bytes[0] = (byte) (data & 0xff);
        bytes[1] = (byte) ((data & 0xff00) >> 8);
        return bytes;
    }

    public static byte[] getBytes(char data) {
        byte[] bytes = new byte[2];
        bytes[0] = (byte) (data);
        bytes[1] = (byte) (data >> 8);
        return bytes;
    }

    public static byte[] getBytes(boolean data) {
        byte[] bytes = new byte[1];
        bytes[0] = (byte) (data ? 1 : 0);
        return bytes;
    }

    public static byte[] getBytes(int data) {
        byte[] bytes = new byte[4];
        bytes[0] = (byte) (data & 0xff);
        bytes[1] = (byte) ((data & 0xff00) >> 8);
        bytes[2] = (byte) ((data & 0xff0000) >> 16);
        bytes[3] = (byte) ((data & 0xff000000) >> 24);
        return bytes;
    }

    public static byte[] getUnsignedShortBytes(int data) {
        byte[] bytes = new byte[2];
        bytes[0] = (byte) (data & 0xff);
        bytes[1] = (byte) ((data & 0xff00) >> 8);
        return bytes;
    }

    public static byte[] getBytes(long data) {
        byte[] bytes = new byte[8];
        bytes[0] = (byte) (data & 0xff);
        bytes[1] = (byte) ((data >> 8) & 0xff);
        bytes[2] = (byte) ((data >> 16) & 0xff);
        bytes[3] = (byte) ((data >> 24) & 0xff);
        bytes[4] = (byte) ((data >> 32) & 0xff);
        bytes[5] = (byte) ((data >> 40) & 0xff);
        bytes[6] = (byte) ((data >> 48) & 0xff);
        bytes[7] = (byte) ((data >> 56) & 0xff);
        return bytes;
    }

    public static byte[] getBytes(float data) {
        int intBits = Float.floatToIntBits(data);
        return getBytes(intBits);
    }

    public static byte[] getBytes(double data) {
        long intBits = Double.doubleToLongBits(data);
        return getBytes(intBits);
    }

    public static byte[] getBytes(String data, String charsetName) {
        Charset charset = Charset.forName(charsetName);
        return data.getBytes(charset);
    }

    public static byte[] getBytes(String data) {
        return getBytes(data, "UTF8");
    }

    public static boolean getBoolean(byte[] bytes) {
        return bytes[0] == 1;
    }

    public static boolean getBoolean(byte[] bytes, int startIndex) {
        return bytes[startIndex] == 1;
    }

    public static short getShort(byte[] bytes) {
        return (short) ((0xff & bytes[0]) | (0xff00 & (bytes[1] << 8)));
    }

    public static int getUnsignedShort(byte[] bytes, int startIndex) {
        return getShort(bytes, startIndex) & 0xFFFF;
    }

    public static short getShort(byte[] bytes, int startIndex) {
        return (short) ((0xff & bytes[startIndex]) | (0xff00 & (bytes[startIndex + 1] << 8)));
    }

    public static char getChar(byte[] bytes) {
        return (char) ((0xff & bytes[0]) | (0xff00 & (bytes[1] << 8)));
    }

    public static char getChar(byte[] bytes, int startIndex) {
        return (char) ((0xff & bytes[startIndex]) | (0xff00 & (bytes[startIndex + 1] << 8)));
    }

    public static int getInt(byte[] bytes) {
//        return (0xff & bytes[0]) | (0xff00 & (bytes[1] << 8)) | (0xff0000 & (bytes[2] << 16)) | (0xff000000 & (bytes[3] << 24));
        return lBytesToInt(bytes);
    }

    public static int getInt(byte[] bytes, int startIndex) {
//        return (0xff & bytes[startIndex]) | (0xff00 & (bytes[startIndex + 1] << 8)) | (0xff0000 & (bytes[startIndex + 2] << 16)) | (0xff000000 & (bytes[startIndex + 3] << 24));
        return lBytesToInt(bytes, startIndex);
    }

//    public static long getLong(byte[] bytes) {
//        return (0xffL & (long) bytes[0]) | (0xff00L & ((long) bytes[1] << 8)) | (0xff0000L & ((long) bytes[2] << 16))
//                | (0xff000000L & ((long) bytes[3] << 24))
//                | (0xff00000000L & ((long) bytes[4] << 32)) | (0xff0000000000L & ((long) bytes[5] << 40)) | (0xff000000000000L & ((long) bytes[6] << 48)) | (0xff00000000000000L & ((long) bytes[7] << 56));
//    }
//
//    public static long getLong(byte[] bytes, int startIndex) {
//        return (0xffL & (long) bytes[startIndex]) | (0xff00L & ((long) bytes[startIndex + 1] << 8)) | (0xff0000L & ((long) bytes[startIndex + 2] << 16)) | (0xff000000L & ((long) bytes[startIndex + 3] << 24))
//                | (0xff00000000L & ((long) bytes[startIndex + 4] << 32)) | (0xff0000000000L & ((long) bytes[startIndex + 5] << 40)) | (0xff000000000000L & ((long) bytes[startIndex + 6] << 48)) | (0xff00000000000000L & ((long) bytes[startIndex + 7] << 56));
//    }
    public static long getLong(byte[] bytes, int start) {
        int i;
        int len = 8;
        int cnt = 0;
        byte[] tmp = new byte[len];
        for (i = start; i < (start + len); i++) {
            tmp[cnt] = bytes[i];
            cnt++;
        }
        long accum = 0;
        i = 0;
        for (int shiftBy = 0; shiftBy < 64; shiftBy += 8) {
            accum |= ((long) (tmp[i] & 0xff)) << shiftBy;
            i++;
        }
        return accum;
    }

    public static float getFloat(byte[] bytes) {
        return Float.intBitsToFloat(getInt(bytes));
    }

    public static double getDouble(byte[] bytes) {
        long l = getLong(bytes, 0);
        return Double.longBitsToDouble(l);
    }

    public static float getFloat(byte[] bytes, int startIndex) {
        byte[] result = new byte[4];
        System.arraycopy(bytes, startIndex, result, 0, 4);
        return Float.intBitsToFloat(getInt(result));
    }

//    public static double getDouble(byte[] bytes, int startIndex) {
//        byte[] result = new byte[8];
//        System.arraycopy(bytes, startIndex, result, 0, 8);
//        long l = getLong(result);
//        return Double.longBitsToDouble(l);
//    }
    public static double getDouble(byte[] arr, int start) {
        return Double.longBitsToDouble(getLong(arr, start));
    }

    public static String getString(byte[] bytes, String charsetName) {
        return new String(bytes, Charset.forName(charsetName));
    }

    public static String getString(byte[] bytes) {
        return getString(bytes, "UTF8");
    }

    public static int lBytesToInt(byte[] b) {
        return lBytesToInt(b, 0);
    }

    public static int lBytesToInt(byte[] b, int startIndex) {
        int s = 0;
        int len = startIndex + 3;
        for (int i = 0; i < 3; i++) {
            if (b[len - i] >= 0) {
                s += b[len - i];
            } else {
                s = s + 256 + b[len - i];
            }
            s *= 256;
        }
        if (b[startIndex] >= 0) {
            s += b[startIndex];
        } else {
            s = s + 256 + b[startIndex];
        }
        return s;
    }

    private BitConverter() {
    }
}