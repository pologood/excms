package cn.lonsun.webservice.processEngine.vo;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
 * Created by lonsun on 2014/12/15.
 *
 * 流程模板定义
 */
public class ProcessVO {
    /** 主键 */
    private Long processId;
    /** 模块ID */
    private Long moduleId;
    /**所属类别*/
    private Long packageId;
    /**所属类别名称*/
    private String packageName;
    /**流程名称*/
    private String name;
    /**流程排序*/
    private Long order = 100L;
    private Long packageOrder=0L;
    /**流程排序*/
    private Integer version = Integer.valueOf(1);
    /**状态
     * disable - 禁用
     * enable - 启用
     * */
    private String state;
    /**
     * 是否能归档
     *disable - 禁用
     *enable 启用
     * */
    private String canArchive;
    /**
     * 创建人组织ID
     */
    private Long createOrgId;
    /**
     * 创建人ID
     */
    private Long createUserId;
    /**
     * 版本ID
     */
    private Long versionId;
    /**
     * 流程表单类型
     */
    private String formType;
    /**
     * 流程启动表单ID
     */
    private Long formId;
    /**
     * 流程启动链接
     */
    private String formFristHref;
    /**
     * 流程流转链接
     */
    private String formEditHref;
    /**
     * 标题
     */
    private String title;
    /**
     * 流程描述
     */
    private String desc;
    /**
     * 开始活动主键
     */
    private Long activityId ;
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private Date create;

    private String formTitle;

    public Long getProcessId() {
        return processId;
    }

    public Long getModuleId() {
        return moduleId;
    }

    public void setModuleId(Long moduleId) {
        this.moduleId = moduleId;
    }

    public Long getPackageId() {
        return packageId;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getName() {
        return name;
    }

    public Long getOrder() {
        return order;
    }

    public Integer getVersion() {
        return version;
    }

    public String getState() {
        return state;
    }

    public String getCanArchive() {
        return canArchive;
    }

    public Long getCreateOrgId() {
        return createOrgId;
    }

    public Long getCreateUserId() {
        return createUserId;
    }

    public Long getVersionId() {
        return versionId;
    }

    public String getFormType() {
        return formType;
    }

    public Long getFormId() {
        return formId;
    }

    public String getFormFristHref() {
        return formFristHref;
    }

    public String getFormEditHref() {
        return formEditHref;
    }

    public String getTitle() {
        return title;
    }

    public String getDesc() {
        return desc;
    }

    public Date getCreate() {
        return create;
    }

    public void setProcessId(Long processId) {
        this.processId = processId;
    }

    public void setPackageId(Long packageId) {
        this.packageId = packageId;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOrder(Long order) {
        this.order = order;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setCanArchive(String canArchive) {
        this.canArchive = canArchive;
    }

    public void setCreateOrgId(Long createOrgId) {
        this.createOrgId = createOrgId;
    }

    public void setCreateUserId(Long createUserId) {
        this.createUserId = createUserId;
    }

    public void setVersionId(Long versionId) {
        this.versionId = versionId;
    }

    public void setFormType(String formType) {
        this.formType = formType;
    }

    public void setFormId(Long formId) {
        this.formId = formId;
    }

    public void setFormFristHref(String formFristHref) {
        this.formFristHref = formFristHref;
    }

    public void setFormEditHref(String formEditHref) {
        this.formEditHref = formEditHref;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setCreate(Date create) {
        this.create = create;
    }

    public Long getActivityId() {
        return activityId;
    }

    public void setActivityId(Long activityId) {
        this.activityId = activityId;
    }

    public String getFormTitle() {
        return formTitle;
    }

    public void setFormTitle(String formTitle) {
        this.formTitle = formTitle;
    }

    public Long getPackageOrder() {
        return packageOrder;
    }

    public void setPackageOrder(Long packageOrder) {
        this.packageOrder = packageOrder;
    }
}
