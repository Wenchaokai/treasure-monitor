package com.best.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
import com.best.domain.User;
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

		return "member-add";
	}

	@RequestMapping(value = "/user/add-member.do")
	public String addMember(HttpServletRequest req, HttpServletResponse resp, Model model) throws IOException {

		if (!CommonUtils.checkSessionTimeOut(req))
			return "redirect:/login.do";

		String userCount = req.getParameter("userCount");
		Integer count = userService.checkUserCountExisted(userCount);
		if (count > 0) {
			model.addAttribute("memberExisted", Boolean.TRUE);
			return "member-add";
		}
		String userName = req.getParameter("userName");
		String userPassword = req.getParameter("userPassword");
		String userRole = req.getParameter("userRole");
		User user = new User();
		user.setUserCount(userCount);
		user.setUserName(userName);
		user.setUserPassword(userPassword);
		user.setUserRole(Integer.parseInt(userRole));

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
