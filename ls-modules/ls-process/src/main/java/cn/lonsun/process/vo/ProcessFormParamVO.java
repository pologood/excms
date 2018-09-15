package cn.lonsun.process.vo;

import cn.lonsun.base.ProcessBusinessType;
import cn.lonsun.common.util.AppUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * 流程表单参数VO
 * Created by zhu124866 on 2015-12-18.
 */
public class ProcessFormParamVO {

    private Long processFormId;//表单办理ID

    private String moduleCode;//流程模块

    private String processName;//流程名称

    private Long processId;//流程主键

    private Long taskId;//任务主键

    private Integer startFlow;//开始流程标识

    private Integer viewForm;//查看表单标识

    private String processBusinessType;//流程业务类型

    private Long dataId;//数据ID

    private Long columnId;//栏目ID

    public Long getProcessId() {
        return processId;
    }

    public void setProcessId(Long processId) {
        this.processId = processId;
    }

    public Integer getViewForm() {
        return viewForm;
    }

    public void setViewForm(Integer viewForm) {
        this.viewForm = viewForm;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getModuleCode() {
        return moduleCode;
    }

    public void setModuleCode(String moduleCode) {
        this.moduleCode = moduleCode;
    }

    public Integer getStartFlow() {
        return startFlow;
    }

    public void setStartFlow(Integer startFlow) {
        this.startFlow = startFlow;
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        if(!AppUtil.isEmpty(processName)){
            try {
                processName =  URLDecoder.decode(processName, "UTF-8");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        this.processName = processName;
    }

    public Long getProcessFormId() {
        return processFormId;
    }

    public void setProcessFormId(Long processFormId) {
        this.processFormId = processFormId;
    }

    public String getProcessBusinessType() {
        return processBusinessType;
    }

    public ProcessBusinessType getProcessBusinessTypeEnum() {
        ProcessBusinessType processBusinessTypeEnum = null;
        if(!AppUtil.isEmpty(processBusinessType)){
            try {
                processBusinessTypeEnum = ProcessBusinessType.valueOf(processBusinessType);
            }catch (Exception e){e.printStackTrace();}

        }
        return processBusinessTypeEnum;
    }

    public void setProcessBusinessType(String processBusinessType) {
        this.processBusinessType = processBusinessType;
    }

    public Long getDataId() {
        return dataId;
    }

    public void setDataId(Long dataId) {
        this.dataId = dataId;
    }


    public Long getColumnId() {
        return columnId;
    }

    public void setColumnId(Long columnId) {
        this.columnId = columnId;
    }
}
