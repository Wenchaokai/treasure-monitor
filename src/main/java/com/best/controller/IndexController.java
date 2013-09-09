package com.best.controller;

import java.io.IOException;
import java.text.ParseException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.best.service.TreasureService;
import com.best.utils.CommonUtils;

/**
 * ClassName:IndexController Description:
 * 
 * @author ChaoKai Wen email:wenchaokai@gmail.com
 * @version
 * @Date 2013-8-27
 */
@Controller
public class IndexController {

	@Autowired
	private TreasureService treasureService;

	@RequestMapping(value = "/login.do")
	public String checkUser(HttpServletRequest req, HttpServletResponse resp, Model model) throws IOException {
		if (CommonUtils.checkSessionTimeOut(req)) {
			return "redirect:/index.do";
		}
		String userCount = req.getParameter("userCount");
		String errorLogin = req.getParameter("errorLogin");
		if (null != userCount)
			model.addAttribute("userCount", userCount);
		if (null != errorLogin)
			model.addAttribute("errorLogin", Boolean.parseBoolean(errorLogin));
		return "login";
	}

	@RequestMapping(value = "/index.do")
	public String showIndex(HttpServletRequest req, HttpServletResponse resp, Model model) throws IOException, ParseException {
		if (!CommonUtils.checkSessionTimeOut(req))
			return "redirect:/login.do";

		treasureService.constructOverview(model);

		return "index";
	}
}
