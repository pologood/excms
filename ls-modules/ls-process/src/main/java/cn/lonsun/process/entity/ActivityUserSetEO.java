package cn.lonsun.process.entity;

import cn.lonsun.base.ProcessBusinessType;
import cn.lonsun.core.base.entity.ABaseEntity;

import javax.persistence.*;

/**
 * 流程表单EO
 * Created by liuk on 2017-03-22.
 */
@Entity
@Table(name="ACTIVITY_USER_SET")
public class ActivityUserSetEO extends ABaseEntity{

    /** ID */
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="ID")
    private Long id;

    /** 流程定义ID */
    @Column(name="PROCESS_ID")
    private Long processId;

    /** 活动id */
    @Column(name="ACTIVITY_ID")
    private Long activityId;

    /** 站点ID */
    @Column(name="SITE_ID")
    private Long siteId;

    /** 活动办理人员名称 */
    @Column(name="USER_NAMES")
    private String userNames;

    /** 活动办理人员id */
    @Column(name="USER_IDS")
    private String userIds;

    /** 活动办理部门ids */
    @Column(name="ORGAN_IDS")
    private String organIds;

    /** 活动办理单位ids */
    @Column(name="UNIT_IDS")
    private String unitIds;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getUserNames() {
        return userNames;
    }

    public void setUserNames(String userNames) {
        this.userNames = userNames;
    }

    public String getUserIds() {
        return userIds;
    }

    public void setUserIds(String userIds) {
        this.userIds = userIds;
    }

    public String getOrganIds() {
        return organIds;
    }

    public void setOrganIds(String organIds) {
        this.organIds = organIds;
    }

    public String getUnitIds() {
        return unitIds;
    }

    public void setUnitIds(String unitIds) {
        this.unitIds = unitIds;
    }
}
