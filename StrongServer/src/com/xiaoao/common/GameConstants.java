package com.xiaoao.common;

/**
 * 游戏静态参数
 * @Author: siyunlong
 * @Version: V1.00
 * @Create Date: 2015-5-8下午3:22:37
 */
public class GameConstants {
	public static final byte TRUE = 1;
	public static final byte FALSE = 0;
	
	public static final byte MAN = 0;
	public static final byte WOMAN = 1;
	
	/**
	 * 毫秒
	 */
	public static final long MILLISECOND = 1l;
	/**
	 * 秒
	 */
	public static final long SECOND= 1000*MILLISECOND;
	/**
	 * 分钟
	 */
	public static final long MINUTE= 60*SECOND;
	/**
	 * 小时
	 */
	public static final long HOURR= 60*MINUTE;
	/**
	 * 天
	 */
	public static final long DAY = 24*HOURR;
	
	/**
	 * 玩家30分钟木有心跳，就死亡了，无法抢救！
	 */
	public static final long KICK_TIME = 10*MINUTE;
	
	/**
	 * 清楚玩家基本信息时间
	 */
	public static final long CLEAN_USER_INFO_TIME = HOURR;
	
}
