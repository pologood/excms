package cn.lonsun.system.globalconfig.internal.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import cn.lonsun.core.base.entity.ABaseEntity;

/**
 * 
 * @ClassName: GlobConfigCateEO
 * @Description: 全局配置分类实体类
 * @author Hewbing
 * @date 2015年8月24日 上午10:41:23
 *
 */
@Entity
@Table(name = "cms_global_config_category")
public class GlobConfigCateEO extends ABaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8558552733095622118L;
	//主键
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "CATEGORY_ID")
	private Long categoryId;
	
	//配置类名称
	@Column(name = "NAME")
	private String name;
	
	//配置类编码
	@Column(name="CODE")
	private String code;
	
	//配置类唯一编码
	@Column(name="KEY")
	private String key;
	
	public Long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
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

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
	
	
}
