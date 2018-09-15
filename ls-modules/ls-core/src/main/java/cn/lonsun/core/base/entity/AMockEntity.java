package cn.lonsun.core.base.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import java.util.Date;

/**
 * 框架实体的抽象类-同时支持物理和逻辑删除
 * 
 * @author 徐建华
 * 
 */
@MappedSuperclass
public class AMockEntity implements IMockEntity {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 881540343350073755L;

	/**
	 * 正常状态：Normal,已删除:Removed
	 * 
	 * @author xujh
	 * 
	 */
	public enum RecordStatus {
		Normal, Removed;
	}

	/**
	 * 记录状态：正常：Normal,已删除:Removed
	 * */
	@Column(name = "RECORD_STATUS")
	private String recordStatus = RecordStatus.Normal.toString();

	/** 创建人ID */
	@Column(name = "CREATE_USER_ID", updatable = false)
	private Long createUserId;
	/**
	 * 创建组织
	 * */
	@Column(name = "CREATE_ORGAN_ID", updatable = false)
	private Long createOrganId;

	/**
	 * 创建时间 用户前端日期类型字符串自动转换 用户Date类型转换Json字符类型的格式化.
	 * 注：数据库如果为TimeStamp类型存储的时间，在json输出的时候默认返回格林威治时间，需要增加timezone属性
	 * */
	@Column(name = "CREATE_DATE", updatable = false)
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
	@JSONField(format = "yyyy-MM-dd HH:mm:ss")
	private Date createDate;

	/**
	 * 更新人ID
	 **/
	@Column(name = "UPDATE_USER_ID")
	private Long updateUserId;

	/**
	 * 更新时间 用户前端日期类型字符串自动转换 用户Date类型转换Json字符类型的格式化.
	 * 注：数据库如果为TimeStamp类型存储的时间，在json输出的时候默认返回格林威治时间，需要增加timezone属性
	 * */
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
	@Column(name = "UPDATE_DATE")
	@JSONField(format = "yyyy-MM-dd HH:mm:ss")
	private Date updateDate;//

	public Long getCreateUserId() {
		return createUserId;
	}

	public void setCreateUserId(Long createUserId) {
		this.createUserId = createUserId;
	}

	public Long getCreateOrganId() {
		return createOrganId;
	}

	public void setCreateOrganId(Long createOrganId) {
		this.createOrganId = createOrganId;
	}

	public Long getUpdateUserId() {
		return updateUserId;
	}

	public void setUpdateUserId(Long updateUserId) {
		this.updateUserId = updateUserId;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public String getRecordStatus() {
		return recordStatus;
	}

	public void setRecordStatus(String recordStatus) {
		this.recordStatus = recordStatus;
	}

}
