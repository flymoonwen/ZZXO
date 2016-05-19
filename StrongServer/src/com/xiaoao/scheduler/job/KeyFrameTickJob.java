package com.xiaoao.scheduler.job;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.xiaoao.GameServer;
import com.xiaoao.pojo.Room;
import com.xiaoao.pojo.Room.RoomState;
import com.xiaoao.user.UserLogic;

public class KeyFrameTickJob implements Job {
	
    private static final Logger logger = LogManager.getLogger(KeyFrameTickJob.class);
    
    
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		
		for(Room room : UserLogic.getInstance().getRooms(RoomState.MARCH)){
			room.updateKeyFrameTick();
		}
	}

}
