package com.best.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import com.best.Constants;
import com.best.domain.District;
import com.best.domain.Treasure;

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
	private static String[] distincts = new String[] { "北京", "上海", "重庆", "天津", "河北", "山西", "河南", "辽宁", "吉林", "黑龙江", "内蒙古", "江苏",
			"山东", "安徽", "浙江", "福建", "湖北", "湖南", "广东", "广西", "江西", "四川", "海南", "贵州", "云南", "西藏", "陕西", "甘肃", "青海", "宁夏", "新疆",
			"台湾", "香港", "澳门" };

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
		JSONObject style = new JSONObject();
		style.put("color", "#0080FF");
		JSONObject dataSet = new JSONObject();
		dataSet.put("name", "监控项目数");
		JSONArray values = new JSONArray();
		for (Treasure treasure : treasures) {
			values.add(treasure.getMonitorNums());
		}
		dataSet.put("values", values);
		dataSet.put("style", style);
		return dataSet;
	}

	private static JSONObject createAlarmJSON(List<Treasure> treasures) {
		JSONObject style = new JSONObject();
		style.put("color", "#FF7300");
		JSONObject dataSet = new JSONObject();
		dataSet.put("name", "报警项目数");
		JSONArray values = new JSONArray();
		for (Treasure treasure : treasures) {
			values.add(treasure.getAlarmNums());
		}
		dataSet.put("values", values);
		dataSet.put("style", style);
		return dataSet;
	}

	public static String createJSON(List<Treasure> treasures) {
		JSONArray dataSet = new JSONArray();
		dataSet.add(createMonitorJSON(treasures));
		dataSet.add(createAlarmJSON(treasures));

		JSONObject valueAxis = new JSONObject();
		valueAxis.put("name", "数量");
		valueAxis.put("unit", "个");

		JSONArray labels = new JSONArray();
		for (Treasure treasure : treasures) {
			labels.add(treasure.getDateTime());
		}

		JSONObject indexAxis = new JSONObject();
		indexAxis.put("name", "时间");
		indexAxis.put("unit", "");
		indexAxis.put("labels", labels);

		JSONObject data = new JSONObject();
		data.put("indexAxis", indexAxis);
		data.put("valueAxis", valueAxis);
		data.put("dataSets", dataSet);

		JSONObject res = new JSONObject();
		res.put("data", data);

		return res.toString();
	}

}
