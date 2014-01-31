package com.best.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.best.dao.StatisticDao;
import com.best.domain.IdoStatistic;
import com.best.domain.WareHouse;
import com.best.utils.CommonUtils;

/**
 * ClassName:StatisticService Description:
 * 
 * @author ChaoKai Wen email:wenchaokai@gmail.com
 * @version
 * @Date 2013-9-11
 */
@Service("statisticService")
public class StatisticService extends BaseService {

	@Autowired
	private StatisticDao statisticDao;

	@Value("${best.base.dir}")
	private String baseDir;

	@Value("${best.base.pic.show.size}")
	private Integer showSize;

	@Value("${best.base.colr.patten}")
	private String color;

	@Value("${best.base.memcache.timeout}")
	private Integer memcachedTimeOut;

	private static final String SKU_IDO_STATISTIC_REPORT_PREFIX = "SKU_IDO_STATISTIC_";

	private static final String SKU_DIS_STATISTIC_REPORT_PREFIX = "SKU_DIS_STATISTIC_REPORT_PREFIX_";

	private static final String SKU_PER_STATISTIC_REPORT_PREFIX = "SKU_PER_STATISTIC_REPORT_PREFIX_";

	public List<IdoStatistic> _getWareHouseSkuIdoCount(String skuCode, String wareHouseCode, String customerCode,
			String startTime, String endTime) {
		// skuCode==-1 代表所有SKU,wareHouseCode代表所有仓库
		StringBuffer keyAppend = new StringBuffer(SKU_IDO_STATISTIC_REPORT_PREFIX);
		keyAppend.append(skuCode).append("_").append(customerCode).append("_").append(startTime).append("_").append(endTime);
		keyAppend.append("_").append(wareHouseCode);
		String key = keyAppend.toString();

		List<IdoStatistic> res = null;
		try {
			res = memcachedClient.get(key);
		} catch (Exception e) {
		}
		if (res == null) {
			List<String> wareHouseCodes = null;
			if (!"-1".equals(wareHouseCode)) {
				wareHouseCodes = new ArrayList<String>();
				wareHouseCodes.add(wareHouseCode);
			}
			res = statisticDao.getWareHouseSkuIdoCount(skuCode, wareHouseCodes, customerCode, startTime, endTime);

			try {
				memcachedClient.set(key, memcachedTimeOut, res);
			} catch (Exception e) {
			}
		}

		return res;
	}

	public List<IdoStatistic> _getDistributedSkuIdoCount(String skuCode, String customerCode, String startTime, String endTime) {
		StringBuffer keyAppend = new StringBuffer(SKU_DIS_STATISTIC_REPORT_PREFIX);
		keyAppend.append(skuCode).append("_").append(customerCode).append("_").append(startTime).append("_").append(endTime);

		String key = keyAppend.toString();

		List<IdoStatistic> res = null;
		try {
			res = memcachedClient.get(key);
		} catch (Exception e) {
		}

		if (res == null) {
			res = statisticDao.getDistributedSkuIdoCount(skuCode, customerCode, startTime, endTime);

			try {
				memcachedClient.set(key, memcachedTimeOut, res);
			} catch (Exception e) {
			}
		}
		return res;
	}

	public List<IdoStatistic> _getPercentSkuIdoCount(String skuCode, String customerCode, String startTime, String endTime) {
		StringBuffer keyAppend = new StringBuffer(SKU_PER_STATISTIC_REPORT_PREFIX);
		keyAppend.append(skuCode).append("_").append(customerCode).append("_").append(startTime).append("_").append(endTime);

		String key = keyAppend.toString();

		List<IdoStatistic> res = null;
		try {
			res = memcachedClient.get(key);
		} catch (Exception e) {
		}

		if (res == null) {
			res = statisticDao.getPercentSkuIdoCount(skuCode, customerCode, startTime, endTime);

			try {
				memcachedClient.set(key, memcachedTimeOut, res);
			} catch (Exception e) {
			}
		}
		return res;
	}

