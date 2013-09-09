package com.best.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.rubyeye.xmemcached.exception.MemcachedException;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.best.domain.AlertMonitor;
import com.best.domain.District;
import com.best.domain.Monitor;
import com.best.domain.WareHouse;
import com.best.service.AlertMonitorService;
import com.best.service.MonitorService;
import com.best.utils.CommonUtils;

/**
 * ClassName:AlertMonitorController Description:
 * 
 * @author ChaoKai Wen email:wenchaokai@gmail.com
 * @version
 * @Date 2013-9-7
 */
@Controller
public class AlertMonitorController {

	@Autowired
	private AlertMonitorService alertMonitorService;

	@Autowired
	private MonitorService monitorService;

	@RequestMapping(value = "/warn/alertmonitor-manage.do")
	public String alertMonitorManager(HttpServletRequest req, HttpServletResponse resp, Model model) throws IOException {

		if (!CommonUtils.checkSessionTimeOut(req))
			return "redirect:/login.do";

		String monitorId = req.getParameter("monitorId");
		if ("-1".equals(monitorId))
			monitorId = "";
		if (StringUtils.isNotBlank(monitorId))
			model.addAttribute("monitorId", monitorId);
		else
			model.addAttribute("monitorId", "");

		String currentPage = req.getParameter("currentPage");
		int totalPage = 0;
		List<AlertMonitor> res = null;
		totalPage = alertMonitorService.getAlertMonitorsTotalSize(monitorId);

		if (StringUtils.isBlank(currentPage))
			currentPage = "1";
		Integer page = Integer.parseInt(currentPage);
		if (!(totalPage == 0 || page > totalPage)) {
			res = alertMonitorService.getAlertMonitorMonitors(monitorId, page - 1);
		}

		if (null == res)
			res = new ArrayList<AlertMonitor>();

		List<Monitor> monitors = monitorService.getAllMonitor();

		model.addAttribute("res", res);

		model.addAttribute("currentPage", page);
		int start = page - 2;
		if (start <= 1)
			start = 1;
		int end = start + 4;
		if (end > totalPage) {
			int dis = end - totalPage;
			end = totalPage;
			start -= dis;
			if (start <= 1)
				start = 1;
		}

		model.addAttribute("monitors", monitors);
		model.addAttribute("start", start);
		model.addAttribute("end", end);
		model.addAttribute("prePage", ((page - 1) < 1 ? 1 : (page - 1)) + "");
		model.addAttribute("nextPage", ((page + 1) > totalPage ? totalPage : (page + 1)) + "");
		return "/warn/warn-manage";
	}

	@RequestMapping(value = "/warn/alertmonitor-delete.do")
	public String alertMonitorDelete(HttpServletRequest req, HttpServletResponse resp, Model model) throws IOException,
			TimeoutException, InterruptedException, MemcachedException {
		if (!CommonUtils.checkSessionTimeOut(req))
			return "redirect:/login.do";

		String alertMonitorId = req.getParameter("alertMonitorId");
		String monitorId = req.getParameter("monitorId");
		if (StringUtils.isBlank(monitorId))
			monitorId = "";

		alertMonitorService.deleteAlertMonitor(Long.parseLong(alertMonitorId.trim()), monitorId);

		return "redirect:/warn/alertmonitor-manage.do?monitorId=" + monitorId;
	}

	@RequestMapping(value = "/warn/alertmonitor-add-view.do")
	public String alertMonitorAddView(HttpServletRequest req, HttpServletResponse resp, Model model) throws IOException {
		if (!CommonUtils.checkSessionTimeOut(req))
			return "redirect:/login.do";

		List<Monitor> monitors = monitorService.getAllMonitor();

		List<District> districts = CommonUtils.getDistricts();

		List<List<District>> list = new ArrayList<List<District>>();
		int size = districts.size();
		for (int index = 0; index < size; index++) {
			int start = index * 5;
			if (start > size)
				break;
			int end = start + 5;
			if (end > size)
				end = size;
			list.add(districts.subList(start, end));
		}

		model.addAttribute("districts", list);

		model.addAttribute("monitors", monitors);

		return "/warn/warn-add";
	}

