package com.best.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * ClassName:DateUtil Description:
 * 
 * @author ChaoKai Wen email:wenchaokai@gmail.com
 * @version
 * @Date 2013-8-26
 */
public class DateUtil {
	public static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	public static SimpleDateFormat currentSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public static String getCurrentDateString() {
		Date date = new Date(System.currentTimeMillis());
		String sDateTime = sdf.format(date);
		return sDateTime;
	}

	public static String getNextDate(String dateString) throws ParseException {
		Date date = sdf.parse(dateString);
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DAY_OF_YEAR, 1);
		String sDateTime = sdf.format(cal.getTime());
		return sDateTime;
	}

	public static String getPreDate(String dateString) throws ParseException {
		Date date = sdf.parse(dateString);
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DAY_OF_YEAR, -1);
		String sDateTime = sdf.format(cal.getTime());
		return sDateTime;
	}

	public static String getPreSevenDate() throws ParseException {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_YEAR, -7);
		String sDateTime = sdf.format(cal.getTime());
		return sDateTime;
	}

	public static String getPreDate() throws ParseException {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_YEAR, -1);
		String sDateTime = sdf.format(cal.getTime());
		return sDateTime;
	}

	public static List<String> getDate() {
		List<String> res = new ArrayList<String>();
		for (int index = -28; index < 0; index++) {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DAY_OF_YEAR, index);
			String sDateTime = sdf.format(cal.getTime());
			res.add(sDateTime);
		}
		return res;
	}

	public static List<String> getDateTimes(String startTime, String endTime) throws ParseException {
		List<String> res = new ArrayList<String>();
		String currentTime = startTime;
		while (!currentTime.equals(endTime)) {
			res.add(currentTime);
			currentTime = getNextDate(currentTime);
		}
		res.add(endTime);
		return res;
	}

	public static String getCurrentSdf() {
		String sDateTime = currentSdf.format(new Date());
		return sDateTime;
	}

}
