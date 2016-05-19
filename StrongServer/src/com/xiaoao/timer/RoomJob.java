package com.xiaoao.timer;

import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.xiaoao.netty.message.MessageFactory;
import com.xiaoao.netty.message.ServerMsg;
import com.xiaoao.pojo.Room;

/**
 * 战斗线定时管理器
 * @author xiaoaogame
 *
 */
public class RoomJob implements Job{

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		JobDetail job = context.getJobDetail();
		Room room = (Room)job.getJobDataMap().get("room");

//		room.boradRoomTimer();
		
		room.updateKeyFrameTick();
		
		
	}
}
