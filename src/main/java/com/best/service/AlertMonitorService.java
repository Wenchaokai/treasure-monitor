package com.best.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.best.dao.AlertMonitorDao;
import com.best.domain.AlertMonitor;

/**
 * ClassName:AlertMonitorService Description:
 * 
 * @author ChaoKai Wen email:wenchaokai@gmail.com
 * @version
 * @Date 2013-9-7
 */
@Service("alertMonitorService")
public class AlertMonitorService extends BaseService {

	@Autowired
	private AlertMonitorDao alertMonitorDao;

	private static final String ALERT_MONITOR_PREFIX = "ALERT_MONITOR_";
	private static final String ALERT_MONITOR_USER_PREFIX = "ALERT_MONITOR_USER_PREFIX_";
	private static final String ALERT_MONITOR_PAGESIZE_PREFIX = "ALERT_MONITOR_PAGESIZE_";
	private static final String ALERT_MONITOR_USER_PAGESIZE_PREFIX = "ALERT_MONITOR_USER_PAGESIZE_";
	private static final String ALERT_MONITOR_SINGLE_PREFIX = "ALERT_MONITOR_SINGLE_";

	public int getAlertMonitorsTotalSize(String monitorId, String userCount) {
		if (StringUtils.isNotBlank(monitorId))
			return getAlertMonitorsPageSizeByMonitorId(monitorId);
		else
			return getAllAlertMonitorsPageSize(userCount);
	}

	public List<AlertMonitor> getAlertMonitorMonitors(String monitorId, int end, String userCount) {
		if (StringUtils.isNotBlank(monitorId))
			return getAlertMonitors(monitorId, end);
		else
			return getAllAlertMonitors(end, userCount);
	}

	private Integer getAllAlertMonitorsPageSize(String userCount) {
		Integer res = null;
		try {
			res = memcachedClient.get(ALERT_MONITOR_USER_PAGESIZE_PREFIX + userCount);
		} catch (Exception e) {
		}
		if (null != res)
			return res;
		res = alertMonitorDao.getAllAlertMonitorTotalSize(userCount);
		try {
			memcachedClient.add(ALERT_MONITOR_USER_PAGESIZE_PREFIX + userCount, 0, res);
		} catch (Exception e) {
		}
		return res;
	}

	private Integer getAlertMonitorsPageSizeByMonitorId(String monitorId) {
		Integer res = null;
		try {
			res = memcachedClient.get(ALERT_MONITOR_PAGESIZE_PREFIX + monitorId);
		} catch (Exception e) {
		}
		if (null != res)
			return res;
		res = alertMonitorDao.getAlertMonitorTotalSize(Long.parseLong(monitorId.trim()));
		try {
			memcachedClient.add(ALERT_MONITOR_PAGESIZE_PREFIX + monitorId, 0, res);
		} catch (Exception e) {
		}
		return res;
	}

	private List<AlertMonitor> getAllAlertMonitors(int currentPage, String userCount) {
		List<AlertMonitor> res = null;
		// 从缓存读取数据
		try {
			res = memcachedClient.get(ALERT_MONITOR_USER_PREFIX + userCount);
		} catch (Exception e) {
		}
		int start = currentPage * alertMonitorDao.pageSize;
		int end = start + alertMonitorDao.pageSize;
		if (null != res && res.size() > start) {
			return res.subList(start, res.size() > end ? end : res.size());
		}
		res = alertMonitorDao.getAllAlertMonitors(userCount);

		try {
			memcachedClient.add(ALERT_MONITOR_USER_PREFIX + userCount, 0, res);
		} catch (Exception e) {
		}

		if (res.size() < start) {
			return new ArrayList<AlertMonitor>();
		}
		return res.subList(start, res.size() > end ? end : res.size());
	}

	public List<AlertMonitor> getAlertMonitors(String monitorId) {
		List<AlertMonitor> res = null;
		// 从缓存读取数据
		try {
			res = memcachedClient.get(ALERT_MONITOR_PREFIX + monitorId);
		} catch (Exception e) {
		}

		res = alertMonitorDao.getAlertMonitorByMonitorId(Long.parseLong(monitorId));

		try {
			memcachedClient.add(ALERT_MONITOR_PREFIX + monitorId, 0, res);
		} catch (Exception e) {
		}

		return res;
	}

