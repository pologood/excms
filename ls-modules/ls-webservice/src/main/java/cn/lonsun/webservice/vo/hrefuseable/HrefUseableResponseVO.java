package cn.lonsun.webservice.vo.hrefuseable;

import java.io.Serializable;

/**
 * @author gu.fei
 * @version 2017-09-21 17:15
 */
public class HrefUseableResponseVO implements Serializable {

    private static final long serialVersionUID = 0L;

    private Long taskId;

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

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
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
}
