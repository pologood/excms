package cn.lonsun.type.internal.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import cn.lonsun.core.base.entity.AMockEntity;

/**
 * 业务对象类型，通用
 * 
 * @author xujh
 *
 */
@Entity
@Table(name = "system_business_type")
public class BusinessTypeEO extends AMockEntity {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 4950877924640584657L;

    public enum Type {
        Module, // 业务模块
        Position, // 职务
        Role, // 角色类型
        URLResource// URL资源分类
    }

    public enum Scope {
        GOA, // 业务模块
        CMS// URL资源分类
    }

    // 业务主键
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "business_type_id")
    private Long businessTypeId;
    // 是否系统(初始化数据)定义，自定义(自行添加的)：false
    @Column(name = "is_system")
    private boolean isSystem = false;
    // 类型，用于标识此业务对象的用途
    @Column(name = "type")
    private String type;
    // 编码
    @Column(name = "code")
    private String code;
    // 用于显示的名称
    @Column(name = "name")
    private String name;
    // 所属用户ID
    @Column(name = "user_id")
    private Long userId;
    // 所属业务对象主键
    @Column(name = "case_id")
    private Long caseId;
    // 业务对象类型，例如：对人员进行分组，那么对应的就是PersonEO
    @Column(name = "case_type")
    private String caseType;
    // 业务对象类型下的编码，可以用于区分各模块下的子类型，caseType和caseCode共同确定组件下的第一级类型
    @Column(name = "case_code")
    private String caseCode;
    // 描述
    @Column(name = "description")
    private String description;
    // 序号，用于排序
    @Column(name = "sort_Num")
    private Integer sortNum;
    // 父类型主键
    @Column(name = "parent_id")
    private Long parentId;
    // 是否存在子类型
    @Column(name = "has_children")
    private boolean hasChildren;
    // 范围
    @Column(name = "scope")
    private String scope = Scope.CMS.toString();

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

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }
}