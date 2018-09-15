package cn.lonsun.webservice.vo;

/**
 * WebService管理分页查询VO
 * 
 * @author xujh
 * @date 2014年11月4日 上午8:31:16
 * @version V1.0
 */
public class WebServiceQueryVO {
	//系统编码
	private String systemCode;
	// 系统名称
	private String systemName;
	// 服务编码
	private String code;
	// 服务地址
	private String uri;
	// 命名空间
	private String nameSpace;
	
	private String description;

	private Long pageIndex = Long.valueOf(0);

	private Integer pageSize = Integer.valueOf(15);
	// 排序字段
	private String sortField;
	// 是否逆序排序
	private String sortOrder;

	public String getSystemName() {
		return systemName;
	}

	public void setSystemName(String systemName) {
		this.systemName = systemName;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getNameSpace() {
		return nameSpace;
	}

	public void setNameSpace(String nameSpace) {
		this.nameSpace = nameSpace;
	}
	public Long getPageIndex() {
		return pageIndex;
	}

	public void setPageIndex(Long pageIndex) {
		this.pageIndex = pageIndex;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public String getSortField() {
		return sortField;
	}

	public void setSortField(String sortField) {
		this.sortField = sortField;
	}

	public String getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(String sortOrder) {
		this.sortOrder = sortOrder;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getSystemCode() {
		return systemCode;
	}

	public void setSystemCode(String systemCode) {
		this.systemCode = systemCode;
	}
}
