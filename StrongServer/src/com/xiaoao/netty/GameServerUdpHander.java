package com.xiaoao.netty;

import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.channel.WriteCompletionEvent;

import com.xiaoao.netty.message.ClientMsg;
import com.xiaoao.pojo.User;
import com.xiaoao.user.UserLogic;

public class GameServerUdpHander extends  SimpleChannelUpstreamHandler{

private static final Logger logger = LogManager.getLogger(GameServerHandler.class.getName());
    
    private final AtomicBoolean isInProcess;
    private final StringBuilder sb = new StringBuilder(32);

    GameServerUdpHander() {
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
