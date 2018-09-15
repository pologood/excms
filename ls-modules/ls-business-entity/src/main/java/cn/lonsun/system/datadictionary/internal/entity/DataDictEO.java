package cn.lonsun.system.datadictionary.internal.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import cn.lonsun.core.base.entity.ABaseEntity;

/**
 * 
 * @ClassName: DataDictEO
 * @Description: 数据字典实体类
 * @author Hewbing
 * @date 2015年8月24日 上午10:14:08
 *
 */
@Entity
@Table(name="cms_data_dictionary")
public class DataDictEO extends ABaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="DATA_DICT_ID")
    private Long dictId;
    //唯一编码
    @Column(name="CODE")
    private String code;
    //字典名称
    @Column(name="NAME")
    private String name;
    //是否被引用
    @Column(name="ISUSED")
    private Integer isUsed=0;
    //描述
    @Column(name="DESCRIPTION")
    private String description;

    @Column(name="SITE_ID")
    private Long siteId;
    
    @Column(name="IS_OPEN")
    private Integer isOpen=0;
    
	public Long getDictId() {
		return dictId;
	}

	public void setDictId(Long dictId) {
		this.dictId = dictId;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getIsUsed() {
		return isUsed;
	}

	public void setIsUsed(Integer isUsed) {
		this.isUsed = isUsed;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Long getSiteId() {
		return siteId;
	}

	public void setSiteId(Long siteId) {
		this.siteId = siteId;
	}

	public Integer getIsOpen() {
		return isOpen;
	}

	public void setIsOpen(Integer isOpen) {
		this.isOpen = isOpen;
	}
    
	
}
