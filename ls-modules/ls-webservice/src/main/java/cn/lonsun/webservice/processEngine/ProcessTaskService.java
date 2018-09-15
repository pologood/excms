/*
 * 2014-12-16 <br/>
 * 
 */
package cn.lonsun.webservice.processEngine;

import cn.lonsun.core.util.Pagination;
import cn.lonsun.webservice.processEngine.enums.EFieldControlType;
import cn.lonsun.webservice.processEngine.enums.TaskStatus;
import cn.lonsun.webservice.processEngine.vo.*;

import java.util.List;
import java.util.Map;

/**
 * 流程任务服务
 */
public interface ProcessTaskService {
    /**
     * 创建当前运行任务查询对象，此查询会过滤掉串办中隐藏的任务
     * @param userInfo
     * @param pagination
     * @return
     */
    public List<TaskVO> pageQueryRuTask(UserInfoVO userInfo, Pagination pagination);

    /**
     * 查询活动操作按扭
     * @param userInfo
     * @param activityId
     * @return
     */
    public List<Button> getActivityOpertions(UserInfoVO userInfo, Long activityId,TaskStatus taskStatus);

    /**
     * 待办任务列表
     * @param userInfo
     * @param pagination
     * @param  queryTask
     * @param assigneeId
     * @param swimlaneIds
     * @return
     */
    public Pagination getTaskByUser (UserInfoVO userInfo,Pagination pagination, QueryTask queryTask,long assigneeId, Long[] swimlaneIds);

    /**
     * 待办任务列表
     * @param userInfo
     * @param queryTask
     * @param assigneeId
     * @param size
     * @param roleIds
     * @return
     */
    public List<TaskVO> getTaskListByUser (UserInfoVO userInfo,QueryTask queryTask,long assigneeId,int size, Long[] roleIds);
    /**
     * 任务处理
     * <b>注意，在此操作中，如果gateway类型的节点中设置了流转条件，则必须要传入相应的参数，否则下一步活动/任务无法执行</b>
     * @param userInfo
     * @param taskId
     * @param taskHandleParam
     */
    public List<TaskVO> complete(UserInfoVO userInfo,Long taskId,TaskHandleParam taskHandleParam);

    /**
     * 表单可编辑字段
     * @param userInfo
     * @param activityId
     * @param fieldControlTypes
     * @return
     */
    public List<FieldVO> getFieldControls(UserInfoVO userInfo,Long activityId,EFieldControlType[] fieldControlTypes);

    /**
     * 后继活动类型
     * @param userInfo
     * @param taskId
     * @return
     */
    public Integer getSuccessorTypeByTaskId(UserInfoVO userInfo,Long taskId);

    /**
     * 后继活动类型(支持路由)
     * @param userInfo
     * @param taskId
     * @param formData
     * @return
     */
    public Integer getSuccessorTypeByTaskIdByForm(UserInfoVO userInfo,Long taskId,Map<String,Object> formData);

    /**
     *  任务签收
     * @param userInfo
     * @param taskId
     * @return
     */
    public Boolean signTask(UserInfoVO userInfo,Long taskId);

    /***
     * 任务详细
     * @param userInfo
     * @param taskId
     * @return
     */
    public TaskVO getRuTaskById(UserInfoVO userInfo,Long taskId);

    /**
     * 办理完毕，串联派发给taskAssigneeList用户
     * @param userInfo
     * @param taskId
     * @param taskHandleParam
     * @return
     */
    public List<TaskVO> autoSerialComplete(UserInfoVO userInfo,Long taskId,TaskHandleParam taskHandleParam);

    /**
     * 根据业务实例ID删除待处理任务
     * @param userInfo
     * @param recordId
     * @return
     */
    public Boolean deleteRuTaskByRecordId(UserInfoVO userInfo,Long recordId);
}
