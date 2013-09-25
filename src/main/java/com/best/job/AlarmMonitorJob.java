package com.best.job;

import java.text.ParseException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.best.domain.AlertData;
import com.best.domain.AlertMonitor;
import com.best.domain.IdoStatistic;
import com.best.domain.Monitor;
import com.best.domain.WareHouse;
import com.best.domain.WmsStatistic;
import com.best.service.AlertMonitorService;
import com.best.service.AlertService;
import com.best.service.MonitorService;
import com.best.service.StatisticService;
import com.best.service.WareHouseService;
import com.best.service.WmsStatisticService;
import com.best.utils.DateUtil;

/**
 * ClassName:AlarmMonitorJob Description:
 * 
 * @author ChaoKai Wen email:wenchaokai@gmail.com
 * @version
 * @Date 2013-9-8
 */
public class AlarmMonitorJob {

	private static final Logger LOG = LoggerFactory.getLogger(AlarmMonitorJob.class);

	@Autowired
	private AlertMonitorService alertMonitorService;

	@Autowired
	private AlertService alertService;

	@Autowired
	private StatisticService statisticService;

	@Autowired
	private MonitorService monitorService;

	@Autowired
	private WmsStatisticService wmsStatisticService;

	@Autowired
	private WareHouseService wareHouseService;

	public void execute() throws JobExecutionException, ParseException {
		LOG.info("开始进行报警条件检测");
		int alertSize = 0;
		int start = 0;
		String currentDateTime = DateUtil.getPreDate();
		while (true) {
			List<AlertMonitor> res = alertMonitorService.getStartedAlertMonitorProject(start, 500);
			for (AlertMonitor alertMonitor : res) {
				Long monitorId = alertMonitor.getMonitorId();
				Monitor monitor = monitorService.monitorView(monitorId);
				Boolean flag = false;
				if (alertMonitor.getAlertMonitorIndex() == 1) {
					// 订单量
					if (alertMonitor.getAlertMonitorWareHouseCode().equals("-1"))
						continue;
					flag = detectionIdoStatistic(alertMonitor, currentDateTime, currentDateTime, monitor);
				} else if (alertMonitor.getAlertMonitorIndex() == 2) {
					// 库存量
					if (alertMonitor.getAlertMonitorWareHouseCode().equals("-1"))
						continue;
					flag = detectionIdoWmsStatistic(alertMonitor, currentDateTime, currentDateTime, monitor);
				} else if (alertMonitor.getAlertMonitorIndex() == 3) {
					// 区域分布占比
					if (CollectionUtils.isEmpty(alertMonitor.getDistributes()) || alertMonitor.getDistributes().size() > 1)
						continue;
					flag = detectionIdoDistributedStatistic(alertMonitor, currentDateTime, currentDateTime, monitor);
				} else {
					// sku占比
					flag = detectionIdoPercentStatistic(alertMonitor, currentDateTime, currentDateTime, monitor);
				}
				if (flag) {
					alertSize++;
					// 监控出现错误
					if (alertMonitor.getAlertMonitorCount() + 1 >= alertMonitor.getAlertMonitorDay()) {
						// 超过了阀值
						sendInfo(alertMonitor);

						// 添加报警信息
						AlertData alertData = new AlertData();
						alertData.setAlertMsg(alertMonitor.formatAlertMsg());
						alertData.setAlertTime(DateUtil.getCurrentSdf());
						alertData.setMonitorId(alertMonitor.getMonitorId());
						alertData.setMonitorName(alertMonitor.getMonitorName());
						try {
							alertService.addAlertData(alertData);
						} catch (ParseException e) {
							e.printStackTrace();
						}
						// 重新置0
						alertMonitor.setAlertMonitorCount(0);
						alertMonitorService.updateAlertMonitorCount(alertMonitor);
					} else {
						alertMonitor.setAlertMonitorCount(alertMonitor.getAlertMonitorCount() + 1);
						alertMonitorService.updateAlertMonitorCount(alertMonitor);
					}
				} else {
					// 要重新置0
					alertMonitor.setAlertMonitorCount(0);
					alertMonitorService.updateAlertMonitorCount(alertMonitor);
				}
			}

			start += 500;
			if (null == res || res.size() < 500)
				break;
		}
		LOG.info("报警条件检测结束，存在{}个没有达到预期的监控条件", alertSize);
	}

