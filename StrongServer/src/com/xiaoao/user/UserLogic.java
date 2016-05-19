package com.xiaoao.user;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.procedure.TObjectProcedure;import sun.org.mozilla.javascript.internal.ast.NumberLiteral;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.coyote.http11.filters.VoidInputFilter;
import org.apache.http.nio.protocol.ThrottlingHttpServiceHandler;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.tomcat.util.http.fileupload.ThresholdingOutputStream;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.quartz.JobDetail;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.mchange.io.impl.EndsWithFilenameFilter;
import com.sun.jmx.snmp.daemon.CommunicationException;
import com.sun.org.apache.xml.internal.security.c14n.implementations.Canonicalizer11_OmitComments;
import com.xiaoao.GameServer;
import com.xiaoao.cache.CacheTest;
import com.xiaoao.common.CommNum;
import com.xiaoao.netty.IMsgProcesser;
import com.xiaoao.netty.Router;
import com.xiaoao.netty.message.ClientMsg;
import com.xiaoao.netty.message.MessageCenter;
import com.xiaoao.netty.message.MessageFactory;
import com.xiaoao.netty.message.MsgConstant;
import com.xiaoao.netty.message.ServerMsg;
import com.xiaoao.pojo.Room;
import com.xiaoao.pojo.Room.RoomState;
import com.xiaoao.pojo.SyncData;
import com.xiaoao.pojo.User;
import com.xiaoao.pojo.User.UserState;
import com.xiaoao.pojo.UserMatchInfo;
import com.xiaoao.timer.GameTimerManager;
import com.xiaoao.util.BitConverter;
import com.xiaoao.util.RandomNameManager;


import org.joda.time.DateTime;

/**
 * 用户相关逻辑处理类
 * @Author: siyunlong
 * @Version: V1.00
 * @Create Date: 2015-5-8下午2:03:41
 */
public class UserLogic implements IMsgProcesser{
	
    private static final Logger logger = LogManager.getLogger(UserLogic.class);
    
    
	//在线用户类 更多T详情 去CoollectTest查看
	private final Map<String,User> olUsers = new HashMap<String,User>(10000);
	
	private final List<Room> olRooms = new ArrayList<Room>(10000);
	
	
	
	private static final UserLogic instance = new UserLogic();
    public static UserLogic getInstance() {
        return instance;
    }
	
	
	@Override
	public void registMsgId() {
		Router.getInstance().registMsg(CommNum.MSG_C2S_LOGIN, this);
		Router.getInstance().registMsg(CommNum.MSG_C2S_CREATE_ROOM, this);
		Router.getInstance().registMsg(CommNum.MSG_C2S_INTO_ROOM, this);
		Router.getInstance().registMsg(CommNum.MSG_C2S_START_GAME, this);
		Router.getInstance().registMsg(CommNum.MSG_C2S_EXIT_ROOM, this);
		Router.getInstance().registMsg(CommNum.MSG_C2S_RUNNING_INFO, this);
		Router.getInstance().registMsg(CommNum.MSG_C2S_GAME_COMPLETE, this);
		Router.getInstance().registMsg(CommNum.MSG_C2S_LOADING, this);
		Router.getInstance().registMsg(CommNum.MSG_C2S_SCENE2ROOM, this);
		Router.getInstance().registMsg(CommNum.MSG_C2S_START_ENTER_SCENE, this);
		Router.getInstance().registMsg(CommNum.MSG_C2S_PING, this);
		Router.getInstance().registMsg(CommNum.MSG_C2S_RUNNING_INFO2, this);
		Router.getInstance().registMsg(CommNum.MSG_C2S_CHANGE_CAR, this);
		Router.getInstance().registMsg(CommNum.MSG_C2S_SYNC3, this);
		
		Router.getInstance().registMsg(CommNum.MSG_C2S_RUNNING_INFO4, this);
		
		Router.getInstance().registMsg(CommNum.MSG_C2S_CLIENT_REQ_SRV_SYNC, this);
		Router.getInstance().registMsg(CommNum.MSG_C2S_GET_CLIENT_SYNCDATA_RESULT, this);
		
		Router.getInstance().registMsg(CommNum.MSG_C2S_RUNNING_INFO5_RET, this);
		
		Router.getInstance().registMsg(CommNum.MSG_C2S_SYNC_ACTIONS_BROAD, this);
		
		Router.getInstance().registMsg(CommNum.MSG_C2S_UPLOAD_DATA_BROAD, this);
	}

