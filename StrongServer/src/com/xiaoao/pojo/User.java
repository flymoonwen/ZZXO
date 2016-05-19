package com.xiaoao.pojo;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

import org.jboss.netty.channel.ChannelHandlerContext;

import com.xiaoao.netty.message.ServerMsg;

/**
 * 用户类  
 * @Author: siyunlong
 * @Version: V1.00
 * @Create Date: 2015-5-8下午3:31:40
 */
public class User implements Serializable{
	private static final AtomicInteger NPCSCENEIDSEQUENCE = new AtomicInteger(10000);
	/**
	 * 
	 */
	private static final long serialVersionUID = -5956821281827967859L;
	
	public enum UserState{
		LOBBY,//大厅中
		ROOM,//房间里
		LOADING,//加载地图
		READY,//准备开始
		RUNNING,//正在进行
		END//比赛结束
	}
	
	/**
	 * 唯一id
	 */
	private int id;
	/**
	 * 登录名称
	 */
	private String username;
	/**
	 * 昵称
	 */
	private String nickName;
	/**
	 * 当前所使用车id
	 */
	private int carId;
	/**
	 * 玩家所属状态
	 */
	private UserState state = UserState.LOBBY;
	
	public Room room;//所在房间
	
	// 角色socket
	private transient ChannelHandlerContext ctx;
	
	public User() {
		this.id = NPCSCENEIDSEQUENCE.incrementAndGet();
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public ChannelHandlerContext getCtx() {
		return ctx;
	}

	public void setCtx(ChannelHandlerContext ctx) {
		this.ctx = ctx;
	}
	
	public void setState(UserState state) {
		this.state = state;
	}

	public UserState getState() {
		return state;
	}
	
	public void setCarId(int carId) {
		this.carId = carId;
	}
	
	public int getCarId() {
		return carId;
	}

	public Room getRoom() {
		return room;
	}

	public void setRoom(Room room) {
		this.room = room;
		if(this.room != null) {
			setState(UserState.ROOM);
		}else{
			setState(UserState.LOBBY);
		}
	}

	public void fillMsg(ServerMsg msg) {
		msg.appendInt(id);
		msg.appendString(nickName);
		msg.appendInt(carId);
		msg.appendInt(state.ordinal());
	}
}