	@RequestMapping(value = "/warn/alertmonitor-add.do")
	public String alertMonitorAdd(HttpServletRequest req, HttpServletResponse resp, Model model) throws IOException {
		if (!CommonUtils.checkSessionTimeOut(req))
			return "redirect:/login.do";

		String monitor = req.getParameter("alertMonitor");
		String[] parts = monitor.split("#");
		String monitorId = parts[0];
		String monitorName = parts[1];
		String alertMonitorIndex = req.getParameter("alertMonitorIndex");
		String alertMonitorWareHouse = req.getParameter("alertMonitorWareHouse");
		parts = alertMonitorWareHouse.split("#");
		String wareHouseId = parts[0];
		String wareHouseName = parts[1];
		String alertMonitorSku = req.getParameter("alertMonitorSku");
		String alertMonitorDay = req.getParameter("alertMonitorDay");
		String alertMonitorCompare = req.getParameter("alertMonitorCompare");
		String alertMonitorNum = req.getParameter("alertMonitorNum");
		String alertMonitorUnit = req.getParameter("alertMonitorUnit");
		String alertMonitorMsg = req.getParameter("alertMonitorMsg");
		String alertMonitorSmsCheckbox = req.getParameter("alertMonitorSmsCheckbox");
		String alertMonitorEmailCheckbox = req.getParameter("alertMonitorEmailCheckbox");

		String[] alertMonitorSmses = req.getParameterValues("alertMonitorSms");
		StringBuffer smsString = new StringBuffer();
		if (null != alertMonitorSmses) {
			for (String sms : alertMonitorSmses) {
				if (StringUtils.isNotBlank(sms)) {
					smsString.append(sms).append(",");
				}
			}
		}
		String[] alertMonitorEmails = req.getParameterValues("alertMonitorEmail");
		StringBuffer emailString = new StringBuffer();
		if (null != alertMonitorEmails) {
			for (String email : alertMonitorEmails) {
				if (StringUtils.isNotBlank(email)) {
					emailString.append(email).append(",");
				}
			}
		}
		String alertMonitorStatus = req.getParameter("alertMonitorStatus");

		String monitorDistrict[] = null;
		if ("3".equals(alertMonitorIndex)) {
			monitorDistrict = req.getParameterValues("districtName");
		}

		StringBuffer districtsString = new StringBuffer();
		if (null != monitorDistrict) {
			for (String district : monitorDistrict) {
				if (StringUtils.isNotBlank(district)) {
					districtsString.append(district).append(",");
				}
			}
		}

		AlertMonitor alertMonitor = new AlertMonitor();
		alertMonitor.setMonitorId(Long.parseLong(monitorId));
		alertMonitor.setMonitorName(monitorName);
		alertMonitor.setAlertMonitorIndex(Integer.parseInt(alertMonitorIndex));
		alertMonitor.setAlertMonitorWareHouseId(Integer.parseInt(wareHouseId));
		alertMonitor.setAlertMonitorWareHouseName(wareHouseName);
		alertMonitor.setAlertMonitorSku(alertMonitorSku);
		alertMonitor.setAlertMonitorDay(Integer.parseInt(alertMonitorDay));
		alertMonitor.setAlertMonitorNum(Integer.parseInt(alertMonitorNum));
		alertMonitor.setAlertMonitorUnit(Integer.parseInt(alertMonitorUnit));
		alertMonitor.setAlertMonitorMsg(alertMonitorMsg);
		alertMonitor.setAlertMonitorCompare(Integer.parseInt(alertMonitorCompare));
		alertMonitor.setAlertMonitorStatus(Integer.parseInt(alertMonitorStatus));
		alertMonitor.setAlertMonitorSms(smsString.toString());
		alertMonitor.setAlertMonitorEmail(emailString.toString());
		alertMonitor.setAlertMonitorDistrict(districtsString.toString());
		if (null != alertMonitorSmsCheckbox)
			alertMonitor.setAlertMonitorEnableSms(1);
		else
			alertMonitor.setAlertMonitorEnableSms(0);

		if (null != alertMonitorEmailCheckbox)
			alertMonitor.setAlertMonitorEnableEmail(1);
		else
			alertMonitor.setAlertMonitorEnableEmail(0);

		alertMonitorService.insertAlertMonitor(alertMonitor);

		return "redirect:/warn/alertmonitor-manage.do";
	}

	@RequestMapping(value = "/warn/alertmonitor-update-view.do")
	public String alertMonitorUpdateView(HttpServletRequest req, HttpServletResponse resp, Model model) throws IOException {
		if (!CommonUtils.checkSessionTimeOut(req))
			return "redirect:/login.do";

		String alertMonitorId = req.getParameter("alertMonitorId");

		AlertMonitor alertMonitor = alertMonitorService.getAlertMonitorMonitor(Long.parseLong(alertMonitorId));

		List<Monitor> monitors = monitorService.getAllMonitor();

		Monitor currentMonitor = null;
		for (Monitor monitor : monitors) {
			if (monitor.getMonitorId().longValue() == alertMonitor.getMonitorId().longValue())
				currentMonitor = monitor;
		}

		List<District> districts = CommonUtils.getDistricts();
		if (alertMonitor.getAlertMonitorIndex() == 3) {
			String[] parts = alertMonitor.getAlertMonitorDistrict().split(",");
			for (District district : districts) {
				String identify = district.getDistrictId() + "#" + district.getDistrictName();
				for (String part : parts) {
					if (part.trim().equals(identify)) {
						district.setChecked(1);
						break;
					}
				}
			}
		}

		List<List<District>> list = new ArrayList<List<District>>();
		int size = districts.size();
		for (int index = 0; index < size; index++) {
			int start = index * 5;
			if (start > size)
				break;
			int end = start + 5;
			if (end > size)
				end = size;
			list.add(districts.subList(start, end));
		}

		List<WareHouse> wareHouses = currentMonitor.getWareHouses();
		for (WareHouse wareHouse : wareHouses) {
			if (wareHouse.getId().intValue() == alertMonitor.getAlertMonitorWareHouseId().intValue()) {
				wareHouse.setChecked(Boolean.TRUE);
				break;
			}
		}

		model.addAttribute("wareHouses", wareHouses);

		model.addAttribute("districts", list);

		model.addAttribute("monitors", monitors);

		model.addAttribute("currentMonitor", currentMonitor);

		model.addAttribute("alertMonitor", alertMonitor);

		return "/warn/warn-update";
	}