	@Override
	public void doProcess(int msgId, ClientMsg msg, ChannelHandlerContext ctx) {
		switch (msgId) {
			case CommNum.MSG_C2S_LOGIN:
				userLogin(msg, ctx);
				break;
			case CommNum.MSG_C2S_CREATE_ROOM:
				createRoom(msg, ctx);
				break;
			case CommNum.MSG_C2S_INTO_ROOM:
				intoRoom(msg, ctx);
				break;
			case CommNum.MSG_C2S_START_GAME:
				startGame(msg, ctx);
				break;
			case CommNum.MSG_C2S_EXIT_ROOM:
				exitRoom(msg, ctx);
				break;
			case CommNum.MSG_C2S_RUNNING_INFO:
				syncGameProgress(msg, ctx);
				break;
			case CommNum.MSG_C2S_RUNNING_INFO2:
				syncGameProgress2(msg, ctx);
				break;
			case CommNum.MSG_C2S_SYNC3:
				syncGameProgress3(msg, ctx);
				break;
			case CommNum.MSG_C2S_LOADING:
				syncLoadProgress(msg, ctx);
				break;
			case CommNum.MSG_C2S_GAME_COMPLETE:
				completeGame(msg, ctx);
				break;
			case CommNum.MSG_C2S_SCENE2ROOM:
				sceneToRoom(ctx);
				break;
			case CommNum.MSG_C2S_START_ENTER_SCENE:
				readRunning(msg,ctx);
				break;
			case CommNum.MSG_C2S_PING:
				Ping(msg, ctx);
				break;
			case CommNum.MSG_C2S_RUNNING_INFO4:
				syncGameProgress4(msg, ctx);
				break;
			case CommNum.MSG_C2S_CHANGE_CAR:
				syncGameProgress4(msg, ctx);
				changeCar(msg, ctx);
				break;
			case CommNum.MSG_C2S_CLIENT_REQ_SRV_SYNC:
				resqClientSysnState(msg, ctx);
				break;
			case CommNum.MSG_C2S_GET_CLIENT_SYNCDATA_RESULT:
				resqClientSyncData(msg, ctx);
				break;
			case CommNum.MSG_C2S_RUNNING_INFO5_RET:
				syncGameProgress5(msg, ctx);				
				break;
			case CommNum.MSG_C2S_SYNC_ACTIONS_BROAD:
				resqSyncActions(msg, ctx);
				break;
			case CommNum.MSG_C2S_UPLOAD_DATA_BROAD:
				resqUploadData(msg, ctx);
				break;
		}
	}
	
	/**
	 * 用户登录
	 * @param msg
	 * @param ctx
	 */
	private void userLogin(ClientMsg msg, ChannelHandlerContext ctx) {
		String userName = msg.parseString();
		//进行处理
		User user = olUsers.get(userName);
		if(user == null) {
			user = new User();
			user.setUsername(userName);
			String nickName = RandomNameManager.getInstance().randomName();
			user.setNickName(nickName);
			olUsers.put(userName, user);
		}
		logger.debug("登录成功："+userName+"-"+user.getNickName());
		user.setCtx(ctx); //用户上绑定socket
		ctx.setAttachment(user);//socket 上绑定
		
		//回复客户端登录成功
		ServerMsg serverMsg = new ServerMsg();
		serverMsg.appendMsgId(CommNum.MSG_S2C_LOGIN_RET);
		serverMsg.appendInt(user.getId());
		serverMsg.appendString("登录成功");
		serverMsg.appendString(user.getNickName());
		long now = System.currentTimeMillis();
		serverMsg.appendLong(now);
		MessageCenter.getInstance().sendMsg(serverMsg, ctx);
		
		//发送房间列表给玩家
		sendRoomListToUser(user);
	}
	
	public void createRoom(ClientMsg msg, ChannelHandlerContext ctx) {
		String roomName = msg.parseString();
		User user = (User)ctx.getAttachment();
		Room newRoom = new Room(user.getId());
		newRoom.setRoomName(roomName);
		Room oldRoom = user.getRoom();
		if(oldRoom != null) {
			//异常了
			return;
		}
		newRoom.addUser(user);
		addRoom(newRoom);
		
		ServerMsg serverMsg = new ServerMsg();
		serverMsg.appendMsgId(CommNum.MSG_S2C_CREATE_ROOM_RET);
		newRoom.fillMsg(serverMsg);
		MessageCenter.getInstance().sendMsg(serverMsg, ctx);
		//通知在大厅里的人添加房间
		broadMessage(serverMsg);
	}
	
