package cn.lonsun.rbac.internal.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import cn.lonsun.core.base.entity.ABaseEntity;

/**
 * 平台交换中心基本信息
 *
 * @author xujh
 * @version 1.0
 * 2015年4月24日
 *
 */
@Entity
@Table(name = "rbac_platform")
public class PlatformEO extends ABaseEntity {

	/**
	 * UID
	 */
	private static final long serialVersionUID = 8587289379899833416L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ID")
	private Long id;
	// 平台名称
	@Column(name = "NAME")
	private String name;
	//是否外部平台数据交换中心，只能有一条记录是false
	@Column(name="IS_EXTERNAL",updatable=false)
	private Boolean isExternal = Boolean.TRUE;
	// 平台编码
	@Column(name = "CODE")
	private String code;
	// 平台对外初始化服务地址
	@Column(name = "WEBSERVICE_URL")
	private String webserviceUrl;
	// 平台对外服务命名空间
	@Column(name = "NAME_SPACE")
	private String nameSpace;
	// 平台对外初始化服务方法
	@Column(name = "METHOD")
	private String method;
	// 描述
	@Column(name = "DESCRIPTION")
	private String description;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getWebserviceUrl() {
		return webserviceUrl;
	}
	public void setWebserviceUrl(String webserviceUrl) {
		this.webserviceUrl = webserviceUrl;
	}
	public String getNameSpace() {
		return nameSpace;
	}
	public void setNameSpace(String nameSpace) {
		this.nameSpace = nameSpace;
	}
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Boolean getIsExternal() {
		return isExternal;
	}
	public void setIsExternal(Boolean isExternal) {
		this.isExternal = isExternal;
	}
}
