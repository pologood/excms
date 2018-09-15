package cn.lonsun.rbac.vo;


import cn.lonsun.type.internal.entity.BusinessTypeEO;

/**
 * 资源分类VO
 * @author xujh
 *
 */
public class ResourceTypeVO extends BusinessTypeEO {
	
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 5830291987053372629L;

	private Long id;
	
	private Long pid;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getPid() {
		return pid;
	}

	public void setPid(Long pid) {
		this.pid = pid;
	}
}