	private void intoRoom(ClientMsg msg, ChannelHandlerContext ctx) {
		int roomId = msg.parseInt();
		Room room = getRoomById(roomId);
		User user = (User)ctx.getAttachment();
		Room oldRoom = user.getRoom();
		if(oldRoom != null) {
			//异常了
			oldRoom.removeUser(user);
		}
		if(room == null) {
			MessageCenter.getInstance().sendCommonErrorMsg(CommNum.MSG_S2C_INTO_ROOM_RET, "房间不存在", ctx);
			return;
		}
		if(room.isFull()) {
			MessageCenter.getInstance().sendCommonErrorMsg(CommNum.MSG_S2C_INTO_ROOM_RET, "房间已满", ctx);
			return;
		}
		room.addUser(user);
		//通知此玩家
		ServerMsg serverMsg = new ServerMsg();
		serverMsg.appendMsgId(CommNum.MSG_S2C_INTO_ROOM_RET);
		serverMsg.appendBoolean(true);
		room.fillMsg(serverMsg);
		room.broadMessage(serverMsg);
	}
	
	private void exitRoom(ClientMsg msg, ChannelHandlerContext ctx) {
		User user = (User)ctx.getAttachment();
		Room room = user.getRoom();
		if(room == null) {
			MessageCenter.getInstance().sendCommonErrorMsg(CommNum.MSG_S2C_START_GAME_RET, "房间不存在", ctx);
			return;
		}
		room.removeUser(user);
		ServerMsg serverMsg = new ServerMsg();
		serverMsg.appendMsgId(CommNum.MSG_S2C_EXIT_ROOM_RET);
		serverMsg.appendInt(user.getId());
		MessageCenter.getInstance().sendMsg(serverMsg, user.getCtx());
		
		if(room.isEmpty()) {
			//通知其他玩家这个房间删除
			deleteRoom(room);
			serverMsg = new ServerMsg();
			serverMsg.appendMsgId(CommNum.MSG_S2C_REMOVE_ROOM);
			serverMsg.appendInt(room.getRoomId());
			broadMessage(serverMsg,user.getId());
		}else{
			//通知其他玩家
			serverMsg = new ServerMsg();
			serverMsg.appendMsgId(CommNum.MSG_S2C_EXIT_ROOM_RET);
			serverMsg.appendInt(user.getId());
			serverMsg.appendInt(room.getOwnerId());
			room.broadMessage(serverMsg);
		}
		//发送房间列表给玩家
		sendRoomListToUser(user);
	}
	
	
	private void startGame(ClientMsg msg, ChannelHandlerContext ctx) {
		User user = (User)ctx.getAttachment();
		Room room = user.getRoom();
		
		if(room == null) {
			MessageCenter.getInstance().sendCommonErrorMsg(CommNum.MSG_S2C_START_GAME_RET, "房间不存在", ctx);
			return;
		}
		if(!room.isCanStart()) {
			MessageCenter.getInstance().sendCommonErrorMsg(CommNum.MSG_S2C_START_GAME_RET, "不能开始", ctx);
			return;
		}
		room.startGame();
		ServerMsg serverMsg = new ServerMsg();
		serverMsg.appendMsgId(CommNum.MSG_S2C_START_GAME_RET);
		serverMsg.appendBoolean(true);
		room.fillMsg(serverMsg);
		room.broadMessage(serverMsg);
		
		serverMsg = new ServerMsg();
		serverMsg.appendMsgId(CommNum.MSG_S2C_REMOVE_ROOM);
		serverMsg.appendInt(room.getRoomId());
		broadMessage(serverMsg);
	}
	
	/**
	 * loading同步
	 */
	public void syncLoadProgress(ClientMsg msg, ChannelHandlerContext ctx) {
		User user = (User)ctx.getAttachment();
		Room room = user.getRoom();
		if(room == null) {
			return;
		}
		int loadData = msg.parseInt();
		UserMatchInfo info = room.getUserMatchInfo(user.getId());
		info.setLoad(loadData);
		
		if(room.allUserIsLoadComplete()) {
			//所有玩家都加载完成,发送进入场景消息
			ServerMsg serverMsg = new ServerMsg();
			serverMsg.appendMsgId(CommNum.MSG_S2C_ENTER_SCENE);
			room.fillMsg(serverMsg);
			room.broadMessage(serverMsg);
			return;
		}
		
		ServerMsg serverMsg = new ServerMsg();
		serverMsg.appendMsgId(CommNum.MSG_S2C_LOAD_RET);
		serverMsg.appendInt(user.getId());
		serverMsg.appendInt(loadData);
		//广播给其他成员
		room.broadMessage(serverMsg, user.getId());
	}
	
