package com.best.controller;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.best.Constants;
import com.best.domain.Customer;
import com.best.domain.Monitor;
import com.best.domain.Treasure;
import com.best.domain.User;
import com.best.domain.WareHouse;
import com.best.service.AlertMonitorService;
import com.best.service.CustomerService;
import com.best.service.MonitorService;
import com.best.service.TreasureService;
import com.best.service.WareHouseService;
import com.best.utils.CommonUtils;
import com.best.utils.DateUtil;

/**
 * ClassName:MonitorController Description:
 * 
 * @author ChaoKai Wen email:wenchaokai@gmail.com
 * @version
 * @Date 2013-8-31
 */
@Controller
public class MonitorController {

	@Autowired
	private MonitorService monitorService;

	@Autowired
	private TreasureService treasureService;

	@Autowired
	private CustomerService customerService;

	@Autowired
	private WareHouseService wareHouseService;

	@Autowired
	private AlertMonitorService alertMonitorService;

	@RequestMapping(value = "/monitor/monitor-manage.do")
	public String monitorManager(HttpServletRequest req, HttpServletResponse resp, Model model) throws IOException {

		if (!CommonUtils.checkSessionTimeOut(req))
			return "redirect:/login.do";

		String currentPage = req.getParameter("currentPage");
		int totalPage = 0;
		List<Monitor> res = null;
		totalPage = monitorService.getMonitorsTotalSize();

		if (StringUtils.isBlank(currentPage))
			currentPage = "1";
		Integer page = Integer.parseInt(currentPage);
		if (!(totalPage == 0 || page > totalPage)) {
			res = monitorService.getMonitors(page - 1);
		}

		if (null == res)
			res = new ArrayList<Monitor>();

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
		if (end < start)
			end = start;

		model.addAttribute("start", start);
		model.addAttribute("end", end);
		model.addAttribute("prePage", ((page - 1) < 1 ? 1 : (page - 1)) + "");
		model.addAttribute("nextPage", ((page + 1) > totalPage ? totalPage : (page + 1)) + "");
		return "/monitor/monitor-manage";
	}

	@RequestMapping(value = "/monitor/monitor-delete.do")
	public String deleteMonitor(HttpServletRequest req, HttpServletResponse resp, Model model) throws IOException, ParseException {

		if (!CommonUtils.checkSessionTimeOut(req))
			return "redirect:/login.do";

		String monitorId = req.getParameter("monitor_id");

		monitorService.deleteMonitor(Long.parseLong(monitorId.trim()));

		alertMonitorService.deleteMonitor(Long.parseLong(monitorId.trim()));

		deleteTreasure();

		return "redirect:/monitor/monitor-manage.do";
	}

	@RequestMapping(value = "/monitor/monitor-view.do")
	public String monitorView(HttpServletRequest req, HttpServletResponse resp, Model model) throws IOException {

		if (!CommonUtils.checkSessionTimeOut(req))
			return "redirect:/login.do";

		String monitorId = req.getParameter("monitor_id");

		Monitor monitor = monitorService.monitorView(Long.parseLong(monitorId.trim()));

		Set<String> wareHouseCodes = monitor.getMonitorWareHouseCode();

		List<WareHouse> wareHouses = wareHouseService.getAllWareHouse();
		for (WareHouse wareHouse : wareHouses) {
			wareHouse.setChecked(Boolean.FALSE);
			if (wareHouseCodes.contains(wareHouse.getWareHouseCode()))
				wareHouse.setChecked(Boolean.TRUE);
		}
		List<List<WareHouse>> list = new ArrayList<List<WareHouse>>();
		int size = wareHouses.size();
		for (int index = 0; index < size; index++) {
			int start = index * 5;
			if (start > size)
				break;
			int end = start + 5;
			if (end > size)
				end = size;
			list.add(wareHouses.subList(start, end));
		}

		model.addAttribute("wareHouses", list);

		model.addAttribute("res", monitor);

		return "/monitor/monitor-view";
	}

	@RequestMapping(value = "/monitor/monitor-update-view.do")
	public String monitorUpdateView(HttpServletRequest req, HttpServletResponse resp, Model model) throws IOException {
		if (!CommonUtils.checkSessionTimeOut(req))
			return "redirect:/login.do";

		String monitorId = req.getParameter("monitor_id");

		Monitor monitor = monitorService.monitorView(Long.parseLong(monitorId.trim()));

		Set<String> wareHouseCodes = monitor.getMonitorWareHouseCode();

		List<Customer> customers = customerService.getAllCustomer();
		model.addAttribute("customers", customers);

		List<WareHouse> wareHouses = wareHouseService.getAllWareHouse();
		for (WareHouse wareHouse : wareHouses) {
			wareHouse.setChecked(Boolean.FALSE);
			if (wareHouseCodes.contains(wareHouse.getWareHouseCode()))
				wareHouse.setChecked(Boolean.TRUE);
		}

		List<List<WareHouse>> list = new ArrayList<List<WareHouse>>();
		int size = wareHouses.size();
		for (int index = 0; index < size; index++) {
			int start = index * 5;
			if (start > size)
				break;
			int end = start + 5;
			if (end > size)
				end = size;
			list.add(wareHouses.subList(start, end));
		}
		model.addAttribute("wareHouses", list);

		model.addAttribute("monitorCustomerCode", monitor.getMonitorCustomerCode());

		model.addAttribute("res", monitor);

		return "/monitor/monitor-update";
	}

