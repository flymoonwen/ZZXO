package com.xiaoao.netty;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;

/**
 * Netty ChannelPipeline工厂
 * @Author: siyunlong
 * @Version: V1.00
 * @Create Date: 2015-5-8下午3:31:05
 */
public class GameServerPipelineFactory implements ChannelPipelineFactory {
    @Override
    public ChannelPipeline getPipeline() throws Exception {
        ChannelPipeline pipeline = Channels.pipeline();
        pipeline.addLast("decoder", new GameDecoder());
        pipeline.addLast("encoder", new GameEncoder());     
        pipeline.addLast("handler", new GameServerHandler());
        return pipeline;
    }
}
