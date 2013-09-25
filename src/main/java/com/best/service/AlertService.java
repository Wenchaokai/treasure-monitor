package com.best.service;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import net.rubyeye.xmemcached.exception.MemcachedException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.best.dao.AlertDao;
import com.best.domain.AlertData;
import com.best.domain.Treasure;
import com.best.utils.DateUtil;

/**
 * ClassName:AlertService Description:
 * 
 * @author ChaoKai Wen email:wenchaokai@gmail.com
 * @version
 * @Date 2013-9-4
 */
@Service("alertService")
public class AlertService extends BaseService {
	@Autowired
	private AlertDao alertDao;

	@Autowired
	private TreasureService treasureService;

	private static final String ALERT_LIST_PREFIX = "ALERT_LIST_";
	private static final String ALERT_PREFIX = "ALERT_";
	private static final String ALERT_PAGESIZE_PREFIX = "ALERT_PAGESIZE_";

	@Value("${best.base.dir}")
	private String baseDir;

	public void addAlertData(AlertData alertData) throws ParseException {
		alertDao.addAlertData(alertData);
		Long monitorId = alertData.getMonitorId();

		try {
			memcachedClient.set(ALERT_PREFIX + monitorId, 0, alertData);
			memcachedClient.delete(ALERT_PAGESIZE_PREFIX + monitorId);
		} catch (Exception e) {
		}
		Treasure treasure = new Treasure();
		treasure.setAlarmNums(1);
		treasure.setMonitorNums(0);
		treasure.setDateTime(DateUtil.getCurrentDateString());
		treasureService.updateTreasure(treasure);
	}

	public List<AlertData> getAlertDataByMonitorId(Long monitorId, Integer currentPage) {
		List<AlertData> res = null;
		// 从缓存读取数据
		try {
			res = memcachedClient.get(ALERT_LIST_PREFIX + monitorId);
		} catch (TimeoutException e) {
		} catch (InterruptedException e) {
		} catch (MemcachedException e) {
		}
		int start = currentPage * alertDao.pageSize;
		int end = start + alertDao.pageSize;
		File file = new File(baseDir, "/monitor/" + monitorId + ".xls");
		if (null != res && res.size() > start && file.exists()) {
			return res.subList(start, res.size() > end ? end : res.size());
		}
		res = alertDao.findAlertsByPageSize(monitorId, end);

		createExcelFile(res, monitorId);

		try {
			memcachedClient.add(ALERT_LIST_PREFIX + monitorId, 0, res);
		} catch (TimeoutException e) {
		} catch (InterruptedException e) {
		} catch (MemcachedException e) {
		}

		if (res.size() < start) {
			return new ArrayList<AlertData>();
		}
		return res.subList(start, res.size() > end ? end : res.size());
	}

	private void createExcelFile(List<AlertData> res, Long monitorId) {
		String title[] = { "检查时间", "监控项目", "告警消息" };
		if (null == res)
			res = new ArrayList<AlertData>();
		String contents[][] = new String[res.size()][3];
		int index = 0;
		for (AlertData data : res) {
			String[] content = new String[3];
			content[0] = data.getAlertTime();
			content[1] = data.getMonitorName();
			content[2] = data.getAlertMsg();
			contents[index++] = content;
		}

		try {
			// t.xls为要新建的文件名
			File parentFile = new File(baseDir, "/monitor/");
			if (!parentFile.exists()) {
				parentFile.mkdirs();
			}
			File file = new File(parentFile, monitorId + ".xls");
			if (file.exists())
				file.delete();
			WritableWorkbook book = Workbook.createWorkbook(file);
			// 生成名为“第一页”的工作表，参数0表示这是第一页
			WritableSheet sheet = book.createSheet("第一页", 0);

			// 写入内容
			for (int i = 0; i < title.length; i++)
				sheet.addCell(new Label(i, 0, title[i]));

			for (int i = 0; i < title.length; i++) // context
			{
				for (int j = 0; j < res.size(); j++) {
					sheet.addCell(new Label(i, j + 1, contents[j][i]));
				}
			}

			// 写入数据
			book.write();
			// 关闭文件
			book.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int getAlertDataTotalPageByMonitorId(Long monitorId) {
		Integer res = null;
		try {
			res = memcachedClient.get(ALERT_PAGESIZE_PREFIX + monitorId);
		} catch (TimeoutException e) {
		} catch (InterruptedException e) {
		} catch (MemcachedException e) {
		}
		if (null != res)
			return res;
		res = alertDao.findAlertsTotalSize(monitorId);
		try {
			memcachedClient.add(ALERT_PAGESIZE_PREFIX + monitorId, 0, res);
		} catch (TimeoutException e) {
		} catch (InterruptedException e) {
		} catch (MemcachedException e) {
		}
		return res;
	}
}
