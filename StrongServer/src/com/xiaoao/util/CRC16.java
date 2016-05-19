package com.xiaoao.util;

/**
 * CRC16实现类
 */
public class CRC16 {
    private static final CRC16 instance = new CRC16();
    private static final int gPloy = 0x1021; // 生成多项式

    public static CRC16 getInstance() {
        return instance;
    }

    private final int[] crcTable = new int[256];

    private CRC16() {
        computeCrcTable();
    }

    private int getCrcOfByte(int aByte) {
        int value = aByte << 8;
        for (int count = 7; count >= 0; count--) {
            if ((value & 0x8000) != 0) { // 高第16位为1，可以按位异或
                value = (value << 1) ^ gPloy;
            } else {
                value <<= 1; // 首位为0，左移
            }
        }
        value &= 0xFFFF; // 取低16位的值
        return  value;
    }

    /*
     * 生成0 - 255对应的CRC16校验码
     */
    private void computeCrcTable() {
        for (int i = 0; i < 256; i++) {
            crcTable[i] = getCrcOfByte(i);
        }
    }

    /**
     * 计算data的crc码，返回short
     *
     * @param data
     */
    public short getCrc(byte[] data) {
        int crc = 0;
        int length = data.length;
        for (int i = 0; i < length; i++) {
            crc = ((crc & 0xFF) << 8) ^ crcTable[(((crc & 0xFF00) >> 8) ^ data[i]) & 0xFF];
        }
        crc &= 0xFFFF;
        return (short) crc;
    }

}
