package cn.lonsun.monitor.task.internal.entity.vo;

import cn.lonsun.common.vo.PageQueryVO;

/**
 * @author gu.fei
 * @version 2017-10-24 14:07
 */
public class SiteVisitQueryVO extends PageQueryVO {


    //任务ID
    private Long taskId;

    private Long siteId;

    private String begin;

    private String end;

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public String getBegin() {
        return begin;
    }

    public void setBegin(String begin) {
        this.begin = begin;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }
}