	private Boolean detectionIdoWmsStatistic(AlertMonitor alertMonitor, String startTime, String endTime, Monitor monitor) {
		String customerCode = monitor.getMonitorCustomerCode();

		String skuCode = alertMonitor.getAlertMonitorSku();
		String wareHouseCode = alertMonitor.getAlertMonitorWareHouseCode();
		Set<String> codes = new HashSet<String>();
		codes.add(wareHouseCode);
		List<WareHouse> wareHouses = wareHouseService.getWareHouseList(codes);
		String whCode = "";
		String orgCode = "";
		if (CollectionUtils.isNotEmpty(wareHouses)) {
			whCode = wareHouses.get(0).getWmsWareHouseCode();
			orgCode = wareHouses.get(0).getWmsOrgCode();
		}
		List<WmsStatistic> statistics = wmsStatisticService._getWmsStatistic(skuCode, customerCode, whCode, orgCode, startTime,
				endTime);
		if (CollectionUtils.isNotEmpty(statistics)) {
			for (WmsStatistic idoStatistic : statistics) {
				int count = idoStatistic.getQtyEach();
				if (alertMonitor.getAlertMonitorCompare() == 1) {
					if (count > alertMonitor.getAlertMonitorNum())
						return Boolean.TRUE;
				} else if (alertMonitor.getAlertMonitorCompare() == 2) {
					if (count < alertMonitor.getAlertMonitorNum())
						return Boolean.TRUE;
				} else if (alertMonitor.getAlertMonitorCompare() == 3) {
					if (count == alertMonitor.getAlertMonitorNum())
						return Boolean.TRUE;
				} else if (alertMonitor.getAlertMonitorCompare() == 4) {
					if (count >= alertMonitor.getAlertMonitorNum())
						return Boolean.TRUE;
				} else if (alertMonitor.getAlertMonitorCompare() == 5) {
					if (count <= alertMonitor.getAlertMonitorNum())
						return Boolean.TRUE;
				}
			}
		} else {
			int count = 0;
			if (alertMonitor.getAlertMonitorCompare() == 1) {
				if (count > alertMonitor.getAlertMonitorNum())
					return Boolean.TRUE;
			} else if (alertMonitor.getAlertMonitorCompare() == 2) {
				if (count < alertMonitor.getAlertMonitorNum())
					return Boolean.TRUE;
			} else if (alertMonitor.getAlertMonitorCompare() == 3) {

				if (count == alertMonitor.getAlertMonitorNum())
					return Boolean.TRUE;
			} else if (alertMonitor.getAlertMonitorCompare() == 4) {
				if (count >= alertMonitor.getAlertMonitorNum())
					return Boolean.TRUE;
			} else if (alertMonitor.getAlertMonitorCompare() == 5) {
				if (count <= alertMonitor.getAlertMonitorNum())
					return Boolean.TRUE;
			}
		}
		return Boolean.FALSE;
	}