	/**
	 * 同步场景内游戏数据
	 * @param msg
	 * @param ctx
	 */
	public void syncGameProgress(ClientMsg msg, ChannelHandlerContext ctx) {
		User user = (User)ctx.getAttachment();
		Room room = user.getRoom();
		if(room == null) {
			//MessageCenter.getInstance().sendCommonErrorMsg(CommNum.MSG_UPDATE_PLAYERS_INFO, "房间不存在", ctx);
			return;
		}

		
		byte[] match = msg.parseBytes();
		
		UserMatchInfo info = room.getUserMatchInfo(user.getId());
		info.setMatchInfo(match);
		
		ServerMsg serverMsg = new ServerMsg();
		serverMsg.appendMsgId(CommNum.MSG_UPDATE_PLAYERS_INFO);
		serverMsg.appendInt(user.getId());
		serverMsg.appendBytes(match);
		
		//广播给其他成员
		room.broadMessage(serverMsg, user.getId());
	}
	
	/**
	 * 转发客户端动作
	 * @param msg
	 * @param ctx
	 */
	public void syncGameProgress2(ClientMsg msg, ChannelHandlerContext ctx) {
		User user = (User)ctx.getAttachment();
		Room room = user.getRoom();
		if(room == null) {
			//MessageCenter.getInstance().sendCommonErrorMsg(CommNum.MSG_UPDATE_PLAYERS_INFO, "房间不存在", ctx);
			return;
		}
		
		
		
		byte[] match = msg.parseBytes();
		int m_nPos = 0;
		int nLen = BitConverter.getInt(match, m_nPos);
		m_nPos +=  MsgConstant.INT_SIZE;
		int clientTargetFrame = BitConverter.getInt(match, m_nPos);
		m_nPos +=  MsgConstant.INT_SIZE;
		
		
		//服务器当前帧数
		int serverKeyFrame = room.getKeyFrameTick().get();
		
		logger.info(" serverKeyFrame " + serverKeyFrame + " clientCurrFrame " + clientTargetFrame + "  offset  " + ( serverKeyFrame - clientTargetFrame));
		//客户端当前操作是否有效
		
		//服务器帧数已过当前帧数
		if (serverKeyFrame > clientTargetFrame) {
			return;
		}
		
		//服务器帧数
		if ( clientTargetFrame - serverKeyFrame < 10) {
			return;
		}
		

		UserMatchInfo info = room.getUserMatchInfo(user.getId());
		info.setMatchInfo(match);

		ServerMsg serverMsg = MessageFactory.getInstance().getServerMsg();
		serverMsg.appendMsgId(CommNum.MSG_UPDATE_PLAYERS_INFO2);
		serverMsg.appendInt(user.getId());
		serverMsg.appendBytes(match);

		//广播同场景玩家
		room.broadcastMessage(serverMsg);	

		
		

	}
	
	public void syncGameProgress3(ClientMsg msg, ChannelHandlerContext ctx) {
		User user = (User)ctx.getAttachment();
		Room room = user.getRoom();
		if(room == null) {
			return;
		}
		byte[] match = msg.parseBytes();
		room.addSyncData(new SyncData(user.getId(), match));
	}
	
	public void syncGameProgress4(ClientMsg msg, ChannelHandlerContext ctx) {
		User user = (User)ctx.getAttachment();
//		Room room = user.getRoom();
//		if(room == null) {
//			//MessageCenter.getInstance().sendCommonErrorMsg(CommNum.MSG_UPDATE_PLAYERS_INFO, "房间不存在", ctx);
//			return;
//		}
		
		byte[] match = msg.parseBytes();
		
		ServerMsg serverMsg = new ServerMsg();
		serverMsg.appendMsgId(CommNum.MSG_C2S_RUNNING_INFO4_RET);
		serverMsg.appendInt(user.getId());
		serverMsg.appendBytes(match);
		//广播给其他成员
		MessageCenter.getInstance().sendMsg(serverMsg, ctx);
	}
	
