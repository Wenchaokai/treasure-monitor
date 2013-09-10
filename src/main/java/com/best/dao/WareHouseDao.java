package com.best.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Repository;

import com.best.domain.WareHouse;
import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * ClassName:WareHouseDao Description:
 * 
 * @author ChaoKai Wen email:wenchaokai@gmail.com
 * @version
 * @Date 2013-9-7
 */
@Repository("wareHouseDao")
public class WareHouseDao extends BaseDao {

	private static final String space = "wareHouseSpace.";

	@Resource(name = "sqlMapClient")
	protected SqlMapClient sqlMapClient;

	@SuppressWarnings("unchecked")
	public List<WareHouse> getAllWareHouse() {
		Map<String, Object> map = new HashMap<String, Object>();

		String selectString = "GET_ALL_WAREHOUSE";

		List<WareHouse> res = new ArrayList<WareHouse>();
		int pageSize = 500;
		int start = 0;
		while (true) {
			map.put("start", start);
			map.put("pageSize", pageSize);
			List<WareHouse> obj = (List<WareHouse>) this.list(space + selectString, map, sqlMapClient);
			if (obj == null || obj.size() == 0) {
				break;
			}
			res.addAll(obj);
			if (obj.size() < pageSize)
				break;
			start += pageSize;
		}
		return res;
	}

}
