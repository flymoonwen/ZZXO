package com.xiaoao.common;

import com.sun.org.apache.xml.internal.resolver.helpers.PublicId;

public final class CommNum {

/*-------------------------------------玩家交互  1000--2000-------------------------------------*/

	/**
	*客户端发送登录消息<br />
	MSG_C2S_LOGIN<br />
	{<br />
	&nbsp;string 用户名<br />
	}
	*/
	public static final int MSG_C2S_LOGIN=1000;
	/**
	*服务器返回登录消息<br />
	MSG_S2C_LOGIN_RET<br />
	{<br />
	&nbsp;int 用户id<br />
	&nbsp;string 登录成功提示<br />
	}
	*/
	public static final int MSG_S2C_LOGIN_RET=1001;
	/**
	*服务器发送房间列表<br />
	MSG_S2C_ROOM_LIST<br />
	{<br />
	&nbsp;int 房间个数<br />
	&nbsp;for(房间个数){<br />
	&nbsp;&nbsp;int 房间id<br />
	&nbsp;&nbsp;string 房间名称<br />
	&nbsp;&nbsp;int 房主id<br />
	&nbsp;&nbsp;int 房间状态 0：空闲  1：正在进行<br />
	&nbsp;&nbsp;int 用户数量<br />
	&nbsp;&nbsp;for(用户数量) {<br />
	&nbsp;&nbsp;&nbsp;int 用户id<br />
	&nbsp;&nbsp;&nbsp;string 用户名<br />
	&nbsp;&nbsp;&nbsp;int 使用的赛车id<br />
	&nbsp;&nbsp;&nbsp;int 用户状态 //0:大厅中 1:房间里 2:加载地图 3:准备开始 4:正在进行 5:比赛结束<br />
	&nbsp;&nbsp;}<br />
	&nbsp;&nbsp;<br />
	&nbsp;}<br />
	}
	*/
	public static final int MSG_S2C_ROOM_LIST=1002;
	/**
	*客户端发送创建房间消息<br />
	MSG_C2S_CREATE_ROOM<br />
	{<br />
	&nbsp;<br />
	}
	*/
	public static final int MSG_C2S_CREATE_ROOM=1003;
	/**
	*服务器返回创建房间消息<br />
	MSG_S2C_CREATE_ROOM_RET<br />
	{<br />
	&nbsp;int 房间id<br />
	&nbsp;string 房间名称<br />
	&nbsp;int 房主id<br />
	&nbsp;int 房间状态 0：空闲  1：正在进行<br />
	&nbsp;int 用户数量<br />
	&nbsp;for(用户数量) {<br />
	&nbsp;&nbsp;int 用户id<br />
	&nbsp;&nbsp;string 用户名<br />
	&nbsp;&nbsp;int 用户状态 //0:大厅中 1:房间里 2:加载地图 3:准备开始 4:正在进行 5:比赛结束<br />
	&nbsp;}<br />
	&nbsp;//同时在大厅里的人会收到服务器发送的房间列表更新消息<br />
	}
	*/
	public static final int MSG_S2C_CREATE_ROOM_RET=1004;
	/**
	*客户端发送进入房间消息<br />
	MSG_C2S_INTO_ROOM<br />
	{<br />
	&nbsp;int 房间id<br />
	}
	*/
	public static final int MSG_C2S_INTO_ROOM=1005;
	/**
	*服务器返回进入房间消息<br />
	MSG_S2C_INTO_ROOM_RET<br />
	{<br />
	&nbsp;bool 是否进入成功<br />
	&nbsp;if(成功) {<br />
	&nbsp;&nbsp;int 房间id<br />
	&nbsp;&nbsp;string 房间名称<br />
	&nbsp;&nbsp;int 房主id<br />
	&nbsp;&nbsp;int 房间状态 0：空闲  1：正在进行<br />
	&nbsp;&nbsp;int 用户数量<br />
	&nbsp;&nbsp;for(用户数量) {<br />
	&nbsp;&nbsp;&nbsp;int 用户id<br />
	&nbsp;&nbsp;&nbsp;string 用户名<br />
	&nbsp;&nbsp;&nbsp;int 用户状态 //0:大厅中 1:房间里 2:加载地图 3:准备开始 4:正在进行 5:比赛结束<br />
	&nbsp;&nbsp;}<br />
	&nbsp;}else{<br />
	&nbsp;&nbsp;string 失败原因<br />
	&nbsp;}<br />
	}
	*/
	public static final int MSG_S2C_INTO_ROOM_RET=1006;
	/**
	*客户端发送开始游戏消息<br />
	MSG_C2S_START_GAME<br />
	{<br />
	}
	*/
	public static final int MSG_C2S_START_GAME=1007;
	/**
	*服务器返回开始游戏消息<br />
	MSG_S2C_START_GAME_RET<br />
	{<br />
	&nbsp;bool 是否成功<br />
	&nbsp;if(成功){<br />
	&nbsp;&nbsp;//成功后会把此消息发给同房间里的所有人<br />
	&nbsp;&nbsp;int 房间id<br />
	&nbsp;&nbsp;string 房间名称<br />
	&nbsp;&nbsp;int 房主id<br />
	&nbsp;&nbsp;int 房间状态 0：空闲  1：正在进行<br />
	&nbsp;&nbsp;int 用户数量<br />
	&nbsp;&nbsp;for(用户数量) {<br />
	&nbsp;&nbsp;&nbsp;int 用户id<br />
	&nbsp;&nbsp;&nbsp;string 用户名<br />
	&nbsp;&nbsp;&nbsp;int 用户状态 //0:大厅中 1:房间里 2:加载地图 3:准备开始 4:正在进行 5:比赛结束<br />
	&nbsp;&nbsp;}<br />
	&nbsp;}else{<br />
	&nbsp;&nbsp;string 失败原因<br />
	&nbsp;}<br />
	}
	*/
	public static final int MSG_S2C_START_GAME_RET=1008;
	/**
	*客户端发送退出房间消息<br />
	MSG_C2S_EXIT_ROOM<br />
	{<br />
	}
	*/
	public static final int MSG_C2S_EXIT_ROOM=1009;
	/**
	*服务器返回退出房间消息<br />
	MSG_S2C_EXIT_ROOM_RET<br />
	{<br />
	&nbsp;int 退出玩家id<br />
	}
	*/
	public static final int MSG_S2C_EXIT_ROOM_RET=1010;
	/**
	*服务器发送添加房间消息<br />
	MSG_S2C_ADD_ROOM<br />
	{<br />
	&nbsp;int 房间id<br />
	&nbsp;string 房间名称<br />
	&nbsp;int 房主id<br />
	&nbsp;int 房间状态 0：空闲  1：正在进行<br />
	&nbsp;int 用户数量<br />
	&nbsp;for(用户数量) {<br />
	&nbsp;&nbsp;int 用户id<br />
	&nbsp;&nbsp;string 用户名<br />
	&nbsp;&nbsp;int 用户状态 //0:大厅中 1:房间里 2:加载地图 3:准备开始 4:正在进行 5:比赛结束<br />
	&nbsp;}<br />
	}
	*/
	public static final int MSG_S2C_ADD_ROOM=1011;
	/**
	*服务器发送删除房间消息<br />
	MSG_S2C_REMOVE_ROOM<br />
	{<br />
	&nbsp;int 所删除房间id <br />
	}
	*/
	public static final int MSG_S2C_REMOVE_ROOM=1012;
	/**
	*客户端发送比赛时玩家信息消息<br />
	MSG_C2S_RUNNING_INFO<br />
	{<br />
	&nbsp;bytes 游戏内数据<br />
	}
	*/
	public static final int MSG_C2S_RUNNING_INFO=1013;
	/**
	*服务器更新比赛时所有玩家信息<br />
	MSG_UPDATE_PLAYERS_INFO<br />
	{<br />
	&nbsp;int 玩家id<br />
	&nbsp;bytes 游戏内数据<br />
	}
	*/
	public static final int MSG_UPDATE_PLAYERS_INFO=1014;
	/**
	*客户端发送完成比赛消息<br />
	MSG_C2S_GAME_COMPLETE<br />
	{<br />
	&nbsp;int 所用毫秒<br />
	}
	*/
	public static final int MSG_C2S_GAME_COMPLETE=1015;
	/**
	*服务器返回完成比赛消息<br />
	MSG_S2C_GAME_COMPLETE_RET<br />
	{<br />
	&nbsp;int 玩家id<br />
	}
	*/
	public static final int MSG_S2C_GAME_COMPLETE_RET=1016;
	/**
	*客户端发送加载完成信息<br />
	MSG_C2S_LOADING<br />
	{<br />
	&nbsp;int 加载完成百分比<br />
	}
	*/
	public static final int MSG_C2S_LOADING=1017;
	/**
	*服务器返回玩家加载百分比<br />
	MSG_S2C_LOAD_RET<br />
	{<br />
	&nbsp;int 玩家id<br />
	&nbsp;int 加载完成百分比<br />
	}
	*/
	public static final int MSG_S2C_LOAD_RET=1018;
	/**
	*服务器广播进入场景消息<br />
	MSG_S2C_ENTER_SCENE<br />
	{<br />
	&nbsp;int 房间id<br />
	&nbsp;string 房间名称<br />
	&nbsp;int 房主id<br />
	&nbsp;int 房间状态 0：空闲  1：正在进行<br />
	&nbsp;int 用户数量<br />
	&nbsp;for(用户数量) {<br />
	&nbsp;&nbsp;int 用户id<br />
	&nbsp;&nbsp;string 用户名<br />
	&nbsp;&nbsp;int 用户状态 //0:大厅中 1:房间里 2:加载地图 3:准备开始 4:正在进行 5:比赛结束<br />
	&nbsp;}<br />
	}
	*/
	public static final int MSG_S2C_ENTER_SCENE=1019;
	/**
	*服务器广播比赛结束奖励消息<br />
	MSG_S2C_MATCH_REWARD<br />
	{<br />
	&nbsp;//此list按照完成时间排序之后<br />
	&nbsp;int 人员数量<br />
	&nbsp;for(数量) {<br />
	&nbsp;&nbsp;int 玩家id<br />
	&nbsp;&nbsp;int 所用时间<br />
	&nbsp;&nbsp;int 所发奖励<br />
	&nbsp;&nbsp;string 车名<br />
	&nbsp;}<br />
	}
	*/
	public static final int MSG_S2C_MATCH_REWARD=1020;
	/**
	*客户端发送场景返回房间消息<br />
	MSG_C2S_SCENE2ROOM<br />
	{<br />
	}
	*/
	public static final int MSG_C2S_SCENE2ROOM=1021;
	/**
	*服务器返回场景返回房间消息<br />
	MSG_S2C_SCENE2ROOM_RET<br />
	{<br />
	}
	*/
	public static final int MSG_S2C_SCENE2ROOM_RET=1022;
	/**
	*客户端发送开始进入场景消息<br />
	MSG_C2S_START_ENTER_SCENE<br />
	{<br />
	&nbsp;string 车的型号<br />
	}
	*/
	public static final int MSG_C2S_START_ENTER_SCENE=1023;
	/**
	*服务器广播开始奔跑消息<br />
	MSG_S2C_START_RUNNING<br />
	{<br />
	}
	*/
	public static final int MSG_S2C_START_RUNNING=1024;
	/**
	*客户端发送PING值消息<br />
	MSG_C2S_PPING<br />
	{<br />
	&nbsp;bytes 客户端上传数据<br />
	}
	*/
	public static final int MSG_C2S_PING=1025;
	/**
	*服务器返回PING值消息<br />
	MSG_S2C_PING_RET<br />
	{<br />
	&nbsp;bytes 转发服务器收到的数据<br />
	}
	*/
	public static final int MSG_S2C_PING_RET=1026;
	/**
	*客户端发送比赛时玩家信息消息2 1027<br />
	MSG_C2S_RUNNING_INFO2<br />
	{<br />
	&nbsp;bytes 游戏内数据<br />
	}
	*/
	public static final int MSG_C2S_RUNNING_INFO2=1027;
	/**
	*服务器更新比赛时所有玩家信息2<br />
	MSG_UPDATE_PLAYERS_INFO2<br />
	{<br />
	&nbsp;int 玩家id<br />
	&nbsp;bytes 游戏内数据<br />
	*/
	public static final int MSG_UPDATE_PLAYERS_INFO2=1028;
	/**
	*客户端发送换车消息<br />
	MSG_C2S_CHANGE_CAR<br />
	{<br />
	&nbsp;int 车id<br />
	}
	*/
	public static final int MSG_C2S_CHANGE_CAR=1029;
	/**
	*服务器返回换车消息<br />
	MSG_S2C_CHANGE_CAR_RET<br />
	{<br />
	&nbsp;int 新的车id<br />
	}
	*/
	public static final int MSG_S2C_CHANGE_CAR_RET=1030;
	/**
	*客户端发送比赛时玩家信息消息3<br />
	MSG_C2S_SYNC3<br />
	{<br />
	&nbsp;byte[] data<br />
	}
	*/
	public static final int MSG_C2S_SYNC3=1031;
	/**
	*服务器返回定时广播消息<br />
	MSG_S2C_SYNC_BROAD3<br />
	{<br />
	&nbsp;int 数量<br />
	&nbsp;for(num) {<br />
	&nbsp;&nbsp;int 玩家id<br />
	&nbsp;&nbsp;bytes 数据<br />
	}
	&nbsp;}<br />
	*/
	public static final int MSG_S2C_SYNC_BROAD3=1032;
	
	
	/**
	 * 客户端请求帧数同步<br />
	 */
	public static final int MSG_C2S_RUNNING_INFO5_RET = 1033;
	
	
	
