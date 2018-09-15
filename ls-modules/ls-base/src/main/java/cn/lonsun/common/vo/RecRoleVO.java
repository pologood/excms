package cn.lonsun.common.vo;

/**
 * 角色参数接收VO
 * Created by zhusy on 2015-1-27.
 */
public class RecRoleVO {

    private Long roleId;//角色ID

    private String roleName;//角色名称

    private Long unitId;//角色单位ID

    private String unitName;//角色单位名称

    private String roleCode;//角色编码

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }
}
