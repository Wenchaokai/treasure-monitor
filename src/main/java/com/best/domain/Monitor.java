package com.best.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

/**
 * ClassName:Monitor Description:
 * 
 * @author ChaoKai Wen email:wenchaokai@gmail.com
 * @version
 * @Date 2013-8-31
 */
public class Monitor implements Serializable {

	private static final long serialVersionUID = 3277832334829896335L;

	private Long monitorId;
	private String monitorName;
	private String monitorCustomerName;
	private String monitorCustomerId;
	private String monitorWarehouseNameList;
	private String monitorWarehouseIdList;
	private Integer monitorStatus;
	private String monitorStartTime;
	private String monitorIndexList;
	private Long monitorResponserId;
	private String monitorSku;

	public String getMonitorName() {
		return monitorName;
	}

	public void setMonitorName(String monitorName) {
		this.monitorName = monitorName;
	}

	public Long getMonitorId() {
		return monitorId;
	}

	public void setMonitorId(Long monitorId) {
		this.monitorId = monitorId;
	}

	public String getMonitorCustomerName() {
		return monitorCustomerName;
	}

	public void setMonitorCustomerName(String monitorCustomerName) {
		this.monitorCustomerName = monitorCustomerName;
	}

	public String getMonitorCustomerId() {
		return monitorCustomerId;
	}

	public void setMonitorCustomerId(String monitorCustomerId) {
		this.monitorCustomerId = monitorCustomerId;
	}

	public String getMonitorWarehouseNameList() {
		return monitorWarehouseNameList;
	}

	public void setMonitorWarehouseNameList(String monitorWarehouseNameList) {
		this.monitorWarehouseNameList = monitorWarehouseNameList;
	}

	public String getMonitorWarehouseIdList() {
		return monitorWarehouseIdList;
	}

	public void setMonitorWarehouseIdList(String monitorWarehouseIdList) {
		this.monitorWarehouseIdList = monitorWarehouseIdList;
	}

	public Integer getMonitorStatus() {
		return monitorStatus;
	}

	public void setMonitorStatus(Integer monitorStatus) {
		this.monitorStatus = monitorStatus;
	}

	public String getMonitorStartTime() {
		return monitorStartTime;
	}

	public void setMonitorStartTime(String monitorStartTime) {
		this.monitorStartTime = monitorStartTime;
	}

	public String getMonitorIndexList() {
		return monitorIndexList;
	}

	public void setMonitorIndexList(String monitorIndexList) {
		this.monitorIndexList = monitorIndexList;
	}

	public Long getMonitorResponserId() {
		return monitorResponserId;
	}

	public void setMonitorResponserId(Long monitorResponserId) {
		this.monitorResponserId = monitorResponserId;
	}

	public List<WareHouse> getWareHouses() {
		String[] wareHouseIds = this.monitorWarehouseIdList.split(",");
		String[] wareHouseNames = this.monitorWarehouseNameList.split(",");
		int size = wareHouseIds.length < wareHouseNames.length ? wareHouseIds.length : wareHouseNames.length;
		List<WareHouse> wareHouses = new ArrayList<WareHouse>();
		for (int index = 0; index < size; index++) {
			WareHouse wareHouse = new WareHouse();
			wareHouse.setId(Integer.parseInt(wareHouseIds[index]));
			wareHouse.setWareHouseName(wareHouseNames[index]);
			wareHouses.add(wareHouse);
		}
		return wareHouses;
	}

	public Set<Integer> getMonitorWareHouseId() {
		Set<Integer> res = new HashSet<Integer>();
		if (StringUtils.isNotBlank(monitorWarehouseIdList)) {
			String[] parts = monitorWarehouseIdList.split(",");
			for (String id : parts) {
				res.add(Integer.parseInt(id));
			}
		}
		return res;
	}

	public List<Integer> getMonitorIndexSet() {
		Set<Integer> res = new HashSet<Integer>();
		if (StringUtils.isNotBlank(monitorIndexList)) {
			String[] parts = monitorIndexList.split(",");
			for (String id : parts) {
				res.add(Integer.parseInt(id));
			}
		}
		return new ArrayList<Integer>(res);
	}

	public String getMonitorSku() {
		return monitorSku;
	}

	public void setMonitorSku(String monitorSku) {
		monitorSku = monitorSku.replace('；', ';');
		this.monitorSku = monitorSku;
	}

	public List<String> getMonitorSkus() {
		List<String> res = new ArrayList<String>();
		if (StringUtils.isNotBlank(monitorSku)) {
			String[] parts = monitorSku.split(";|；");
			for (String sku : parts) {
				res.add(sku.trim());
			}
		}
		return res;
	}

}
