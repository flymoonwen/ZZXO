package xiaoao.strong.netty;

import java.util.Calendar;
import java.util.Locale;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;

import xiaoao.strong.common.MessageComm;
import xiaoao.strong.netty.message.ClientMsg;
import xiaoao.strong.netty.message.ServerMsg;
import xiaoao.strong.user.UserLogic;

public class GameClientHandler extends SimpleChannelHandler{
	@Override
	public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e)
			throws Exception {
		System.out.println("客户端:连接关闭");
		super.channelClosed(ctx, e);
		System.exit(-1);
	}

	@Override
	public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e)
			throws Exception {
		System.out.println("客户端:建立连接");
		super.channelConnected(ctx, e);
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
			throws Exception {
		

		ServerMsg msg = (ServerMsg) e.getMessage();
		
		int msgid = msg.getMsgID();
		System.out.println("客户端:收到信息  " +" ServerMsg ID: " + msgid + " " + Calendar.getInstance().getTime());
		
        if (!(msg instanceof ServerMsg)) {
            return;
        }
        
        if(msgid == MessageComm.S2C_LOGIN_MSG_RET) {
        	UserLogic.getInstance().loginSuc(msg);
        } else if(msgid == MessageComm.C2S_SAY_HELLO) {
        	UserLogic.getInstance().sayHello(msg);
        }
	}
}
