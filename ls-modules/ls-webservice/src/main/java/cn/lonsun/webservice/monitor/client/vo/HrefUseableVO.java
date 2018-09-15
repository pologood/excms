package cn.lonsun.webservice.monitor.client.vo;

import java.util.Date;

/**
 * @author gu.fei
 * @version 2017-11-23 11:16
 */
public class HrefUseableVO {

    //报告ID
    private Long reportId;

    //检测时间
    private Date monitorDate;

    //访问地址
    private String visitUrl;

    //访问地址
    private String parentUrl;

    //是否可以访问
    private Integer isVisitable = 0;

    //访问返回编码
    private Integer respCode;

    //不可访问原因
    private String reason;

    //是否首页
    private Integer isIndex = 0;

    //域名
    private String domain;

    public Long getReportId() {
        return reportId;
    }

    public void setReportId(Long reportId) {
        this.reportId = reportId;
    }

    public Date getMonitorDate() {
        return monitorDate;
    }

    public void setMonitorDate(Date monitorDate) {
        this.monitorDate = monitorDate;
    }

    public String getVisitUrl() {
        return visitUrl;
    }

    public void setVisitUrl(String visitUrl) {
        this.visitUrl = visitUrl;
    }

    public String getParentUrl() {
        return parentUrl;
    }

    public void setParentUrl(String parentUrl) {
        this.parentUrl = parentUrl;
    }

    public Integer getIsVisitable() {
        return isVisitable;
    }

    public void setIsVisitable(Integer isVisitable) {
        this.isVisitable = isVisitable;
    }

    public Integer getRespCode() {
        return respCode;
    }

    public void setRespCode(Integer respCode) {
        this.respCode = respCode;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Integer getIsIndex() {
        return isIndex;
    }

    public void setIsIndex(Integer isIndex) {
        this.isIndex = isIndex;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }
}
