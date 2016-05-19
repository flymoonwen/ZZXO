package xiaoao.strong;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

import xiaoao.strong.common.MessageComm;
import xiaoao.strong.netty.GameClientPipelineFactory;
import xiaoao.strong.netty.message.ClientMsg;

/**
 * 客户端
 * @Author: siyunlong
 * @Version: V1.00
 * @Create Date: 2015-5-8上午8:41:29
 */
public class GameClient {
	private ClientBootstrap bootstrap;
	private ChannelFuture channelFuture;  
    private Channel channel;
    public static GameClient instance;
    public static GameClient getInstance() {
    	if(instance == null) {
    		instance = new GameClient();
    		instance.init();
    	}
    	return instance;
    }
	private boolean init() {
		// Client服务启动器
		System.setProperty("java.net.preferIPv4Stack", "true");  
        System.setProperty("java.net.preferIPv6Addresses", "false");  
		bootstrap = new ClientBootstrap(
				new NioClientSocketChannelFactory(
						Executors.newCachedThreadPool(),
						Executors.newCachedThreadPool()));
		bootstrap.setOption("child.tcpNoDelay", true);  
        bootstrap.setOption("child.keepAlive", true);
		// 设置一个处理服务端消息和各种消息事件的类(Handler)
		bootstrap.setPipelineFactory(new GameClientPipelineFactory());

		// 连接到本地的7666端口的服务端
		channelFuture = bootstrap.connect(new InetSocketAddress("115.159.1.36", 8666));
//		channelFuture = bootstrap.connect(new InetSocketAddress("127.0.0.1", 8666));
		channel = channelFuture.awaitUninterruptibly().getChannel(); 
		return true;
	}
	
	public void stop() {  
        channelFuture.awaitUninterruptibly();  
        if (!channelFuture.isSuccess()) {  
            channelFuture.getCause().printStackTrace();  
        }  
        channelFuture.getChannel().getCloseFuture().awaitUninterruptibly();  
        bootstrap.releaseExternalResources();  
    }  
	
	/**
	 * 发送消息
	 * @param msg
	 */
	public void sendMsg(ClientMsg msg) {
		channel.write(msg);
	}
	
	/**
	 * 测试  登录
	 * @param userName
	 */
	public void login(String userName) {
		ClientMsg msg = new ClientMsg();
		msg.appendMsgId(MessageComm.C2S_LOGIN_MSG);
		msg.appendString(userName);
		sendMsg(msg);
	}
	
	private void excuteMatch(){
		ClientMsg msg = new ClientMsg();
		msg.appendMsgId(MessageComm.C2S_ROOMMATCH_MATCH);
		sendMsg(msg);
	}
	
	private void addMatching(){
		ClientMsg msg = new ClientMsg();
		msg.appendMsgId(MessageComm.C2S_ROOMMATCH_ADDMATCHING);
		sendMsg(msg);
	}
	
	private void removeMatching(){
		ClientMsg msg = new ClientMsg();
		msg.appendMsgId(MessageComm.C2S_ROOMMATCH_REMOVEMATCHING);
		sendMsg(msg);
	}
	public static void main(String[] args) {
		GameClient client = GameClient.getInstance();
		client.login("USER_"+System.currentTimeMillis());
	}
}

