package com.best.domain.wms;

import java.io.Serializable;

/**
 * ClassName:WMSCustomer Description:
 * 
 * @author ChaoKai Wen email:wenchaokai@gmail.com
 * @version
 * @Date 2013-9-23
 */
public class WMSCustomer implements Serializable {

	private static final long serialVersionUID = -8992707728181113004L;

	private Integer id;
	private String customerCode;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCustomerCode() {
		return customerCode;
	}

	public void setCustomerCode(String customerCode) {
		this.customerCode = customerCode;
	}

}
