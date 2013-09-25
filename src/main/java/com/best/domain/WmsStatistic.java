package com.best.domain;

import java.io.Serializable;

/**
 * ClassName:WmsStatistic Description:
 * 
 * @author ChaoKai Wen email:wenchaokai@gmail.com
 * @version
 * @Date 2013-9-23
 */
public class WmsStatistic implements Serializable {

	private static final long serialVersionUID = -3356956648319611341L;

	private Integer qtyEach = 0;
	private Integer qtyOnholdEach = 0;
	private Integer qtyUseEach = 0;
	private Integer qtyHold4PAEach = 0;
	private String dayTime;

	public Integer getQtyEach() {
		return qtyEach == null ? 0 : qtyEach;
	}

	public void setQtyEach(Integer qtyEach) {
		this.qtyEach = qtyEach;
	}

	public Integer getQtyOnholdEach() {
		return qtyOnholdEach == null ? 0 : qtyOnholdEach;
	}

	public void setQtyOnholdEach(Integer qtyOnholdEach) {
		this.qtyOnholdEach = qtyOnholdEach;
	}

	public Integer getQtyUseEach() {
		return qtyUseEach == null ? 0 : qtyUseEach;
	}

	public void setQtyUseEach(Integer qtyUseEach) {
		this.qtyUseEach = qtyUseEach;
	}

	public Integer getQtyHold4PAEach() {
		return qtyHold4PAEach == null ? 0 : qtyHold4PAEach;
	}

	public void setQtyHold4PAEach(Integer qtyHold4PAEach) {
		this.qtyHold4PAEach = qtyHold4PAEach;
	}

	public String getDayTime() {
		return dayTime;
	}

	public void setDayTime(String dayTime) {
		this.dayTime = dayTime;
	}

}
