package com.best.dao;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Repository;

import com.best.domain.Treasure;
import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * ClassName:TreasureDao Description:
 * 
 * @author ChaoKai Wen email:wenchaokai@gmail.com
 * @version
 * @Date 2013-8-31
 */
@Repository("treasureDao")
public class TreasureDao extends BaseDao {

	@Resource(name = "sqlMapClient")
	protected SqlMapClient sqlMapClient;

	private static final String space = "treasureSpace.";

	public Object insertTreasure(Treasure treasure) {
		return insert(space + "INSERT_TREASURE", treasure, sqlMapClient);
	}

	public void insertTreasure(List<Treasure> treasures) {
		this.startTransaction(sqlMapClient);
		for (Treasure treasure : treasures) {
			insert(space + "INSERT_TREASURE", treasure, sqlMapClient);
		}
		this.endTransaction(sqlMapClient);
	}

	public int updateTreasure(Treasure treasure) {
		return update(space + "UPDATE_TREASURE", treasure, sqlMapClient);
	}

	@SuppressWarnings("unchecked")
	public List<Treasure> findTreasure(List<String> dateTimes) {
		List<Treasure> res = new ArrayList<Treasure>();
		if (CollectionUtils.isNotEmpty(dateTimes)) {
			return (List<Treasure>) this.list(space + "SELECT_TREASURE", dateTimes, sqlMapClient);
		}
		return res;
	}

	public Treasure getTreasure(String dateTime) {
		return (Treasure) this.object(space + "SELECT_TREASURE_SINGLE", dateTime, sqlMapClient);
	}

	public Treasure getNearestTreasure() {
		return (Treasure) this.object(space + "SELECT_NEAREST_TREASURE_SINGLE", "", sqlMapClient);
	}

}
