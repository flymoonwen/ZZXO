package xiaoao.strong.netty.message;

import java.io.UnsupportedEncodingException;

import xiaoao.strong.util.BitConverter;
import xiaoao.strong.util.CRC16;
import xiaoao.strong.util.Encrypt;
import xiaoao.strong.util.ZLibUtils;

/**
 * 服务器消息类
 *
 */
public class ClientMsg {

    private static final int INIT_MSG_LEN = 1024;
    private static final int COMM_COMPRESS_SIZE = 300;
    private byte[] m_aBuff;
    private byte[] packBuff;//pack结果
    private boolean isPacked;//是否已pack过
    private int m_nLen;
//    private int m_nPos;
    private final StringBuilder logString = new StringBuilder(64);
    private final StringBuilder tempString = new StringBuilder(128);
//    private boolean needLog;

    public ClientMsg() {
        m_aBuff = new byte[INIT_MSG_LEN];
    }

    public void passivate() {
        m_nLen = 0;
//        m_nPos = 0;
        tempString.setLength(0);
        logString.setLength(0);
        this.isPacked = false;
        packBuff = null;
//        needLog = true;
    }

    public void setMsgTo(String ip) {
        tempString.setLength(0);
        tempString.append("[send]").append("[").append(ip).append("]");
    }

    private void ensureCapacity(int length) {
        if (m_nLen + length > m_aBuff.length) {
            byte[] tmp = new byte[m_aBuff.length + INIT_MSG_LEN];
            System.arraycopy(m_aBuff, 0, tmp, 0, m_aBuff.length);
            m_aBuff = tmp;
        }
    }

    public void appendInt(int i) {
        ensureCapacity(MsgConstant.INT_SIZE);
        System.arraycopy(BitConverter.getBytes(i), 0, m_aBuff, m_nLen, MsgConstant.INT_SIZE);
        m_nLen += MsgConstant.INT_SIZE;
        logString.append("[").append(i).append("]");
//        logString.append("[Int:").append(i).append("]");
    }

    public void appendLong(long i) {
        ensureCapacity(MsgConstant.LONG_SIZE);
        System.arraycopy(BitConverter.getBytes(i), 0, m_aBuff, m_nLen, MsgConstant.LONG_SIZE);
        m_nLen += MsgConstant.LONG_SIZE;
        logString.append("[").append(i).append("]");
//        logString.append("[Long：").append(i).append("]");
    }

    public void appendMsgId(int s) {
//        if (s == CommNum.MSG_S2C_CHAT_RET) {
//            needLog = false;
//        }
        appendShort(s);
    }

    public void appendShort(int s) {
        ensureCapacity(MsgConstant.SHORT_SIZE);

        System.arraycopy(BitConverter.getBytes((short) s), 0, m_aBuff, m_nLen, MsgConstant.SHORT_SIZE);
        m_nLen += MsgConstant.SHORT_SIZE;
//        m_nLen = m_nPos;

        logString.append("[").append(s).append("]");
//        logString.append("[Short:").append(s).append("]");
    }

    public void appendUnsignedShort(int s) {
        ensureCapacity(MsgConstant.SHORT_SIZE);

        System.arraycopy(BitConverter.getUnsignedShortBytes(s), 0, m_aBuff, m_nLen, MsgConstant.SHORT_SIZE);
        m_nLen += MsgConstant.SHORT_SIZE;
        logString.append("[").append(s).append("]");
//        logString.append("[UnsignedShort:").append(s).append("]");
    }

    public void appendByte(byte b) {
        ensureCapacity(1);

        m_aBuff[m_nLen] = b;
        m_nLen += 1;
//        m_nLen = m_nPos;
        logString.append("[").append(b).append("]");
//        logString.append("[Byte:").append(b).append("]");
    }

    public void appendUnit(ClientMsg msg) {
        ensureCapacity(msg.m_nLen);
        System.arraycopy(msg.m_aBuff, 0, m_aBuff, m_nLen, msg.m_nLen);
        m_nLen += msg.m_nLen;
//        m_nLen = m_nPos;
        logString.append("[").append(msg.logString).append("]");
//        logString.append("[Unit:").append(msg.logString).append("]");
    }

    public void appendBoolean(boolean bool) {
        ensureCapacity(1);

        if (bool) {
            m_aBuff[m_nLen] = 1;
        } else {
            m_aBuff[m_nLen] = 0;
        }
        m_nLen += 1;
//        m_nLen = m_nPos;
        logString.append("[").append(m_aBuff[m_nLen - 1]).append("]");
//        logString.append("[Boolean:").append(m_aBuff[m_nLen - 1]).append("]");
    }

