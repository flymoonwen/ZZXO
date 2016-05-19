package xiaoao.strong.netty;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;

import xiaoao.strong.netty.message.ServerMsg;
import xiaoao.strong.util.BitConverter;
import xiaoao.strong.util.CRC16;

/**
 * Netty消息decoder
 */
public class GameDecoder extends FrameDecoder {

    @Override
    protected ServerMsg decode(
            final ChannelHandlerContext ctx, final Channel channel, final ChannelBuffer buffer) throws Exception {
    	
        if (buffer.readableBytes() < 2) {
            return null;
        }
        buffer.markReaderIndex();
        short dataLength = BitConverter.getShort(buffer.readBytes(2).array());
        if (buffer.readableBytes() < dataLength) {
            buffer.resetReaderIndex();
            return null;
        }

        boolean isCompress = (buffer.readByte() == 1);

        short crc16 = BitConverter.getShort(buffer.readBytes(2).array());
        byte[] decoded = buffer.readBytes(dataLength - 3).array();
        if (CRC16.getInstance().getCrc(decoded) == crc16) {
            ServerMsg recive = new ServerMsg();
            recive.createFromMem(decoded, isCompress);
            recive.setMsgFrom(ctx.getChannel().getRemoteAddress().toString());
            return recive;
        }
        return null;

    }
}
