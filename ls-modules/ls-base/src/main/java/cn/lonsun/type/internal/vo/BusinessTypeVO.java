package cn.lonsun.type.internal.vo;

import java.io.Serializable;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import cn.lonsun.core.base.entity.AMockEntity;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 业务对象类型，通用
 * 
 * @author xujh
 *
 */
public class BusinessTypeVO implements Serializable {

    /**
     * serialVersionUID:TODO.
     */
    private static final long serialVersionUID = 5176219050858620317L;
    
    public enum Type{
        Module,//业务模块
        Position,//职务
        Role,//角色类型
        URLResource//URL资源分类
    }
    // 业务主键
    private Long businessTypeId;
    //是否系统(初始化数据)定义，自定义(自行添加的)：false
    private boolean isSystem = false;
    //类型，用于标识此业务对象的用途
    private String type;
    //编码
    private String code;
    //用于显示的名称
    private String name;
    //所属用户ID
    private Long userId;
    //所属业务对象主键
    private Long caseId;
    //业务对象类型，例如：对人员进行分组，那么对应的就是PersonEO
    private String caseType;
    //业务对象类型下的编码，可以用于区分各模块下的子类型，caseType和caseCode共同确定组件下的第一级类型
    private String caseCode;
    //描述
    private String description;
    //序号，用于排序
    private Integer sortNum;
    //父类型主键
    private Long parentId;
    //是否存在子类型
    private boolean hasChildren;
    private Long pid;
    private String pname;
    private Boolean isParent = false;
    
    private String recordStatus = AMockEntity.RecordStatus.Normal.toString(); //记录状态，正常：Normal,已删除:Removed
    private Long createUserId;//创建人ID
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private Date createDate = new Date();//创建时间
    private Long updateUserId;//更新人ID
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private Date updateDate = new Date();//更新时间

    public boolean isSystem() {
        return isSystem;
    }
    public void setSystem(boolean isSystem) {
        this.isSystem = isSystem;
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
    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    public Long getCaseId() {
        return caseId;
    }
    public void setCaseId(Long caseId) {
        this.caseId = caseId;
    }
    public String getCaseType() {
        return caseType;
    }
    public void setCaseType(String caseType) {
        this.caseType = caseType;
    }
    public String getCaseCode() {
        return caseCode;
    }
    public void setCaseCode(String caseCode) {
        this.caseCode = caseCode;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public Long getParentId() {
        return parentId;
    }
    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    public Long getBusinessTypeId() {
        return businessTypeId;
    }

    public void setBusinessTypeId(Long businessTypeId) {
        this.businessTypeId = businessTypeId;
    }

    public Integer getSortNum() {
        return sortNum;
    }

    public void setSortNum(Integer sortNum) {
        this.sortNum = sortNum;
    }
	public boolean getHasChildren() {
		return hasChildren;
	}
	public void setHasChildren(boolean hasChildren) {
		this.hasChildren = hasChildren;
	}
    public Long getPid() {
        return pid;
    }
    public void setPid(Long pid) {
        this.pid = pid;
    }
    public String getPname() {
        return pname;
    }
    public void setPname(String pname) {
        this.pname = pname;
    }
    public Boolean getIsParent() {
        return isParent;
    }
    public void setIsParent(Boolean isParent) {
        this.isParent = isParent;
    }
    public String getRecordStatus() {
        return recordStatus;
    }
    public void setRecordStatus(String recordStatus) {
        this.recordStatus = recordStatus;
    }
    public Long getCreateUserId() {
        return createUserId;
    }
    public void setCreateUserId(Long createUserId) {
        this.createUserId = createUserId;
    }
    public Date getCreateDate() {
        return createDate;
    }
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
    public Long getUpdateUserId() {
        return updateUserId;
    }
    public void setUpdateUserId(Long updateUserId) {
        this.updateUserId = updateUserId;
    }
    public Date getUpdateDate() {
        return updateDate;
    }
    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }
    
}
