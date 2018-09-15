package cn.lonsun.monitor.task.internal.entity.vo;

import java.io.Serializable;

/**
 * @author gu.fei
 * @version 2017-09-29 16:01
 */
public class JobParamVO implements Serializable {


    private static final long serialVersionUID = 1L;

    private Long reportId;

    private Long siteId;

    private Long taskId;

    private String url;

    private int timeout;

    private Long overTime;

    private Long schedId;

    private String cron;

    private String registerCode;

    public Long getReportId() {
        return reportId;
    }

    public void setReportId(Long reportId) {
        this.reportId = reportId;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public Long getOverTime() {
        return overTime;
    }

    public void setOverTime(Long overTime) {
        this.overTime = overTime;
    }

    public Long getSchedId() {
        return schedId;
    }

    public void setSchedId(Long schedId) {
        this.schedId = schedId;
    }

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public String getRegisterCode() {
        return registerCode;
    }

    public void setRegisterCode(String registerCode) {
        this.registerCode = registerCode;
    }
}