	private Boolean detectionIdoPercentStatistic(AlertMonitor alertMonitor, String startTime, String endTime, Monitor monitor) {
		String customerCode = monitor.getMonitorCustomerCode();
		String skuCode = alertMonitor.getAlertMonitorSku();
		List<IdoStatistic> statistics = statisticService._getPercentSkuIdoCount(skuCode, customerCode, startTime, endTime);
		if (CollectionUtils.isNotEmpty(statistics)) {
			int totalCount = 0;
			for (IdoStatistic idoStatistic : statistics) {
				totalCount += idoStatistic.getNumCount();
			}
			for (IdoStatistic idoStatistic : statistics) {
				if (!idoStatistic.getSkuCode().equals(skuCode))
					continue;
				int numCount = idoStatistic.getNumCount();
				Double percent = (numCount + 0.0) / totalCount * 100;
				if (alertMonitor.getAlertMonitorCompare() == 1) {
					if (percent > alertMonitor.getAlertMonitorNum())
						return Boolean.TRUE;
				} else if (alertMonitor.getAlertMonitorCompare() == 2) {
					if (percent < alertMonitor.getAlertMonitorNum())
						return Boolean.TRUE;
				} else if (alertMonitor.getAlertMonitorCompare() == 3) {

					if (percent == alertMonitor.getAlertMonitorNum())
						return Boolean.TRUE;
				} else if (alertMonitor.getAlertMonitorCompare() == 4) {
					if (percent >= alertMonitor.getAlertMonitorNum())
						return Boolean.TRUE;
				} else if (alertMonitor.getAlertMonitorCompare() == 5) {
					if (percent <= alertMonitor.getAlertMonitorNum())
						return Boolean.TRUE;
				}
			}
		} else {
			int count = 0;
			if (alertMonitor.getAlertMonitorCompare() == 1) {
				if (count > alertMonitor.getAlertMonitorNum())
					return Boolean.TRUE;
			} else if (alertMonitor.getAlertMonitorCompare() == 2) {
				if (count < alertMonitor.getAlertMonitorNum())
					return Boolean.TRUE;
			} else if (alertMonitor.getAlertMonitorCompare() == 3) {

				if (count == alertMonitor.getAlertMonitorNum())
					return Boolean.TRUE;
			} else if (alertMonitor.getAlertMonitorCompare() == 4) {
				if (count >= alertMonitor.getAlertMonitorNum())
					return Boolean.TRUE;
			} else if (alertMonitor.getAlertMonitorCompare() == 5) {
				if (count <= alertMonitor.getAlertMonitorNum())
					return Boolean.TRUE;
			}
		}
		return Boolean.FALSE;
	}

	private Boolean detectionIdoDistributedStatistic(AlertMonitor alertMonitor, String startTime, String endTime, Monitor monitor) {
		String customerCode = monitor.getMonitorCustomerCode();
		// String wareHouseCode = alertMonitor.getAlertMonitorWareHouseCode();
		String skuCode = alertMonitor.getAlertMonitorSku();
		List<String> provinces = alertMonitor.getDistributes();
		List<IdoStatistic> statistics = statisticService._getDistributedSkuIdoCount(skuCode, customerCode, startTime, endTime);
		if (CollectionUtils.isNotEmpty(statistics)) {
			int totalCount = 0;
			for (IdoStatistic idoStatistic : statistics) {
				totalCount += idoStatistic.getNumCount();
			}
			for (IdoStatistic idoStatistic : statistics) {
				boolean flag = false;
				for (String province : provinces) {
					if (idoStatistic.getProvince().startsWith(province)) {
						flag = true;
						break;
					}
				}
				if (!flag)
					continue;
				int numCount = idoStatistic.getNumCount();
				Double percent = (numCount + 0.0) / totalCount * 100;
				if (alertMonitor.getAlertMonitorCompare() == 1) {
					if (percent > alertMonitor.getAlertMonitorNum())
						return Boolean.TRUE;
				} else if (alertMonitor.getAlertMonitorCompare() == 2) {
					if (percent < alertMonitor.getAlertMonitorNum())
						return Boolean.TRUE;
				} else if (alertMonitor.getAlertMonitorCompare() == 3) {

					if (percent == alertMonitor.getAlertMonitorNum())
						return Boolean.TRUE;
				} else if (alertMonitor.getAlertMonitorCompare() == 4) {
					if (percent >= alertMonitor.getAlertMonitorNum())
						return Boolean.TRUE;
				} else if (alertMonitor.getAlertMonitorCompare() == 5) {
					if (percent <= alertMonitor.getAlertMonitorNum())
						return Boolean.TRUE;
				}
			}
		} else {
			int count = 0;
			if (alertMonitor.getAlertMonitorCompare() == 1) {
				if (count > alertMonitor.getAlertMonitorNum())
					return Boolean.TRUE;
			} else if (alertMonitor.getAlertMonitorCompare() == 2) {
				if (count < alertMonitor.getAlertMonitorNum())
					return Boolean.TRUE;
			} else if (alertMonitor.getAlertMonitorCompare() == 3) {

				if (count == alertMonitor.getAlertMonitorNum())
					return Boolean.TRUE;
			} else if (alertMonitor.getAlertMonitorCompare() == 4) {
				if (count >= alertMonitor.getAlertMonitorNum())
					return Boolean.TRUE;
			} else if (alertMonitor.getAlertMonitorCompare() == 5) {
				if (count <= alertMonitor.getAlertMonitorNum())
					return Boolean.TRUE;
			}
		}
		return Boolean.FALSE;
	}

