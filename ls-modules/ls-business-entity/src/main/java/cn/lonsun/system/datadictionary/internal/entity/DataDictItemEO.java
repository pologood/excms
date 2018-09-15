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
 * @ClassName: DataDictItemEO
 * @Description: 字典项实体类
 * @author Hewbing
 * @date 2015年8月25日 上午10:15:32
 *
 */
@Entity
@Table(name="cms_data_dictionary_item")
public class DataDictItemEO extends ABaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="ITEM_ID")
    private Long itemId;
    //唯一编码
    @Column(name="CODE")
    private String code;
    //value值
    @Column(name="value")
    private String value;
    //字典项名称
    @Column(name="NAME")
    private String name;
    //排序
    @Column(name="SORTNUM")
    private Integer sortNum;
    //是否默认值
    @Column(name="IS_DEFAULT")
    private Integer isDefault=0;
	//是否隐藏
	@Column(name="IS_HIDE")
	private Integer isHide=0;
	//对应字典
    @Column(name="DATA_DIC_ID")
    private Long dataDicId;
    //对应字典名
    @Column(name="DATA_DIC_NAME")
    private String dataDicName;
    //描述
    @Column(name="DESCRIPTION")
    private String description;
    //站点ID
    @Column(name="SITE_ID")
    private Long siteId;
    
	public Long getItemId() {
		return itemId;
	}

	public void setItemId(Long itemId) {
		this.itemId = itemId;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getDataDicId() {
		return dataDicId;
	}

	public void setDataDicId(Long dataDicId) {
		this.dataDicId = dataDicId;
	}

	public Integer getSortNum() {
		return sortNum;
	}

	public void setSortNum(Integer sortNum) {
		this.sortNum = sortNum;
	}

	public Integer getIsDefault() {
		return isDefault;
	}

	public void setIsDefault(Integer isDefault) {
		this.isDefault = isDefault;
	}

	public Integer getIsHide() {
		return isHide;
	}

	public void setIsHide(Integer isHide) {
		this.isHide = isHide;
	}

	public String getDataDicName() {
		return dataDicName;
	}

	public void setDataDicName(String dataDicName) {
		this.dataDicName = dataDicName;
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
    
    
}
