/*
 * 2014-12-13 <br/>
 * 
 */
package cn.lonsun.webservice.processEngine.vo;

public class UserInfoVO {
    /**
     * 用户组织ID
     */
    private Long orgId;
    /**
     * 用户组织名称
     */
    private String orgName;
    /**
     * 用户单位ID
     */
    private Long unitId;
    /**
     * 用户单位名称
     */
    private String unitName;
    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 用户名称
     */
    private String userName;

    public UserInfoVO(){}

    public UserInfoVO(Long unitId) {
        this.unitId = unitId;
    }

    public UserInfoVO(Long orgId, Long userId) {
        this.orgId = orgId;
        this.userId = userId;
    }

    

    public UserInfoVO(String userName, Long orgId, String orgName, Long unitId, String unitName, Long userId) {
        this.userName = userName;
        this.orgId = orgId;
        this.orgName = orgName;
        this.unitId = unitId;
        this.unitName = unitName;
        this.userId = userId;
    }

    public Long getOrgId() {
        return orgId;
    }

    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
