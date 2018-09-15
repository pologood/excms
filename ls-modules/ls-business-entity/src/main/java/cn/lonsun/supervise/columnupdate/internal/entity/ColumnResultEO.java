package cn.lonsun.supervise.columnupdate.internal.entity;

import cn.lonsun.core.base.entity.AMockEntity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author gu.fei
 * @version 2015-5-14 8:23
 */
@Entity
@Table(name="CMS_SUPERVISE_COLUMN_RESULT")
public class ColumnResultEO extends AMockEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id; //主键ID

    @Column(name = "P_ID")
    private Long pId; //父栏目ID

    @Column(name = "TASK_TYPE")
    private String taskType; //任务类型

    @Column(name = "TASK_ID")
    private Long taskId; //任务ID

    @Column(name = "COLUMN_ID")
    private Long columnId; //任务ID

    @Column(name = "CSITE_ID")
    private Long cSiteId;

    @Column(name = "COLUMN_NAME")
    private String columnName; //栏目名称

    @Column(name = "UPDATE_NUM")
    private Long updateNum; //栏目更新数量

    @Column(name = "PLAN_NUM")
    private Long planNum; //栏目计划更新数量

    @Column(name = "GUEST_NUM")
    private Long guestNum; //未回复留言数量

    @Transient
    private String siteName; //站点名称

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getpId() {
        return pId;
    }

    public void setpId(Long pId) {
        this.pId = pId;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Long getColumnId() {
        return columnId;
    }

    public void setColumnId(Long columnId) {
        this.columnId = columnId;
    }

    public Long getcSiteId() {
        return cSiteId;
    }

    public void setcSiteId(Long cSiteId) {
        this.cSiteId = cSiteId;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public Long getUpdateNum() {
        return updateNum;
    }

    public void setUpdateNum(Long updateNum) {
        this.updateNum = updateNum;
    }

    public Long getPlanNum() {
        return planNum;
    }

    public void setPlanNum(Long planNum) {
        this.planNum = planNum;
    }

    public Long getGuestNum() {
        return guestNum;
    }

    public void setGuestNum(Long guestNum) {
        this.guestNum = guestNum;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }
}