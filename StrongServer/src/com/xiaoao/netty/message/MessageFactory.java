package com.xiaoao.netty.message;

import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

/**
 * 消息工厂
 * @Author: siyunlong
 * @Version: V1.00
 * @Create Date: 2016-5-3下午1:34:12
 */
public class MessageFactory {

    private static final MessageFactory instance = new MessageFactory();
    private static final int MAXLENGTH = 200;
    public static MessageFactory getInstance() {
        return instance;
    }

    private final ObjectPool<ServerMsg> serverMsgPool;

    private MessageFactory() {
        GenericObjectPoolConfig cfg = new GenericObjectPoolConfig();
        cfg.setMaxIdle(MAXLENGTH);
        cfg.setMinIdle(MAXLENGTH);
        cfg.setMaxTotal(MAXLENGTH * 2);
        serverMsgPool = new GenericObjectPool<ServerMsg>(new ServerMsgPooledFactory(), cfg);
    }

    public ServerMsg getServerMsg() {
        try {
        	int activeMsgNum = serverMsgPool.getNumActive();
        	if(activeMsgNum > MAXLENGTH) {
        		 System.out.println("存在不释放消息体");
        	}
//        	System.out.println("active:"+activeMsgNum+"  idle"+serverMsgPool.getNumIdle());
            return serverMsgPool.borrowObject();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return getUnPooledServerMsg();
    }

    public ServerMsg getUnPooledServerMsg() {
        return new ServerMsg();
    }

    public void releaseServerMsg(ServerMsg msg) {
        try {
            serverMsgPool.returnObject(msg);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
