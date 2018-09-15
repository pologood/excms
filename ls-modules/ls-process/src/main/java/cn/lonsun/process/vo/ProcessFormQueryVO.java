package cn.lonsun.process.vo;

import cn.lonsun.common.vo.PageQueryVO;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * Created by zhu124866 on 2015-12-23.
 */
public class ProcessFormQueryVO extends PageQueryVO {

    private String moduleCode;

    private String title;

    @DateTimeFormat(pattern="yyyy-MM-dd")//用户前端日期类型字符串自动转换
    private Date startDate;

    @DateTimeFormat(pattern="yyyy-MM-dd")//用户前端日期类型字符串自动转换
    private Date endDate;

    private Integer[] formStatus;

    private Long userId;

    private Long organId;

    private Long unitId;

    private String createPersonName;

    private String curActivityName;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Integer[] getFormStatus() {
        return formStatus;
    }

    public void setFormStatus(Integer[] formStatus) {
        this.formStatus = formStatus;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getModuleCode() {
        return moduleCode;
    }

    public void setModuleCode(String moduleCode) {
        this.moduleCode = moduleCode;
    }

    public Long getOrganId() {
        return organId;
    }

    public void setOrganId(Long organId) {
        this.organId = organId;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public String getCreatePersonName() {
        return createPersonName;
    }

    public void setCreatePersonName(String createPersonName) {
        this.createPersonName = createPersonName;
    }

    public String getCurActivityName() {
        return curActivityName;
    }

    public void setCurActivityName(String curActivityName) {
        this.curActivityName = curActivityName;
    }
}
