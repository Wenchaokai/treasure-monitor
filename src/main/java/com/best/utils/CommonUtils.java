package com.best.utils;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import com.best.Constants;
import com.best.domain.District;
import com.best.domain.Treasure;
import com.best.domain.WareHouse;
import com.best.domain.WmsStatistic;

/**
 * ClassName:CommonUtils Description:
 * 
 * @author ChaoKai Wen email:wenchaokai@gmail.com
 * @version
 * @Date 2013-8-28
 */
public class CommonUtils {
	private static BASE64Decoder decoder = new BASE64Decoder();
	private static BASE64Encoder encoder = new BASE64Encoder();
	private static String[] distincts = new String[] { "北京", "上海", "重庆市", "天津", "河北", "山西", "河南", "辽宁", "吉林省", "黑龙江", "内蒙古自治区",
			"江苏", "山东", "安徽", "浙江", "福建", "湖北", "湖南", "广东", "广西", "江西", "四川", "海南省", "贵州", "云南", "西藏", "陕西", "甘肃", "青海", "宁夏",
			"新疆", "台湾", "香港", "澳门" };

	public static List<District> getDistricts() {
		List<District> districtList = new ArrayList<District>();
		for (int index = 0; index < distincts.length; index++) {
			District district = new District();
			district.setDistrictId(index);
			district.setDistrictName(distincts[index]);
			districtList.add(district);
		}
		return districtList;
	}

	public static String encoder(String plainText) {
		if (StringUtils.isBlank(plainText))
			return plainText;
		String encoderText = encoder.encode(encoder.encode(plainText.getBytes()).getBytes());
		return encoderText;
	}

	public static String decoder(String encoderText) throws IOException {
		if (StringUtils.isBlank(encoderText))
			return encoderText;
		byte[] decode = decoder.decodeBuffer(new String(decoder.decodeBuffer(encoderText)));
		return new String(decode);
	}

	public static Boolean checkSessionTimeOut(HttpServletRequest request) throws IOException {
		Object obj = request.getSession().getAttribute(Constants.USER_TOKEN_IDENTIFY);
		if (null == obj)
			return Boolean.FALSE;
		return Boolean.TRUE;
	}

	private static JSONObject createMonitorJSON(List<Treasure> treasures) {
		JSONObject dataSet = new JSONObject();
		dataSet.put("name", "监控项目数");
		JSONArray values = new JSONArray();
		for (Treasure treasure : treasures) {
			values.add(treasure.getMonitorNums());
		}
		dataSet.put("data", values);
		return dataSet;
	}

	private static JSONObject createAlarmJSON(List<Treasure> treasures) {
		JSONObject dataSet = new JSONObject();
		dataSet.put("name", "报警项目数");
		JSONArray values = new JSONArray();
		for (Treasure treasure : treasures) {
			values.add(treasure.getAlarmNums());
		}
		dataSet.put("data", values);
		return dataSet;
	}

	public static String createJSON(List<Treasure> treasures) {
		JSONArray dataSet = new JSONArray();
		dataSet.add(createMonitorJSON(treasures));
		dataSet.add(createAlarmJSON(treasures));

		// JSONObject valueAxis = new JSONObject();
		// valueAxis.put("name", "数量");
		// valueAxis.put("unit", "个");
		//
		JSONArray labels = new JSONArray();
		for (Treasure treasure : treasures) {
			labels.add(treasure.getDateTime());
		}

		JSONObject res = new JSONObject();
		res.put("success", true);
		res.put("type", "column");
		res.put("title", "告警项目统计");
		res.put("yAxis", "数量情况 (个)");
		res.put("categories", labels);
		res.put("series", dataSet);

		return res.toString();
	}

