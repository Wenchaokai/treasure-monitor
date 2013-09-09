package com.best.job;

import org.quartz.JobExecutionException;

/**
 * ClassName:AlarmMonitorJob Description:
 * 
 * @author ChaoKai Wen email:wenchaokai@gmail.com
 * @version
 * @Date 2013-9-8
 */
public class AlarmMonitorJob {

	public void execute() throws JobExecutionException {
		System.out.println("AlarmMonitorJob");
	}

}
