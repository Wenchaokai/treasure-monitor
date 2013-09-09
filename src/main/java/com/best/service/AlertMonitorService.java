package com.best.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import net.rubyeye.xmemcached.exception.MemcachedException;

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
	private static final String ALERT_MONITOR_PAGESIZE_PREFIX = "ALERT_MONITOR_PAGESIZE_";
	private static final String ALERT_MONITOR_SINGLE_PREFIX = "ALERT_MONITOR_SINGLE_";

	public int getAlertMonitorsTotalSize(String monitorId) {
		if (StringUtils.isNotBlank(monitorId))
			return getAlertMonitorsPageSizeByMonitorId(monitorId);
		else
			return getAllAlertMonitorsPageSize();
	}

	public List<AlertMonitor> getAlertMonitorMonitors(String monitorId, int end) {
		if (StringUtils.isNotBlank(monitorId))
			return getAlertMonitors(monitorId, end);
		else
			return getAllAlertMonitors(end);
	}

	private Integer getAllAlertMonitorsPageSize() {
		Integer res = null;
		try {
			res = memcachedClient.get(ALERT_MONITOR_PAGESIZE_PREFIX);
		} catch (TimeoutException e) {
		} catch (InterruptedException e) {
		} catch (MemcachedException e) {
		}
		if (null != res)
			return res;
		res = alertMonitorDao.getAllAlertMonitorTotalSize();
		try {
			memcachedClient.add(ALERT_MONITOR_PAGESIZE_PREFIX, 0, res);
		} catch (TimeoutException e) {
		} catch (InterruptedException e) {
		} catch (MemcachedException e) {
		}
		return res;
	}

	private Integer getAlertMonitorsPageSizeByMonitorId(String monitorId) {
		Integer res = null;
		try {
			res = memcachedClient.get(ALERT_MONITOR_PAGESIZE_PREFIX + monitorId);
		} catch (TimeoutException e) {
		} catch (InterruptedException e) {
		} catch (MemcachedException e) {
		}
		if (null != res)
			return res;
		res = alertMonitorDao.getAlertMonitorTotalSize(Long.parseLong(monitorId.trim()));
		try {
			memcachedClient.add(ALERT_MONITOR_PAGESIZE_PREFIX + monitorId, 0, res);
		} catch (TimeoutException e) {
		} catch (InterruptedException e) {
		} catch (MemcachedException e) {
		}
		return res;
	}

	private List<AlertMonitor> getAllAlertMonitors(int currentPage) {
		List<AlertMonitor> res = null;
		// 从缓存读取数据
		try {
			res = memcachedClient.get(ALERT_MONITOR_PREFIX);
		} catch (TimeoutException e) {
		} catch (InterruptedException e) {
		} catch (MemcachedException e) {
		}
		int start = currentPage * alertMonitorDao.pageSize;
		int end = start + alertMonitorDao.pageSize;
		if (null != res && res.size() > start) {
			return res.subList(start, res.size() > end ? end : res.size());
		}
		res = alertMonitorDao.getAllAlertMonitors();

		try {
			memcachedClient.add(ALERT_MONITOR_PREFIX, 0, res);
		} catch (TimeoutException e) {
		} catch (InterruptedException e) {
		} catch (MemcachedException e) {
		}

		if (res.size() < start) {
			return new ArrayList<AlertMonitor>();
		}
		return res.subList(start, res.size() > end ? end : res.size());
	}

	private List<AlertMonitor> getAlertMonitors(String monitorId, int currentPage) {
		List<AlertMonitor> res = null;
		// 从缓存读取数据
		try {
			res = memcachedClient.get(ALERT_MONITOR_PREFIX + monitorId);
		} catch (TimeoutException e) {
		} catch (InterruptedException e) {
		} catch (MemcachedException e) {
		}
		int start = currentPage * alertMonitorDao.pageSize;
		int end = start + alertMonitorDao.pageSize;
		if (null != res && res.size() > start) {
			return res.subList(start, res.size() > end ? end : res.size());
		}
		res = alertMonitorDao.getAlertMonitorByMonitorId(Long.parseLong(monitorId));

		try {
			memcachedClient.add(ALERT_MONITOR_PREFIX + monitorId, 0, res);
		} catch (TimeoutException e) {
		} catch (InterruptedException e) {
		} catch (MemcachedException e) {
		}

		if (res.size() < start) {
			return new ArrayList<AlertMonitor>();
		}
		return res.subList(start, res.size() > end ? end : res.size());
	}

	public void deleteAlertMonitor(long alertMonitorId, String monitorId) {
		alertMonitorDao.deleteAlertMonitor(alertMonitorId);
		try {
			memcachedClient.delete(ALERT_MONITOR_PREFIX + monitorId);
			memcachedClient.delete(ALERT_MONITOR_PREFIX);
			memcachedClient.delete(ALERT_MONITOR_PAGESIZE_PREFIX + monitorId);
			memcachedClient.delete(ALERT_MONITOR_PAGESIZE_PREFIX);
			memcachedClient.delete(ALERT_MONITOR_SINGLE_PREFIX + alertMonitorId);
		} catch (TimeoutException e) {
		} catch (InterruptedException e) {
		} catch (MemcachedException e) {
		}
	}

	public void insertAlertMonitor(AlertMonitor alertMonitor) {
		alertMonitorDao.insertAlertMonitor(alertMonitor);
		try {
			memcachedClient.delete(ALERT_MONITOR_PREFIX + alertMonitor.getMonitorId());
			memcachedClient.delete(ALERT_MONITOR_PREFIX);
			memcachedClient.delete(ALERT_MONITOR_PAGESIZE_PREFIX + alertMonitor.getMonitorId());
			memcachedClient.delete(ALERT_MONITOR_PAGESIZE_PREFIX);
		} catch (TimeoutException e) {
		} catch (InterruptedException e) {
		} catch (MemcachedException e) {
		}

	}

	public AlertMonitor getAlertMonitorMonitor(long alertMonitorId) {
		AlertMonitor res = null;
		try {
			res = memcachedClient.get(ALERT_MONITOR_SINGLE_PREFIX + alertMonitorId);
		} catch (TimeoutException e) {
		} catch (InterruptedException e) {
		} catch (MemcachedException e) {
		}
		if (null != res)
			return res;
		res = alertMonitorDao.getAlertMonitor(alertMonitorId);
		try {
			memcachedClient.add(ALERT_MONITOR_SINGLE_PREFIX + alertMonitorId, 0, res);
		} catch (TimeoutException e) {
		} catch (InterruptedException e) {
		} catch (MemcachedException e) {
		}
		return res;
	}

	public void updateAlertMonitor(AlertMonitor alertMonitor) {
		alertMonitorDao.updateAlertMonitor(alertMonitor);
		try {
			memcachedClient.delete(ALERT_MONITOR_PREFIX + alertMonitor.getMonitorId());
			memcachedClient.delete(ALERT_MONITOR_PREFIX);
			memcachedClient.delete(ALERT_MONITOR_PAGESIZE_PREFIX + alertMonitor.getMonitorId());
			memcachedClient.delete(ALERT_MONITOR_PAGESIZE_PREFIX);
			memcachedClient.delete(ALERT_MONITOR_SINGLE_PREFIX + alertMonitor.getAlertMonitorId());
		} catch (TimeoutException e) {
		} catch (InterruptedException e) {
		} catch (MemcachedException e) {
		}
	}

	public void deleteMonitor(long monitorId) {
		alertMonitorDao.deleteMonitor(monitorId);
		try {
			memcachedClient.delete(ALERT_MONITOR_PREFIX + monitorId);
			memcachedClient.delete(ALERT_MONITOR_PREFIX);
			memcachedClient.delete(ALERT_MONITOR_PAGESIZE_PREFIX + monitorId);
			memcachedClient.delete(ALERT_MONITOR_PAGESIZE_PREFIX);
		} catch (TimeoutException e) {
		} catch (InterruptedException e) {
		} catch (MemcachedException e) {
		}
	}

}
