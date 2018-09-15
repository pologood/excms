package cn.lonsun.monitor.internal.entity;

import cn.lonsun.core.base.entity.ABaseEntity;

import javax.persistence.*;

/**
 *
 * @ClassName: MonitorColumnDetailEO
 * @Description: 日常监测互动更新详细
 * @author liuk
 * @date 2017年9月27日
 *
 */
@Entity
@Table(name="MONITOR_INTERACT_DETAIL")
public class MonitorInteractDetailEO extends ABaseEntity {

	/**
	 *
	 */
	private static final long serialVersionUID = -121L;

	public Long getUnreplyCount() {
		return unreplyCount;
	}

	public void setUnreplyCount(Long unreplyCount) {
		this.unreplyCount = unreplyCount;
	}

//	//栏目类型
//	public enum COLUMN_TYPE{
//		columnType_ZWZX,//政务咨询类栏目
//		columnType_DCZJ,//调查征集类栏目
//		columnType_HDFT//互动访谈类栏目
//	}

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
	private Long columnId;

	//栏目类型
	@Column(name="COLUMN_TYPE")
	private String columnType;

	//未回复数量
	@Column(name="UNREPLY_COUNT")
	private Long unreplyCount;

	//超过三个月未回复留言的contentId
	@Column(name="UNREPLY_IDS")
	private String unreplyIds;

	//栏目更新数量
	@Column(name="UPDATE_COUNT")
	private Long updateCount;

	@Transient
	private String columnName;

	@Transient
	private String columnUrl;

	public MonitorInteractDetailEO() {
	}

	/**
	 * 给hibernate统计使用
	 * @param columnId
	 * @param updateCount
	 */
	public MonitorInteractDetailEO(Long columnId, Long updateCount,Long unreplyCount) {
		this.columnId = columnId;
		this.updateCount = updateCount;
		this.unreplyCount = unreplyCount;
	}

	/**
	 * 给hibernate统计使用
	 * @param columnId
	 * @param updateCount
	 */
	public MonitorInteractDetailEO(Long columnId, Long updateCount,Long unreplyCount,String unreplyIds) {
		this.columnId = columnId;
		this.updateCount = updateCount;
		this.unreplyCount = unreplyCount;
		this.unreplyIds = unreplyIds;
	}

	/**
	 * 给hibernate统计使用
	 * @param columnId
	 * @param updateCount
	 */
	public MonitorInteractDetailEO(Long columnId, Long updateCount) {
		this.columnId = columnId;
		this.updateCount = updateCount;
	}

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

	public Long getColumnId() {
		return columnId;
	}

	public void setColumnId(Long columnId) {
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

	public String getColumnUrl() {
		return columnUrl;
	}

	public void setColumnUrl(String columnUrl) {
		this.columnUrl = columnUrl;
	}

	public String getUnreplyIds() {
		return unreplyIds;
	}

	public void setUnreplyIds(String unreplyIds) {
		this.unreplyIds = unreplyIds;
	}
}
