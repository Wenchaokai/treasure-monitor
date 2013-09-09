package com.best.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Repository;

import com.best.domain.AlertMonitor;
import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * ClassName:AlertMonitorDao Description:
 * 
 * @author ChaoKai Wen email:wenchaokai@gmail.com
 * @version
 * @Date 2013-9-7
 */
@Repository("alertMonitorDao")
public class AlertMonitorDao extends BaseDao {

	@Resource(name = "sqlMapClient")
	protected SqlMapClient sqlMapClient;

	private static final String space = "alertMonitorSpace.";

	@SuppressWarnings("unchecked")
	public List<AlertMonitor> getAllAlertMonitors() {
		Map<String, Object> map = new HashMap<String, Object>();

		// return (List<AlertMonitor>) this.list(space +
		// "GET_ALL_ALERTMONITOR_BY_PAGESIZE", map, sqlMapClient);

		List<AlertMonitor> res = new ArrayList<AlertMonitor>();
		int pageSize = 500;
		int start = 0;
		while (true) {
			map.put("start", start);
			map.put("pageSize", pageSize);
			List<AlertMonitor> obj = (List<AlertMonitor>) this
					.list(space + "GET_ALL_ALERTMONITOR_BY_PAGESIZE", map, sqlMapClient);
			if (obj == null || obj.size() == 0) {
				break;
			}
			res.addAll(obj);
			if (obj.size() < pageSize)
				break;
			start += pageSize;
		}

		return res;

	}

	public Integer getAllAlertMonitorTotalSize() {
		Map<String, String> map = new HashMap<String, String>();
		Integer res = (Integer) this.object(space + "GET_ALL_ALERTMONITOR_BY_TOTALSIZE_MONITORID", map, sqlMapClient);
		if (res % pageSize == 0)
			return res / pageSize;
		return res / pageSize + 1;
	}

	public Integer getAlertMonitorTotalSize(Long monitorId) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("monitorId", monitorId);
		Integer res = (Integer) this.object(space + "GET_ALERTMONITOR_BY_TOTALSIZE_MONITORID", map, sqlMapClient);
		if (res % pageSize == 0)
			return res / pageSize;
		return res / pageSize + 1;
	}

	@SuppressWarnings("unchecked")
	public List<AlertMonitor> getAlertMonitorByMonitorId(Long monitorId) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("monitorId", monitorId);
		return (List<AlertMonitor>) this.list(space + "GET_ALERTMONITOR_BY_PAGESIZE_MONITORID", map, sqlMapClient);
	}

	public void deleteAlertMonitor(Long alertMonitorId) {
		this.delete(space + "DELETE_ALERTMONITOR", alertMonitorId, sqlMapClient);
	}

	public void insertAlertMonitor(AlertMonitor alertMonitor) {
		this.insert(space + "INSERT_ALERTMONITOR", alertMonitor, sqlMapClient);
	}

	public AlertMonitor getAlertMonitor(long alertMonitorId) {
		return (AlertMonitor) this.object(space + "FIND_ALERT_MONITOR", alertMonitorId, sqlMapClient);
	}

	public void updateAlertMonitor(AlertMonitor alertMonitor) {
		this.update(space + "UPDATE_ALERTMONITOR", alertMonitor, sqlMapClient);
	}

	public void deleteMonitor(long monitorId) {
		this.delete(space + "DELETE_MONITOR", monitorId, sqlMapClient);
	}
}
