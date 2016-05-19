package xiaoao.strong.netty;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;

import xiaoao.strong.netty.message.ClientMsg;

/**
 * Netty消息encoder
 */
public class GameEncoder extends OneToOneEncoder {

    @Override
    protected Object encode(
            ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception {
    	if (!(msg instanceof ClientMsg)) {
            return msg;
        }

    	ClientMsg res = (ClientMsg) msg;
        byte[] data = res.packToMem();
        
        ChannelBuffer buf = channel.getConfig().getBufferFactory().getBuffer(data.length);
        buf.writeBytes(data);
        return buf;
    }
}
