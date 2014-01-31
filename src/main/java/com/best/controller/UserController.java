package com.best.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeoutException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.rubyeye.xmemcached.exception.MemcachedException;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.best.Constants;
import com.best.domain.Customer;
import com.best.domain.User;
import com.best.service.CustomerService;
import com.best.service.UserService;
import com.best.utils.CommonUtils;

/**
 * ClassName:UserController Description:
 * 
 * @author ChaoKai Wen email:wenchaokai@gmail.com
 * @version
 * @Date 2013-8-27
 */
@Controller
public class UserController {

	@Autowired
	private UserService userService;

	@Autowired
	private CustomerService customerService;

	@RequestMapping(value = "/user/checkUser.do")
	public String checkUser(HttpServletRequest req, HttpServletResponse resp, Model model) throws IOException {
		String userCount = req.getParameter("userName");
		String userPassword = req.getParameter("password");

		User user = new User();
		user.setUserCount(userCount);
		user.setUserPassword(userPassword);

		user = userService.checkUser(user);

		if (user == null) {
			// 输入有错误

			model.addAttribute("userCount", userCount);
			model.addAttribute("errorLogin", Boolean.TRUE);

			return "redirect:/login.do";
		} else {
			HttpSession session = req.getSession();
			session.setAttribute(Constants.USER_TOKEN_IDENTIFY, user);
			return "redirect:/index.do";
		}
	}

	@RequestMapping(value = "/user/loginout.do")
	public String loginout(HttpServletRequest req, HttpServletResponse resp) throws IOException {

		HttpSession session = req.getSession();
		User user = (User) session.getAttribute(Constants.USER_TOKEN_IDENTIFY);
		if (user != null) {
			session.removeAttribute(Constants.USER_TOKEN_IDENTIFY);
		}

		return "redirect:/login.do";
	}

	@RequestMapping(value = "/user/infomanager.do")
	public String infoManager(HttpServletRequest req, HttpServletResponse resp, Model model) throws IOException {

		if (!CommonUtils.checkSessionTimeOut(req))
			return "redirect:/login.do";

		String userIdString = req.getParameter("userId");
		String userName = req.getParameter("userName");
		if (null != userIdString) {
			model.addAttribute("userId", userIdString);
			model.addAttribute("userName", userName);
		} else {
			HttpSession session = req.getSession();
			User user = (User) session.getAttribute(Constants.USER_TOKEN_IDENTIFY);
			model.addAttribute("userId", user.getUserId());
			model.addAttribute("userName", user.getUserName());
		}

		return "member-set";
	}

	@RequestMapping(value = "/user/member-update-view.do")
	public String memberUpdateView(HttpServletRequest req, HttpServletResponse resp, Model model) throws IOException {

		if (!CommonUtils.checkSessionTimeOut(req))
			return "redirect:/login.do";

		String userId = req.getParameter("userId");
		User user = userService.findUser(Long.parseLong(userId.trim()));
		user.setUserPassword(CommonUtils.decoder(user.getUserPassword()));
		Set<String> customerCodes = user.getCustomerCodes();
		List<Customer> res = customerService.getAllCustomer();
		for (Customer customer : res) {
			if (customer.getCustomerName().length() > 10)
				customer.setCustomerName(customer.getCustomerName().substring(0, 10));
			if (user.getUserRole() == 0) {
				// 普通用户
				if (customerCodes.contains(customer.getCustomerCode()))
					customer.setChecked(Boolean.TRUE);
			}
		}

		StringBuilder builder = new StringBuilder();
		boolean flag = false;
		for (Customer customer : res) {
			if (flag)
				builder.append(";");
			builder.append(customer.getCustomerCode() + ":" + customer.getCustomerName() + ":" + customer.getChecked());
			flag = true;
		}
		model.addAttribute("customerString", builder.toString());

		List<List<Customer>> customers = new ArrayList<List<Customer>>();
		for (int index = 0;; index++) {
			int start = index * 3;
			int end = (start + 3) > res.size() ? res.size() : (start + 3);
			if (start > res.size())
				break;
			customers.add(res.subList(start, end));
		}
		model.addAttribute("customers", customers);
		model.addAttribute("modifyUser", user);

		return "member-update";
	}

	@RequestMapping(value = "/user/modifyPassword.do")
	public String modifyPassword(HttpServletRequest req, HttpServletResponse resp, Model model) throws IOException {

		if (!CommonUtils.checkSessionTimeOut(req))
			return "redirect:/login.do";

		Long userId = Long.parseLong(req.getParameter("userId"));
		String oldPassword = req.getParameter("oldPassowrd");
		String newPassword = req.getParameter("newPassowrd");

		int modifyCount = userService.updateUserPassword(userId, oldPassword, newPassword);

		if (modifyCount == 0) {
			model.addAttribute("error", Boolean.TRUE);
			return "member-set";
		} else {
			model.addAttribute("error", Boolean.FALSE);
			model.addAttribute("modifyStatus", Boolean.TRUE);
			return "member-set";
		}
	}

