package cn.lonsun.common.vo;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.TipsMode;

/**
 * 单位部门接收对象VO
 * Created by zhusy on 2015-5-21.
 */
public class RecUnitOrganVO {

    private Long unitId;

    private String unitName;

    private Long organId;

    private String organName;

    private Integer type;//类型(单位:0,部门:1)

    private String platformCode;//平台编码

    private Boolean isExternalOrgan = Boolean.FALSE;//是否外平台

    private String dn;//LDAP唯一标识

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

    public Long getOrganId() {
        return organId;
    }

    public void setOrganId(Long organId) {
        this.organId = organId;
    }

    public String getOrganName() {
        return organName;
    }

    public void setOrganName(String organName) {
        this.organName = organName;
    }

    public Integer getType() {
        if(null == type){
            if(unitId != null && organId == null){
                type = 0;
            }else if(organId != null){
                type = 1;
            }else{
                type = 0;
            }
        }
        return type;
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


    @Override
    public boolean equals(Object obj) {
        RecUnitOrganVO vo = (RecUnitOrganVO)obj;
        if(vo.getType().equals(0)){
            return null != unitId ? (unitId.equals(vo.unitId)):false;
        }else {
            return null != organId ? (organId.equals(vo.organId)):false;
        }

    }
}
