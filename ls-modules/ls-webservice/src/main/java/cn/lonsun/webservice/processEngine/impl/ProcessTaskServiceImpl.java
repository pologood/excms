/*
 * 2014-12-16 <br/>
 * 
 */
package cn.lonsun.webservice.processEngine.impl;

import cn.lonsun.core.util.Pagination;
import cn.lonsun.webservice.processEngine.ProcessTaskService;
import cn.lonsun.webservice.processEngine.enums.EFieldControlType;
import cn.lonsun.webservice.processEngine.enums.TaskStatus;
import cn.lonsun.webservice.processEngine.util.AppValueUtil;
import cn.lonsun.webservice.processEngine.util.EngineUtil;
import cn.lonsun.webservice.processEngine.util.WSClient;
import cn.lonsun.webservice.processEngine.vo.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/***
 * 流程任务服务
 */
@Service("processTaskService")
public class ProcessTaskServiceImpl implements ProcessTaskService {
    private enum Codes {
        process_pageQueryRuTask,
        process_getActivityButtons,
        process_getTaskByUser,
        process_complete,
        process_getFieldControls,
        process_getSuccessorTypeByTaskId,
        process_signTask,
        process_getRuTaskById,
        process_getTaskListByUser,
        process_autoSerialComplete,
        process_deleteRuTaskByRecordId,
        process_getSuccessorTypeByTaskIdByForm
    }
    @Override
    public List<TaskVO> pageQueryRuTask(UserInfoVO userInfo, Pagination pagination) {
        return (List<TaskVO>)WSClient.getList(Codes.process_pageQueryRuTask.name(),
                new Object[]{
                        userInfo,
                        pagination
                },
                TaskVO.class
                );
    }

    @Override
    public List<Button> getActivityOpertions(UserInfoVO userInfo, Long activityId,TaskStatus taskStatus) {
        return (List<Button>) WSClient.getList(Codes.process_getActivityButtons.name(),
                new Object[]{
                        userInfo,
                        activityId,
                        taskStatus
                },
                Button.class
        );
    }
    @Override
    public Pagination getTaskByUser (UserInfoVO userInfo,Pagination pagination, QueryTask queryTask,long assigneeId, Long[] swimlaneIds){
        Pagination rest =   (Pagination)WSClient.getObject(Codes.process_getTaskByUser.name(),
                new Object[]{
                        userInfo,
                        pagination,
                        queryTask,
                        assigneeId,
                        swimlaneIds
                },
                Pagination.class
        );

        //data数解包
        List _list = rest.getData();
        for(Object obj:_list){
            if(obj instanceof  Map){
                Map map = (Map)obj;
                if(null !=map.get("data")){
                   map.putAll(EngineUtil.parseData((String)map.get("data")));
                   map.remove("data");
                }
            }
        }

        return rest;
    }
    @Override
    public List<TaskVO> getTaskListByUser (UserInfoVO userInfo,QueryTask queryTask,long assigneeId, int size,Long[] roleIds){
        List<TaskVO> rest =   (List)WSClient.getList(Codes.process_getTaskListByUser.name(),
                new Object[]{
                        userInfo,
                        queryTask,
                        assigneeId,
                        size,
                        roleIds
                },
                TaskVO.class
        );
        return rest;
    }
    @Override
    public List<TaskVO> complete(UserInfoVO userInfo,Long taskId,TaskHandleParam taskHandleParam){
        return (List<TaskVO>)WSClient.getList(Codes.process_complete.name(),
                new Object[]{
                        userInfo,
                        taskId,
                        taskHandleParam
                },
                TaskVO.class
        );
    }
    @Override
    public List<FieldVO> getFieldControls(UserInfoVO userInfo,Long activityId,EFieldControlType[] fieldControlTypes){
        return ( List<FieldVO>)WSClient.getList(Codes.process_getFieldControls.name(),
                new Object[]{
                        userInfo,
                        activityId,
                        fieldControlTypes
                },
                FieldVO.class
                );
    }
    @Override
    public Integer getSuccessorTypeByTaskId(UserInfoVO userInfo,Long taskId){
        return (Integer)WSClient.getObject(Codes.process_getSuccessorTypeByTaskId.name(),
                new Object[]{
                        userInfo,
                        taskId
                },
                Integer.class
                );
    }
    @Override
    public Integer getSuccessorTypeByTaskIdByForm(UserInfoVO userInfo,Long taskId,Map<String,Object> formData){
        FormDataVO formDataVO = new FormDataVO();
        formDataVO.setTaskId(taskId);
        //TODO reqMap的值数组转为非数组 modify 2016-3-29 @roc
        formData = AppValueUtil.copyIndex0ByMap(formData);
        if(null == taskId || taskId<1) return Integer.valueOf(0);
        formDataVO.setData(formData);
        return (Integer)WSClient.getObject(Codes.process_getSuccessorTypeByTaskIdByForm.name(),
                new Object[]{
                        userInfo,
                        formDataVO
                },
                Integer.class
        );
    }
    @Override
    public Boolean signTask(UserInfoVO userInfo,Long taskId){
        return (Boolean) WSClient.getObject(Codes.process_signTask.name(),
                new Object[]{
                        userInfo,
                        taskId
                },
                Boolean.class
                );
    }
    @Override
    public TaskVO getRuTaskById(UserInfoVO userInfo,Long taskId){
      return (TaskVO) WSClient.getObject(Codes.process_getRuTaskById.name(),
                new Object[]{
                        userInfo,
                        taskId
                },
                TaskVO.class
        );
    }
    @Override
    public List<TaskVO> autoSerialComplete(UserInfoVO userInfo,Long taskId,TaskHandleParam taskHandleParam){
        return (List<TaskVO>) WSClient.getList(Codes.process_autoSerialComplete.name(),
                new Object[]{
                        userInfo,
                        taskId,
                        taskHandleParam
                },
                TaskVO.class
        );
    }
    @Override
    public Boolean deleteRuTaskByRecordId(UserInfoVO userInfo,Long recordId){
        return (Boolean) WSClient.getObject(Codes.process_deleteRuTaskByRecordId.name(),
                new Object[]{
                        userInfo,
                        recordId
                },
                Boolean.class
        );
    }
}