	public static void createIdoSkuExcelFile(String startTime, String endTime, Map<String, Map<String, Integer>> datas,
			Map<String, WareHouse> wareHouses, String baseDir, String key) throws ParseException {
		List<String> dateTimes = DateUtil.getDateTimes(startTime, endTime);
		List<String> titles = new ArrayList<String>();
		titles.add("日期");
		List<String> codeOrder = new ArrayList<String>();
		for (WareHouse wareHouse : wareHouses.values()) {
			titles.add(wareHouse.getWareHouseName() + "订单量");
			titles.add(wareHouse.getWareHouseName() + "订单量占比");
			codeOrder.add(wareHouse.getWareHouseCode());
		}
		titles.add("合计");

		try {
			// t.xls为要新建的文件名
			File parentFile = new File(baseDir, "/monitor/dingdang/");
			if (!parentFile.exists()) {
				parentFile.mkdirs();
			}
			File file = new File(parentFile, key + ".xls");
			if (file.exists())
				file.delete();
			WritableWorkbook book = Workbook.createWorkbook(file);
			// 生成名为“第一页”的工作表，参数0表示这是第一页
			WritableSheet sheet = book.createSheet("第一页", 0);

			// 写入内容
			for (int i = 0; i < titles.size(); i++)
				sheet.addCell(new Label(i, 0, titles.get(i)));

			for (int i = 0; i < dateTimes.size(); i++) {
				String dateTime = dateTimes.get(dateTimes.size() - i - 1);
				sheet.addCell(new Label(0, i + 1, dateTime));
				Map<String, Integer> numCountsMap = datas.get(dateTime);
				if (numCountsMap != null) {
					int totalCount = 0;
					for (Integer num : numCountsMap.values()) {
						if (num == null)
							num = 0;
						totalCount += num;
					}
					int index = 1;
					for (int j = 0; j < codeOrder.size(); j++) {
						Integer idoNum = numCountsMap.get(codeOrder.get(j));
						if (null == idoNum)
							idoNum = 0;
						double app = 0;
						if (idoNum != 0 && totalCount != 0) {
							app = (idoNum + 0.0) / totalCount * 100;
						}
						sheet.addCell(new Label(index++, i + 1, idoNum + ""));
						sheet.addCell(new Label(index++, i + 1, app + "%"));
					}
					sheet.addCell(new Label(index++, i + 1, totalCount + ""));
				} else {
					int totalCount = 0;
					int index = 1;
					for (int j = 0; j < codeOrder.size(); j++) {
						Integer idoNum = 0;
						double app = 0;
						sheet.addCell(new Label(index++, i + 1, idoNum + ""));
						sheet.addCell(new Label(index++, i + 1, app + "%"));
					}
					sheet.addCell(new Label(index++, i + 1, totalCount + ""));
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

	public static String createIdoSkuJSON(String startTime, String endTime, Map<String, Map<String, Integer>> datas,
			Map<String, WareHouse> wareHouses, String title, int showSize, String colors) throws ParseException {

		List<String> dateTimes = DateUtil.getDateTimes(startTime, endTime);

		JSONArray labels = new JSONArray();
		for (String dateTime : dateTimes) {
			labels.add(dateTime);
		}

		JSONObject indexAxis = new JSONObject();
		indexAxis.put("name", "时间");
		indexAxis.put("unit", "");
		indexAxis.put("labels", labels);

		JSONObject valueAxis = new JSONObject();
		valueAxis.put("name", "数量");
		valueAxis.put("unit", "个");

		JSONArray dataSet = new JSONArray();

		int index = 0;

		for (Entry<String, Map<String, Integer>> entry : datas.entrySet()) {
			String wareHouseCode = entry.getKey();
			WareHouse wareHouse = wareHouses.get(wareHouseCode);
			if (wareHouse == null)
				continue;

			JSONObject data = new JSONObject();
			data.put("name", wareHouse.getWareHouseName() + title);
			JSONArray values = new JSONArray();
			for (String dateTime : dateTimes) {
				Integer numCount = entry.getValue().get(dateTime);
				if (numCount == null)
					numCount = 0;
				values.add(numCount);
			}
			data.put("data", values);

			dataSet.add(data);

			index++;

			if (index >= showSize)
				break;
		}

		if (index == 0) {
			JSONObject data = new JSONObject();
			data.put("name", title);
			JSONArray values = new JSONArray();
			for (String dateTime : dateTimes) {
				values.add(0);
			}
			data.put("data", values);

			dataSet.add(data);
		}

		JSONObject data = new JSONObject();
		data.put("indexAxis", indexAxis);
		data.put("valueAxis", valueAxis);
		data.put("dataSets", dataSet);

		JSONObject res = new JSONObject();
		res.put("success", true);
		res.put("type", "line");
		res.put("title", "仓库订单量");
		res.put("yAxis", "数量情况 (个)");
		res.put("categories", labels);
		res.put("series", dataSet);

		return res.toString();
	}

	static class Color {
		double number;
		String color;

		public Color(double number, String color) {
			this.number = number;
			this.color = color;
		}
	}

	static List<Color> colorList = new ArrayList<CommonUtils.Color>();

	static {
		colorList.add(new Color(0.005, "#FFFFB2"));
		colorList.add(new Color(0.02, "#FECC5C"));
		colorList.add(new Color(0.05, "#FD8D3C"));
		colorList.add(new Color(0.08, "#F03B20"));
		colorList.add(new Color(1, "#BD0026"));
	}

	public static String createIdoDisSkuJSON(String startTime, String endTime, Map<String, Integer> provinceMap) {
		int totalCount = 0;
		Map<String, Integer> mapCount = new HashMap<String, Integer>();
		for (String province : distincts) {
			mapCount.put(province, 0);
		}
		for (Map.Entry<String, Integer> entry : provinceMap.entrySet()) {
			String province = entry.getKey();
			Integer numCount = entry.getValue();
			totalCount += numCount;
			for (String element : distincts) {
				if (province.startsWith(element)) {
					Integer count = mapCount.get(element);
					count += numCount;
					mapCount.put(element, count);
					break;
				}
			}
		}

		JSONArray data = new JSONArray();
		for (String element : distincts) {
			Integer count = mapCount.get(element);
			double percent = 0;
			if (totalCount != 0)
				percent = (count + 0.0) / totalCount;
			String colorPatten = "";
			for (Color color : colorList) {
				if (percent <= color.number) {
					colorPatten = color.color;
					break;
				}
			}
			data.add(element + "-" + colorPatten + "-" + count + "-" + percent * 100);
		}

		JSONObject res = new JSONObject();
		res.put("data", data);
		res.put("success", true);
		return res.toString();
	}

	public static void createIdoDisSkuExcelFile(String startTime, String endTime, Map<String, Map<String, Integer>> dateMap,
			String baseDir, String key) throws ParseException {

		List<String> dateTimes = DateUtil.getDateTimes(startTime, endTime);
		List<String> titles = new ArrayList<String>();
		titles.add("日期");
		for (String province : distincts) {
			titles.add(province);
		}
		titles.add("合计(单)");

		try {
			// t.xls为要新建的文件名
			File parentFile = new File(baseDir, "/monitor/dingdang/");
			if (!parentFile.exists()) {
				parentFile.mkdirs();
			}
			File file = new File(parentFile, key + ".xls");
			if (file.exists())
				file.delete();
			WritableWorkbook book = Workbook.createWorkbook(file);
			// 生成名为“第一页”的工作表，参数0表示这是第一页
			WritableSheet sheet = book.createSheet("第一页", 0);

			// 写入内容
			for (int i = 0; i < titles.size(); i++)
				sheet.addCell(new Label(i, 0, titles.get(i)));

			for (int i = 0; i < dateTimes.size(); i++) {
				String dateTime = dateTimes.get(dateTimes.size() - i - 1);
				sheet.addCell(new Label(0, i + 1, dateTime));
				Map<String, Integer> numCountsMap = dateMap.get(dateTime);

				int totalCount = 0;
				Map<String, Integer> mapCount = new HashMap<String, Integer>();
				for (String province : distincts) {
					mapCount.put(province, 0);
				}
				if (null != numCountsMap) {
					for (Map.Entry<String, Integer> entry : numCountsMap.entrySet()) {
						String province = entry.getKey();
						Integer numCount = entry.getValue();
						totalCount += numCount;
						for (String element : distincts) {
							if (province.startsWith(element)) {
								Integer count = mapCount.get(element);
								count += numCount;
								mapCount.put(element, count);
								break;
							}
						}
					}
				}

				int index = 1;
				for (String element : distincts) {
					Integer count = mapCount.get(element);
					sheet.addCell(new Label(index++, i + 1, count + ""));
				}
				sheet.addCell(new Label(index++, i + 1, totalCount + ""));

			}

			// 写入数据
			book.write();
			// 关闭文件
			book.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static String createIdoSkuPercentJSON(String startTime, String endTime, Map<String, Map<String, Double>> percentMap,
			Map<Double, String> skuPercentMap, String title, Integer showSize, String colors) throws ParseException {
		// percentMap key是DateTime，value是<skucode,percent>
		List<String> dateTimes = DateUtil.getDateTimes(startTime, endTime);

		JSONArray labels = new JSONArray();
		for (String dateTime : dateTimes) {
			labels.add(dateTime);
		}

		JSONArray dataSet = new JSONArray();

		int index = 0;
		Map<String, Map<String, Double>> skuCodeMap = new HashMap<String, Map<String, Double>>();
		// key是skucode，value是<DayTIME,percent>

		for (Entry<String, Map<String, Double>> entry : percentMap.entrySet()) {
			String dateTime = entry.getKey();
			Map<String, Double> value = entry.getValue();
			for (Entry<String, Double> tempEntry : value.entrySet()) {
				Map<String, Double> temp = skuCodeMap.get(tempEntry.getKey());
				if (null == temp) {
					temp = new HashMap<String, Double>();
					skuCodeMap.put(tempEntry.getKey(), temp);
				}
				temp.put(dateTime, tempEntry.getValue());
			}
		}

		for (Entry<Double, String> orderPercent : skuPercentMap.entrySet()) {
			index++;
			String skuCode = orderPercent.getValue();
			if (index >= showSize)
				break;
			Map<String, Double> datePercent = skuCodeMap.get(skuCode);

			JSONObject data = new JSONObject();
			data.put("name", skuCode + title);
			JSONArray values = new JSONArray();
			for (String dateTime : dateTimes) {
				Double numCount = datePercent.get(dateTime);
				if (numCount == null)
					numCount = 0.0;
				values.add(numCount);
			}
			data.put("data", values);

			dataSet.add(data);

		}

		if (index == 0) {
			JSONObject data = new JSONObject();
			data.put("name", title);
			JSONArray values = new JSONArray();
			for (String dateTime : dateTimes) {
				values.add(0.0);
			}
			data.put("data", values);

			dataSet.add(data);
		}

		JSONObject res = new JSONObject();

		res.put("success", true);
		res.put("type", "column");
		res.put("title", "分仓SKU与未分仓SKU关联销售情况");
		res.put("yAxis", "百分比 (%)");
		res.put("categories", labels);
		res.put("series", dataSet);

		return res.toString();
	}

	public static void createIdoSkuPerExcelFile(String startTime, String endTime, Map<String, Map<String, Double>> percentMap,
			Map<Double, String> skuPercentMap, String baseDir, String excelFileName) throws ParseException {
		List<String> dateTimes = DateUtil.getDateTimes(startTime, endTime);
		List<String> titles = new ArrayList<String>();
		titles.add("日期");
		for (String skuCode : skuPercentMap.values()) {
			titles.add(skuCode + "占比");
		}

		try {
			// t.xls为要新建的文件名
			File parentFile = new File(baseDir, "/monitor/dingdang/");
			if (!parentFile.exists()) {
				parentFile.mkdirs();
			}
			File file = new File(parentFile, excelFileName + ".xls");
			if (file.exists())
				file.delete();
			WritableWorkbook book = Workbook.createWorkbook(file);
			// 生成名为“第一页”的工作表，参数0表示这是第一页
			WritableSheet sheet = book.createSheet("第一页", 0);

			// 写入内容
			for (int i = 0; i < titles.size(); i++)
				sheet.addCell(new Label(i, 0, titles.get(i)));

			for (int i = 0; i < dateTimes.size(); i++) {
				String dateTime = dateTimes.get(dateTimes.size() - i - 1);
				sheet.addCell(new Label(0, i + 1, dateTime));

				Map<String, Double> percents = percentMap.get(dateTime);
				int index = 1;
				for (String skuCode : skuPercentMap.values()) {
					if (null != percents) {
						Double zhanbi = percents.get(skuCode);
						if (zhanbi == null)
							zhanbi = 0.0;
						sheet.addCell(new Label(index++, i + 1, zhanbi + "%"));
					} else {
						sheet.addCell(new Label(index++, i + 1, "0%"));
					}
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

	public static String createWmsSkuJSON(Map<String, Map<String, WmsStatistic>> wmsStatistic, List<WareHouse> wareHouses,
			String currentWareHouseCode, String currentWareHouseName, String startTime, String endTime) throws ParseException {

		List<String> dateTimes = DateUtil.getDateTimes(startTime, endTime);

		Map<String, WmsStatistic> statistic = wmsStatistic.get(currentWareHouseCode);
		JSONArray qtyEachList = new JSONArray();
		JSONArray qtyOnholdEachList = new JSONArray();
		JSONArray qtyUseEachList = new JSONArray();
		JSONArray qtyHold4PAEachList = new JSONArray();
		JSONArray labels = new JSONArray();
		for (String dateTime : dateTimes) {
			labels.add(dateTime);
			WmsStatistic statistic2 = statistic.get(dateTime);
			if (null != statistic2) {
				qtyEachList.add(statistic2.getQtyEach());
				qtyOnholdEachList.add(statistic2.getQtyOnholdEach());
				qtyUseEachList.add(statistic2.getQtyUseEach());
				qtyHold4PAEachList.add(statistic2.getQtyHold4PAEach());
			} else {
				qtyEachList.add(0);
				qtyOnholdEachList.add(0);
				qtyUseEachList.add(0);
				qtyHold4PAEachList.add(0);
			}
		}

		JSONArray dataSet = new JSONArray();
		JSONObject qtyEachData = new JSONObject();
		qtyEachData.put("name", currentWareHouseName + "库存量");
		qtyEachData.put("data", qtyEachList);
		dataSet.add(qtyEachData);

		JSONObject qtyOnholdEachData = new JSONObject();
		qtyOnholdEachData.put("name", currentWareHouseName + "冻结量");
		qtyOnholdEachData.put("data", qtyOnholdEachList);
		dataSet.add(qtyOnholdEachData);

		JSONObject qtyUseEachData = new JSONObject();
		qtyUseEachData.put("name", currentWareHouseName + "可用量");
		qtyUseEachData.put("data", qtyUseEachList);
		dataSet.add(qtyUseEachData);

		JSONObject qtyHold4PAEachData = new JSONObject();
		qtyHold4PAEachData.put("name", currentWareHouseName + "待上架量");
		qtyHold4PAEachData.put("data", qtyHold4PAEachList);
		dataSet.add(qtyHold4PAEachData);

		JSONObject res = new JSONObject();
		res.put("success", true);
		res.put("type", "column");
		res.put("title", "仓库库存量");
		res.put("yAxis", "数量情况 (个)");
		res.put("categories", labels);
		res.put("series", dataSet);

		return res.toString();
	}

	public static void createWmsSkuExcelFile(Map<String, Map<String, WmsStatistic>> wmsStatistic, List<WareHouse> wareHouses,
			String baseDir, String excelFileName, String startTime, String endTime) throws ParseException {
		List<String> titles = new ArrayList<String>();
		titles.add("库存量");
		titles.add("冻结量");
		titles.add("可用量");
		titles.add("待上架量");

		List<String> dateTimes = DateUtil.getDateTimes(startTime, endTime);

		try {
			// t.xls为要新建的文件名
			File parentFile = new File(baseDir, "/monitor/dingdang/");
			if (!parentFile.exists()) {
				parentFile.mkdirs();
			}
			File file = new File(parentFile, excelFileName + ".xls");
			if (file.exists())
				file.delete();
			WritableWorkbook book = Workbook.createWorkbook(file);
			// 生成名为“第一页”的工作表，参数0表示这是第一页
			WritableSheet sheet = book.createSheet("第一页", 0);

			sheet.addCell(new Label(0, 0, "日期"));
			sheet.mergeCells(0, 0, 0, 1);

			int index = 1;
			for (WareHouse wareHouse : wareHouses) {
				sheet.addCell(new Label(index, 0, wareHouse.getWareHouseName()));
				sheet.mergeCells(index, 0, index + 3, 0);
				for (int i = 0; i < titles.size(); i++)
					sheet.addCell(new Label(i + index, 1, titles.get(i)));
				index += 4;
			}

			// 写入内容

			for (int i = 0; i < dateTimes.size(); i++) {
				String dateTime = dateTimes.get(dateTimes.size() - i - 1);
				sheet.addCell(new Label(0, i + 2, dateTime));

				Map<String, WmsStatistic> statisticMap = wmsStatistic.get(dateTime);
				index = 1;
				for (WareHouse wareHouse : wareHouses) {
					String wareHouseCode = wareHouse.getWareHouseCode();
					if (null != statisticMap) {
						WmsStatistic wmsStatistic2 = statisticMap.get(wareHouseCode);
						if (null != wmsStatistic2) {
							sheet.addCell(new Label(index++, i + 2, wmsStatistic2.getQtyEach() + ""));
							sheet.addCell(new Label(index++, i + 2, wmsStatistic2.getQtyOnholdEach() + ""));
							sheet.addCell(new Label(index++, i + 2, wmsStatistic2.getQtyUseEach() + ""));
							sheet.addCell(new Label(index++, i + 2, wmsStatistic2.getQtyHold4PAEach() + ""));
						} else {
							sheet.addCell(new Label(index++, i + 2, "0"));
							sheet.addCell(new Label(index++, i + 2, "0"));
							sheet.addCell(new Label(index++, i + 2, "0"));
							sheet.addCell(new Label(index++, i + 2, "0"));
						}
					} else {
						sheet.addCell(new Label(index++, i + 2, "0"));
						sheet.addCell(new Label(index++, i + 2, "0"));
						sheet.addCell(new Label(index++, i + 2, "0"));
						sheet.addCell(new Label(index++, i + 2, "0"));
					}
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

}
