package com.xiaoao.pojo;

/**
 * 玩家比赛信息
 * @Author: siyunlong
 * @Version: V1.00
 * @Create Date: 2016-4-1上午10:45:29
 */
public class UserMatchInfo {
	//玩家id
	private int userId;
	//load百分比
	private int load;
	//玩家比赛信息
	private byte[] matchInfo;
	//完成所需时间
	private int completeTime;
	private String carname;
	
	
	public UserMatchInfo(int userId) {
		this.userId = userId;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public int getLoad() {
		return load;
	}
	public void setLoad(int load) {
		this.load = load;
	}
	public int getCompleteTime() {
		return completeTime;
	}
	public void setCompleteTime(int completeTime) {
		this.completeTime = completeTime;
	}
	public byte[] getMatchInfo() {
		return matchInfo;
	}
	public void setMatchInfo(byte[] matchInfo) {
		this.matchInfo = matchInfo;
	}
	public String getCarname() {
		return carname;
	}
	public void setCarname(String carname) {
		this.carname = carname;
	}
	
}
