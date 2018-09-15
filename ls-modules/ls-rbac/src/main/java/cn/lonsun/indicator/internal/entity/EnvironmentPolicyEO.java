package cn.lonsun.indicator.internal.entity;

import cn.lonsun.core.base.entity.AMockEntity;

/**
 * 
 * 环境设置
 *
 * @date: 2014年9月15日 下午3:43:43 
 * @author yy
 */
public class EnvironmentPolicyEO extends AMockEntity {
	
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 974306812782110873L;
	
	public enum Type{
		Input,
		Radio,
		CheckBox,
		Selected
	}
	
	private Long id;
	//类型
	private String type;
	//名称
	private String name;
	//配置编码
	private String code;
	//配置值
	private String value;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
