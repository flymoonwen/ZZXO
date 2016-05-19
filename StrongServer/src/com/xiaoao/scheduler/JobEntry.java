package com.xiaoao.scheduler;

import org.quartz.Job;

public class JobEntry {

	private Job job;
	private String cron;
	private String name;
	private String group;

	public JobEntry(Job job, String cron) {
		this.job = job;
		this.cron = cron;
	}

	public JobEntry(Job job, String cron, String name, String group) {
		this.job = job;
		this.cron = cron;
		this.name = name;
		this.group = group;
	}

	public Job getJob() {
		return job;
	}

	public void setJob(Job job) {
		this.job = job;
	}

	public String getCron() {
		return cron;
	}

	public void setCron(String cron) {
		this.cron = cron;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

}