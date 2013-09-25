package com.best.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Repository;

import com.best.domain.WmsStatistic;
import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * ClassName:WmsStatisticDao Description:
 * 
 * @author ChaoKai Wen email:wenchaokai@gmail.com
 * @version
 * @Date 2013-9-23
 */
@Repository("wmsStatisticDao")
public class WmsStatisticDao extends BaseDao {

	private static final String space = "wmsStatisticSpace.";

	@Resource(name = "wmsSqlMapClient")
	protected SqlMapClient sqlMapClient;

	@SuppressWarnings("unchecked")
	public List<WmsStatistic> getWmsStatistic(String skuCode, String customerCode, String wareHouseCode, String orgCode,
			String startTime, String endTime) {
		Map<String, Object> map = new HashMap<String, Object>();
		if (!"-1".equals(skuCode))
			map.put("SKUCODE", skuCode);
		map.put("CUSTOMERCODE", customerCode);
		map.put("WHCODE", wareHouseCode);
		map.put("ORGCODE", orgCode);
		map.put("startTime", startTime);
		map.put("endTime", endTime);

		return (List<WmsStatistic>) this.list(space + "SELECT_WMS_INFO", map, sqlMapClient);
	}
}
