package com.best.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Repository;

import com.best.domain.AlertData;
import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * ClassName:AlertDao Description:
 * 
 * @author ChaoKai Wen email:wenchaokai@gmail.com
 * @version
 * @Date 2013-9-4
 */
@Repository("alertDao")
public class AlertDao extends BaseDao {

	@Resource(name = "sqlMapClient")
	protected SqlMapClient sqlMapClient;

	private static final String space = "alertSpace.";

	@SuppressWarnings("unchecked")
	public List<AlertData> findAlertsByPageSize(Long monitorId, int end) {
		Map<String, Object> map = new HashMap<String, Object>();

		String selectString = "GET_ALERT_BY_PAGESIZE";

		map.put("monitorId", monitorId);
		map.put("pageSize", 500);
		List<AlertData> res = new ArrayList<AlertData>();
		int start = 0;
		while (start < end) {
			map.put("start", start);
			List<AlertData> obj = (List<AlertData>) this.list(space + selectString, map, sqlMapClient);
			if (obj != null) {
				res.addAll(obj);
			}
			start += 500;
		}
		return res;

	}

	public Integer findAlertsTotalSize(Long monitorId) {
		Map<String, Object> map = new HashMap<String, Object>();

		String selectString = "GET_ALERT_BY_TOTALSIZE";

		map.put("monitorId", monitorId);
		map.put("pageSize", pageSize);
		Integer res = (Integer) this.object(space + selectString, map, sqlMapClient);
		if (res % pageSize == 0)
			return res / pageSize;
		return res / pageSize + 1;
	}

	public void addAlertData(AlertData alertData) {
		this.insert(space + "INSERT_ALERTDATA", alertData, sqlMapClient);
	}

}
