package com.best.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Repository;

import com.best.domain.Monitor;
import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * ClassName:MonitorDao Description:
 * 
 * @author ChaoKai Wen email:wenchaokai@gmail.com
 * @version
 * @Date 2013-8-31
 */
@Repository("monitorDao")
public class MonitorDao extends BaseDao {

	@Resource(name = "sqlMapClient")
	protected SqlMapClient sqlMapClient;

	private static final String space = "monitorSpace.";

	@SuppressWarnings("unchecked")
	public List<Monitor> findMonitorsByPageSize(int end) {
		Map<String, Object> map = new HashMap<String, Object>();

		String selectString = "GET_MONITOR_BY_PAGESIZE";

		// map.put("pageSize", end);
		// return (List<Monitor>) this.list(space + selectString, map,
		// sqlMapClient);

		List<Monitor> res = new ArrayList<Monitor>();
		int page = 500;
		int start = 0;
		while (start <= end) {
			map.put("start", start);
			map.put("pageSize", page);
			List<Monitor> obj = (List<Monitor>) this.list(space + selectString, map, sqlMapClient);
			if (obj == null || obj.size() == 0) {
				break;
			}
			res.addAll(obj);
			if (obj.size() < page)
				break;
			start += page;
		}

		return res;

	}

	public Integer findTotalSize() {
		Map<String, Object> map = new HashMap<String, Object>();

		String selectString = "GET_MONITOR_BY_TOTALSIZE";

		map.put("pageSize", pageSize);
		Integer res = (Integer) this.object(space + selectString, map, sqlMapClient);
		if (res % pageSize == 0)
			return res / pageSize;
		return res / pageSize + 1;
	}

	public int delteMonitor(Long monitorId) {
		return delete(space + "DELETE_MONITOR", monitorId, sqlMapClient);
	}

	public Monitor getMonitor(long monitorId) {
		return (Monitor) object(space + "QUERY_MONITOR", monitorId, sqlMapClient);
	}

	public void updateMonitor(Monitor monitor) {
		this.update(space + "UPDATE_MONITOR", monitor, sqlMapClient);
	}

	public Monitor addMonitor(Monitor monitor) {
		Long monitorId = (Long) this.insert(space + "INSERT_USER", monitor, sqlMapClient);
		monitor = getMonitor(monitorId);
		return monitor;
	}

	@SuppressWarnings("unchecked")
	public List<Monitor> getAllMonitor() {
		Map<String, Object> map = new HashMap<String, Object>();
		// return (List<Monitor>) this.list(space + "SELECT_ALL_MONITOR", map,
		// sqlMapClient);

		List<Monitor> res = new ArrayList<Monitor>();
		int page = 500;
		int start = 0;
		while (true) {
			map.put("start", start);
			map.put("pageSize", page);
			List<Monitor> obj = (List<Monitor>) this.list(space + "SELECT_ALL_MONITOR", map, sqlMapClient);
			if (obj == null || obj.size() == 0) {
				break;
			}
			res.addAll(obj);
			if (obj.size() < page)
				break;
			start += page;
		}

		return res;

	}

}
