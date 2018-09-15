package cn.lonsun.rbac.internal.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import cn.lonsun.core.base.entity.AMockEntity;

/**
 * 资源
 * @author xujh
 *
 */
@Entity
@Table(name="RBAC_RESOURCE")
public class ResourceEO extends AMockEntity {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -3608370761342336491L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO) 
	@Column(name="resource_id")
	private Long resourceId;
//	//所属模块或应用
//	@Column(name="module_id")
//	private Long moduleId;
	//资源所属类型
	@Column(name="resource_type_id")
	private Long resourceTypeId;
	//资源的名称
	@Column(name="name")
	private String name;
//	//模块名-项目上下文
//	@Column(name="app_context")
//	private String appContext;
//	//请求控制器名-controller注解名
//	@Column(name="controller")
//	private String controller;
//	//请求方法名-method注解名,建议只有method支持模糊匹配
//	@Column(name="method")
//	private String method;
	//是否模糊匹配
	@Column(name="is_wildcard")
	private boolean isWildcard = false;
	//资源的路径：可以使用通配符,表示访问资源的链接，使用相对路径
	@Column(name="uri")
	private String uri;
	//资源信息描述
	@Column(name="description")
	private String description;
	// 指示器ID
	@Column(name="indicator_id")
	private Long indicatorId;
	
	//是否可用
    @Column(name="is_enable")
    private boolean isEnable = true;

	public Long getResourceId() {
		return resourceId;
	}

	public void setResourceId(Long resourceId) {
		this.resourceId = resourceId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isWildcard() {
		return isWildcard;
	}

	public void setWildcard(boolean isWildcard) {
		this.isWildcard = isWildcard;
	}

	public Long getResourceTypeId() {
		return resourceTypeId;
	}

	public void setResourceTypeId(Long resourceTypeId) {
		this.resourceTypeId = resourceTypeId;
	}

    public Long getIndicatorId() {
        return indicatorId;
    }

    public void setIndicatorId(Long indicatorId) {
        this.indicatorId = indicatorId;
    }

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public boolean getIsEnable() {
		return isEnable;
	}

	public void setIsEnable(boolean isEnable) {
		this.isEnable = isEnable;
	}
}
