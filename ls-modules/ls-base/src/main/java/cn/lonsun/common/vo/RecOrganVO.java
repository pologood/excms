package cn.lonsun.common.vo;

/**
 * 接收部门VO
 * Created by zhusy on 2015-3-27.
 */
public class RecOrganVO {

    private Long organId;

    private String organName;

    private Long unitId;

    private String unitName;


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
}