	public void syncGameProgress5(ClientMsg msg, ChannelHandlerContext ctx) {
		
		DateTime now = DateTime.now();
		User user = (User)ctx.getAttachment();

		
		int ex = msg.parseInt();
		int clientCurrFrame = msg.parseInt();
		Room room = user.getRoom();
		
		int isSyncNeed = 0;
		
		ServerMsg serverMsg = new ServerMsg();
		serverMsg.appendMsgId(CommNum.MSG_S2C_RUNNING_INFO5_RESP);
		serverMsg.appendInt(user.getId());
		

		//判断当前客户端帧数延迟范围
//		int serverKeyFrame = (int) (( room.getKeyFrameTick().get() - 1 ) * 15  + 15 * ( now.getMillisOfSecond() / 1000.0f));
		int serverKeyFrame = room.getKeyFrameTick().get();
//		serverKeyFrame = serverKeyFrame - 120 > 0 ? serverKeyFrame - 120 : 0;
		if ( serverKeyFrame - clientCurrFrame > 20) {
			isSyncNeed = 1;
		}
		
//		logger.info(" serverKeyFrame " + serverKeyFrame + " clientCurrFrame " + clientCurrFrame + "  offset  " + ( serverKeyFrame - clientCurrFrame));
		
		serverMsg.appendInt(isSyncNeed);
		serverMsg.appendInt(serverKeyFrame);
		MessageCenter.getInstance().sendMsg(serverMsg, ctx);
		
		
//		System.out.println(" userId " + user.getId() + " " + (serverKeyFrame - clientCurrFrame) + "  " + (DateTime.now().getMillis() - now.getMillis()));
		
	}
	
	
	
	
	public void completeGame(ClientMsg msg, ChannelHandlerContext ctx) {
		User user = (User)ctx.getAttachment();
		Room room = user.getRoom();
		if(room == null) {
			//MessageCenter.getInstance().sendCommonErrorMsg(CommNum.MSG_S2C_GAME_COMPLETE_RET, "房间不存在", ctx);
			return;
		}
		if(!user.getState().equals(UserState.RUNNING)) {
			return;
		}
		//完成时间
		int completeTime = msg.parseInt();
		
		UserMatchInfo info = room.getUserMatchInfo(user.getId());
		info.setCompleteTime(completeTime);
		user.setState(UserState.END);
		if(room.allUserIsRunComplete()) {
			ServerMsg serverMsg = new ServerMsg();
			serverMsg.appendMsgId(CommNum.MSG_S2C_MATCH_REWARD);
			//所有人都到终点了
			List<UserMatchInfo> sortList = room.getSortUserMatchInfo();
			int size = sortList.size();
			serverMsg.appendInt(size);
			for (int i = 0; i < size; i++) {
				UserMatchInfo umi = sortList.get(i);
				int gold = (10-i)*100;
				if(gold<=0) gold=100;
				serverMsg.appendInt(umi.getUserId());
				serverMsg.appendInt(umi.getCompleteTime());
				serverMsg.appendInt(gold);
			}
			room.broadMessage(serverMsg);
			room.endGame();
			return;
		}
		
		ServerMsg serverMsg = new ServerMsg();
		serverMsg.appendMsgId(CommNum.MSG_S2C_GAME_COMPLETE_RET);
		serverMsg.appendInt(user.getId());
		serverMsg.appendInt(completeTime);
		//广播给其他成员
		room.broadMessage(serverMsg, user.getId());
	}
	
	/**
	 * 场景返回房间消息
	 */
	public void sceneToRoom(ChannelHandlerContext ctx) {
		User user = (User)ctx.getAttachment();
		if(user.getState() == UserState.END) {
			user.setState(UserState.ROOM);
		}
		
		ServerMsg serverMsg = new ServerMsg();
		serverMsg.appendMsgId(CommNum.MSG_S2C_SCENE2ROOM_RET);
		MessageCenter.getInstance().sendMsg(serverMsg, user.getCtx());
	}
	
	public void readRunning(ClientMsg msg,ChannelHandlerContext ctx) {
		User user = (User)ctx.getAttachment();
		Room room = user.getRoom();
		if(room == null) {
			return;
		}
		String carName = msg.parseString();
		UserMatchInfo info = room.getUserMatchInfo(user.getId());
		info.setCarname(carName);
		
		if(user.getState() == UserState.LOADING) {
			user.setState(UserState.READY);
		}
		if(room.allUserIsReady()) {

			ServerMsg serverMsg = new ServerMsg();
			serverMsg.appendMsgId(CommNum.MSG_S2C_START_RUNNING);
			room.broadMessage(serverMsg);
			room.enterScene();//进入场景状态
		}
	}
	