	public String getWareHouseSkuIdoCount(String skuCode, Map<String, WareHouse> wareHouse, String customerCode,
			String startTime, String endTime, String key, Model model) {

		File file = new File(baseDir, skuCode);
		if (!file.exists())
			file.mkdir();
		File showFile = new File(file, key);
		List<IdoStatistic> res = null;
		try {
			res = memcachedClient.get(key);
		} catch (Exception e) {
		}
		if (res == null) {
			res = statisticDao.getWareHouseSkuIdoCount(skuCode, new ArrayList<String>(wareHouse.keySet()), customerCode,
					startTime, endTime);

			if (res.size() > 0) {
				try {
					memcachedClient.set(key, memcachedTimeOut, res);
				} catch (Exception e) {
				}
			}
		}

		// WHCODE作为key
		Map<String, Map<String, Integer>> dateSet = new HashMap<String, Map<String, Integer>>();
		// Date 作为Key
		Map<String, Map<String, Integer>> wareHouseMap = new HashMap<String, Map<String, Integer>>();
		for (IdoStatistic idoStatistic : res) {
			// WareHouseCode 作为Key
			Map<String, Integer> dateData = dateSet.get(idoStatistic.getWareHouseCode());
			if (null == dateData) {
				dateData = new HashMap<String, Integer>();
				dateSet.put(idoStatistic.getWareHouseCode(), dateData);
			}
			dateData.put(idoStatistic.getDateTime(), idoStatistic.getNumCount());

			// DateTime作为Key
			dateData = wareHouseMap.get(idoStatistic.getDateTime());
			if (null == dateData) {
				dateData = new HashMap<String, Integer>();
				wareHouseMap.put(idoStatistic.getDateTime(), dateData);
			}
			dateData.put(idoStatistic.getWareHouseCode(), idoStatistic.getNumCount());
		}
		try {
			// 生成页面展示文件
			String content = CommonUtils.createIdoSkuJSON(startTime, endTime, dateSet, wareHouse, "订单量", showSize, color);
			if (null != content) {
				FileOutputStream outputStream = null;
				try {
					outputStream = new FileOutputStream(showFile);
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				}
				try {
					IOUtils.write(content, outputStream);
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					IOUtils.closeQuietly(outputStream);
				}
			}

			model.addAttribute("currentFilePath", "/json/" + skuCode + "/" + showFile.getName());

			// 生成excel文件
			CommonUtils.createIdoSkuExcelFile(startTime, endTime, wareHouseMap, wareHouse, baseDir, key);
			model.addAttribute("currentExcelFile", "/json/monitor/dingdang/" + key + ".xls");
		} catch (Exception e) {
			e.printStackTrace();
		}

		return showFile.getName();
	}

