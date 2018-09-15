
package cn.lonsun.process.service;


import cn.lonsun.common.vo.ReceiverVO;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.process.vo.*;
import cn.lonsun.webservice.processEngine.enums.TaskStatus;
import cn.lonsun.webservice.processEngine.vo.*;

import java.util.List;
import java.util.Map;

/**
 * 自定义表单流程服务接口
 *@date 2014-12-15 15:27  <br/>
 *@author zhusy  <br/>
 *@version v1.0  <br/>
 */

public interface IProcessService {

    /**
     * 获取流程过程包列表
     * @param siteId
     * @param moduleCode
     * @return
     */
    List<PackageVO> getPackageList(Long siteId, String moduleCode);


    /**
     * 获取当前活动的后续活动
     *
     * @param userInfoVO
     * @param elementId
     * @return
     */
    List<ActivityVO> getNextActivities(UserInfoVO userInfoVO, Long elementId);

    /**
     * 获取当前活动的后续活动
     *
     * @param userInfoVO
     * @param elementId
     * @return
     */
    List<ActivityVO> getNextActivities(UserInfoVO userInfoVO,Map<String,Object> paramMap);


    List<ActivityControlVO> getNextActivityControls(UserInfoVO userInfoVO, Long elementId, Long taskId);

    /**
     * 流程启动服务
     *
     * @param userInfoVO
     * @param paramMap
     * @return
     */
    void processStart(UserInfoVO userInfoVO, Map<String, Object> paramMap) throws Exception;

    /**
     * 流程流转
     *
     * @param userInfoVO
     * @param paramMap
     * @return
     */
    int processRun(UserInfoVO userInfoVO, Map<String, Object> paramMap) throws Exception;

    /**
     * 流程办结
     * @param paramMap
     * @return
     */
    int processComplete(UserInfoVO userInfoVO,Map<String, Object> paramMap) throws Exception;


    /**
     * 获取流程待办任务分页列表
     * @param userInfoVO
     * @param swimlaneIds
     * @param queryVO
     * @return
     */
    Pagination getTaskPagination(UserInfoVO userInfoVO, Long[] swimlaneIds, TaskQueryVO queryVO);

    /**
     * 获取活动定义的可编辑字段
     *
     * @param userInfo
     * @param activityId
     * @return
     */
    FormFieldControlVO getEditableFields4Activity(UserInfoVO userInfo,Long activityId);



    /**
     * 获取当前活动功能按钮
     *
     * @param userInfo
     * @param activityId
     *@param  taskId
     * @return
     */
    List<Button> getButtons(UserInfoVO userInfo, Long activityId, Long taskId);

    /**
     * 获取下一步办理类型
     *
     * @param userInfoVO
     * @param taskId
     *        当前任务ID
     * @return (0: 下一步 1:办理完毕 100:办结)
     */
    Integer getNextStepType(UserInfoVO userInfoVO, Long taskId );
    /**
     * 获取下一步办理类型
     *
     * @param userInfoVO
     * @return (0: 下一步 1:办理完毕 100:办结)
     */
    Integer getNextStepType(UserInfoVO userInfoVO, Map<String,Object> paramMap);

    /**
     * 任务办理完毕
     * @param userInfoVO
     * @param paramMap
     * @return
     */
    int taskFinish(UserInfoVO userInfoVO, Map<String, Object> paramMap) throws Exception;



    /**
     * 获取已办流程按钮
     * @param userInfoVO
     * @param processId
     * @return
     */
    List<Button> getButtonsByTaskStatus(UserInfoVO userInfoVO, Long processId,TaskStatus taskStatus);

    /**
     * 获取流程
     * @param siteId
     * @param moduleCode
     * @return
     */
    List<ProcessVO> getProcessList(Long siteId,String moduleCode);

    /**
     * 获取手工登记按钮
     * @return
     */
    List<Button> getButtons4ManulRegister();

    /**
     * 获取登记办结查看表单按钮
     * @return
     */
    List<Button> getButtons4RegisterFinishViewForm();


    /**
     * 获取流程图
     * @param userInfoVO
     * @param processId
     * @param recordId
     * @param activityId
     * @return
     */
    DiagramVO getDiagram(UserInfoVO userInfoVO,Long processId, Long recordId,Long activityId);

    /**
     * 任务签收
     * @param userInfoVO
     * @param taskId
     * @return
     */
    Boolean taskSign(UserInfoVO userInfoVO, Long taskId);

    /**
     * 获取办理日志
     * @param userInfoVO
     * @param procInstId
     * @param recordId
     * @return
     */
    List<HistLogVO> getHistLogs(UserInfoVO userInfoVO, Long procInstId, Long recordId);

    /**
     * 获取退回列表操作项
     * @param userInfoVO
     * @param procInstId
     *@param taskId  @return
     */
    FallbackVO getFallbackOptions(UserInfoVO userInfoVO, Long procInstId, Long taskId);

    /**
     * 提交退回操作
     * @param userInfoVO
     * @param paramMap
     * @return
     */
    int fallBack(UserInfoVO userInfoVO,Map<String,Object> paramMap);

    /**
     * 保存流程终止操作
     * @param userInfoVO
     * @param procInstId
     * @param recordId
     * @param reason
     * @param moduleCode
     */
    void saveProcessTerminate(UserInfoVO userInfoVO, Long procInstId,Long recordId,String reason,String moduleCode);

    /**
     * 根据流程实例ID获取正在运行的任务列表
     * @param userInfoVO
     * @param procInstId
     * @return
     */
    List<TaskVO> getRuExecutionTaskList(UserInfoVO userInfoVO, Long procInstId);