	private List<AlertMonitor> getAlertMonitors(String monitorId, int currentPage) {
		List<AlertMonitor> res = null;
		// 从缓存读取数据
		try {
			res = memcachedClient.get(ALERT_MONITOR_PREFIX + monitorId);
		} catch (Exception e) {
		}
		int start = currentPage * alertMonitorDao.pageSize;
		int end = start + alertMonitorDao.pageSize;
		if (null != res && res.size() > start) {
			return res.subList(start, res.size() > end ? end : res.size());
		}
		res = alertMonitorDao.getAlertMonitorByMonitorId(Long.parseLong(monitorId));

		try {
			memcachedClient.add(ALERT_MONITOR_PREFIX + monitorId, 0, res);
		} catch (Exception e) {
		}

		if (res.size() < start) {
			return new ArrayList<AlertMonitor>();
		}
		return res.subList(start, res.size() > end ? end : res.size());
	}

	public void deleteAlertMonitor(long alertMonitorId, String monitorId, String userCount) {
		alertMonitorDao.deleteAlertMonitor(alertMonitorId);
		try {
			memcachedClient.delete(ALERT_MONITOR_PREFIX + monitorId);
			memcachedClient.delete(ALERT_MONITOR_USER_PREFIX + userCount);
			memcachedClient.delete(ALERT_MONITOR_PAGESIZE_PREFIX + monitorId);
			memcachedClient.delete(ALERT_MONITOR_USER_PAGESIZE_PREFIX + userCount);
			memcachedClient.delete(ALERT_MONITOR_SINGLE_PREFIX + alertMonitorId);
		} catch (Exception e) {
		}
	}

	public void deleteAlertMonitorByParent(long parent) {
		alertMonitorDao.deleteAlertMonitorByParent(parent);
	}

	public Long insertAlertMonitor(AlertMonitor alertMonitor) {
		Long alertMonitorId = alertMonitorDao.insertAlertMonitor(alertMonitor);
		try {
			memcachedClient.delete(ALERT_MONITOR_PREFIX + alertMonitor.getMonitorId());
			memcachedClient.delete(ALERT_MONITOR_USER_PREFIX + alertMonitor.getUserCount());
			memcachedClient.delete(ALERT_MONITOR_PAGESIZE_PREFIX + alertMonitor.getMonitorId());
			memcachedClient.delete(ALERT_MONITOR_USER_PAGESIZE_PREFIX + alertMonitor.getUserCount());
		} catch (Exception e) {
		}

		return alertMonitorId;

	}

	public AlertMonitor getAlertMonitorMonitor(long alertMonitorId) {
		AlertMonitor res = null;
		try {
			res = memcachedClient.get(ALERT_MONITOR_SINGLE_PREFIX + alertMonitorId);
		} catch (Exception e) {
		}
		if (null != res)
			return res;
		res = alertMonitorDao.getAlertMonitor(alertMonitorId);
		try {
			memcachedClient.add(ALERT_MONITOR_SINGLE_PREFIX + alertMonitorId, 0, res);
		} catch (Exception e) {
		}
		return res;
	}

	public void updateAlertMonitor(AlertMonitor alertMonitor, String userCount) {
		alertMonitorDao.updateAlertMonitor(alertMonitor);
		try {
			memcachedClient.delete(ALERT_MONITOR_PREFIX + alertMonitor.getMonitorId());
			memcachedClient.delete(ALERT_MONITOR_USER_PREFIX + userCount);
			memcachedClient.delete(ALERT_MONITOR_PAGESIZE_PREFIX + alertMonitor.getMonitorId());
			memcachedClient.delete(ALERT_MONITOR_USER_PAGESIZE_PREFIX + userCount);
			memcachedClient.delete(ALERT_MONITOR_SINGLE_PREFIX + alertMonitor.getAlertMonitorId());
		} catch (Exception e) {
		}
	}

	public void deleteMonitor(long monitorId, String userCount) {
		List<AlertMonitor> alertMonitors = this.getAlertMonitors(monitorId + "");
		if (CollectionUtils.isNotEmpty(alertMonitors))
			for (AlertMonitor alertMonitor : alertMonitors) {
				alertMonitorDao.deleteAlertMonitor(alertMonitor.getAlertMonitorId());
			}
		// alertMonitorDao.deleteMonitor(monitorId);
		try {
			memcachedClient.delete(ALERT_MONITOR_PREFIX + monitorId);
			memcachedClient.delete(ALERT_MONITOR_USER_PREFIX + userCount);
			memcachedClient.delete(ALERT_MONITOR_PAGESIZE_PREFIX + monitorId);
			memcachedClient.delete(ALERT_MONITOR_USER_PAGESIZE_PREFIX + userCount);
		} catch (Exception e) {
		}
	}

	public List<AlertMonitor> getStartedAlertMonitorProject(int start, int size) {
		return alertMonitorDao.getStartedAlertMonitorProject(start, size);
	}

	public void updateAlertMonitorCount(AlertMonitor alertMonitor) {
		alertMonitorDao.updateAlertMonitorCount(alertMonitor);
	}

}
