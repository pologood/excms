package cn.lonsun.common.vo;


public class PlatformVO{
	private Long id;
	// 平台名称
	private String name;
	//是否外平台编码
	private Boolean isExternal;
	// 平台编码
	private String code;
	// 平台对外初始化服务地址
	private String webserviceUrl;
	// 平台对外服务命名空间
	private String nameSpace;
	// 平台对外初始化服务方法
	private String method;
	// 描述
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
