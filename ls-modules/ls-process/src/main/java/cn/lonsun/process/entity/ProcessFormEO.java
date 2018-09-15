package cn.lonsun.process.entity;

import cn.lonsun.base.ProcessBusinessType;
import cn.lonsun.core.base.entity.ABaseEntity;

import javax.persistence.*;

/**
 * 流程表单EO
 * Created by zhusy on 2015-12-18.
 */
@Entity
@Table(name="FORM_PROCESS_FORM")
public class ProcessFormEO extends ABaseEntity{

    /** ID */
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="PROCESS_FORM_ID")
    private Long processFormId;

    /** 模块编码 */
    @Column(name="MODULE_CODE")
    private String moduleCode;

    /** 流程定义ID */
    @Column(name="PROCESS_ID")
    private Long processId;

    /** 流程名称 */
    @Column(name="PROCESS_NAME")
    private String processName;

    /** 流程实例ID */
    @Column(name="PROC_INST_ID")
    private Long procInstId;

    /** 当前活动实例ID */
    @Column(name="CUR_ACTINST_ID")
    private Long curActinstId;

    /** 当前活动名称 */
    @Column(name="CUR_ACTIVITY_NAME")
    private String curActivityName;

    /** 流程业务业务类型 */
    @Column(name="PROCESS_BUSINESS_TYPE")
    @Enumerated(EnumType.ORDINAL)
    private ProcessBusinessType processBusinessType;

    /** 数据记录ID */
    @Column(name="DATA_ID")
    private Long dataId;

    /** 标题 */
    @Column(name="TITLE")
    private String title;

    /** 表单办理状态 */
    @Column(name="FORM_STATUS")
    private Integer formStatus;

    /** 创建人姓名 */
    @Column(name="CREATE_PERSON_NAME")
    private String createPersonName;

    /** 创建人单位ID */
    @Column(name="CREATE_UNIT_ID")
    private Long createUnitId;

    /** 栏目ID */
    @Column(name="COLUMN_ID")
    private Long columnId;

    /** 栏目名称 */
    @Column(name="COLUMN_NAME")
    private String columnName;

    public Long getProcessFormId() {
        return processFormId;
    }

    public void setProcessFormId(Long processFormId) {
        this.processFormId = processFormId;
    }

    public String getModuleCode() {
        return moduleCode;
    }

    public void setModuleCode(String moduleCode) {
        this.moduleCode = moduleCode;
    }

    public Long getProcessId() {
        return processId;
    }

    public void setProcessId(Long processId) {
        this.processId = processId;
    }

    public Long getProcInstId() {
        return procInstId;
    }

    public void setProcInstId(Long procInstId) {
        this.procInstId = procInstId;
    }

    public Long getCurActinstId() {
        return curActinstId;
    }

    public void setCurActinstId(Long curActinstId) {
        this.curActinstId = curActinstId;
    }

    public String getCurActivityName() {
        return curActivityName;
    }

    public void setCurActivityName(String curActivityName) {
        this.curActivityName = curActivityName;
    }

    public ProcessBusinessType getProcessBusinessType() {
        return processBusinessType;
    }

    public void setProcessBusinessType(ProcessBusinessType processBusinessType) {
        this.processBusinessType = processBusinessType;
    }

    public Long getDataId() {
        return dataId;
    }

    public void setDataId(Long dataId) {
        this.dataId = dataId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getFormStatus() {
        return formStatus;
    }

    public void setFormStatus(Integer formStatus) {
        this.formStatus = formStatus;
    }

    public String getCreatePersonName() {
        return createPersonName;
    }

    public void setCreatePersonName(String createPersonName) {
        this.createPersonName = createPersonName;
    }

    public Long getCreateUnitId() {
        return createUnitId;
    }

    public void setCreateUnitId(Long createUnitId) {
        this.createUnitId = createUnitId;
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public Long getColumnId() {
        return columnId;
    }

    public void setColumnId(Long columnId) {
        this.columnId = columnId;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }
}
