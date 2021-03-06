package com.best.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

/**
 * ClassName:User Description:
 * 
 * @author ChaoKai Wen email:wenchaokai@gmail.com
 * @version
 * @Date 2013-8-27
 */
public class User implements Serializable {

	private static final long serialVersionUID = -398744926531951923L;

	private Long userId;
	private String userCount;
	private String userName;
	private Integer userRole;
	private String userPassword;
	private String userCustomers;

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getUserCount() {
		return userCount;
	}

	public void setUserCount(String userCount) {
		this.userCount = userCount;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Integer getUserRole() {
		return userRole;
	}

	public void setUserRole(Integer userRole) {
		this.userRole = userRole;
	}

	public String getUserPassword() {
		return userPassword;
	}

	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}

	public String getUserCustomers() {
		return userCustomers;
	}

	public void setUserCustomers(String userCustomers) {
		this.userCustomers = userCustomers;
	}

	public Set<String> getCustomerCodes() {
		Set<String> customerCodes = new HashSet<String>();
		if (StringUtils.isNotBlank(userCustomers)) {
			String[] parts = userCustomers.split(",");
			for (String part : parts) {
				customerCodes.add(part);
			}
		}
		return customerCodes;
	}

}
