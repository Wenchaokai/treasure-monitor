package com.best.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.best.Constants;

/**
 * ClassName:RequestFilter Description:
 * 
 * @author ChaoKai Wen email:wenchaokai@gmail.com
 * @version
 * @Date 2013-8-28
 */
public class RequestFilter implements Filter {

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException,
			ServletException {
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		HttpServletResponse response = (HttpServletResponse) servletResponse;

		Object obj = request.getSession().getAttribute(Constants.USER_TOKEN_IDENTIFY);
		String status = request.getParameter("status");
		if (null != obj || null != status) {
			chain.doFilter(request, response);
			return;
		}

		response.getWriter().write("<script type=\"text/javascript\" language=\"javascript\">");
		response.getWriter().write("top.location.href='" + "/login.do?status=1'");
		response.getWriter().write("</script>");
		return;

	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
	}

}