	@RequestMapping(value = "/warn/alertmonitor-update.do")
	public String alertMonitorUpdate(HttpServletRequest req, HttpServletResponse resp, Model model) throws IOException {
		if (!CommonUtils.checkSessionTimeOut(req))
			return "redirect:/login.do";

		String alertMonitorId = req.getParameter("alertMonitorId");
		String monitor = req.getParameter("alertMonitor");
		String[] parts = monitor.split("#");
		String monitorId = parts[0];
		String monitorName = parts[1];
		String alertMonitorIndex = req.getParameter("alertMonitorIndex");
		String alertMonitorWareHouse = req.getParameter("alertMonitorWareHouse");
		parts = alertMonitorWareHouse.split("#");
		String wareHouseId = parts[0];
		String wareHouseName = parts[1];
		String alertMonitorSku = req.getParameter("alertMonitorSku");
		String alertMonitorDay = req.getParameter("alertMonitorDay");
		String alertMonitorCompare = req.getParameter("alertMonitorCompare");
		String alertMonitorNum = req.getParameter("alertMonitorNum");
		String alertMonitorUnit = req.getParameter("alertMonitorUnit");
		String alertMonitorMsg = req.getParameter("alertMonitorMsg");
		String alertMonitorSmsCheckbox = req.getParameter("alertMonitorSmsCheckbox");
		String alertMonitorEmailCheckbox = req.getParameter("alertMonitorEmailCheckbox");

		String[] alertMonitorSmses = req.getParameterValues("alertMonitorSms");
		StringBuffer smsString = new StringBuffer();
		if (null != alertMonitorSmses) {
			for (String sms : alertMonitorSmses) {
				if (StringUtils.isNotBlank(sms)) {
					smsString.append(sms).append(",");
				}
			}
		}
		String[] alertMonitorEmails = req.getParameterValues("alertMonitorEmail");
		StringBuffer emailString = new StringBuffer();
		if (null != alertMonitorEmails) {
			for (String email : alertMonitorEmails) {
				if (StringUtils.isNotBlank(email)) {
					emailString.append(email).append(",");
				}
			}
		}
		String alertMonitorStatus = req.getParameter("alertMonitorStatus");

		String monitorDistrict[] = null;
		if ("3".equals(alertMonitorIndex)) {
			monitorDistrict = req.getParameterValues("districtName");
		}

		StringBuffer districtsString = new StringBuffer();
		if (null != monitorDistrict) {
			for (String district : monitorDistrict) {
				if (StringUtils.isNotBlank(district)) {
					districtsString.append(district).append(",");
				}
			}
		}

		AlertMonitor alertMonitor = alertMonitorService.getAlertMonitorMonitor(Long.parseLong(alertMonitorId));
		alertMonitor.setMonitorId(Long.parseLong(monitorId));
		alertMonitor.setMonitorName(monitorName);
		alertMonitor.setAlertMonitorIndex(Integer.parseInt(alertMonitorIndex));
		alertMonitor.setAlertMonitorWareHouseId(Integer.parseInt(wareHouseId));
		alertMonitor.setAlertMonitorWareHouseName(wareHouseName);
		alertMonitor.setAlertMonitorSku(alertMonitorSku);
		alertMonitor.setAlertMonitorDay(Integer.parseInt(alertMonitorDay));
		alertMonitor.setAlertMonitorNum(Integer.parseInt(alertMonitorNum));
		alertMonitor.setAlertMonitorUnit(Integer.parseInt(alertMonitorUnit));
		alertMonitor.setAlertMonitorMsg(alertMonitorMsg);
		alertMonitor.setAlertMonitorCompare(Integer.parseInt(alertMonitorCompare));
		alertMonitor.setAlertMonitorStatus(Integer.parseInt(alertMonitorStatus));
		alertMonitor.setAlertMonitorSms(smsString.toString());
		alertMonitor.setAlertMonitorEmail(emailString.toString());
		alertMonitor.setAlertMonitorDistrict(districtsString.toString());
		if (null != alertMonitorSmsCheckbox)
			alertMonitor.setAlertMonitorEnableSms(1);
		else
			alertMonitor.setAlertMonitorEnableSms(0);

		if (null != alertMonitorEmailCheckbox)
			alertMonitor.setAlertMonitorEnableEmail(1);
		else
			alertMonitor.setAlertMonitorEnableEmail(0);

		alertMonitorService.updateAlertMonitor(alertMonitor);

		return "redirect:/warn/alertmonitor-manage.do";
	}
}
