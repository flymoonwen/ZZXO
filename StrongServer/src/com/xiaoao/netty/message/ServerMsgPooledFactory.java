package com.xiaoao.netty.message;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

/**
 * 消息池
 * @Author: siyunlong
 * @Version: V1.00
 * @Create Date: 2016-5-3下午1:34:25
 */
class ServerMsgPooledFactory extends BasePooledObjectFactory<ServerMsg> {

    @Override
    public ServerMsg create(){
        return new ServerMsg();
    }

  
    @Override
    public PooledObject<ServerMsg> wrap(ServerMsg obj) {
        return new DefaultPooledObject<ServerMsg>(obj);
    }

    @Override
    public void passivateObject(PooledObject<ServerMsg> p){
        p.getObject().passivate();
    }
}