    /**
     * 创建待阅任务
     *
     * @param user
     * @param taskId
     * @param assignees
     * @param moduleCode
     * @param smsRemind
     * @return
     */
    boolean createReadTasks(UserInfoVO user, Long taskId, AssigneeVO[] assignees, String moduleCode, Integer smsRemind);

    /**
     * 更新待阅收文为已阅状态
     * @param recordId
     * @param user
     * @param taskId
     * @param moduleCode
     * @param readAdvice
     * @return
     */
    boolean updateReadTask(Long recordId, UserInfoVO user, Long taskId, String moduleCode, String readAdvice);


    /**
     * 任务移交
     * @param userInfoVO
     * @param taskId
     * @param reason
     * @param assignee
     * @param isSms
     */
    int saveTransfer(UserInfoVO userInfoVO, String moduleCode, Long taskId, String reason, ReceiverVO assignee, Integer isSms);

    /**
     * 根据TaskId获取未办理的任务
     * @param userInfoVO
     * @param taskId
     * @return
     */
    TaskVO getRunTaskById(UserInfoVO userInfoVO,Long taskId);

    /**
     * 催办
     * @param userInfoVO
     * @param moduleCode
     * @param taskId
     * @param pressReason
     * @param isSms
     */
    void savePress(UserInfoVO userInfoVO, String moduleCode, Long taskId, String pressReason, Integer isSms);

    /**
     * 任务检出
     * @param userInfoVO
     * @param taskId
     * @return
     */
    Boolean claimTask(UserInfoVO userInfoVO, Long taskId);

    /**
     * 任务取消检出
     * @param userInfoVO
     * @param taskId
     * @return
     */
    Boolean unClaimTask(UserInfoVO userInfoVO, Long taskId);


    /**
     * 获取任务检出操作时显示的按钮
     * @param moduleCode
     * @return
     */
    List<Button> getClaimButtons(String moduleCode);

    /**
     * 获取指定活动下的任务
     * @param userInfoVO
     * @param procInstId
     * @param actinstId
     * @return
     */
    List<TaskVO> getActivityTaskList(UserInfoVO userInfoVO, Long procInstId, Long actinstId);


    /**
     * 获取指定流程的第一个活动
     * @param userInfo
     * @param processId
     * @return
     */
    ActivityVO getFirstActivity(UserInfoVO userInfo,Long processId);

    /**
     * 获取指定人指定条数的待办任务
     * @param userInfo
     * @param queryTask
     * @param roleIds
     * @param limit
     * @return
     */
    List<TaskVO> getWaitTaskList(UserInfoVO userInfo,QueryTask queryTask,Long[] roleIds,int limit);


    /**
     * 根据流程实例ID获取当前流程下所有的阅件任务
     * @param userInfo
     * @param procInstId
     * @return
     */
    List<TaskVO> getReaderTaskList(UserInfoVO userInfo, Long procInstId);

    /**
     * 获取快速发文查看表单按钮
     * @return
     */
    List<Button> getQuickSendViewButtons();

    /**
     * 获取快速发文可编辑字段
     * @return
     */
    FormFieldControlVO getQuickSendEditableFields();

    /**
     * 获取快速发文可编辑按钮
     * @return
     */
    List<Button> getQuickSendButtons();

    /**
     * 获取手工登记可编辑字段
     * @return
     */
    FormFieldControlVO getManulRegisterEditableFields();

    /**
     * 保存表单数据
     * @param userInfoVO
     * @param paramMap
     */
    Map<String,Object> saveFormData(UserInfoVO userInfoVO, Map<String, Object> paramMap) throws Exception;

    /**
     * 判断任务是否办理完毕
     * @param userInfoVO
     * @param taskId
     * @return
     */
    boolean getTaskIsComplete(UserInfoVO userInfoVO, Long taskId);

    /**
     * 流程自动流转到发起人
     * @return
     */
    void processAutoToOwner(UserInfoVO userInfoVO, Map<String,Object> paramMap) throws Exception;

    /**
     * 获取自动流转至发起人时可操作按钮
     * @param moduleCode
     * @param taskId
     * @param userInfo
     * @return
     */
    List<Button> getAutoToOwnerButtons(String moduleCode, Long taskId, UserInfoVO userInfo);

    /**
     * 获取活动定义
     * @param userInfoVO
     * @param activityId
     * @return
     */
    ActivityVO getActivity(UserInfoVO userInfoVO, Long activityId);

    /**
     * 获取下一步默认办理人
     * @param userInfoVO
     * @param activityId
     * @param taskId
     * @return
     */
    Map<String,Object> getDefaultHandler4NextActivity(UserInfoVO userInfoVO, Long activityId, Long taskId);

    /**
     * 获取流程发起人
     * @param userInfoVO
     * @param taskId
     * @return
     */
    ReceiverVO getProcessStarter(UserInfoVO userInfoVO, Long taskId);

    /**
     * 删除待办任务
     * @param recordId
     */
    void deleteTaskByRecordId(Long recordId);

    /**
     * 获取活动字段控制
     * @param userInfoVO
     * @param activityId
     * @return
     */
    ActivityFieldControlVO getActivityFieldControl(UserInfoVO userInfoVO, Long activityId);


    List<ActivityControlVO> getNextActivityControls(UserInfoVO userInfoVO, Map<String, Object> paramMap);

    Map<String,Object> getDefaultHandler4NextActivity(UserInfoVO userInfoVO, Map<String, Object> paramMap);

    List<Button> getButtons(UserInfoVO userInfoVO, Long activityId, Long taskId, Map<String, Object> formData);

    List<Button> getAutoToOwnerButtons(String moduleCode, Long taskId, UserInfoVO userInfoVO, Map<String, Object> formData);

    /**
     * 获取流程启动数据
     * @param processVO
     * @return
     */
    ProcessStartVO getProcessStartData(UserInfoVO userInfo,ProcessVO processVO);
}
