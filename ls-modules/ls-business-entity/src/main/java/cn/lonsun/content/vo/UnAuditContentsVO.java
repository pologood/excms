package cn.lonsun.content.vo;

import cn.lonsun.common.vo.PageQueryVO;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 
 * @ClassName: UnAuditContentsVO
 * @Description: 未审核内容
 * @author Hewbing
 * @date 2015年12月7日 下午5:48:51
 *
 */
public class UnAuditContentsVO extends PageQueryVO {

	private Long siteId;
	
	private Long[] columnIds;

	private Long[] exceptColumnIds;
	
	private String title;

	private String typeCode;

	private Long columnId;

	private Integer isPublish;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date startTime;

	//结束时间
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date endTime;

	public Integer getIsPublish() {
		return isPublish;
	}

	public void setIsPublish(Integer isPublish) {
		this.isPublish = isPublish;
	}

	public Long getSiteId() {
		return siteId;
	}

	public void setSiteId(Long siteId) {
		this.siteId = siteId;
	}

	public Long[] getColumnIds() {
		return columnIds;
	}

	public void setColumnIds(Long[] columnIds) {
		this.columnIds = columnIds;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTypeCode() {
		return typeCode;
	}

	public void setTypeCode(String typeCode) {
		this.typeCode = typeCode;
	}

	public Long getColumnId() {
		return columnId;
	}

	public void setColumnId(Long columnId) {
		this.columnId = columnId;
	}

	public Long[] getExceptColumnIds() {
		return exceptColumnIds;
	}

	public void setExceptColumnIds(Long[] exceptColumnIds) {
		this.exceptColumnIds = exceptColumnIds;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
}
