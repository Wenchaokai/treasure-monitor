package com.best.domain;

import java.io.Serializable;

/**
 * ClassName:District Description:
 * 
 * @author ChaoKai Wen email:wenchaokai@gmail.com
 * @version
 * @Date 2013-9-7
 */
public class District implements Serializable {

	private static final long serialVersionUID = 6688194532119438224L;

	private Integer districtId;
	private String districtName;
	private Integer checked = 0;

	public Integer getDistrictId() {
		return districtId;
	}

	public void setDistrictId(Integer districtId) {
		this.districtId = districtId;
	}

	public String getDistrictName() {
		return districtName;
	}

	public void setDistrictName(String districtName) {
		this.districtName = districtName;
	}

	public Integer getChecked() {
		return checked;
	}

	public void setChecked(Integer checked) {
		this.checked = checked;
	}

}