	private Boolean detectionIdoStatistic(AlertMonitor alertMonitor, String startTime, String endTime, Monitor monitor) {

		String customerCode = monitor.getMonitorCustomerCode();
		String wareHouseCode = alertMonitor.getAlertMonitorWareHouseCode();
		String skuCode = alertMonitor.getAlertMonitorSku();
		List<IdoStatistic> statistics = statisticService._getWareHouseSkuIdoCount(skuCode, wareHouseCode, customerCode,
				startTime, endTime);
		if (CollectionUtils.isNotEmpty(statistics)) {
			for (IdoStatistic idoStatistic : statistics) {
				int count = idoStatistic.getNumCount();
				if (alertMonitor.getAlertMonitorCompare() == 1) {
					if (count > alertMonitor.getAlertMonitorNum())
						return Boolean.TRUE;
				} else if (alertMonitor.getAlertMonitorCompare() == 2) {
					if (count < alertMonitor.getAlertMonitorNum())
						return Boolean.TRUE;
				} else if (alertMonitor.getAlertMonitorCompare() == 3) {
					if (count == alertMonitor.getAlertMonitorNum())
						return Boolean.TRUE;
				} else if (alertMonitor.getAlertMonitorCompare() == 4) {
					if (count >= alertMonitor.getAlertMonitorNum())
						return Boolean.TRUE;
				} else if (alertMonitor.getAlertMonitorCompare() == 5) {
					if (count <= alertMonitor.getAlertMonitorNum())
						return Boolean.TRUE;
				}
			}
		} else {
			int count = 0;
			if (alertMonitor.getAlertMonitorCompare() == 1) {
				if (count > alertMonitor.getAlertMonitorNum())
					return Boolean.TRUE;
			} else if (alertMonitor.getAlertMonitorCompare() == 2) {
				if (count < alertMonitor.getAlertMonitorNum())
					return Boolean.TRUE;
			} else if (alertMonitor.getAlertMonitorCompare() == 3) {

				if (count == alertMonitor.getAlertMonitorNum())
					return Boolean.TRUE;
			} else if (alertMonitor.getAlertMonitorCompare() == 4) {
				if (count >= alertMonitor.getAlertMonitorNum())
					return Boolean.TRUE;
			} else if (alertMonitor.getAlertMonitorCompare() == 5) {
				if (count <= alertMonitor.getAlertMonitorNum())
					return Boolean.TRUE;
			}
		}
		return Boolean.FALSE;
	}

	private void sendInfo(AlertMonitor alertMonitor) {
		LOG.warn("触发了报警条件，monitorName=[{}] 开始发送信息", alertMonitor.getMonitorName());
		// List<String> receivers = new ArrayList<String>();
		// if (alertMonitor.getAlertMonitorEnableEmail() == 1) {
		// String emails = alertMonitor.getAlertMonitorEmail();
		// if (StringUtils.isNotBlank(emails)) {
		// String[] parts = emails.split(",");
		// for (String string : parts) {
		// if (StringUtils.isNotBlank(string))
		// receivers.add(string);
		// }
		// }
		// }
		//
		// if (alertMonitor.getAlertMonitorEnableSms() == 1) {
		// String smss = alertMonitor.getAlertMonitorSms();
		// if (StringUtils.isNotBlank(smss)) {
		// String[] parts = smss.split(",");
		// for (String string : parts) {
		// if (StringUtils.isNotBlank(string))
		// receivers.add(string);
		// }
		// }
		// }
		// if (receivers.size() > 0) {
		// // 发送消息
		// SmsInfo smsInfo = new
		// SmsInfoBuilder().setReceivers(receivers.toArray(new
		// String[0])).setPriority(1).setSign("【监控宝】")
		// .setSubject("报警消息").setContent(alertMonitor.formatAlertMsg()).setKey(UUID.randomUUID().toString()).build();
		//
		// // 发短信设置，他们来设置
		// }
	}

}
