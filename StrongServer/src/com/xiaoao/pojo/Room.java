package com.xiaoao.pojo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.quartz.JobDetail;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.xiaoao.GameServer;
import com.xiaoao.common.CommNum;
import com.xiaoao.netty.message.MessageCenter;
import com.xiaoao.netty.message.MessageFactory;
import com.xiaoao.netty.message.ServerMsg;
import com.xiaoao.pojo.User.UserState;
import com.xiaoao.timer.GameTimerManager;
import com.xiaoao.user.UserLogic;

public class Room extends Broadcaster{
	
	
    private static final Logger logger = LogManager.getLogger(GameServer.class);
    
	
	private static final AtomicInteger NPCSCENEIDSEQUENCE = new AtomicInteger(200);

	private int roomId;
	private String roomName;
	private int ownerId;//房主名
	//在房间里的所有人
	private List<User> roomUsers = new ArrayList<User>();
	//房间里玩家比赛信息
	private Map<Integer,UserMatchInfo> userMatchMap = new HashMap<Integer,UserMatchInfo>();
	//房间状态
	private RoomState state = RoomState.IDEL;
	
	private List<SyncData> roomTimerData = new ArrayList<SyncData>();
	//定时器
	private JobDetail roomTimer;
	
	private AtomicInteger keyFrameTick = new AtomicInteger(0);
	
	
	public HashMultimap<Integer, SyncData> syncDataMap = HashMultimap.create(); 
	
	
	public enum RoomState{
		IDEL,//空闲时
		MARCH//名赛中
	}
	
	public Room(int ownerId) {
		super("Room:"+ownerId);
		this.roomId = NPCSCENEIDSEQUENCE.incrementAndGet();
		this.ownerId = ownerId;
	}
	
	public int getRoomId() {
		return roomId;
	}
	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}
	

	public RoomState getState() {
		return state;
	}

	public void setState(RoomState state) {
		this.state = state;
	}

	public void addUser(User user) {
		user.setRoom(this);
		roomUsers.add(user);
		addGroup(user);
	}
	
	public boolean isFull() {
		return roomUsers.size()>=8;
	}
	
	public boolean isCanStart() {
		for (User user : roomUsers) {
			if(!user.getState().equals(UserState.ROOM)) {
				return false;
			}
		}
		return true;
	}
	
	public boolean isEmpty() {
		return roomUsers.isEmpty();
	}
	
	public boolean removeUser(User user) {
		user.setRoom(null);
		roomUsers.remove(user);
		leaveGroup(user);
		if(this.ownerId == user.getId() && roomUsers.size()>0) {
			//房主退出
			this.ownerId = roomUsers.get(0).getId();
		}
		return true;
	}
	
	
	public int getOwnerId() {
		return ownerId;
	}

	public void startGame() {
		for (User user:roomUsers) {
			user.setState(UserState.LOADING);
			userMatchMap.put(user.getId(), new UserMatchInfo(user.getId()));
		}
		this.state = RoomState.MARCH;
		
		
	}
	
	public void enterScene() {
		for (User user:roomUsers) {
			user.setState(UserState.RUNNING);
		}
		
		this.startTimer();
	}
	
	public void endGame() {
//		for (User user:roomUsers) {
//			user.setState(UserState.ROOM);
//		}
		userMatchMap.clear();
		this.state = RoomState.IDEL;
		if(roomTimer != null) {
			GameTimerManager.getInstance().pauseJob(roomTimer);
		}
		
		ServerMsg serverMsg = new ServerMsg();
		serverMsg.appendMsgId(CommNum.MSG_S2C_CREATE_ROOM_RET);
		fillMsg(serverMsg);
		
		this.keyFrameTick.set(0);
		//通知在大厅里的人添加房间
		UserLogic.getInstance().broadMessage(serverMsg);
	}
	
	public void startTimer() {
		//开始定时任务
		if(roomTimer == null) {
			this.keyFrameTick.set(0);
			roomTimer = GameTimerManager.getInstance().addRoomTimer(this);
		}else{
			GameTimerManager.getInstance().resumeJob(roomTimer);
		}
	}
	
	public void endTimer() {
		if(roomTimer != null) {
			GameTimerManager.getInstance().deleteJob(roomTimer);
			this.roomTimer = null;
		}
	}
	
	public List<User> getRoomUsers() {
		return roomUsers;
	}


	public UserMatchInfo getUserMatchInfo(int userId) {
		if(!userMatchMap.containsKey(userId)) {
			userMatchMap.put(userId, new UserMatchInfo(userId));
		}
		return userMatchMap.get(userId);
	}
	
	public boolean allUserIsLoadComplete() {
		boolean isComplete = true;
		for (UserMatchInfo info : userMatchMap.values()) {
			if(info.getLoad() < 100) {
				isComplete = false;
				break;
			}
		}
		return isComplete;
	}
	public boolean allUserIsRunComplete() {
		for (User user : roomUsers) {
			if(!user.getState().equals(UserState.END)) {
				return false;
			}
		}
		return true;
	}
	public boolean allUserIsReady() {
		for (User user : roomUsers) {
			if(!user.getState().equals(UserState.READY)) {
				return false;
			}
		}
		return true;
	}
	/**
	 * 获取排名
	 * @return
	 */
	public List<UserMatchInfo> getSortUserMatchInfo() {
		List<UserMatchInfo> sortList = new ArrayList<UserMatchInfo>(userMatchMap.values());
		Collections.sort(sortList, new Comparator<UserMatchInfo>() {
			@Override
			public int compare(UserMatchInfo o1, UserMatchInfo o2) {
				if(o1.getCompleteTime() < o2.getCompleteTime()) {
					return 1;
				}
				return -1;
			}
		});
		return sortList;
	}
	
	public void fillMsg(ServerMsg msg) {
		msg.appendInt(roomId);
		msg.appendString(roomName);
		msg.appendInt(ownerId);
		msg.appendInt(state.ordinal());
		msg.appendInt(roomUsers.size());
		for (User user:roomUsers) {
			user.fillMsg(msg);
		}
	}
	
	public void addSyncData(SyncData syncData) {
		roomTimerData.add(syncData);
	}
	
	public void boradRoomTimer() {
		if(roomTimerData.isEmpty()) {
			return;
		}
		List<SyncData> copys = new ArrayList<SyncData>(roomTimerData);
		roomTimerData.clear();
		ServerMsg serverMsg = MessageFactory.getInstance().getServerMsg();
		serverMsg.appendMsgId(CommNum.MSG_S2C_SYNC_BROAD3);
		serverMsg.appendInt(copys.size());
		for (SyncData data : copys) {
			data.fillMsg(serverMsg);
		}
		copys.clear();
		broadcastMessage(serverMsg);
	}
	
	public void broadMessage(ServerMsg serverMsg) {
		broadMessage(serverMsg, -1);
	}
	
	public void broadMessage(ServerMsg serverMsg,int prohibitUserId) {
		for (int i = 0; i < roomUsers.size(); i++) {
			User user = roomUsers.get(i);
			if(user.getId() != prohibitUserId) {
				MessageCenter.getInstance().sendMsg(serverMsg, user.getCtx());
			}
		}
	}
	
	
	public void updateKeyFrameTick(){
		int preFrame = keyFrameTick.get();
		int currFrame = keyFrameTick.incrementAndGet();
		DateTime now = DateTime.now();
//		logger.info( now + "  room id: " + roomId +" preKeyFrame " + preFrame + " currFrame " + currFrame);
	}

	public AtomicInteger getKeyFrameTick() {
		return keyFrameTick;
	}
	
	
}
