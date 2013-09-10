package com.best.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import net.rubyeye.xmemcached.exception.MemcachedException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.best.dao.MonitorDao;
import com.best.domain.Monitor;

/**
 * ClassName:MonitorService Description:
 * 
 * @author ChaoKai Wen email:wenchaokai@gmail.com
 * @version
 * @Date 2013-8-31
 */
@Service("monitorService")
public class MonitorService extends BaseService {

	@Autowired
	private MonitorDao monitorDao;

	private static final String MONITOR_RESULTS_KEY = "MONITOR_RESULTS_KEY";
	private static final String MONITOR_ALL_RESULTS_KEY = "MONITOR_ALL_RESULTS_KEY";
	private static final String MONITOR_TOTALSIZE_RESULTS_KEY = "MONITOR_TOTALSIZE_RESULTS_KEY";
	private static final String MONITOR_PREFIX = "MONITOR_";

	public List<Monitor> getMonitors(Integer page) {
		List<Monitor> res = null;
		// 从缓存读取数据
		try {
			res = memcachedClient.get(MONITOR_RESULTS_KEY);
		} catch (TimeoutException e1) {
		} catch (InterruptedException e1) {
		} catch (MemcachedException e1) {
		}
		int start = page * monitorDao.pageSize;
		int end = start + monitorDao.pageSize;
		if (null != res && res.size() > start) {
			return res.subList(start, res.size() > end ? end : res.size());
		}
		res = monitorDao.findMonitorsByPageSize(end);

		try {
			memcachedClient.add(MONITOR_RESULTS_KEY, 0, res);
		} catch (TimeoutException e) {
		} catch (InterruptedException e) {
		} catch (MemcachedException e) {
		}

		if (res.size() < start) {
			return new ArrayList<Monitor>();
		}
		return res.subList(start, res.size() > end ? end : res.size());

	}

	public int getMonitorsTotalSize() {
		Integer res = null;
		try {
			res = memcachedClient.get(MONITOR_TOTALSIZE_RESULTS_KEY);
		} catch (TimeoutException e) {
		} catch (InterruptedException e) {
		} catch (MemcachedException e) {
		}
		if (null != res)
			return res;
		res = monitorDao.findTotalSize();
		try {
			memcachedClient.add(MONITOR_TOTALSIZE_RESULTS_KEY, 0, res);
		} catch (TimeoutException e) {
		} catch (InterruptedException e) {
		} catch (MemcachedException e) {
		}
		return res;
	}

	public void deleteMonitor(Long monitorId) {
		monitorDao.delteMonitor(monitorId);
		// 清除缓存
		try {
			memcachedClient.delete(MONITOR_TOTALSIZE_RESULTS_KEY);
			memcachedClient.delete(MONITOR_RESULTS_KEY);
			memcachedClient.delete(MONITOR_ALL_RESULTS_KEY);
		} catch (TimeoutException e) {
		} catch (InterruptedException e) {
		} catch (MemcachedException e) {
		}
	}

	public Monitor monitorView(long monitorId) {
		Monitor res = null;
		try {
			res = memcachedClient.get(MONITOR_PREFIX + monitorId);
		} catch (TimeoutException e1) {
		} catch (InterruptedException e1) {
		} catch (MemcachedException e1) {
		}
		if (null != res)
			return res;
		res = monitorDao.getMonitor(monitorId);
		try {
			memcachedClient.add(MONITOR_PREFIX + monitorId, 0, res);
		} catch (TimeoutException e) {
		} catch (InterruptedException e) {
		} catch (MemcachedException e) {
		}
		return res;
	}

	public Monitor updateMonitor(Monitor monitor) {
		monitorDao.updateMonitor(monitor);
		try {
			memcachedClient.delete(MONITOR_PREFIX + monitor.getMonitorId());
			memcachedClient.add(MONITOR_PREFIX + monitor.getMonitorId(), 0, monitor);
			memcachedClient.delete(MONITOR_TOTALSIZE_RESULTS_KEY);
			memcachedClient.delete(MONITOR_RESULTS_KEY);
			memcachedClient.delete(MONITOR_ALL_RESULTS_KEY);
		} catch (TimeoutException e) {
		} catch (InterruptedException e) {
		} catch (MemcachedException e) {
		}
		return monitor;
	}

	public Monitor insertMonitor(Monitor monitor) {
		monitor = monitorDao.addMonitor(monitor);
		try {
			memcachedClient.add(MONITOR_PREFIX + monitor.getMonitorId(), 0, monitor);
			memcachedClient.delete(MONITOR_TOTALSIZE_RESULTS_KEY);
			memcachedClient.delete(MONITOR_RESULTS_KEY);
			memcachedClient.delete(MONITOR_ALL_RESULTS_KEY);
		} catch (TimeoutException e) {
		} catch (InterruptedException e) {
		} catch (MemcachedException e) {
		}
		return monitor;
	}

	public List<Monitor> getAllMonitor() {
		List<Monitor> res = null;
		try {
			res = memcachedClient.get(MONITOR_ALL_RESULTS_KEY);
		} catch (TimeoutException e) {
		} catch (InterruptedException e) {
		} catch (MemcachedException e) {
		}
		if (null != res)
			return res;
		res = monitorDao.getAllMonitor();
		try {
			memcachedClient.set(MONITOR_ALL_RESULTS_KEY, 0, res);
		} catch (TimeoutException e) {
		} catch (InterruptedException e) {
		} catch (MemcachedException e) {
		}
		return res;
	}
}
