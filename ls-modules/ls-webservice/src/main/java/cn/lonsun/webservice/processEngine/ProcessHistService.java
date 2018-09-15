package cn.lonsun.webservice.processEngine;

import cn.lonsun.webservice.processEngine.vo.*;

import java.util.List;

/**
 * Created by lonsun on 2014/12/24.
 */
public interface ProcessHistService {
    /**
     * 流程记录
     * @param userInfo
     * @param proinstId  String 流程实例ID
     * @param recordId
     * @return
     */
    public List<HistLogVO> getHistLogByRecordId(UserInfoVO userInfo,Long proinstId,Long recordId);

    /**
     * 流程图查看描述XML
     * @param userInfo
     * @param processId
     * @param recordId
     * @return
     */
    public DiagramVO getDiagram(UserInfoVO userInfo,Long processId,Long recordId,Long curActivityId);

    /**
     * 选择退回
     * @param userInfo
     * @param proinstId
     * @param taskId
     * @return
     */
    public FallbackVO getFallbackOptions(UserInfoVO userInfo,Long proinstId,Long taskId);

    /**
     * 退回执行操作
     * @param userInfo
     * @param actinstId
     * @param taskId
     * @return
     */
    public List<TaskVO> fallbackTask(UserInfoVO userInfo,Long actinstId,Long taskId,String logDescr);

    /**
     * 委托办理操作
     * @param userInfo
     * @param taskId
     * @param delegateUserInfo
     * @return
     */
    public TaskVO delegateTask(UserInfoVO userInfo,Long taskId, UserInfoVO delegateUserInfo,String logDescr);

    /**
     * 派发阅件
     * @param userInfo
     * @param taskId
     * @param taskReaders
     * @return
     */
    public List<TaskVO> startReaderTask(UserInfoVO userInfo,Long taskId,AssigneeVO[] taskReaders);

    /**
     * 根据流程实例查询阅件任务列表
     * @param userInfo
     * @param procInstId
     * @return
     */
    public List<TaskVO> getReaderTaskByProcInstId(UserInfoVO userInfo,Long procInstId);
    /**
     * 阅件办理
     * @param userInfo
     * @param taskId
     * @return
     */
    public Boolean completeReaderTask(UserInfoVO userInfo,Long taskId,String logDescr);

    /**
     * 角色任务检出操作
     * @param userInfo
     * @param taskId
     * @return
     */
    public Boolean claimTask(UserInfoVO userInfo,Long taskId);

    /**
     * 角色任务取出检出操作
     * @param userInfo
     * @param taskId
     * @return
     */
    public Boolean unclaimTask(UserInfoVO userInfo,Long taskId);

    /***
     * 根据活动实例查询任务
     * @param userInfo
     * @param procInstId
     * @param actInstId
     * @return
     */
    public List<TaskVO> getHistTaskByActInstId(UserInfoVO userInfo,Long procInstId,Long actInstId);
}
