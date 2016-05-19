package com.xiaoao.netty.message;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import com.xiaoao.util.BitConverter;
import com.xiaoao.util.Encrypt;
import com.xiaoao.util.ZLibUtils;

/**
 * 客户端消息类
 * @Author: siyunlong
 * @Version: V1.00
 * @Create Date: 2015-5-8下午3:23:58
 */
public class ClientMsg {

    private static final int ERRORPARSE = -999;
    private byte[] m_aBuff;
    private int m_nLen;
    private int m_nPos;
    private final StringBuilder logString = new StringBuilder(64);
    private boolean singleDeal;

    public ClientMsg() {
    }

    public void passivate() {
        logString.setLength(0);
        m_nPos = 0;
        singleDeal = false;
    }

    public int parseInt() {
        if (m_nPos + MsgConstant.INT_SIZE > m_nLen) {
            return ERRORPARSE;
        }

        int result = BitConverter.getInt(m_aBuff, m_nPos);
        m_nPos += MsgConstant.INT_SIZE;
        logString.append("[").append(result).append("]");
        return result;
    }

    public long parseLong() {
        if (m_nPos + MsgConstant.LONG_SIZE > m_nLen) {
            return ERRORPARSE;
        }

        long result = BitConverter.getLong(m_aBuff, m_nPos);
        m_nPos += MsgConstant.LONG_SIZE;
        logString.append("[").append(result).append("]");
        return result;
    }

    public int parseShort() {
        if (m_nPos + MsgConstant.SHORT_SIZE > m_nLen) {
            return ERRORPARSE;
        }

        int result = BitConverter.getShort(m_aBuff, m_nPos);
        m_nPos += MsgConstant.SHORT_SIZE;
        logString.append("[").append(result).append("]");
        return result;
    }

    public int parseUnsignedShort() {
        if (m_nPos + MsgConstant.SHORT_SIZE > m_nLen) {
            return ERRORPARSE;
        }

        int result = BitConverter.getUnsignedShort(m_aBuff, m_nPos);
        m_nPos += MsgConstant.SHORT_SIZE;
        logString.append("[").append(result).append("]");
        return result;
    }

    public byte parseByte() {
        if (m_nPos + 1 > m_nLen) {
            return 0;
        }

        byte result = m_aBuff[m_nPos];
        m_nPos += 1;
        logString.append("[").append(result).append("]");
        return result;
    }

    public boolean parseBoolean() {
        if (m_nPos + 1 > m_nLen) {
            return false;
        }

        byte result = m_aBuff[m_nPos];
        m_nPos += 1;
        logString.append("[").append(result).append("]");
        return result == 1;
    }

    public double parseDouble() {
        if (m_nPos + MsgConstant.DOUBLE_SIZE > m_nLen) {
            return ERRORPARSE;
        }

        double result = BitConverter.getDouble(m_aBuff, m_nPos);
        m_nPos += MsgConstant.DOUBLE_SIZE;
        logString.append("[").append(result).append("]");
        return result;
    }

    public float parseFloat() {
        if (m_nPos + MsgConstant.FLOAT_SIZE > m_nLen) {
            return ERRORPARSE;
        }

        float result = BitConverter.getFloat(m_aBuff, m_nPos);
        m_nPos += MsgConstant.FLOAT_SIZE;
        logString.append("[").append(result).append("]");
        return result;
    }

    public String parseString() {
        try {
            if (m_nPos + MsgConstant.SHORT_SIZE > m_nLen) {
                return "";
            }

            int nLen = BitConverter.getShort(m_aBuff, m_nPos);

            if (m_nPos + MsgConstant.SHORT_SIZE + nLen > m_nLen) {
                return "";
            }

            m_nPos += MsgConstant.SHORT_SIZE;

            String result = new String(m_aBuff, m_nPos, nLen, "utf-8");
            m_nPos += nLen;
            logString.append("[").append(result).append("]");
            return result;
        } catch (UnsupportedEncodingException ex) {
            return "";
        }
    }
    
    public byte[] parseBytes() {
        try {
            if (m_nPos + MsgConstant.INT_SIZE > m_nLen) {
                return null;
            }
            int nLen = BitConverter.getInt(m_aBuff, m_nPos);
            if (m_nPos + MsgConstant.INT_SIZE + nLen > m_nLen) {
                return null;
            }
            m_nPos += MsgConstant.INT_SIZE;
            byte[] result = new byte[nLen];
            System.arraycopy(m_aBuff, m_nPos, result, 0, result.length);
            m_nPos += nLen;
            logString.append("[").append(Arrays.toString(result)).append("]");
            return result;
        } catch (Exception ex) {
            return null;
        }
    }

    public int getMsgID() {
        if (m_nLen < MsgConstant.SHORT_SIZE) {
            return 0;
        } else {
//            int msgId = parseShort();
//            return msgId;
            return parseShort();
        }
    }

/// 
/// socket数据包格式：
/// ------------|-----------------------|------------------------------|--------------------------------------------------------
/// |iLen       | CompressFlag(1byte)   |     整个BBB的Crc16           |          Xor变换后，假设为BBB                          |
/// |值是AAA    |                       |     Short类型（2字节）        |---------------------------|---------------------------|
/// |           |                       |                              |           压缩数据或未压缩数据                         |
/// |           |                       |                              |---------------------------|---------------------------|
/// |           |                       |                              | msgID 类型为Short（2字节） |  byte[] byteData业务数据体 |
/// |           |-----------------------|------------------------------|---------------------------|---------------------------|
/// |           |                                       假设长度为AAA                                                           |
/// |-----------|-----------------------|--------------------------------------------------------------------------------------|
/// 	
    public void createFromMem(byte[] aBuff, boolean isCompress) {
        Encrypt.encryptComm(aBuff);
        if (isCompress) {
            m_aBuff = ZLibUtils.decompress(aBuff);
        } else {
            m_aBuff = aBuff;
        }
        m_nLen = m_aBuff.length;
    }

    public void createFromInnerMem(byte[] aBuff) {
        m_aBuff = aBuff;
        m_nLen = m_aBuff.length;
    }

    public void setMsgFrom(String ip) {
        logString.append("[get ]").append("[").append(ip).append("]");
    }

    public boolean isSingleDeal() {
        return singleDeal;
    }

    public void setSingleDeal(boolean isSingleDeal) {
        this.singleDeal = isSingleDeal;
    }

    @Override
    public String toString() {
        return logString.toString();
    }

//
//    public ClientMsg clone() {
//        ClientMsg cloneObj = new ClientMsg();
//        cloneObj.m_aBuff = new byte[m_nLen];
//        System.arraycopy(this.m_aBuff, 0, cloneObj.m_aBuff, 0, m_nLen);
//        cloneObj.m_nLen = this.m_nLen;
//        cloneObj.m_nPos = this.m_nPos;
//        cloneObj.logString.append(this.logString);        
//        return cloneObj;        
//    }
}
