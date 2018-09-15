
package cn.lonsun.common.vo;

/**
 *
 * 接收单位VO对象
 *@date 2014-12-12 10:26  <br/>
 *@author zhusy  <br/>
 *@version v1.0  <br/>
 */
public class ReceiveUnitVO {

    private Long unitId;

    private String unitName;

    private String platformCode;//平台编码

    private Boolean isExternalOrgan = Boolean.FALSE;//是否外平台

    private String dn;//LDAP唯一标识


    @Override
    public boolean equals(Object obj) {
        ReceiveUnitVO vo=(ReceiveUnitVO)obj;
        return null!=unitId ? (unitId.longValue() == vo.unitId.longValue()):true;
    }

    @Override
    public int hashCode() {
        return null!=unitId ? unitId.hashCode() : 0;
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

    public String getPlatformCode() {
        return platformCode;
    }

    public void setPlatformCode(String platformCode) {
        this.platformCode = platformCode;
    }

    public Boolean getIsExternalOrgan() {
        return isExternalOrgan;
    }

    public void setIsExternalOrgan(Boolean isExternalOrgan) {
        this.isExternalOrgan = isExternalOrgan;
    }

    public String getDn() {
        return dn;
    }

    public void setDn(String dn) {
        this.dn = dn;
    }
}
