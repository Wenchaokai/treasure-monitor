package com.best.domain;

import java.io.Serializable;

/**
 * ClassName:WareHouse Description:
 * 
 * @author ChaoKai Wen email:wenchaokai@gmail.com
 * @version
 * @Date 2013-9-6
 */
public class WareHouse implements Serializable {

	private static final long serialVersionUID = 1404786853360663848L;

	private Integer id;
	private String wareHouseName;
	private String wareHouseCode;
	private String wmsOrgCode;
	private String wmsWareHouseCode;
	private Boolean checked = false;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getWareHouseName() {
		return wareHouseName;
	}

	public void setWareHouseName(String wareHouseName) {
		this.wareHouseName = wareHouseName;
	}

	public Boolean getChecked() {
		return checked;
	}

	public void setChecked(Boolean checked) {
		this.checked = checked;
	}

	public String getWareHouseCode() {
		return wareHouseCode;
	}

	public void setWareHouseCode(String wareHouseCode) {
		this.wareHouseCode = wareHouseCode;
	}

	public String getWmsOrgCode() {
		return wmsOrgCode;
	}

	public void setWmsOrgCode(String wmsOrgCode) {
		this.wmsOrgCode = wmsOrgCode;
	}

	public String getWmsWareHouseCode() {
		return wmsWareHouseCode;
	}

	public void setWmsWareHouseCode(String wmsWareHouseCode) {
		this.wmsWareHouseCode = wmsWareHouseCode;
	}

}
