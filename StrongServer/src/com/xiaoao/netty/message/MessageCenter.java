package com.xiaoao.netty.message;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.group.ChannelGroup;

import com.xiaoao.GameServer;

/**
 * 消息处理中心，负责消息的发送
 * @Author: siyunlong
 * @Version: V1.00
 * @Create Date: 2015-5-8下午3:24:16
 */
public class MessageCenter {

    private static final Logger logger = LogManager.getLogger(MessageCenter.class);
    
    
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
    public void sendMsg(ServerMsg msg, ChannelHandlerContext ctx) {
        if (ctx == null) {
            return;
        }
        Channel socket = ctx.getChannel();
        if (socket.isConnected()) {
            msg.setMsgTo(socket.getRemoteAddress().toString());
            String log = msg.toString();
            if(!log.contains("[1026]") && !log.contains("[1014]") && !log.contains("[1013]")) {
            	logger.info(log);
            }
            socket.write(msg);
        }
    }

    /**
     * 发送频道消息
     *
     * @param msg 要发送的消息
     */
    public void sendChannelMessage(ServerMsg msg, ChannelGroup sockets) {
        try {
        	if(sockets == null) {
        		return;
        	}
        	msg.setMsgTo(sockets.getName());
        	String log = msg.toString();
            if(!log.contains("[1026]")) {
            	logger.info(log);
            }
            sockets.write(msg);
        } catch (Exception ex) {
        	ex.printStackTrace();
        }finally{
        	MessageFactory.getInstance().releaseServerMsg(msg);
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
        ServerMsg msg = new ServerMsg();
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
    	ServerMsg msg = new ServerMsg();
        msg.appendMsgId(msgId);
        msg.appendBoolean(true);
        sendMsg(msg, socket);
    }

}