	@RequestMapping(value = "/user/add-member-index.do")
	public String addMemberIndex(HttpServletRequest req, HttpServletResponse resp, Model model) throws IOException {

		if (!CommonUtils.checkSessionTimeOut(req))
			return "redirect:/login.do";

		String memberExisted = req.getParameter("memberExisted");
		if (StringUtils.isNotBlank(memberExisted)) {
			model.addAttribute("memberExisted", Boolean.parseBoolean(memberExisted));
		}

		List<Customer> res = customerService.getAllCustomer();
		for (Customer customer : res) {
			if (customer.getCustomerName().length() > 10)
				customer.setCustomerName(customer.getCustomerName().substring(0, 10));
		}

		StringBuilder builder = new StringBuilder();
		boolean flag = false;
		for (Customer customer : res) {
			if (flag)
				builder.append(";");
			builder.append(customer.getCustomerCode() + ":" + customer.getCustomerName() + ":" + customer.getChecked());
			flag = true;
		}
		model.addAttribute("customerString", builder.toString());

		List<List<Customer>> customers = new ArrayList<List<Customer>>();
		for (int index = 0;; index++) {
			int start = index * 3;
			int end = (start + 3) > res.size() ? res.size() : (start + 3);
			if (start > res.size())
				break;
			customers.add(res.subList(start, end));
		}
		model.addAttribute("customers", customers);

		return "member-add";
	}

	@RequestMapping(value = "/user/update-member.do")
	public String updateMember(HttpServletRequest req, HttpServletResponse resp, Model model) throws IOException {

		if (!CommonUtils.checkSessionTimeOut(req))
			return "redirect:/login.do";

		String userCount = req.getParameter("userCount");
		String userName = req.getParameter("userName");
		String userPassword = req.getParameter("userPassword");
		String userRole = req.getParameter("userRole");
		String[] userCustomers = req.getParameterValues("userCustomer");
		String userCustomer = "";
		if (Integer.parseInt(userRole) == 0) {
			if (userCustomers != null && userCustomers.length > 0) {
				boolean isOr = false;
				for (String customer : userCustomers) {
					if (customer.trim().length() > 0) {
						if (isOr)
							userCustomer += ",";
						userCustomer += customer;
						isOr = true;
					}
				}
			}
		}
		User user = new User();
		user.setUserCount(userCount);
		user.setUserName(userName);
		user.setUserPassword(userPassword);
		user.setUserRole(Integer.parseInt(userRole));
		user.setUserCustomers(userCustomer);

		userService.updateMember(user);

		return "redirect:/user/member-manage.do";
	}

	@RequestMapping(value = "/user/add-member.do")
	public String addMember(HttpServletRequest req, HttpServletResponse resp, Model model) throws IOException {

		if (!CommonUtils.checkSessionTimeOut(req))
			return "redirect:/login.do";

		String userCount = req.getParameter("userCount");
		Integer count = userService.checkUserCountExisted(userCount);
		if (count > 0) {
			model.addAttribute("memberExisted", Boolean.TRUE);
			return "redirect:/user/add-member-index.do";
		}
		String userName = req.getParameter("userName");
		String userPassword = req.getParameter("userPassword");
		String userRole = req.getParameter("userRole");
		String[] userCustomers = req.getParameterValues("userCustomer");
		String userCustomer = "";
		if (Integer.parseInt(userRole) == 0) {
			if (userCustomers != null && userCustomers.length > 0) {
				boolean isOr = false;
				for (String customer : userCustomers) {
					if (customer.trim().length() > 0) {
						if (isOr)
							userCustomer += ",";
						userCustomer += customer;
						isOr = true;
					}
				}
			}
		}
		User user = new User();
		user.setUserCount(userCount);
		user.setUserName(userName);
		user.setUserPassword(userPassword);
		user.setUserRole(Integer.parseInt(userRole));
		user.setUserCustomers(userCustomer);

		userService.addMember(user);

		return "redirect:/user/member-manage.do";
	}

	@RequestMapping(value = "/user/delete.do")
	public String deleteMember(HttpServletRequest req, HttpServletResponse resp, Model model) throws IOException {

		if (!CommonUtils.checkSessionTimeOut(req))
			return "redirect:/login.do";

		String userId = req.getParameter("userId");

		userService.deleteMember(userId);

		return "redirect:/user/member-manage.do";
	}

	@RequestMapping(value = "/user/member-manage.do")
	public String memberManager(HttpServletRequest req, HttpServletResponse resp, Model model) throws IOException {

		if (!CommonUtils.checkSessionTimeOut(req))
			return "redirect:/login.do";

		String searchName = req.getParameter("searchName");
		String searchCount = req.getParameter("searchCount");
		String currentPage = req.getParameter("currentPage");
		int totalPage = 0;
		List<User> res = null;
		try {
			totalPage = userService.getTotalSize(searchName, searchCount);
		} catch (TimeoutException e1) {
		} catch (InterruptedException e1) {
		} catch (MemcachedException e1) {
		}

		if (StringUtils.isBlank(currentPage))
			currentPage = "1";
		Integer page = Integer.parseInt(currentPage);
		if (!(totalPage == 0 || page > totalPage)) {
			try {
				res = userService.getSearchUsers(searchName, searchCount, page - 1);
			} catch (TimeoutException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
			} catch (MemcachedException e) {
			}
		}

		if (null == res)
			res = new ArrayList<User>();

		model.addAttribute("res", res);
		if (StringUtils.isNotBlank(searchName))
			model.addAttribute("searchName", searchName);
		else
			model.addAttribute("searchName", "");
		if (StringUtils.isNotBlank(searchCount))
			model.addAttribute("searchCount", searchCount);
		else
			model.addAttribute("searchCount", "");
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
		return "member-manage";
	}
}
