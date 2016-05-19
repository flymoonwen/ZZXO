package com.xiaoao.netty;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;

import com.xiaoao.netty.message.ClientMsg;
import com.xiaoao.util.BitConverter;
import com.xiaoao.util.CRC16;

/**
 * Netty消息decoder
 * @Author: siyunlong
 * @Version: V1.00
 * @Create Date: 2015-5-8下午3:24:57
 */
public class GameDecoder extends FrameDecoder {

	@Override
    protected ClientMsg decode(
            final ChannelHandlerContext ctx, final Channel channel, final ChannelBuffer buffer) throws Exception {
        if (buffer.readableBytes() < 2) {
            return null;
        }
        buffer.markReaderIndex();
        //获取消息长度 最大是 32767
        short dataLength = BitConverter.getShort(buffer.readBytes(2).array());
        int allLength = buffer.readableBytes();
        if (allLength < dataLength) {
            buffer.resetReaderIndex();
            return null;
        }
        //是否压缩了
        boolean isCompress = (buffer.readByte() == 1);
        //进行数据校验
        short crc16 = BitConverter.getShort(buffer.readBytes(2).array());
        
        byte[] decoded = buffer.readBytes(dataLength - 3).array();
        if (CRC16.getInstance().getCrc(decoded) == crc16) {
        	//校验成功处理
            ClientMsg recive = new ClientMsg();
            recive.createFromMem(decoded, isCompress);
            //只是日志
            recive.setMsgFrom(ctx.getChannel().getRemoteAddress().toString());
            return recive;
        }
        return null;
    }
}
