package cn.lonsun.content.vo;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class ContentChartQueryVO {

	//站点ID
	private Long siteId;

	//开始时间
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
	@JSONField(format = "yyyy-MM-dd HH:mm:ss")
	private Date startDate;
	//结束时间
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
	@JSONField(format = "yyyy-MM-dd HH:mm:ss")
	private Date endDate;

	private String startStr;

	private String endStr;
	//记录个数
	private Integer num=10;

	private String typeCode;
	private Integer orderType;

	private String isOrgan; //是否按部门统计 1-是
	private String isUser; //是否按人员统计 1-是
	private String isColumn; //是否按栏目统计 1-是

	public Long getSiteId() {
		return siteId;
	}

	public void setSiteId(Long siteId) {
		this.siteId = siteId;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Integer getNum() {
		return num;
	}

	public void setNum(Integer num) {
		this.num = num;
	}

	public String getTypeCode() {
		return typeCode;
	}

	public void setTypeCode(String typeCode) {
		this.typeCode = typeCode;
	}

	public String getStartStr() {
		return startStr;
	}

	public void setStartStr(String startStr) {
		this.startStr = startStr;
	}

	public String getEndStr() {
		return endStr;
	}

	public void setEndStr(String endStr) {
		this.endStr = endStr;
	}

	public Integer getOrderType() {
		return orderType;
	}

	public void setOrderType(Integer orderType) {
		this.orderType = orderType;
	}

	public String getIsOrgan() {
		return isOrgan;
	}

	public void setIsOrgan(String isOrgan) {
		this.isOrgan = isOrgan;
	}

	public String getIsUser() {
		return isUser;
	}

	public void setIsUser(String isUser) {
		this.isUser = isUser;
	}

	public String getIsColumn() {
		return isColumn;
	}

	public void setIsColumn(String isColumn) {
		this.isColumn = isColumn;
	}
}
