package cn.lonsun.rbac.internal.entity;

import cn.lonsun.core.base.entity.AMockEntity;

import javax.persistence.*;

/**
 * 角色
 * 
 * @author xujh
 *
 */
@Entity
@Table(name = "RBAC_ROLE")
public class RoleEO extends AMockEntity {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 4443762074118139928L;

    public enum Type {
        System, // 系统级的，系统管理独有
        Public, // 共用的
        Private// 私有的-一般属于某个部门
    }

    public enum Scope {
        GOA, // 业务模块
        CMS// URL资源分类
    }

    public enum RoleCode {
        super_admin, //超级管理员
        site_admin //站点管理员
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "role_id")
    private Long roleId;
    // 角色类型
    @Column(name = "type")
    private String type = Type.Public.toString();
    // 角色编码，需要预定义，不可随意修改
    @Column(name = "code")
    private String code;
    // 角色名称，系统角色名称不能重复、公用角色名称不能重复，同单位下角色名称不能重复
    @Column(name = "name")
    private String name;
    // 角色所属单位
    @Column(name = "organ_id")
    private Long organId;
    // 角色业务类型Id
    @Column(name = "business_type_id")
    private Long businessTypeId;
    // 角色描述
    @Column(name = "description")
    private String description;
    // 是否预定义，预定义角色不允许系统管理者更新、删除和锁定
    @Column(name = "is_Predefine")
    private Integer isPredefine = 0;
    // 是否用于开发商进行应用配置，如果是，那么除了开发商，其余用户都无法使用该角色
    // @Column(name="is_Owned_By_Developer")
    // private Integer isOwnedByDeveloper;
    @Column(name = "scope")
    private String scope;// 范围

    @Column(name = "SITE_ID")
    private Long siteId;//站点ID

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getOrganId() {
        return organId;
    }

    public void setOrganId(Long organId) {
        this.organId = organId;
    }

    public Long getBusinessTypeId() {
        return businessTypeId;
    }

    public void setBusinessTypeId(Long businessTypeId) {
        this.businessTypeId = businessTypeId;
    }

    // public Integer getIsOwnedByDeveloper() {
    // return isOwnedByDeveloper;
    // }
    // public void setIsOwnedByDeveloper(Integer isOwnedByDeveloper) {
    // this.isOwnedByDeveloper = isOwnedByDeveloper;
    // }
    public Integer getIsPredefine() {
        return isPredefine;
    }

    public void setIsPredefine(Integer isPredefine) {
        this.isPredefine = isPredefine;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }
}