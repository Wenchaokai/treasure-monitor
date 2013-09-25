package com.best.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

/**
 * ClassName:AlertMonitor Description:
 * 
 * @author ChaoKai Wen email:wenchaokai@gmail.com
 * @version
 * @Date 2013-9-7
 */
public class AlertMonitor implements Serializable {

	private static final long serialVersionUID = -7179956916667772436L;

	private Long alertMonitorId;
	private Long monitorId;
	private Integer alertMonitorIndex;
	private String monitorName;
	private Integer alertMonitorDay;
	private Double alertMonitorNum;
	private Integer alertMonitorUnit;
	private String alertMonitorMsg;
	private String alertMonitorSms;
	private String alertMonitorEmail;
	private Integer alertMonitorStatus;
	private Integer alertMonitorCompare;
	private String alertMonitorWareHouseCode;
	private String alertMonitorWareHouseName;
	private String alertMonitorSku;
	private String alertMonitorDistrict;
	private Integer count;
	private Integer alertMonitorEnableSms;
	private Integer alertMonitorEnableEmail;
	private Integer alertMonitorCount;
	private String userCount;
	private Long parentId;

	public Long getAlertMonitorId() {
		return alertMonitorId;
	}

	public void setAlertMonitorId(Long alertMonitorId) {
		this.alertMonitorId = alertMonitorId;
	}

	public Long getMonitorId() {
		return monitorId;
	}

	public void setMonitorId(Long monitorId) {
		this.monitorId = monitorId;
	}

	public Integer getAlertMonitorDay() {
		return alertMonitorDay;
	}

	public void setAlertMonitorDay(Integer alertMonitorDay) {
		this.alertMonitorDay = alertMonitorDay;
	}

	public Double getAlertMonitorNum() {
		return alertMonitorNum;
	}

	public void setAlertMonitorNum(Double alertMonitorNum) {
		this.alertMonitorNum = alertMonitorNum;
	}

	public Integer getAlertMonitorUnit() {
		return alertMonitorUnit;
	}

	public void setAlertMonitorUnit(Integer alertMonitorUnit) {
		this.alertMonitorUnit = alertMonitorUnit;
	}

	public String getAlertMonitorMsg() {
		return alertMonitorMsg;
	}

	public void setAlertMonitorMsg(String alertMonitorMsg) {
		this.alertMonitorMsg = alertMonitorMsg;
	}

	public String getAlertMonitorSms() {
		return alertMonitorSms == null ? "" : alertMonitorSms;
	}

	public void setAlertMonitorSms(String alertMonitorSms) {
		this.alertMonitorSms = alertMonitorSms;
	}

	public String getAlertMonitorEmail() {
		return alertMonitorEmail == null ? "" : alertMonitorEmail;
	}

	public void setAlertMonitorEmail(String alertMonitorEmail) {
		this.alertMonitorEmail = alertMonitorEmail;
	}

	public Integer getAlertMonitorStatus() {
		return alertMonitorStatus;
	}

	public void setAlertMonitorStatus(Integer alertMonitorStatus) {
		this.alertMonitorStatus = alertMonitorStatus;
	}

	public Integer getAlertMonitorCompare() {
		return alertMonitorCompare;
	}

	public void setAlertMonitorCompare(Integer alertMonitorCompare) {
		this.alertMonitorCompare = alertMonitorCompare;
	}

	public String getMonitorName() {
		return monitorName == null ? "" : monitorName;
	}

	public void setMonitorName(String monitorName) {
		this.monitorName = monitorName;
	}

	public Integer getAlertMonitorIndex() {
		return alertMonitorIndex;
	}

	public void setAlertMonitorIndex(Integer alertMonitorIndex) {
		this.alertMonitorIndex = alertMonitorIndex;
	}

	public String getAlertMonitorWareHouseCode() {
		return alertMonitorWareHouseCode;
	}

	public void setAlertMonitorWareHouseCode(String alertMonitorWareHouseCode) {
		this.alertMonitorWareHouseCode = alertMonitorWareHouseCode;
	}

	public String getAlertMonitorWareHouseName() {
		return alertMonitorWareHouseName == null ? "" : alertMonitorWareHouseName;
	}

	public void setAlertMonitorWareHouseName(String alertMonitorWareHouseName) {
		this.alertMonitorWareHouseName = alertMonitorWareHouseName;
	}

