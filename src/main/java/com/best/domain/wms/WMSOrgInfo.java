package com.best.domain.wms;

import java.io.Serializable;

/**
 * ClassName:WMSOrgInfo Description:
 * 
 * @author ChaoKai Wen email:wenchaokai@gmail.com
 * @version
 * @Date 2013-9-23
 */
public class WMSOrgInfo implements Serializable {

	private static final long serialVersionUID = -7307200018855361223L;

	private Integer id;
	private String orgCode;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getOrgCode() {
		return orgCode;
	}

	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}

}
