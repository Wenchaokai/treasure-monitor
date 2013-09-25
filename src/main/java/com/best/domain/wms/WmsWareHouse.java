package com.best.domain.wms;

import java.io.Serializable;

/**
 * ClassName:Wms Description:
 * 
 * @author ChaoKai Wen email:wenchaokai@gmail.com
 * @version
 * @Date 2013-9-23
 */
public class WmsWareHouse implements Serializable {

	private static final long serialVersionUID = -4970139107523036552L;

	private Integer id;
	private String wareHouseCode;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getWareHouseCode() {
		return wareHouseCode;
	}

	public void setWareHouseCode(String wareHouseCode) {
		this.wareHouseCode = wareHouseCode;
	}

}
