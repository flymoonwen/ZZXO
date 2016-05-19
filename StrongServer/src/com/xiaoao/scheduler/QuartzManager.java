package com.xiaoao.scheduler;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** */
/**
 * @Title:Quartz管理类
 * 
 */
public class QuartzManager {

	private static Logger logger = LoggerFactory.getLogger(QuartzManager.class);

	private static Map<String, Scheduler> schedulerMap = new HashMap<String, Scheduler>();

	/** */
	/**
	 * 添加一个定时任务，使用默认的任务组名，触发器名，触发器组名
	 * 
	 * @param name
	 *            任务名
	 * @param group
	 *            任务组名
	 * @param jobList
	 *            任务列表
	 * @param time
	 *            时间设置，参考quartz说明文档
	 * @throws SchedulerException
	 */
	public static void executJobs(List<JobEntry> jobList) throws SchedulerException {

		if (jobList == null || jobList.size() == 0)
			return;

		logger.info(" jobList size:" + jobList.size());

		SchedulerFactory sf = new StdSchedulerFactory();
		Scheduler sched = sf.getScheduler(); // 初始化调度器
		schedulerMap.put(java.util.UUID.randomUUID().toString(), sched);

		int index = 1;
		for (JobEntry jobEntry : jobList) {

			if (jobEntry.getJob() == null || jobEntry.getCron() == null)
				continue;

			JobDetail jobDetail = JobBuilder.newJob(jobEntry.getJob().getClass()).withIdentity(jobEntry.getName() + "_" + index, jobEntry.getGroup()).build();
			CronTrigger trigger = (CronTrigger) TriggerBuilder.newTrigger().withIdentity(jobEntry.getName() + "_" + index, jobEntry.getGroup())
					.withSchedule(CronScheduleBuilder.cronSchedule(jobEntry.getCron())).build();
			Date ft = sched.scheduleJob(jobDetail, trigger); // 设置调度作业
			logger.info(jobDetail.getKey() + " has been scheduled to run at: " + ft + " and repeat based on expression: " + trigger.getCronExpression());
			index++;
		}

		if (!sched.isShutdown())
			sched.start(); // 开启调度任务，执行作业
	}
	
	
	
	public static void shutdown() throws SchedulerException {
		if (schedulerMap != null) {
			for (Entry<String, Scheduler> s : schedulerMap.entrySet()) {
				s.getValue().shutdown();
			}
			schedulerMap.clear();
			schedulerMap = new HashMap<String, Scheduler>();
		}
	}

}
