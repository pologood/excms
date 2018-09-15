package cn.lonsun.rbac.vo;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 角色关联关系VO
 *
 * @author xujh
 * @version 1.0
 * 2015年5月27日
 *
 */
public class RoleRelationVO {

	private Long id;
	
	private Long targetRoleId;
	
	private String targetRoleCode;
		
	private String targetRoleName;
	
	
	
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
	private Date createDate;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTargetRoleName() {
		return targetRoleName;
	}

	public void setTargetRoleName(String targetRoleName) {
		this.targetRoleName = targetRoleName;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Long getTargetRoleId() {
		return targetRoleId;
	}

	public void setTargetRoleId(Long targetRoleId) {
		this.targetRoleId = targetRoleId;
	}

	public String getTargetRoleCode() {
		return targetRoleCode;
	}

	public void setTargetRoleCode(String targetRoleCode) {
		this.targetRoleCode = targetRoleCode;
	}
}
