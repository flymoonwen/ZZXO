package com.xiaoao.netty;

import org.jboss.netty.channel.ChannelHandlerContext;

import com.xiaoao.netty.message.ClientMsg;

/**
 * 服务器消息处理接口
 */
public interface IMsgProcesser {

    /**
     * 注册处理类能够处理的消息id
     */
    void registMsgId();

    /**
     * 消息处理
     *
     * @param msgId 消息ID
     * @param msg 接收的消息
     * @param ctx ChannelHandlerContext
     */
    void doProcess(int msgId, ClientMsg msg, ChannelHandlerContext ctx);
}
