package cn.lonsun.monitor.internal.entity;

import cn.lonsun.core.base.entity.ABaseEntity;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

/**
 *
 * @ClassName: MonitorColumnDetailEO
 * @Description: 日常监测栏目更新详细
 * @author liuk
 * @date 2017年9月27日
 *
 */
@Entity
@Table(name="MONITOR_COLUMN_DETAIL")
public class MonitorColumnDetailEO extends ABaseEntity {

	/**
	 *
	 */
	private static final long serialVersionUID = -121L;

	//信息类型
	public enum INFO_TYPE{
		news,//新闻
		publics//信息公开
	}

	//主键
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="ID")
	private Long id;

	//监测任务id(主表id)
	@Column(name="MONITOR_ID")
	private Long monitorId;

	//所属栏目
	@Column(name="COLUMN_ID")
	private String columnId;

	//栏目类型
	@Column(name="COLUMN_TYPE")
	private String columnType;

	//栏目类型
	@Column(name="INFO_TYPE")
	private String infoType = INFO_TYPE.news.toString();

	//栏目更新数量
	@Column(name="UPDATE_COUNT")
	private Long updateCount;


	//最后发布日期
	@Column(name = "LAST_PUBLISH_DATE")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
	@JSONField(format = "yyyy-MM-dd HH:mm:ss")
	private Date lastPublishDate;

	//应更新周期 天数
	@Column(name="UPDATE_CYCLE")
	private Integer updateCycle;

	//应更新周期 年、月、周
	@Column(name="UPDATE_CYCLE_Str")
	private String updateCycleStr;

	//未更新天数
	@Column(name="UN_PUBLISH_DAYS")
	private Integer unPublishDays;

	//发布日期，存的是yyyy-MM-dd类型的字符串
	@Column(name="PUBLISH_DETAIL")
	private String publishDetail;

	//栏目的访问地址
	@Column(name="COLUMN_URL")
	private String columnUrl;

	@Transient
	private String columnName;

	@Transient
	private String siteName;

	@Transient
	private Double unPublishDays_;

	public MonitorColumnDetailEO() {
	}

	/**
	 * 给hibernate统计使用
	 * @param columnId
	 * @param updateCount
	 */
	public MonitorColumnDetailEO(String columnId, Long updateCount, Date lastPublishDate, Double unPublishDays_) {
		this.columnId = columnId;
		this.updateCount = updateCount;
		this.lastPublishDate = lastPublishDate;
		this.unPublishDays_ = unPublishDays_;
	}

	/**
	 * 给hibernate统计使用
	 * @param columnId
	 * @param updateCount
	 */
	public MonitorColumnDetailEO(String columnId, Long updateCount, Date lastPublishDate, int unPublishDays_) {
		this.columnId = columnId;
		this.updateCount = updateCount;
		this.lastPublishDate = lastPublishDate;
		this.unPublishDays_ = unPublishDays_ + 0.0;
	}

//	public MonitorColumnDetailEO(Long monitorId,String columnType,String infoType,String columnId,Integer updateCycle, Long updateCount,Date lastPublishDate,Integer unPublishDays) {
//		this.monitorId = monitorId;
//		this.columnType = columnType;
//		this.infoType = infoType;
//		this.columnId = columnId;
//		this.updateCycle = updateCycle;
//		this.updateCount = updateCount;
//		this.lastPublishDate = lastPublishDate;
//		this.unPublishDays = unPublishDays;
//	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getMonitorId() {
		return monitorId;
	}

	public void setMonitorId(Long monitorId) {
		this.monitorId = monitorId;
	}

	public String getColumnId() {
		return columnId;
	}

	public void setColumnId(String columnId) {
		this.columnId = columnId;
	}

	public String getColumnType() {
		return columnType;
	}

	public void setColumnType(String columnType) {
		this.columnType = columnType;
	}

	public Long getUpdateCount() {
		return updateCount;
	}

	public void setUpdateCount(Long updateCount) {
		this.updateCount = updateCount;
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public Date getLastPublishDate() {
		return lastPublishDate;
	}

	public void setLastPublishDate(Date lastPublishDate) {
		this.lastPublishDate = lastPublishDate;
	}

	public Integer getUpdateCycle() {
		return updateCycle;
	}

	public void setUpdateCycle(Integer updateCycle) {
		this.updateCycle = updateCycle;
	}

	public Integer getUnPublishDays() {
		return unPublishDays;
	}

	public void setUnPublishDays(Integer unPublishDays) {
		this.unPublishDays = unPublishDays;
	}

	public Double getUnPublishDays_() {
		return unPublishDays_;
	}

	public void setUnPublishDays_(Double unPublishDays_) {
		this.unPublishDays_ = unPublishDays_;
	}

	public String getInfoType() {
		return infoType;
	}

	public void setInfoType(String infoType) {
		this.infoType = infoType;
	}

	public String getPublishDetail() {
		return publishDetail;
	}

	public void setPublishDetail(String publishDetail) {
		this.publishDetail = publishDetail;
	}

	public String getUpdateCycleStr() {
		return updateCycleStr;
	}

	public void setUpdateCycleStr(String updateCycleStr) {
		this.updateCycleStr = updateCycleStr;
	}

	public String getColumnUrl() {
		return columnUrl;
	}

	public void setColumnUrl(String columnUrl) {
		this.columnUrl = columnUrl;
	}

	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}
}
