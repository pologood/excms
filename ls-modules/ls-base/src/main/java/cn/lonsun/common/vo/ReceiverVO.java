package cn.lonsun.common.vo;

/**
 * 所有的公文下一步接收人都通过此VO传递信息到后台
 *
 * @author xujh
 * @version 1.0
 *          2014年12月10日
 */
public class ReceiverVO {
    //单位ID
    private Long unitId;
    //单位名称
    private String unitName;
    //部门/处室ID
    private Long organId;
    //部门/处室名称
    private String organName;
    //用户ID
    private Long userId;
    //用户姓名
    private String personName;
    //手机号
    private String mobile;
    //平台编码
    private String platformCode;
    //是否外平台人员
    private boolean isExternalPerson;

    private String dn;


    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public Long getOrganId() {
        return organId;
    }

    public void setOrganId(Long organId) {
        this.organId = organId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
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

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPlatformCode() {
        return platformCode;
    }

    public void setPlatformCode(String platformCode) {
        this.platformCode = platformCode;
    }

    public boolean isExternalPerson() {
        return isExternalPerson;
    }

    public void setExternalPerson(boolean isExternalPerson) {
        this.isExternalPerson = isExternalPerson;
    }

    public String getDn() {
        return dn;
    }

    public void setDn(String dn) {
        this.dn = dn;
    }
}
