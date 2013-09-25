package com.best.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.best.dao.WmsStatisticDao;
import com.best.domain.WareHouse;
import com.best.domain.WmsStatistic;
import com.best.utils.CommonUtils;

/**
 * ClassName:WmsStatisticService Description:
 * 
 * @author ChaoKai Wen email:wenchaokai@gmail.com
 * @version
 * @Date 2013-9-23
 */
@Service("wmsStatisticService")
public class WmsStatisticService extends BaseService {

	@Autowired
	private WmsStatisticDao wmsStatisticDao;

	@Value("${best.base.dir}")
	private String baseDir;

	@Value("${best.base.memcache.timeout}")
	private Integer memcachedTimeOut;

	private static final String SKU_WMS_STATISTIC_REPORT_PREFIX = "SKU_WMS_STATISTIC_REPORT_PREFIX_";

	public List<WmsStatistic> _getWmsStatistic(String skuCode, String customerCode, String wareHouseCode, String orgCode,
			String startTime, String endTime) {
		StringBuffer keyAppend = new StringBuffer(SKU_WMS_STATISTIC_REPORT_PREFIX);
		keyAppend.append("_").append(skuCode).append("_").append(customerCode).append("_").append(startTime).append("_")
				.append(endTime).append(wareHouseCode).append("_").append(orgCode);
		String key = keyAppend.toString();

		List<WmsStatistic> res = null;
		try {
			res = memcachedClient.get(key);
		} catch (Exception e) {
		}
		if (res == null) {
			res = wmsStatisticDao.getWmsStatistic(skuCode, customerCode, wareHouseCode, orgCode, startTime, endTime);
			try {
				memcachedClient.set(key, memcachedTimeOut, res);
			} catch (Exception e) {
			}
		}
		return res;
	}

	public List<WmsStatistic> getWmsStatistic(String userCount, String skuCode, String customerCode, String wareHouseCode,
			String orgCode, String startTime, String endTime) {
		StringBuffer keyAppend = new StringBuffer(SKU_WMS_STATISTIC_REPORT_PREFIX);
		keyAppend.append(userCount).append("_").append(skuCode).append("_").append(customerCode).append("_").append(startTime)
				.append("_").append(endTime).append(wareHouseCode).append("_").append(orgCode);
		String key = keyAppend.toString();

		List<WmsStatistic> res = null;
		try {
			res = memcachedClient.get(key);
		} catch (Exception e) {
		}
		if (res == null) {
			res = wmsStatisticDao.getWmsStatistic(skuCode, customerCode, wareHouseCode, orgCode, startTime, endTime);
			try {
				memcachedClient.set(key, memcachedTimeOut, res);
			} catch (Exception e) {
			}
		}
		return res;
	}

	public String getWmsSkuIdoCount(String userCount, String currentWareHouseCode, String currentWareHouseName,
			String currentSku, String monitorCustomerCode, List<WareHouse> wareHouses, String startTime, String endTime,
			String key, Model model) {

		File file = new File(baseDir, currentSku);
		if (!file.exists())
			file.mkdir();
		File showFile = new File(file, key);

		Map<String, Map<String, WmsStatistic>> res = new HashMap<String, Map<String, WmsStatistic>>();
		for (WareHouse wareHouse : wareHouses) {
			String wareHouseCode = wareHouse.getWareHouseCode();
			List<WmsStatistic> wmsStatisticList = getWmsStatistic(userCount, currentSku, monitorCustomerCode,
					wareHouse.getWmsWareHouseCode(), wareHouse.getWmsOrgCode(), startTime, endTime);
			Map<String, WmsStatistic> dateMap = new HashMap<String, WmsStatistic>();
			if (CollectionUtils.isNotEmpty(wmsStatisticList)) {
				for (WmsStatistic wmsStatistic : wmsStatisticList) {
					if (null == wmsStatistic.getDayTime())
						continue;
					dateMap.put(wmsStatistic.getDayTime(), wmsStatistic);
				}
			}
			res.put(wareHouseCode, dateMap);
		}

		try {
			// 生成页面展示文件
			String content = CommonUtils.createWmsSkuJSON(res, wareHouses, currentWareHouseCode, currentWareHouseName, startTime,
					endTime);
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

			model.addAttribute("currentFilePath", "/json/" + currentSku + "/" + showFile.getName());

			// 生成excel文件
			CommonUtils.createWmsSkuExcelFile(res, wareHouses, baseDir, key, startTime, endTime);
			model.addAttribute("currentExcelFile", "/json/monitor/dingdang/" + key + ".xls");
		} catch (Exception e) {
			e.printStackTrace();
		}

		return showFile.getName();

	}

}