	@RequestMapping(value = "/monitor/monitor-update.do", method = RequestMethod.POST)
	public String monitorUpdate(HttpServletRequest req, HttpServletResponse resp, Model model) throws IOException {

		if (!CommonUtils.checkSessionTimeOut(req))
			return "redirect:/login.do";

		String monitorId = req.getParameter("monitorId");

		String[] norms = req.getParameterValues("norm");
		String[] wareHouses = req.getParameterValues("wareHouse");

		String monitorCustomer = req.getParameter("selectCustomer");

		String monitorName = req.getParameter("monitorName");

		String sku = req.getParameter("sku");

		Monitor monitor = monitorService.monitorView(Long.parseLong(monitorId.trim()));

		monitor.setMonitorName(monitorName);

		monitor.setMonitorSku(sku);

		String[] parts = monitorCustomer.split("#");

		String customerCode = parts[0];
		String customerName = parts[1];

		String wareCodes = "";
		String wareNames = "";

		if (null != wareHouses) {
			for (String wareHouse : wareHouses) {
				parts = wareHouse.split("#");
				if (StringUtils.isNotBlank(wareCodes))
					wareCodes += ",";
				if (StringUtils.isNotBlank(wareNames))
					wareNames += ",";
				wareCodes += parts[0];
				wareNames += parts[1];
			}
		}

		String normIds = "";
		if (norms != null) {
			for (String norm : norms) {
				parts = norm.split("#");
				if (StringUtils.isNotBlank(normIds))
					normIds += ",";
				normIds += parts[0];
			}
		}

		monitor.setMonitorCustomerCode(customerCode);
		monitor.setMonitorCustomerName(customerName);

		monitor.setMonitorWarehouseCodeList(wareCodes);
		monitor.setMonitorWarehouseNameList(wareNames);

		monitor.setMonitorIndexList(normIds);

		monitorService.updateMonitor(monitor);

		model.addAttribute("res", monitor);

		Set<String> wareHouseCodes = monitor.getMonitorWareHouseCode();

		List<WareHouse> wareHouseList = wareHouseService.getAllWareHouse();
		for (WareHouse wareHouse : wareHouseList) {
			wareHouse.setChecked(Boolean.FALSE);
			if (wareHouseCodes.contains(wareHouse.getWareHouseCode()))
				wareHouse.setChecked(Boolean.TRUE);
		}
		List<List<WareHouse>> list = new ArrayList<List<WareHouse>>();
		int size = wareHouseList.size();
		for (int index = 0; index < size; index++) {
			int start = index * 5;
			if (start > size)
				break;
			int end = start + 5;
			if (end > size)
				end = size;
			list.add(wareHouseList.subList(start, end));
		}

		model.addAttribute("wareHouses", list);

		return "/monitor/monitor-view";
	}

	@RequestMapping(value = "/monitor/monitor-add-view.do")
	public String monitorAddView(HttpServletRequest req, HttpServletResponse resp, Model model) throws IOException {
		if (!CommonUtils.checkSessionTimeOut(req))
			return "redirect:/login.do";

		List<Customer> customers = customerService.getAllCustomer();
		model.addAttribute("customers", customers);

		List<WareHouse> wareHouses = wareHouseService.getAllWareHouse();
		List<List<WareHouse>> list = new ArrayList<List<WareHouse>>();
		int size = wareHouses.size();
		for (int index = 0; index < size; index++) {
			int start = index * 5;
			if (start > size)
				break;
			int end = start + 5;
			if (end > size)
				end = size;
			list.add(wareHouses.subList(start, end));
		}
		model.addAttribute("wareHouses", list);

		return "/monitor/monitor-add";
	}

