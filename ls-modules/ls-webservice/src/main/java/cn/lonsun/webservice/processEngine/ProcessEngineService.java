package cn.lonsun.webservice.processEngine;

import cn.lonsun.webservice.processEngine.enums.EFieldControlType;
import cn.lonsun.webservice.processEngine.enums.TaskStatus;
import cn.lonsun.webservice.processEngine.vo.*;

import java.util.List;
import java.util.Map;

/**
 * Created by lonsun on 2014/12/12.
 */
public interface ProcessEngineService {

    /**
     *  查询流程类别及流程定义列表
     * @param unitId
     * @param moduleCode
     * @return
     *
     * @Deprecated 改用getPackageProcessByThird
     */
     @Deprecated
     public List<ProcessVO> getPackageProcess(Long unitId, String moduleCode);

    /**
     * 支持外部引用
     * @param thirdId
     * @param moduleCode
     * @return
     */
     public List<ProcessVO> getPackageProcessByThird(Long thirdId, String moduleCode);

    /**
     *  查询开始活动
     * @param userInfo
     * @param processId
     * @return
     */
     public ActivityVO getStartEventByProcessId(UserInfoVO userInfo, Long processId);

    /**
     * 查询结束活动
     * @param userInfo
     * @param processId
     * @return
     */
    public ActivityVO getEndEventByProcessId(UserInfoVO userInfo, Long processId);

    /***
     * 查询后继活动列表
     * @param userInfo
     * @param elementId
     * @return
     */
    public List<ActivityVO> getSuccessorActivities(UserInfoVO userInfo, Long elementId);

    /***
     * 查询后继活动列表
     * @param userInfo
     * @param elementId
     * @return
     */
    public List<ActivityVO> getSuccessorActivitiesByForm(UserInfoVO userInfo, Long elementId, Map<String, Object> formData);

    /**
     * 查询启动流程的后续活动
     * @param userInfo
     * @param processId
     * @return
     */
    public List<ActivityVO> getSuccessorActivitiesByProcessId(UserInfoVO userInfo, Long processId);

    /**
     * 根据流程定义ID启动流程
     * @param userInfo
     * @param processId
     * @param startProcessParam
     * @return
     */
    public List<TaskVO> startProcessInstanceById(UserInfoVO userInfo, Long processId, StartProcessParam startProcessParam);

    /***
     * 根据流程定义ID启动流程（跳过启动每一个节点）
     * @param userInfo
     * @param processId
     * @param startProcessParam
     * @return
     */
    public List<TaskVO> startProcessInstanceByIdOmitFristActi(UserInfoVO userInfo, Long processId, StartProcessParam startProcessParam);
    /**
     * 启动流程操作按扭
     * @param userInfo
     * @param processId
     * @return
     */
    public List<Button> getActivityOpertions(UserInfoVO userInfo, Long processId, TaskStatus taskStatus);
    /**
     * 表单可编辑字段
     * @param userInfo
     * @param processId
     * @param fieldControlTypes
     * @return
     */
    public List<FieldVO> getFirstFieldControls(UserInfoVO userInfo, Long processId, EFieldControlType[] fieldControlTypes);

    /**
     * 查询外部Class名称
     * @param userInfo
     * @param activityId
     * @return
     */
    public String getExternalClassById(UserInfoVO userInfo, Long activityId);

    /**
     * 获取开始活动节点对象
     * @param userInfo
     * @param processId
     * @return
     */
    public ActivityVO getFirstActivity(UserInfoVO userInfo, Long processId);

    /**
     * 获取活动定义属性
     * @param userInfo
     * @param activityId
     * @return
     */
    public ActivityVO getActivity(UserInfoVO userInfo, Long activityId);

    public Boolean updateReProcessForFormVer(UserInfoVO userInfo, Long formId, Integer formVer);

    /**
     * 查询流程普通活动列表(排除开始，结束，事件、网关来型)
     * @param userInfo
     * @param processId
     * @return
     */
    public List<ActivityVO> getActivitiesByProcessId(UserInfoVO userInfo, Long processId);
}