	public static final int MSG_C2S_RUNNING_INFO4=9998;
	public static final int MSG_C2S_RUNNING_INFO4_RET=9999;
	
	/**
	 * 客户端请求状态同步  [异常玩家的id -1表示所有,除了自己]
	 */
	public static final int MSG_C2S_CLIENT_REQ_SRV_SYNC = 1034;
	
	/**
	 * int 数据块个数
	 * foreach(block in blocks)
	 * {
	 * 		int 玩家id
	 * 		BLOCK 数据块
	 * }
	 * */
	public static final int MSG_S2C_CLIENT_REQ_SRV_SYNC_RET = 1035;
	
	/**
	 * 服务器要求客户端上传状态
	 */
	public static final int MSG_S2C_GET_CLIENT_SYNCDATA = 1036;
	
	/**
	 *客户端上传状态 
	 *携带一个数据块 byte[]
	 */
	public static final int MSG_C2S_GET_CLIENT_SYNCDATA_RESULT = 1037;
	
	
	/**
	 * 服务器返回是否需要帧数同步<br />
	 * MSG_S2C_RUNNING_INFO5_RESP<br />
	 * {<br />
	 * &nbsp;int 是否需要同步 0不需要  1需要 <br />
	 * &nbsp;int 服务器当前帧数
	 * <br />}
	 * 
	 */
	public static final int MSG_S2C_RUNNING_INFO5_RESP = 1038;
	
	
	/**
	 * 客户端上传操作指令 1039
	 */
	public static final int MSG_C2S_SYNC_ACTIONS_BROAD = 1039;
	
	/**
	 * 客户端上传当前数据 1040
	 * 收集测试数据
	 */
	public static final int MSG_C2S_UPLOAD_DATA_BROAD = 1040;
	
	/**
	 * 服务器推送客户端当前数据 1041
	 * 
	 * 
	 * 	{
			int 客户端id
			bytes 客户端数据块
		}
	 * 
	 */
	public static final int MSG_S2C_UPLOAD_DATA_BROAD = 1041;
	
}