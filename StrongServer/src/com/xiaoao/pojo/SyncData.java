package com.xiaoao.pojo;

import com.xiaoao.netty.message.ServerMsg;

public class SyncData {
	private int userId;
	private byte[] data;
	
	public SyncData(int userId, byte[] data) {
		super();
		this.userId = userId;
		this.data = data;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public byte[] getData() {
		return data;
	}
	public void setData(byte[] data) {
		this.data = data;
	}
	
	public void fillMsg(ServerMsg serverMsg) {
		serverMsg.appendInt(userId);
		serverMsg.appendBytes(data);
	}
}
