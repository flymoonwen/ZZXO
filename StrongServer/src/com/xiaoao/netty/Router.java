package com.xiaoao.netty;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.joda.time.DateTime;

import com.xiaoao.netty.message.ClientMsg;

/**
 * 消息分发类
 * @Author: siyunlong
 * @Version: V1.00
 * @Create Date: 2015-5-8下午3:31:24
 */
public class Router {

    private static final Router instance = new Router();

    public static Router getInstance() {
        return instance;
    }
    /**
     * 消息处理器集合
     */
    private final TIntObjectMap<IMsgProcesser> msgProcesser;

    private Router() {
        msgProcesser = new TIntObjectHashMap<IMsgProcesser>(96);
    }

    /**
     * 注册消息处理类
     *
     * @param msgId 消息id
     * @param process 消息处理器
     */
    public void registMsg(int msgId, IMsgProcesser process) {
        msgProcesser.put(msgId, process);
    }

    /**
     * 处理消息
     *
     * @param msg 具体的消息
     * @param ctx Netty ChannelHandlerContext
     */
    public void process(ClientMsg msg, ChannelHandlerContext ctx) {
        try {
        	
        	DateTime now = DateTime.now();
            int msgId = msg.getMsgID();
            IMsgProcesser process = msgProcesser.get(msgId);
            if (process != null) {
                process.doProcess(msgId, msg, ctx);
            }
            
//            System.out.println(" process msgId " + msgId  +  "  " + ( DateTime.now().getMillis() - now.getMillis()));
        } catch (Exception ex) {
        } finally {
        	String log = msg.toString();
            if(!log.contains("[1025]") && !log.contains("[1013]") && !log.contains("[1014]")) {
            	System.out.println(msg.toString());
            }
        }
    }
}
