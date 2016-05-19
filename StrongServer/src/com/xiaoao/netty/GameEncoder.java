package com.xiaoao.netty;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;

import com.xiaoao.netty.message.ServerMsg;

/**
 * Netty消息encoder
 * @Author: siyunlong
 * @Version: V1.00
 * @Create Date: 2015-5-8下午3:28:14
 */
public class GameEncoder extends OneToOneEncoder {

    @Override
    protected Object encode(
            ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception {
    	if (!(msg instanceof ServerMsg)) {
            return msg;
        }
    	
    	//获取服务器消息
        ServerMsg res = (ServerMsg) msg;
        byte[] data = res.packToMem();
        
        ChannelBuffer buf = channel.getConfig().getBufferFactory().getBuffer(data.length);
        buf.writeBytes(data);
        return buf;
    }
}
