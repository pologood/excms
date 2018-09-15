package cn.lonsun.content.internal.entity;

import cn.lonsun.core.base.entity.AMockEntity;

import javax.persistence.*;

/**
 * 
 * @ClassName: ContentReferRelationEO
 * @Description: 内容引用关系表
 * @author Hewbing
 * @date 2016年3月25日 下午3:31:03
 *
 */
@Entity
@Table(name="CMS_CONTENT_REFER_RELATION")
public class ContentReferRelationEO extends AMockEntity {
	private static final long serialVersionUID = 112121L;
	
    public enum ModelCode {
    	CONTENT,//内容协同的引用
    	PUBLIC,//信息公开的引用
    	MSGSUBMIT//引用至信息报送
    }
	
    public enum TYPE {
    	COPY,//复制引用
    	REFER,//链接引用
    	SYN//同步引用
    }
    
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="ID")
	private Long id;
	
	//引用编码
	@Column(name="MODEL_CODE")
	private String modelCode;
	
	//引用类型
	@Column(name="TYPE")
	private String type=TYPE.COPY.toString();
	
	//源新闻主键
	@Column(name="CAUSE_BY_ID")
	private Long causeById;

	//源栏目
	@Column(name="CAUSE_BY_COLUMN_ID")
	private Long causeByColumnId;

	//源信息公开目录id
	@Column(name="CAUSE_BY_CAT_ID")
	private Long causeByCatId;
	
	//引用者ID
	@Column(name="REFER_ID")
	private Long referId;

	//引用者的栏目
	@Column(name="COLUMN_ID")
	private Long columnId;

	//引用者信息公开目录id
	@Column(name="CAT_ID")
	private Long catId;

	//引用者栏目名称
	@Column(name="REFER_Name")
	private String referName;

	//引用者类型 默认内容管理
	@Column(name="REFER_MODEL_CODE")
	private String referModelCode = ModelCode.CONTENT.toString();

	@Column(name="IS_COLUMN_OPT")
	private Integer isColumnOpt = 0;//是否是栏目复制或者引用 0-否，1-是

	@Transient
	private String personName;

	@Transient
	private String organName;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getModelCode() {
		return modelCode;
	}

	public void setModelCode(String modelCode) {
		this.modelCode = modelCode;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Long getCauseById() {
		return causeById;
	}

	public void setCauseById(Long causeById) {
		this.causeById = causeById;
	}

	public Long getColumnId() {
		return columnId;
	}

	public void setColumnId(Long columnId) {
		this.columnId = columnId;
	}

	public Long getReferId() {
		return referId;
	}

	public void setReferId(Long referId) {
		this.referId = referId;
	}

	public String getReferName() {
		return referName;
	}

	public void setReferName(String referName) {
		this.referName = referName;
	}

	public String getReferModelCode() {
		return referModelCode;
	}

	public void setReferModelCode(String referModelCode) {
		this.referModelCode = referModelCode;
	}

	public Long getCatId() {
		return catId;
	}

	public void setCatId(Long catId) {
		this.catId = catId;
	}

	public String getPersonName() {
		return personName;
	}

	public void setPersonName(String personName) {
		this.personName = personName;
	}

	public String getOrganName() {
		return organName;
	}

	public void setOrganName(String organName) {
		this.organName = organName;
	}

	public Integer getIsColumnOpt() {
		return isColumnOpt;
	}

	public void setIsColumnOpt(Integer isColumnOpt) {
		this.isColumnOpt = isColumnOpt;
	}

	public Long getCauseByCatId() {
		return causeByCatId;
	}

	public void setCauseByCatId(Long causeByCatId) {
		this.causeByCatId = causeByCatId;
	}

	public Long getCauseByColumnId() {
		return causeByColumnId;
	}

	public void setCauseByColumnId(Long causeByColumnId) {
		this.causeByColumnId = causeByColumnId;
	}
}
