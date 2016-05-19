package com.xiaoao.timer;

import static org.quartz.JobBuilder.newJob;

import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import com.xiaoao.pojo.Room;


/**
 * 定时处理管理器
 * @author xiaoaogame
 *
 */
public class GameTimerManager {
	private static final GameTimerManager instance = new GameTimerManager();
    public static GameTimerManager getInstance() {
        return instance;
    }
    
    private SchedulerFactory sf = new StdSchedulerFactory();
    /**
     * 服务器监控定时处理key
     */
    private JobKey monitorJobKey;
    
    public void startTimer() {
    	try {
    		Scheduler sched = sf.getScheduler();
    		sched.start();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("定时任务加载失败");
		}
    	
    }
    
    public void shutdownTimer() {
    	try {
    		Scheduler sched = sf.getScheduler();
        	if(sched.isStarted()) {
        		sched.shutdown(true);
        	}
		} catch (Exception e) {
		}
    }
    
    /**
     * 改变服务器监控任务状态
     */
    public void changeMonitorStatus(boolean isOpen) {
    	try {
    		Scheduler sched = sf.getScheduler();
    		if(isOpen) {
    			//回复
    			sched.resumeJob(monitorJobKey);
    		}else{
    			//暂停
    			sched.pauseJob(monitorJobKey);
    		}
		} catch (Exception e) {
		}
    }
    
    /**
     * 添加战斗线处理任务，每一个战斗线都有处理任务
     * @param line
     */
    public JobDetail addRoomTimer(Room room) {
    	try {
    		Scheduler sched = sf.getScheduler();
    		int roomId = room.getRoomId();
    		String jobName = "room-"+roomId;
    		//每隔1秒处理一次场景
    		JobDetail lineJob = newJob(RoomJob.class).withIdentity(jobName+"-Job", "room-group").build();
    		lineJob.getJobDataMap().put("room", room);
    		Trigger trigger= TriggerBuilder
                    .newTrigger()
                    .withIdentity(jobName+"-trigger", "room-group")
                    .startNow()
                    .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                            .withIntervalInMilliseconds(25)//25ms一次
                            .repeatForever()
//                            .withRepeatCount(-1)        //重复次数(将执行6次)
                            )
                    .build();
    		sched.scheduleJob(lineJob, trigger);
    		
    		return lineJob;
		} catch (Exception e) {
			System.out.println("加载");
		}
    	return null;
    }
    
    /**
     * 暂停某一个任务
     * @param lineJob
     */
    public void pauseJob(JobDetail lineJob) {
    	try {
    		if(lineJob == null) {
    			return;
    		}
    		Scheduler sched = sf.getScheduler();
    		sched.pauseJob(lineJob.getKey());
		} catch (Exception e) {
			System.out.println("加载");
		}
    }
    
    public void deleteJob(JobDetail lineJob) {
    	try {
    		if(lineJob == null) {
    			return;
    		}
    		Scheduler sched = sf.getScheduler();
    		sched.deleteJob(lineJob.getKey());
		} catch (Exception e) {
			System.out.println("加载");
		}
    }
    
    /**
     * 唤醒某一个任务
     * @param lineJob
     */
    public void resumeJob(JobDetail lineJob) {
    	try {
    		if(lineJob == null) {
    			return;
    		}
    		Scheduler sched = sf.getScheduler();
    		sched.resumeJob(lineJob.getKey());
		} catch (Exception e) {
			System.out.println("加载");
		}
    }
    
    
    public SchedulerFactory getSf() {
		return sf;
	}
    
}
