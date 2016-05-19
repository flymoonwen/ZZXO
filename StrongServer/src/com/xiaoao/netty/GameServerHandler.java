package com.xiaoao.netty;

import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;

import com.xiaoao.netty.message.ClientMsg;
import com.xiaoao.pojo.User;
import com.xiaoao.user.UserLogic;

/**
 * Netty 的接受消息处理Handle
 * @Author: siyunlong
 * @Version: V1.00
 * @Create Date: 2015-5-8下午3:29:32
 */
public class GameServerHandler extends SimpleChannelHandler {

    private static final Logger logger = LogManager.getLogger(GameServerHandler.class.getName());
    
    private final AtomicBoolean isInProcess;
    private final StringBuilder sb = new StringBuilder(32);

    GameServerHandler() {
        super();
        isInProcess = new AtomicBoolean(false);
    }

    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent csEvent) throws Exception {
        sb.setLength(0);
        sb.append("[sys ]")
                .append("[").append(ctx.getChannel().getRemoteAddress().toString()).append("]")
                .append("[").append("连接").append("]");
        logger.info(sb.toString());
        System.out.println(sb.toString());
        super.channelConnected(ctx, csEvent);
    }

    @Override
    public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        sb.setLength(0);
        sb.append("[sys ]")
                .append("[").append(ctx.getChannel().getRemoteAddress().toString()).append("]")
                .append("[").append("断开").append("]");
        logger.info(sb.toString());
        System.out.println(sb.toString());
        
        try {
            if (ctx.getAttachment() instanceof User) {
            	User user = (User) ctx.getAttachment();
                UserLogic.getInstance().userLoginOut(user);
            }
            ctx.setAttachment(null);
        } catch (Exception ex) {
        	logger.error(ex);
        }
        super.channelClosed(ctx, e);
    }
    
    /**
     *  对于ChannelHandler，
     *  是UDP与TCP区别的核心所在。
     *  大家都知道UDP是无连接的，
     *  也就是说你通过 MessageEvent 参数对象的 getChannel() 方法获取当前会话连接，
     *  但是其 isConnected() 永远都返回 false。
     *  UDP 开发中在消息获取事件回调方法中，
     *  获取了当前会话连接 channel 对象后可直接通过 channel 的 write 方法发送数据给对端 channel.write(message, remoteAddress)，
     *  第一个参数仍然是要发送的消息对象， 
     *  第二个参数则是要发送的对端 SocketAddress 地址对象。
     *  这里最需要注意的一点是SocketAddress，在TCP通信中我们可以通过channel.getRemoteAddress()获得，
     *  但在UDP通信中，我们必须从MessageEvent中通过调用getRemoteAddress()方法获得对端的SocketAddress 地址。 
     */
    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
        ClientMsg msg = (ClientMsg) e.getMessage();
        if (!(msg instanceof ClientMsg)) {
            return;
        }
        if (!isInProcess.compareAndSet(false, true)) {
            return;
        }
        
        
        Router.getInstance().process(msg, ctx);
        isInProcess.set(false);
    }
    

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
        sb.setLength(0);
        sb.append(ctx.getChannel().getRemoteAddress()).append("user");
        appendErrorMessage(e.getCause());
        logger.info(sb.toString());
    }

    private void appendErrorMessage(Throwable e) {
        StackTraceElement[] stackTrace = e.getStackTrace();
        sb.append("----------------------------------").append("\n");
        sb.append(e.getClass()).append(":").append(e.getMessage()).append("\n");
        for (StackTraceElement t : stackTrace) {
            sb.append("    ").append("at ").append(t.getClassName()).append(".").append(t.getMethodName()).append("(").append(t.getFileName()).append(":").append(t.getLineNumber()).append(")").append("\n");
        }
    }

}
