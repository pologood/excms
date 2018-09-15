package cn.lonsun.process.vo;

import cn.lonsun.base.ProcessBusinessType;
import cn.lonsun.common.fileupload.internal.enums.CaseType;

import cn.lonsun.process.entity.ProcessFormEO;
import cn.lonsun.webservice.processEngine.vo.Button;

import java.util.List;

/**
 * 转发至流程表单数据VO
 * Created by zhu124866 on 2015-12-18.
 */
public class ProcessFormForwardDataVO {

    private ProcessFormParamVO param;//请求参数

    private ProcessFormEO processForm;//表单办理对象

    private Integer needSign;//是否需要签收

    private ActivityFieldControlVO activityFieldControl;//活动字段控制

    private List<Button> buttons;//可操作按钮

    private Long activityId;//当前活动ID

    private String activityName;//当前活动名称

    private Integer autoToOwner;//自动流转至发起人标识

    private String caseType = "";

    private String formUrl;//表单Url


    public ProcessFormParamVO getParam() {
        return param;
    }

    public void setParam(ProcessFormParamVO param) {
        this.param = param;
    }

    public Integer getNeedSign() {
        return needSign;
    }

    public void setNeedSign(Integer needSign) {
        this.needSign = needSign;
    }

    public List<Button> getButtons() {
        return buttons;
    }

    public void setButtons(List<Button> buttons) {
        this.buttons = buttons;
    }

    public Long getActivityId() {
        return activityId;
    }

    public void setActivityId(Long activityId) {
        this.activityId = activityId;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public Integer getAutoToOwner() {
        return autoToOwner;
    }

    public void setAutoToOwner(Integer autoToOwner) {
        this.autoToOwner = autoToOwner;
    }

    public ActivityFieldControlVO getActivityFieldControl() {
        return activityFieldControl;
    }

    public void setActivityFieldControl(ActivityFieldControlVO activityFieldControl) {
        this.activityFieldControl = activityFieldControl;
    }

    public ProcessFormEO getProcessForm() {
        return processForm;
    }

    public void setProcessForm(ProcessFormEO processForm) {
        this.processForm = processForm;
    }

    public String getCaseType() {
        return caseType;
    }

    public void setCaseType(String caseType) {
        this.caseType = caseType;
    }

    public String getFormUrl() {
        if(null == formUrl && null != processForm && null != processForm.getProcessBusinessType()){
            formUrl = processForm.getProcessBusinessType().getFormUrl();
        }
        return formUrl;
    }
}