	public String getDistributedSkuIdoCount(String skuCode, String customerCode, String startTime, String endTime, String key,
			Model model) {

		File file = new File(baseDir, skuCode);
		if (!file.exists())
			file.mkdir();
		File showFile = new File(file, key + ".html");
		List<IdoStatistic> res = null;
		try {
			res = memcachedClient.get(key);
		} catch (Exception e) {
		}

		if (res == null) {
			res = statisticDao.getDistributedSkuIdoCount(skuCode, customerCode, startTime, endTime);

			try {
				memcachedClient.set(key, memcachedTimeOut, res);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		Map<String, Integer> provinceMap = new HashMap<String, Integer>();
		Map<String, Map<String, Integer>> dateMap = new HashMap<String, Map<String, Integer>>();
		Integer totalCount = 0;
		for (IdoStatistic idoStatistic : res) {
			// PROVINCE 作为Key
			if (idoStatistic.getProvince() == null)
				continue;
			Integer numCount = provinceMap.get(idoStatistic.getProvince());
			if (numCount == null) {
				numCount = 0;
			}
			numCount += idoStatistic.getNumCount();
			provinceMap.put(idoStatistic.getProvince(), numCount);
			totalCount += idoStatistic.getNumCount();

			// DateTime作为Key
			Map<String, Integer> dateData = dateMap.get(idoStatistic.getDateTime());
			if (null == dateData) {
				dateData = new HashMap<String, Integer>();
				dateMap.put(idoStatistic.getDateTime(), dateData);
			}
			dateData.put(idoStatistic.getProvince(), idoStatistic.getNumCount());
		}

		try {
			// 生成页面展示文件
			String content = CommonUtils.createIdoDisSkuJSON(startTime, endTime, provinceMap);
			if (null != content) {
				FileOutputStream outputStream = null;
				try {
					outputStream = new FileOutputStream(showFile);
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				}
				try {
					IOUtils.write(content, outputStream);
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					IOUtils.closeQuietly(outputStream);
				}
			}

			model.addAttribute("mapFile", showFile.getName());
			model.addAttribute("totalCount", totalCount == null ? 0 : totalCount);

			// 生成excel文件
			CommonUtils.createIdoDisSkuExcelFile(startTime, endTime, dateMap, baseDir, key);
			model.addAttribute("currentExcelFile", "/json/monitor/dingdang/" + key + ".xls");
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public String getPercentSkuIdoCount(String skuCode, String customerCode, String startTime, String endTime, String key,
			Model model) {

		File file = new File(baseDir, skuCode);
		if (!file.exists())
			file.mkdir();
		File showFile = new File(file, key);
		List<IdoStatistic> res = null;
		try {
			res = memcachedClient.get(key);
		} catch (Exception e) {
		}

		if (res == null) {
			res = statisticDao.getPercentSkuIdoCount(skuCode, customerCode, startTime, endTime);

			try {
				memcachedClient.set(key, memcachedTimeOut, res);
			} catch (Exception e) {
			}
		}
		if (res == null)
			res = new ArrayList<IdoStatistic>();

		// 记录所有订单的ID号
		Set<Long> allOrders = new HashSet<Long>();

		// 保存每天订单总数
		Map<String, Set<Long>> orderCountMap = new HashMap<String, Set<Long>>();

		// 记录每天Sku的对应的订单数
		Map<String, Map<String, Set<Long>>> skuOrderAmounts = new HashMap<String, Map<String, Set<Long>>>();
		Map<String, Set<Long>> skuAmouts = new HashMap<String, Set<Long>>();
		for (IdoStatistic idoStatistic : res) {
			String dayTime = idoStatistic.getDateTime();
			if (idoStatistic.getId() == null)
				continue;

			allOrders.add(idoStatistic.getId());

			if (dayTime == null)
				continue;
			Set<Long> orders = orderCountMap.get(dayTime);
			if (orders == null)
				orders = new HashSet<Long>();

			orders.add(idoStatistic.getId());
			orderCountMap.put(dayTime, orders);

			if (idoStatistic.getSkuCode() == null)
				continue;

			Map<String, Set<Long>> skuOrderMap = skuOrderAmounts.get(dayTime);
			if (skuOrderMap == null) {
				skuOrderMap = new HashMap<String, Set<Long>>();
				skuOrderAmounts.put(dayTime, skuOrderMap);
			}

			Set<Long> skuOrders = skuOrderMap.get(idoStatistic.getSkuCode());
			if (skuOrders == null) {
				skuOrders = new HashSet<Long>();
				skuOrderMap.put(idoStatistic.getSkuCode(), skuOrders);
			}

			skuOrders.add(idoStatistic.getId());

			Set<Long> skus = skuAmouts.get(idoStatistic.getSkuCode());
			if (skus == null) {
				skus = new HashSet<Long>();
				skuAmouts.put(idoStatistic.getSkuCode(), skus);
			}
			skus.add(idoStatistic.getId());
		}

		// 根据全部占比来计算SKUCODE排序
		Map<Double, String> skuPercentMap = new TreeMap<Double, String>(Collections.reverseOrder());
		for (Entry<String, Set<Long>> entry : skuAmouts.entrySet()) {
			if (entry.getKey() == null)
				continue;

			int size = entry.getValue() == null ? 0 : entry.getValue().size();
			int orderSize = allOrders.size();
			double occupy = 0.0;
			if (orderSize != 0) {
				occupy = (size + 0.0) / orderSize * 100;
			}
			if (!entry.getKey().equals(skuCode))
				skuPercentMap.put(occupy, entry.getKey());
		}

		// 每天占比情况
		Map<String, Map<String, Double>> percentMap = new HashMap<String, Map<String, Double>>();
		for (Entry<String, Map<String, Set<Long>>> entry : skuOrderAmounts.entrySet()) {
			if (entry.getKey() == null || entry.getValue() == null)
				continue;
			Map<String, Double> resMap = percentMap.get(entry.getKey());
			if (resMap == null) {
				resMap = new HashMap<String, Double>();
				percentMap.put(entry.getKey(), resMap);
			}
			for (Entry<String, Set<Long>> temp : entry.getValue().entrySet()) {
				if (temp.getKey() == null)
					continue;
				int size = temp.getValue() == null ? 0 : temp.getValue().size();
				int allSize = orderCountMap.get(entry.getKey()) == null ? 0 : orderCountMap.get(entry.getKey()).size();
				double occupy = 0.0;
				if (allSize != 0) {
					occupy = (size + 0.0) / allSize * 100;
				}
				if (!temp.getKey().equals(skuCode))
					resMap.put(temp.getKey(), occupy);
			}
		}

		try {
			// 生成页面展示文件
			String content = CommonUtils.createIdoSkuPercentJSON(startTime, endTime, percentMap, skuPercentMap, "占比", showSize,
					color);
			if (null != content) {
				FileOutputStream outputStream = null;
				try {
					outputStream = new FileOutputStream(showFile);
				} catch (FileNotFoundException e1) {
				}
				try {
					IOUtils.write(content, outputStream);
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					IOUtils.closeQuietly(outputStream);
				}
			}
			model.addAttribute("currentFilePath", "/json/" + skuCode + "/" + showFile.getName());
			model.addAttribute("totalCount", allOrders.size());

			// 生成excel文件
			CommonUtils.createIdoSkuPerExcelFile(startTime, endTime, percentMap, skuPercentMap, baseDir, key);

			model.addAttribute("currentExcelFile", "/json/monitor/dingdang/" + key + ".xls");
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}
