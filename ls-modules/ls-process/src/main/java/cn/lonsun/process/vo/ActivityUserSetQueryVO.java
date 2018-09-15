package cn.lonsun.process.vo;

import cn.lonsun.common.vo.PageQueryVO;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * Created by liuk on 2017-03-22.
 */
public class ActivityUserSetQueryVO extends PageQueryVO {

    /** 流程定义ID */
    private Long processId;

    /** 活动id */
    private Long activityId;

    /** 站点ID */
    private Long siteId;

    /** 活动办理人员名称 */
    private String userName;

    /** 活动办理人员id */
    private String userId;


    public Long getProcessId() {
        return processId;
    }

    public void setProcessId(Long processId) {
        this.processId = processId;
    }

    public Long getActivityId() {
        return activityId;
    }

    public void setActivityId(Long activityId) {
        this.activityId = activityId;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