	private void sendRoomListToUser() {
		List<Room> idleRooms = getIdleRooms();
		final ServerMsg serverMsg = new ServerMsg();
		serverMsg.appendMsgId(CommNum.MSG_S2C_ROOM_LIST);
		serverMsg.appendInt(idleRooms.size());
		for (Room room : idleRooms) {
			room.fillMsg(serverMsg);
		}
		broadMessage(serverMsg);
	}
	
	private void Ping(ClientMsg msg,ChannelHandlerContext ctx) {
		User user = (User)ctx.getAttachment();
		ServerMsg serverMsg = new ServerMsg();
		serverMsg.appendMsgId(CommNum.MSG_S2C_PING_RET);
		serverMsg.appendInt(user.getId());
		serverMsg.appendBytes(msg.parseBytes());
		
		Room room = user.getRoom();
		if(room == null) {
			MessageCenter.getInstance().sendMsg(serverMsg, user.getCtx());
			return;
		}
		room.broadMessage(serverMsg);
	}
	
	public void changeCar(ClientMsg msg,ChannelHandlerContext ctx) {
		User user = (User)ctx.getAttachment();
		user.setCarId(msg.parseInt());
		
		ServerMsg serverMsg = new ServerMsg();
		serverMsg.appendMsgId(CommNum.MSG_S2C_CHANGE_CAR_RET);
		serverMsg.appendInt(user.getCarId());
		MessageCenter.getInstance().sendMsg(serverMsg, user.getCtx());
	}
	
	private void sendRoomListToUser(User user) {
		ServerMsg serverMsg = new ServerMsg();
		serverMsg.appendMsgId(CommNum.MSG_S2C_ROOM_LIST);
		List<Room> idleRooms = getIdleRooms();
		serverMsg.appendInt(idleRooms.size());
		for (Room room : idleRooms) {
			room.fillMsg(serverMsg);
		}
		MessageCenter.getInstance().sendMsg(serverMsg, user.getCtx());
	}
	
	/**
	 * 广播给大厅里的人
	 * @param serverMsg
	 */
	public void broadMessage(ServerMsg serverMsg) {
		//循环
		broadMessage(serverMsg, -1);
	}
	
	public void broadMessage(final ServerMsg serverMsg,final int prohibitUserId) {
		//循环
		for (User user : olUsers.values()) {
			if(user.getState() == UserState.LOBBY && user.getId() != prohibitUserId) {
				MessageCenter.getInstance().sendMsg(serverMsg, user.getCtx());
			}
		}
	}
	
	public void userLoginOut(User user) {
		System.out.println(user.getId()+"  有人退出游戏了");
		olUsers.remove(user.getId());
		Room room = user.getRoom();
		if(room != null) {
			room.removeUser(user);
			if(room.isEmpty()) {
				deleteRoom(room);
			}
		}
	}
	
	public Room getRoomById(int id) {
		for (int i = 0; i < olRooms.size(); i++) {
			Room room = olRooms.get(i);
			if(room.getRoomId() == id) return room;
		}
		return null;
	}
	
	public List<Room> getIdleRooms() {
		List<Room> roomList = new ArrayList<Room>();
		for (int i = 0; i < olRooms.size(); i++) {
			Room room = olRooms.get(i);
			if(room.getState().equals(RoomState.IDEL)) roomList.add(room);
		}
		return roomList;
	}
	
