package com.best.domain;

import java.io.Serializable;

/**
 * ClassName:Treasure Description:
 * 
 * @author ChaoKai Wen email:wenchaokai@gmail.com
 * @version
 * @Date 2013-8-31
 */
public class Treasure implements Serializable {

	private static final long serialVersionUID = -6688707907667509101L;

	private Long id;
	private String dateTime;
	private Integer monitorNums;
	private Integer alarmNums;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDateTime() {
		return dateTime;
	}

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

	public Integer getMonitorNums() {
		return monitorNums;
	}

	public void setMonitorNums(Integer monitorNums) {
		this.monitorNums = monitorNums;
	}

	public Integer getAlarmNums() {
		return alarmNums;
	}

	public void setAlarmNums(Integer alarmNums) {
		this.alarmNums = alarmNums;
	}

}
