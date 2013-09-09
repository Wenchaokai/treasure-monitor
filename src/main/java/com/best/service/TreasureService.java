package com.best.service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import net.rubyeye.xmemcached.exception.MemcachedException;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.best.dao.TreasureDao;
import com.best.domain.Treasure;
import com.best.utils.CommonUtils;
import com.best.utils.DateUtil;

/**
 * ClassName:TreasureService Description:
 * 
 * @author ChaoKai Wen email:wenchaokai@gmail.com
 * @version
 * @Date 2013-8-31
 */
@Service("treasureService")
public class TreasureService extends BaseService {

	@Autowired
	private TreasureDao treasureDao;

	@Value("${best.base.dir}")
	private String baseDir;

	private static final String TREASURE_PRFIX_MEMCACHED = "TREASURE_PRFIX_MEMCACHED_";

	private Treasure addTreasure(Treasure treasure) {

		treasureDao.insertTreasure(treasure);
		treasure = getTreasure(treasure.getDateTime());
		// 要清除缓存
		try {
			memcachedClient.add(TREASURE_PRFIX_MEMCACHED + treasure.getDateTime(), 0, treasure);
		} catch (TimeoutException e) {
		} catch (InterruptedException e) {
		} catch (MemcachedException e) {
		}
		return treasure;
	}

	private Treasure getTreasure(String date) {
		Treasure treasure = null;
		try {
			treasure = memcachedClient.get(TREASURE_PRFIX_MEMCACHED + date);
		} catch (TimeoutException e) {
		} catch (InterruptedException e) {
		} catch (MemcachedException e) {
		}
		if (null == treasure)
			treasure = treasureDao.getTreasure(date);
		if (null != treasure)
			try {
				memcachedClient.set(TREASURE_PRFIX_MEMCACHED + date, 0, treasure);
			} catch (TimeoutException e) {
			} catch (InterruptedException e) {
			} catch (MemcachedException e) {
			}
		return treasure;
	}

	public synchronized int updateTreasure(Treasure treasure) throws ParseException {
		// 有可能是负数
		treasure.setDateTime(DateUtil.getCurrentDateString());
		String date = treasure.getDateTime();
		updateDateTreasure(date);
		int res = treasureDao.updateTreasure(treasure);
		try {
			Treasure temp = memcachedClient.get(TREASURE_PRFIX_MEMCACHED + treasure.getDateTime());
			temp.setMonitorNums(temp.getMonitorNums() + treasure.getMonitorNums());
			temp.setAlarmNums(temp.getAlarmNums() + treasure.getAlarmNums());
			memcachedClient.set(TREASURE_PRFIX_MEMCACHED + treasure.getDateTime(), 0, temp);
		} catch (TimeoutException e) {
		} catch (InterruptedException e) {
		} catch (MemcachedException e) {
		}
		return res;
	}

	private synchronized void updateDateTreasure(String currentDateTime) throws ParseException {
		Treasure currentTreasure = getTreasure(currentDateTime);
		if (null == currentTreasure) {
			Treasure nearestTreasure = treasureDao.getNearestTreasure();
			if (null != nearestTreasure) {
				while (true) {
					String dateTime = nearestTreasure.getDateTime();
					String nextDate = DateUtil.getNextDate(dateTime);
					nearestTreasure.setDateTime(nextDate);
					addTreasure(nearestTreasure);
					if (nextDate.equals(currentDateTime))
						break;

				}
			} else {
				currentTreasure = new Treasure();
				currentTreasure.setAlarmNums(0);
				currentTreasure.setMonitorNums(0);
				currentTreasure.setDateTime(currentDateTime);
				addTreasure(currentTreasure);
			}
		}
	}

	public List<Treasure> getTreasures(List<String> dateTimes) throws IOException, ParseException {
		if (CollectionUtils.isEmpty(dateTimes))
			throw new IOException();
		updateDateTreasure(dateTimes.get(dateTimes.size() - 1));
		Map<String, Treasure> treasureMemcached = new HashMap<String, Treasure>();
		List<String> notCached = new ArrayList<String>();
		for (String dateTime : dateTimes) {
			try {
				Treasure temp = memcachedClient.get(TREASURE_PRFIX_MEMCACHED + dateTime);
				if (temp != null)
					treasureMemcached.put(dateTime, temp);
				else {
					notCached.add(dateTime);
				}
			} catch (TimeoutException e) {
			} catch (InterruptedException e) {
			} catch (MemcachedException e) {
			}
		}

		if (CollectionUtils.isNotEmpty(notCached)) {
			List<Treasure> sqlRes = treasureDao.findTreasure(notCached);
			for (Treasure treasure : sqlRes) {
				treasureMemcached.put(treasure.getDateTime(), treasure);
				try {
					memcachedClient.add(TREASURE_PRFIX_MEMCACHED + treasure.getDateTime(), 0, treasure);
				} catch (TimeoutException e) {
				} catch (InterruptedException e) {
				} catch (MemcachedException e) {
				}

			}
		}

		List<Treasure> res = new ArrayList<Treasure>();
		for (String dateTime : dateTimes) {
			Treasure temp = treasureMemcached.get(dateTime);
			if (null != temp)
				res.add(temp);
			else {
				temp = new Treasure();
				temp.setDateTime(dateTime);
				temp.setMonitorNums(0);
				temp.setAlarmNums(0);
				res.add(temp);
			}
		}
		return res;
	}

	public void constructOverview(Model model) throws IOException, ParseException {
		String currentDate = DateUtil.getCurrentDateString();
		File baseDirFile = new File(baseDir, currentDate);
		if (!baseDirFile.exists())
			baseDirFile.mkdirs();
		File file1 = new File(baseDirFile, "1");
		File file2 = new File(baseDirFile, "2");
		File file3 = new File(baseDirFile, "3");
		File file4 = new File(baseDirFile, "4");
		File files[] = new File[] { file1, file2, file3, file4 };
		if (!file1.exists() || !file2.exists() || !file3.exists() || !file4.exists()) {
			List<String> dates = DateUtil.getDate();
			List<Treasure> res = getTreasures(dates);
			int index = res.size();
			for (int i = 1; i <= 4; i++) {
				int start = index - 7;
				if (start < 0)
					start = 0;
				String json = CommonUtils.createJSON(res.subList(start, index));
				File file = files[i - 1];
				FileWriter writer = new FileWriter(file);
				IOUtils.write(json, writer);
				IOUtils.closeQuietly(writer);
				index = start;
			}
		}

		model.addAttribute("current-1", currentDate + "/" + 1);
		model.addAttribute("current-2", currentDate + "/" + 2);
		model.addAttribute("current-3", currentDate + "/" + 3);
		model.addAttribute("current-4", currentDate + "/" + 4);
	}

}
