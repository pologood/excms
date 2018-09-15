package cn.lonsun.process.entity;

import cn.lonsun.core.base.entity.ABaseEntity;

import javax.persistence.*;

/**
 * 表单办理记录
 * Created by zhu124866 on 2015-12-23.
 */
@Entity
@Table(name="FORM_HANDLE_RECORD")
public class FormHandleRecordEO extends ABaseEntity {

    /** ID */
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="HANDLE_RECORD_ID")
    private Long handleRecordId;

    /** 流程表单 */
    @Column(name="PROCESS_FORM_ID")
    private Long processFormId;

    /** 办理活动名称 */
    @Column(name="ACTIVITY_NAME")
    private String activityName;

    /** 办理人 */
    @Column(name="HANDLE_PERSON_NAME")
    private String handlePersonName;

    /** 办理人单位ID */
    @Column(name="CREATE_UNIT_ID")
    private Long createUnitId;

    /** 办理人单位名称 */
    @Column(name="CREATE_UNIT_NAME")
    private String createUnitName;

    /** 办理任务ID */
    @Column(name="TASK_ID")
    private Long taskId;

    public Long getHandleRecordId() {
        return handleRecordId;
    }

    public void setHandleRecordId(Long handleRecordId) {
        this.handleRecordId = handleRecordId;
    }

    public Long getProcessFormId() {
        return processFormId;
    }

    public void setProcessFormId(Long processFormId) {
        this.processFormId = processFormId;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public Long getCreateUnitId() {
        return createUnitId;
    }

    public void setCreateUnitId(Long createUnitId) {
        this.createUnitId = createUnitId;
    }

    public String getCreateUnitName() {
        return createUnitName;
    }

    public void setCreateUnitName(String createUnitName) {
        this.createUnitName = createUnitName;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getHandlePersonName() {
        return handlePersonName;
    }

    public void setHandlePersonName(String handlePersonName) {
        this.handlePersonName = handlePersonName;
    }
}
