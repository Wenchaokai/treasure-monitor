package com.best.service;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.best.dao.WareHouseDao;
import com.best.domain.WareHouse;

/**
 * ClassName:CustomerService Description:
 * 
 * @author ChaoKai Wen email:wenchaokai@gmail.com
 * @version
 * @Date 2013-9-6
 */
@Service("wareHouseService")
public class WareHouseService extends BaseService {
	@Autowired
	private WareHouseDao wareHouseDao;

	@Value("${best.base.memcache.timeout}")
	private Integer memcachedTimeOut;

	public static final String ALL_WAREHOUSE_KEY = "ALL_WAREHOUSE_KEY";

	public List<WareHouse> getAllWareHouse() {
		List<WareHouse> res = null;
		try {
			res = memcachedClient.get(ALL_WAREHOUSE_KEY);
		} catch (Exception e) {
		}
		if (null != res)
			return res;
		res = wareHouseDao.getAllWareHouse();
		try {
			memcachedClient.set(ALL_WAREHOUSE_KEY, memcachedTimeOut, res);
		} catch (Exception e) {
		}
		return res;
	}

	public List<WareHouse> getWareHouseList(Set<String> keySet) {
		List<WareHouse> res = null;
		res = wareHouseDao.getWareHouseList(keySet);
		return res;
	}
}