    public void appendDouble(double d) {
        ensureCapacity(MsgConstant.DOUBLE_SIZE);

        System.arraycopy(BitConverter.getBytes(d), 0, m_aBuff, m_nLen, MsgConstant.DOUBLE_SIZE);
        m_nLen += MsgConstant.DOUBLE_SIZE;
//        m_nLen = m_nPos;
        logString.append("[").append(d).append("]");
//        logString.append("[Double:").append(d).append("]");
    }

    public void appendFloat(float f) {
        ensureCapacity(MsgConstant.FLOAT_SIZE);

        System.arraycopy(BitConverter.getBytes(f), 0, m_aBuff, m_nLen, MsgConstant.FLOAT_SIZE);
        m_nLen += MsgConstant.FLOAT_SIZE;
//        m_nLen = m_nPos;
        logString.append("[").append(f).append("]");
//        logString.append("[Float:").append(f).append("]");
    }

    public void appendString(String s) {
        try {
            byte[] arrByte = s.getBytes("utf-8");
            int nLen = arrByte.length;
            ensureCapacity(nLen + MsgConstant.SHORT_SIZE);
            System.arraycopy(BitConverter.getBytes((short) nLen), 0, m_aBuff, m_nLen, MsgConstant.SHORT_SIZE);
            m_nLen += MsgConstant.SHORT_SIZE;

            System.arraycopy(arrByte, 0, m_aBuff, m_nLen, nLen);
            m_nLen += nLen;

//            m_nLen = m_nPos;
            logString.append("[").append(s).append("]");
//            logString.append("[String:").append(s).append("]");
        } catch (UnsupportedEncodingException ex) {
        	ex.printStackTrace();
        }
    }

/// 
/// socket数据包格式：
/// ------------|-----------------------|------------------------------|--------------------------------------------------------
/// |iLen       | CompressFlag(1byte)   |     整个BBB的Crc16           |          Xor变换后，假设为BBB                          |
/// |值是AAA    |                       |     Short类型（2字节）        |---------------------------|---------------------------|
/// |           |                       |                              |           压缩数据或未压缩数据                         |
/// |           |                       |                              |---------------------------|---------------------------|
/// |           |                       |                              | msgID 类型为Short（2字节）|  byte[] byteData业务数据体  |
/// |           |-----------------------|------------------------------|---------------------------|---------------------------|
/// |           |                                       假设长度为AAA                                                           |
/// |-----------|-----------------------|--------------------------------------------------------------------------------------|
/// 	
    public byte[] packToMem() throws Exception {
        if (isPacked && packBuff != null) {
            return packBuff;
        }
        byte[] encBuff = new byte[m_nLen];
        System.arraycopy(m_aBuff, 0, encBuff, 0, m_nLen);

        byte[] compressBuff;
        boolean isCompress = false;
        if (m_aBuff.length > COMM_COMPRESS_SIZE) {
            isCompress = true;
            compressBuff = ZLibUtils.compress(encBuff);
        } else {
            compressBuff = encBuff;
        }

        Encrypt.encryptComm(compressBuff);

        int nDestLen = (compressBuff.length + 1 + MsgConstant.SHORT_SIZE);
        packBuff = new byte[nDestLen + MsgConstant.SHORT_SIZE];

        System.arraycopy(BitConverter.getBytes((short) nDestLen), 0, packBuff, 0, MsgConstant.SHORT_SIZE);
        packBuff[MsgConstant.SHORT_SIZE] = isCompress ? (byte) 1 : (byte) 0;

        short crc = CRC16.getInstance().getCrc(compressBuff);

        System.arraycopy(BitConverter.getBytes(crc), 0, packBuff, MsgConstant.SHORT_SIZE + 1, MsgConstant.SHORT_SIZE);

        System.arraycopy(compressBuff, 0, packBuff, MsgConstant.SHORT_SIZE * 2 + 1, compressBuff.length);
        return packBuff;
    }

    public byte[] packToInnerMem() {
        byte[] encBuff = new byte[m_nLen];
        System.arraycopy(m_aBuff, 0, encBuff, 0, m_nLen);
        return encBuff;
    }
//
//    public boolean isNeedLog() {
//        return needLog;
//    }

    @Override
    public String toString() {
        return tempString.append(logString).toString();
    }
}
