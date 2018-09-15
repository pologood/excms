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
 * @ClassName: GlobConfigItemEO
 * @Description: 分类配置项实体类
 * @author Hewbing
 * @date 2015年8月25日 上午10:42:00
 *
 */
@Entity
@Table(name="cms_global_config_item")
public class GlobConfigItemEO extends ABaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4857349989564882716L;

    public enum DataType{
    	number,//数值
    	string,//字符
    	pass,//密码
    }
	//主键
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ITEM_ID")
	private Long itemId;
	
	//配置项名称
	@Column(name = "ITEM_NAME")
	private String itemName;
	
	//key值
	@Column(name = "KEY")
	private String key;
	
	//value值
	@Column(name = "VALUE")
	private String value;
	
	//对应配置类主键
	@Column(name = "CATEGORY_ID")
	private Long cateId;

	//对应配置类key值
	@Column(name="CATEGORY_KEY")
	private String cateKey;
	
	//配置项类型
	@Column(name="DATA_TYPE")
	private String dataType;
	
	public Long getItemId() {
		return itemId;
	}

	public void setItemId(Long itemId) {
		this.itemId = itemId;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Long getCateId() {
		return cateId;
	}

	public void setCateId(Long cateId) {
		this.cateId = cateId;
	}


	public String getCateKey() {
		return cateKey;
	}

	public void setCateKey(String cateKey) {
		this.cateKey = cateKey;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	
}
