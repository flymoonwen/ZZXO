package com.xiaoao.util;

/**
 * 加密工具类
 */
public class Encrypt {

    private static final byte[] salt = {1, 2, 4, 3, 5, 7, 7, 3};

    /**
     * 对通讯数据进行 异或加密
     *
     * @param src
      
     */
    public static void encryptComm(byte[] src) {
//        byte[] result = new byte[src.length];
        for (int i = 0; i < src.length; i++) {
            src[i] = (byte) (src[i] ^ salt[i % salt.length]);
        }
//        return src;
    }

    private Encrypt() {
    }
}
