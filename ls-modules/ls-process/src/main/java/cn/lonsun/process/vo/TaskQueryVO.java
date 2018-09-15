
package cn.lonsun.process.vo;

import cn.lonsun.common.vo.PageQueryVO;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 *
 *@date 2014-12-16 20:37  <br/>
 *@author zhusy  <br/>
 *@version v1.0  <br/>
 */
public class TaskQueryVO extends PageQueryVO {

    private String moduleCode;

    /**
     * 任务标题
     */
    private String title;

    /**
     * 办理步骤
     */
    private String dealStep;

    private Integer year;

    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date startDate;//开始时间

    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date endDate;//结束时间

    public String getModuleCode() {
        return moduleCode;
    }

    public void setModuleCode(String moduleCode) {
        this.moduleCode = moduleCode;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDealStep() {
        return dealStep;
    }

    public void setDealStep(String dealStep) {
        this.dealStep = dealStep;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
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
}
