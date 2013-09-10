package com.best.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.best.domain.AlertData;
import com.best.domain.Monitor;
import com.best.service.AlertService;
import com.best.service.MonitorService;
import com.best.utils.CommonUtils;

/**
 * ClassName:WarnController Description:
 * 
 * @author ChaoKai Wen email:wenchaokai@gmail.com
 * @version
 * @Date 2013-9-4
 */
@Controller
public class WarnController {

	@Autowired
	private AlertService alertService;

	@Autowired
	private MonitorService monitorService;

	@RequestMapping(value = "/warn/warn-manager.do")
	public String alertManager(HttpServletRequest req, HttpServletResponse resp, Model model) throws IOException {

		if (!CommonUtils.checkSessionTimeOut(req))
			return "redirect:/login.do";

		List<Monitor> monitors = monitorService.getAllMonitor();

		Long monitorId = null;
		String monitorIdString = req.getParameter("monitorId");
		if (StringUtils.isBlank(monitorIdString)) {
			if (CollectionUtils.isNotEmpty(monitors)) {
				monitorId = monitors.get(0).getMonitorId();
			}
		} else {
			monitorId = Long.parseLong(monitorIdString);
		}

		model.addAttribute("monitors", monitors);
		model.addAttribute("monitorId", monitorId);

		if (monitorId == null) {
			model.addAttribute("noResult", Boolean.TRUE);
			return "/alert/warn-manager";
		}

		String currentPage = req.getParameter("currentPage");
		int totalPage = 0;
		List<AlertData> res = null;
		totalPage = alertService.getAlertDataTotalPageByMonitorId(monitorId);

		if (StringUtils.isBlank(currentPage))
			currentPage = "1";
		Integer page = Integer.parseInt(currentPage);
		if (!(totalPage == 0 || page > totalPage)) {
			res = alertService.getAlertDataByMonitorId(monitorId, page - 1);
		}

		if (null == res)
			res = new ArrayList<AlertData>();

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

		model.addAttribute("start", start);
		model.addAttribute("end", end);
		model.addAttribute("prePage", ((page - 1) < 1 ? 1 : (page - 1)) + "");
		model.addAttribute("nextPage", ((page + 1) > totalPage ? totalPage : (page + 1)) + "");

		return "/alert/warn-manager";
	}
}
