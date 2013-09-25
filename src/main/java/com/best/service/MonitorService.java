package com.best.service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import net.rubyeye.xmemcached.exception.MemcachedException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.best.dao.MonitorDao;
import com.best.domain.Monitor;
import com.best.domain.Treasure;
import com.best.utils.DateUtil;

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

	@Autowired
	private AlertMonitorService alertMonitorService;

	@Autowired
	private TreasureService treasureService;

	private static final String MONITOR_RESULTS_KEY = "MONITOR_RESULTS_KEY_";
	private static final String MONITOR_ALL_RESULTS_KEY = "MONITOR_ALL_RESULTS_KEY_";
	private static final String MONITOR_TOTALSIZE_RESULTS_KEY = "MONITOR_TOTALSIZE_RESULTS_KEY_";
	private static final String MONITOR_PREFIX = "MONITOR_";

	public List<Monitor> getMonitors(Integer page, String userCount) {
		List<Monitor> res = null;
		// 从缓存读取数据
		try {
			res = memcachedClient.get(MONITOR_RESULTS_KEY + userCount);
		} catch (TimeoutException e1) {
		} catch (InterruptedException e1) {
		} catch (MemcachedException e1) {
		}
		int start = page * monitorDao.pageSize;
		int end = start + monitorDao.pageSize;
		if (null != res && res.size() > start) {
			return res.subList(start, res.size() > end ? end : res.size());
		}
		res = monitorDao.findMonitorsByPageSize(end, userCount);

		try {
			memcachedClient.add(MONITOR_RESULTS_KEY + userCount, 0, res);
		} catch (TimeoutException e) {
		} catch (InterruptedException e) {
		} catch (MemcachedException e) {
		}

		if (res.size() < start) {
			return new ArrayList<Monitor>();
		}
		return res.subList(start, res.size() > end ? end : res.size());

	}

	public int getMonitorsTotalSize(String userCount) {
		Integer res = null;
		try {
			res = memcachedClient.get(MONITOR_TOTALSIZE_RESULTS_KEY + userCount);
		} catch (TimeoutException e) {
		} catch (InterruptedException e) {
		} catch (MemcachedException e) {
		}
		if (null != res)
			return res;
		res = monitorDao.findTotalSize(userCount);
		try {
			memcachedClient.add(MONITOR_TOTALSIZE_RESULTS_KEY + userCount, 0, res);
		} catch (TimeoutException e) {
		} catch (InterruptedException e) {
		} catch (MemcachedException e) {
		}
		return res;
	}

	public void deleteMonitor(Long monitorId, String userCount) {
		// 删除监控项目
		monitorDao.delteMonitor(monitorId);

		// 删除告警项目
		alertMonitorService.deleteMonitor(monitorId, userCount);

		try {
			// 删除每日统计
			deleteTreasure();
			// 清除缓存
			memcachedClient.delete(MONITOR_TOTALSIZE_RESULTS_KEY + userCount);
			memcachedClient.delete(MONITOR_RESULTS_KEY + userCount);
			memcachedClient.delete(MONITOR_ALL_RESULTS_KEY + userCount);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public Monitor monitorView(long monitorId) {
		Monitor res = null;
		try {
			res = memcachedClient.get(MONITOR_PREFIX + monitorId);
		} catch (Exception e1) {
		}
		if (null != res)
			return res;
		res = monitorDao.getMonitor(monitorId);
		try {
			memcachedClient.add(MONITOR_PREFIX + monitorId, 0, res);
		} catch (Exception e) {
		}
		return res;
	}

	public Monitor updateMonitor(Monitor monitor, String userCount) {
		monitorDao.updateMonitor(monitor);
		try {
			memcachedClient.delete(MONITOR_PREFIX + monitor.getMonitorId());
			memcachedClient.add(MONITOR_PREFIX + monitor.getMonitorId(), 0, monitor);
			memcachedClient.delete(MONITOR_TOTALSIZE_RESULTS_KEY + userCount);
			memcachedClient.delete(MONITOR_RESULTS_KEY + userCount);
			memcachedClient.delete(MONITOR_ALL_RESULTS_KEY + userCount);
		} catch (Exception e) {
		}
		return monitor;
	}

	public Monitor insertMonitor(Monitor monitor, String userCount) {
		monitor = monitorDao.addMonitor(monitor);
		try {
			addTreasure();
			memcachedClient.add(MONITOR_PREFIX + monitor.getMonitorId(), 0, monitor);
			memcachedClient.delete(MONITOR_TOTALSIZE_RESULTS_KEY + userCount);
			memcachedClient.delete(MONITOR_RESULTS_KEY + userCount);
			memcachedClient.delete(MONITOR_ALL_RESULTS_KEY + userCount);
		} catch (Exception e) {
		}
		return monitor;
	}

	public List<Monitor> getAllMonitor(String userCount) {
		List<Monitor> res = null;
		try {
			res = memcachedClient.get(MONITOR_ALL_RESULTS_KEY + userCount);
		} catch (Exception e) {
		}
		if (null != res)
			return res;
		res = monitorDao.getAllMonitor(userCount);
		try {
			memcachedClient.set(MONITOR_ALL_RESULTS_KEY + userCount, 0, res);
		} catch (Exception e) {
		}
		return res;
	}

	private void deleteTreasure() throws ParseException {
		Treasure treasure = new Treasure();
		treasure.setDateTime(DateUtil.getCurrentDateString());
		treasure.setAlarmNums(0);
		treasure.setMonitorNums(-1);
		treasureService.updateTreasure(treasure);
	}

	private void addTreasure() throws ParseException {
		Treasure treasure = new Treasure();
		treasure.setDateTime(DateUtil.getCurrentDateString());
		treasure.setAlarmNums(0);
		treasure.setMonitorNums(1);
		treasureService.updateTreasure(treasure);
	}
}
