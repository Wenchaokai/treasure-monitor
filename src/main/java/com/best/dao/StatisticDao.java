package com.best.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Repository;

import com.best.domain.IdoStatistic;
import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * ClassName:StatisticDao Description:
 * 
 * @author ChaoKai Wen email:wenchaokai@gmail.com
 * @version
 * @Date 2013-9-10
 */
@Repository("statisticDao")
public class StatisticDao extends BaseDao {

	private static final String space = "statisticSpace.";

	@Resource(name = "ecbossSqlMapClient")
	protected SqlMapClient sqlMapClient;

	@SuppressWarnings("unchecked")
	public List<IdoStatistic> getWareHouseSkuIdoCount(String skuCode, List<String> wareHouseCodeList, String customerCode,
			String dateTime, String endTime) {
		Map<String, Object> map = new HashMap<String, Object>();
		if (!"-1".equals(skuCode))
			map.put("skuCode", skuCode);
		if (null != wareHouseCodeList)
			map.put("wareHouseCodeList", wareHouseCodeList);
		map.put("customerCode", customerCode);
		map.put("startTime", dateTime);
		map.put("endTime", endTime);

		return (List<IdoStatistic>) this.list(space + "SELECT_SKU_WAREHOUSE_IDO", map, sqlMapClient);
	}

	@SuppressWarnings("unchecked")
	public List<IdoStatistic> getDistributedSkuIdoCount(String skuCode, String customerCode, String dateTime, String endTime) {
		Map<String, Object> map = new HashMap<String, Object>();
		if (!"-1".equals(skuCode))
			map.put("skuCode", skuCode);
		map.put("customerCode", customerCode);
		map.put("startTime", dateTime);
		map.put("endTime", endTime);

		return (List<IdoStatistic>) this.list(space + "SELECT_SKU_PROVINCE_IDO", map, sqlMapClient);
	}

	@SuppressWarnings("unchecked")
	public List<IdoStatistic> getPercentSkuIdoCount(String skuCode, String customerCode, String dateTime, String endTime) {
		Map<String, Object> map = new HashMap<String, Object>();
		if (!"-1".equals(skuCode))
			map.put("skuCode", skuCode);
		map.put("customerCode", customerCode);
		map.put("startTime", dateTime);
		map.put("endTime", endTime);

		return (List<IdoStatistic>) this.list(space + "SELECT_SKU_PERCENT_IDO", map, sqlMapClient);
	}

	/*
	 * public List<IdoStatistic> getDistributedSkuIdoCount(String skuCode,
	 * String wareHouseCode, String customerCode, String startTime, String
	 * endTime) { Map<String, Object> map = new HashMap<String, Object>();
	 * map.put("skuCode", skuCode); map.put("customerCode", customerCode);
	 * map.put("startTime", startTime); map.put("endTime", endTime);
	 * 
	 * return (List<IdoStatistic>) this.list(space +
	 * "SELECT_SKU_PROVINCE_IDO_INCLUDE_WAREHOUSECODE", map, sqlMapClient); }
	 */
}
