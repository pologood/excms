package cn.lonsun.process.vo;

import cn.lonsun.common.vo.PageQueryVO;

/**
 * Created by zhu124866 on 2016-1-11.
 */
public class ProcessAgentQueryVO extends PageQueryVO {

    private String beAgentPersonName;

    private String moduleCode;

    private Long unitId;

    public String getBeAgentPersonName() {
        return beAgentPersonName;
    }

    public void setBeAgentPersonName(String beAgentPersonName) {
        this.beAgentPersonName = beAgentPersonName;
    }

    public String getModuleCode() {
        return moduleCode;
    }

    public void setModuleCode(String moduleCode) {
        this.moduleCode = moduleCode;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }
}