	@RequestMapping(value = "/monitor/monitor-add.do")
	public String monitorAdd(HttpServletRequest req, HttpServletResponse resp, Model model) throws IOException, ParseException {
		if (!CommonUtils.checkSessionTimeOut(req))
			return "redirect:/login.do";

		String[] norms = req.getParameterValues("norm");
		String[] wareHouses = req.getParameterValues("wareHouse");

		String monitorCustomer = req.getParameter("selectCustomer");

		String monitorName = req.getParameter("monitorName");

		String sku = req.getParameter("sku");

		Monitor monitor = new Monitor();

		monitor.setMonitorName(monitorName);

		monitor.setMonitorSku(sku);

		String[] parts = monitorCustomer.split("#");

		String customerCode = parts[0];
		String customerName = parts[1];

		String wareCodes = "";
		String wareNames = "";

		if (null != wareHouses) {
			for (String wareHouse : wareHouses) {
				parts = wareHouse.split("#");
				if (StringUtils.isNotBlank(wareCodes))
					wareCodes += ",";
				if (StringUtils.isNotBlank(wareNames))
					wareNames += ",";
				wareCodes += parts[0];
				wareNames += parts[1];
			}
		}

		String normIds = "";
		if (norms != null) {
			for (String norm : norms) {
				parts = norm.split("#");
				if (StringUtils.isNotBlank(normIds))
					normIds += ",";
				normIds += parts[0];
			}
		}

		monitor.setMonitorCustomerCode(customerCode);
		monitor.setMonitorCustomerName(customerName);

		monitor.setMonitorWarehouseCodeList(wareCodes);
		monitor.setMonitorWarehouseNameList(wareNames);

		monitor.setMonitorIndexList(normIds);
		monitor.setMonitorStatus(0);
		monitor.setMonitorStartTime(DateUtil.getCurrentDateString());

		User obj = (User) req.getSession().getAttribute(Constants.USER_TOKEN_IDENTIFY);
		monitor.setMonitorResponserId(obj.getUserId());

		monitor = monitorService.insertMonitor(monitor);

		model.addAttribute("res", monitor);

		Set<String> wareHouseCodes = monitor.getMonitorWareHouseCode();

		List<WareHouse> wareHouseList = wareHouseService.getAllWareHouse();
		for (WareHouse wareHouse : wareHouseList) {
			wareHouse.setChecked(Boolean.FALSE);
			if (wareHouseCodes.contains(wareHouse.getWareHouseCode()))
				wareHouse.setChecked(Boolean.TRUE);
		}
		List<List<WareHouse>> list = new ArrayList<List<WareHouse>>();
		int size = wareHouseList.size();
		for (int index = 0; index < size; index++) {
			int start = index * 5;
			if (start > size)
				break;
			int end = start + 5;
			if (end > size)
				end = size;
			list.add(wareHouseList.subList(start, end));
		}

		model.addAttribute("wareHouses", list);

		addTreasure();

		return "/monitor/monitor-view";
	}

	@RequestMapping(value = "/monitor/monitor-dingdan-view.do")
	public String monitorDingdanView(HttpServletRequest req, HttpServletResponse resp, Model model) throws IOException,
			ParseException {
		if (!CommonUtils.checkSessionTimeOut(req))
			return "redirect:/login.do";

		String monitorId = req.getParameter("monitor_id");
		String currentIndex = req.getParameter("currentIndex");
		String currentSku = req.getParameter("currentSku");
		String startTime = req.getParameter("startTime");
		if (StringUtils.isBlank(startTime))
			startTime = DateUtil.getPreDate();
		String endTime = req.getParameter("endTime");
		if (StringUtils.isBlank(endTime))
			endTime = DateUtil.getCurrentDateString();
		Integer currentIndexId = 0;
		Monitor monitor = monitorService.monitorView(Long.parseLong(monitorId.trim()));
		if (StringUtils.isBlank(currentIndex)) {
			currentIndexId = monitor.getMonitorIndexSet().get(0);
		} else
			currentIndexId = Integer.parseInt(currentIndex);

		if (StringUtils.isBlank(currentSku)) {
			currentSku = monitor.getMonitorSkus().get(0);
		}
		model.addAttribute("monitor", monitor);
		model.addAttribute("currentIndex", currentIndexId);
		model.addAttribute("currentSku", currentSku);
		model.addAttribute("startTime", startTime);
		model.addAttribute("endTime", endTime);

		return "/monitor/monitor-dingdang-view";
	}

	@RequestMapping(value = "/monitor/map.do")
	public String map(HttpServletRequest req, HttpServletResponse resp, Model model) throws IOException, ParseException {
		if (!CommonUtils.checkSessionTimeOut(req))
			return "redirect:/login.do";

		return "/monitor/map";
	}

	private void deleteTreasure() throws ParseException {
		Treasure treasure = new Treasure();
		treasure.setDateTime(DateUtil.getCurrentDateString());
		treasure.setAlarmNums(0);
		treasure.setMonitorNums(-1);
		treasureService.updateTreasure(treasure);
	}

	private void addTreasure() throws ParseException {
		Treasure treasure = new Treasure();
		treasure.setDateTime(DateUtil.getCurrentDateString());
		treasure.setAlarmNums(0);
		treasure.setMonitorNums(1);
		treasureService.updateTreasure(treasure);
	}

}
