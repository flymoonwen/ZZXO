package com.xiaoao.pojo;

import java.util.Iterator;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;

import com.xiaoao.netty.message.MessageCenter;
import com.xiaoao.netty.message.ServerMsg;

/**
 * 广播器
 * 所有牵涉到广播消息的实体都可以继承此广播器，例如场景，分线，团队等
 * @author xiaoaogame
 *
 */
public abstract class Broadcaster {
	//广播组,将消息广播给此组内的所有人
	private ChannelGroup channelGroup;

	public Broadcaster(String groupName) {
		this.channelGroup = new DefaultChannelGroup(groupName);
	}
	
	/**
     * 广播消息
     * @param msg 消息体
     */
    public void broadcastMessage(ServerMsg msg) {
        MessageCenter.getInstance().sendChannelMessage(msg, channelGroup);
    }
    
    /**
	 * 玩家加入此广播器
	 * @param role
	 */
	public void addGroup(User user) {
		if(user.getCtx() != null) {
			channelGroup.add(user.getCtx().getChannel());
		}
	}
	/**
	 * 玩家离开此广播器
	 * @param role
	 */
	public void leaveGroup(User user) {
		if(user.getCtx() != null) {
			channelGroup.remove(user.getCtx().getChannel());
		}
	}
	/**
	 * 添加广播管理器里的所有对象加入
	 * @param other
	 */
	public void addBroadcaster(Broadcaster other) {
		//添加通道
        Iterator<Channel> its = other.channelGroup.iterator();
        while(its.hasNext()) {
        	this.channelGroup.add(its.next());
        }
	}
	/**
	 * 清空此管理器里的所有接受者
	 */
	public void clearBroadcaster() {
		this.channelGroup.clear();
	}
}