	public String getAlertMonitorSku() {
		return alertMonitorSku == null ? "" : alertMonitorSku;
	}

	public void setAlertMonitorSku(String alertMonitorSku) {
		this.alertMonitorSku = alertMonitorSku;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public String getAlertMonitorDistrict() {
		return alertMonitorDistrict == null ? "" : alertMonitorDistrict;
	}

	public void setAlertMonitorDistrict(String alertMonitorDistrict) {
		this.alertMonitorDistrict = alertMonitorDistrict;
	}

	public Integer getAlertMonitorEnableSms() {
		return alertMonitorEnableSms;
	}

	public void setAlertMonitorEnableSms(Integer alertMonitorEnableSms) {
		this.alertMonitorEnableSms = alertMonitorEnableSms;
	}

	public Integer getAlertMonitorEnableEmail() {
		return alertMonitorEnableEmail;
	}

	public void setAlertMonitorEnableEmail(Integer alertMonitorEnableEmail) {
		this.alertMonitorEnableEmail = alertMonitorEnableEmail;
	}

	public Integer getAlertMonitorCount() {
		return alertMonitorCount;
	}

	public void setAlertMonitorCount(Integer alertMonitorCount) {
		this.alertMonitorCount = alertMonitorCount;
	}

	public String getUserCount() {
		return userCount;
	}

	public void setUserCount(String userCount) {
		this.userCount = userCount;
	}

	public List<String> getSmsInfo() {
		List<String> res = new ArrayList<String>();
		if (StringUtils.isNotBlank(this.alertMonitorSms)) {
			String[] parts = this.alertMonitorSms.split(",");
			for (String part : parts) {
				res.add(part);
			}
		}
		if (res.size() < 4) {
			int index = res.size();
			for (; index < 4; index++) {
				res.add("");
			}

		} else {
			res = res.subList(0, 4);
		}
		return res;
	}

	public List<String> getEmailInfo() {
		List<String> res = new ArrayList<String>();
		if (StringUtils.isNotBlank(this.alertMonitorEmail)) {
			String[] parts = this.alertMonitorEmail.split(",");
			for (String part : parts) {
				res.add(part);
			}
		}
		if (res.size() < 4) {
			int index = res.size();
			for (; index < 4; index++) {
				res.add("");
			}

		} else {
			res = res.subList(0, 4);
		}
		return res;
	}

	public String formatAlertMsg() {
		String msg = alertMonitorMsg;
		if (null == msg)
			return "";
		if ("-1".equals(alertMonitorSku))
			msg = msg.replace("[sku-Name]", "所有SKU");
		else
			msg = msg.replace("[sku-Name]", this.alertMonitorSku);
		if ("-1".equals(alertMonitorWareHouseName))
			msg = msg.replace("[ofc-Name]", "所有仓库");
		else
			msg = msg.replace("[ofc-Name]", this.alertMonitorWareHouseName);
		msg = msg.replace("[date-Num]", this.alertMonitorDay + "");
		msg = msg.replace("[date-Unit]", "日");
		String compareUnit = ">";
		if (this.alertMonitorCompare == 2)
			compareUnit = "<";
		else if (this.alertMonitorCompare == 3)
			compareUnit = "=";
		else if (this.alertMonitorCompare == 4)
			compareUnit = ">=";
		else if (this.alertMonitorCompare == 5)
			compareUnit = "<=";
		msg = msg.replace("[compare-Unit]", compareUnit);
		msg = msg.replace("[warning-Value]", this.alertMonitorNum + "");
		String valueUnit = "件";
		if (this.alertMonitorUnit == 1)
			valueUnit = "%";
		else if (this.alertMonitorUnit == 2)
			valueUnit = "单";
		msg = msg.replace("[warning-Value-Unit]", valueUnit);
		return msg;
	}

	public List<String> getDistributes() {
		if (StringUtils.isBlank(alertMonitorDistrict))
			return new ArrayList<String>();
		String[] provinces = alertMonitorDistrict.split(",");
		List<String> res = new ArrayList<String>();
		for (String province : provinces) {
			String[] city = province.split("#");
			if (city.length != 2)
				continue;
			res.add(city[1]);
		}
		return res;

	}

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public static void main(String[] args) {
		String llString = "daskjfhd[sakf]jkdsa";
		llString = llString.replace("[sakf]", "dddddd");
		System.out.println(llString);
	}
}
