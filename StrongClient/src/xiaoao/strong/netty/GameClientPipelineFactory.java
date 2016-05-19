package xiaoao.strong.netty;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;

public class GameClientPipelineFactory implements ChannelPipelineFactory{
	@Override
	public ChannelPipeline getPipeline() throws Exception {
		ChannelPipeline pipeline = Channels.pipeline();  
		pipeline.addLast("decoder", new GameDecoder());
        pipeline.addLast("encoder", new GameEncoder());   
        pipeline.addLast("handler", new GameClientHandler());  
        return pipeline;    
	}
}