	public List<Room> getRooms(RoomState state){
		List<Room> roomList = new ArrayList<Room>();
		for (int i = 0; i < olRooms.size(); i++) {
			Room room = olRooms.get(i);
			if(room.getState().equals(state)) roomList.add(room);
		}
		return roomList;
	}
	public void addRoom(Room room) {
		olRooms.add(room);
	}
	public void deleteRoom(Room room) {
		room.endTimer();
		olRooms.remove(room);
	}
	
	
	/**
	 * 客户端请求状态同步
	 * @param msg
	 * @param ctx
	 */
	private void resqClientSysnState(ClientMsg msg,ChannelHandlerContext ctx){
		User user = (User)ctx.getAttachment();
		Room room = user.getRoom();
		
		int userId = msg.parseInt();
		
		if(-1 == userId){
			ServerMsg serverMsg = new ServerMsg();
			serverMsg.appendMsgId(CommNum.MSG_S2C_GET_CLIENT_SYNCDATA);
			room.broadMessage(serverMsg, user.getId());
		}else{ 
			ServerMsg serverMsg = new ServerMsg();
			serverMsg.appendMsgId(CommNum.MSG_S2C_GET_CLIENT_SYNCDATA);
			room.broadMessage(serverMsg, user.getId());
		}
		
		if ( !room.syncDataMap.containsKey(user.getId())) {
			room.syncDataMap.put(user.getId(), null);
		}
		
	}
	
	/**
	 * 客户端上传本地场景内数据块
	 * @param msg
	 * @param ctx
	 */
	private void resqClientSyncData(ClientMsg msg,ChannelHandlerContext ctx){
		User user = (User)ctx.getAttachment();
		Room room = user.getRoom();
		
		int userId = user.getId();
		byte[] match = msg.parseBytes();
		SyncData syncData = new SyncData(userId, match);
		
		int maxSize = room.getRoomUsers().size() - 1;
		
		
		for(Integer key: room.syncDataMap.keys()){
			if ( key.intValue() != userId) {
				room.syncDataMap.put( key.intValue(), syncData);
			}
			
			
			if( room.syncDataMap.get(key.intValue()).size() -1 >= maxSize){
				ServerMsg serverMsg = new ServerMsg();
				serverMsg.appendMsgId(CommNum.MSG_S2C_CLIENT_REQ_SRV_SYNC_RET);
				int dataSize = room.syncDataMap.get(key.intValue()).size();
				serverMsg.appendInt(dataSize-1);
				for(SyncData data : room.syncDataMap.get(key.intValue())){
					
					if( data == null ) continue;
					serverMsg.appendInt(data.getUserId());
					serverMsg.appendBytes(data.getData());
				}
				
				for(User roomUser : olUsers.values()){
					if (roomUser.getId() == key.intValue()) {
						System.out.println(" 1035 " + roomUser.getId());
						MessageCenter.getInstance().sendMsg(serverMsg, roomUser.getCtx());	
					}
						
				}
				
				room.syncDataMap.removeAll(key.intValue());
			}
			
		}	
		
	}
	
	private void resqSyncActions(ClientMsg msg,ChannelHandlerContext ctx){
		User user = (User)ctx.getAttachment();
		Room room = user.getRoom();
		if(room == null) {
			//MessageCenter.getInstance().sendCommonErrorMsg(CommNum.MSG_UPDATE_PLAYERS_INFO, "房间不存在", ctx);
			return;
		}
		
		int ex = msg.parseInt();
		int clientCurrFrame = msg.parseInt();
		
		byte[] match = msg.parseBytes(); 
		
		//服务器当前帧数
		int serverKeyFrame = room.getKeyFrameTick().get();
		
		//客户端当前操作是否有效
		if ( serverKeyFrame - clientCurrFrame < 25) {
			UserMatchInfo info = room.getUserMatchInfo(user.getId());
			info.setMatchInfo(match);
			
			ServerMsg serverMsg = MessageFactory.getInstance().getServerMsg();
			serverMsg.appendMsgId(CommNum.MSG_UPDATE_PLAYERS_INFO2);
			serverMsg.appendInt(user.getId());
			serverMsg.appendBytes(match);
			
			//广播同场景玩家
			room.broadcastMessage(serverMsg);
		}
		

		
	}
	
	
	/**
	 * 客户端上传单帧数据 服务器转发后以供客户端收集对比数据
	 * @param msg
	 * @param ctx
	 */
	private void resqUploadData(ClientMsg msg,ChannelHandlerContext ctx){
		User user = (User)ctx.getAttachment();
		Room room = user.getRoom();
		if(room == null) {
			return;
		}

		//客户端数据 已包含当前帧数
		byte[] match = msg.parseBytes();
		
		ServerMsg serverMsg = new ServerMsg();
		serverMsg.appendMsgId(CommNum.MSG_S2C_UPLOAD_DATA_BROAD);
		serverMsg.appendInt(user.getId());
		serverMsg.appendBytes(match);
		
		//广播给其他成员
		room.broadMessage(serverMsg, user.getId());
		
	}
	public  static void main(){
	}
	
}

