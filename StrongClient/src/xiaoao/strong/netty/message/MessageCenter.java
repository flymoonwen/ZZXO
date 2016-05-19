package xiaoao.strong.netty.message;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.group.ChannelGroup;

/**
 * 消息处理中心，负责消息的发送
 *
 */
public class MessageCenter {

    private static final MessageCenter instance = new MessageCenter();

    public static MessageCenter getInstance() {
        return instance;
    }

    /**
     * 发送消息
     *
     * @param msg 要发送的消息
     * @param ctx 接收消息的socket
     */
    public void sendMsg(ClientMsg msg, ChannelHandlerContext ctx) {
        if (ctx == null) {
            return;
        }
        Channel socket = ctx.getChannel();
        if (socket.isConnected()) {
            msg.setMsgTo(socket.getRemoteAddress().toString());            
            socket.write(msg);
        }
    }

    /**
     * 发送频道消息
     *
     * @param msg 要发送的消息
     */
    public void sendChannelMessage(ClientMsg msg, ChannelGroup sockets) {
        try {
            sockets.write(msg);
        } catch (Exception ex) {
        }
    }

    /**
     * 发送一般错误消息 格式： msgId { false; 错误信息 }
     *
     * @param msgId 消息id
     * @param errorMsg 错误信息
     * @param socket 接受消息的socket
     */
    public void sendCommonErrorMsg(int msgId, String errorMsg, ChannelHandlerContext socket) {
        ClientMsg msg = new ClientMsg();
        msg.appendMsgId(msgId);
        msg.appendBoolean(false);
        msg.appendString(errorMsg);
        sendMsg(msg, socket);
    }

    /**
     * 发送正常成功消息 格式： msgId { true }
     *
     * @param msgId 消息id
     * @param socket 接受消息的socket
     */
    public void sendCommonSuccMsg(int msgId, ChannelHandlerContext socket) {
    	ClientMsg msg = new ClientMsg();
        msg.appendMsgId(msgId);
        msg.appendBoolean(true);
        sendMsg(msg, socket);
    }

}
