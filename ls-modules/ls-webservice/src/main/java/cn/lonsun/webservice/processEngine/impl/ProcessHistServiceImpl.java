package cn.lonsun.webservice.processEngine.impl;

import cn.lonsun.webservice.processEngine.ProcessHistService;
import cn.lonsun.webservice.processEngine.util.WSClient;
import cn.lonsun.webservice.processEngine.vo.*;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by lonsun on 2014/12/24.
 */
@Service("processHistService")
public class ProcessHistServiceImpl  implements ProcessHistService{
    private enum Codes{
        process_getHistLogByRecordId,
        process_getDiagram,
        process_getFallbackOptions,
        process_fallbackTask,
        process_delegateTask,
        process_startReaderTask,
        process_claimTask,
        process_completeReaderTask,
        process_unclaimTask,
        process_getHistTaskByActInstId,
        process_getReaderTaskByProcInstId

    }
    @Override
    public List<HistLogVO> getHistLogByRecordId(UserInfoVO userInfo, Long proinstId, Long recordId) {
        return (List<HistLogVO>) WSClient.getList(Codes.process_getHistLogByRecordId.name(),
                new Object[]{
                        userInfo,
                        proinstId,
                        recordId
        },
                HistLogVO.class
        );
    }
    @Override
    public DiagramVO getDiagram(UserInfoVO userInfo,Long processId,Long recordId,Long curActivityId){
        return (DiagramVO)WSClient.getObject(Codes.process_getDiagram.name(),
                new Object[]{
                        userInfo,
                        processId,
                        recordId,
                        curActivityId
                },
                DiagramVO.class
                );
    }

    @Override
    public FallbackVO getFallbackOptions(UserInfoVO userInfo, Long proinstId,  Long taskId) {
        return (FallbackVO)WSClient.getObject(Codes.process_getFallbackOptions.name(),
                new Object[]{
                        userInfo,
                        proinstId,
                        taskId
                },
                FallbackVO.class
                );
    }

    @Override
    public List<TaskVO> fallbackTask(UserInfoVO userInfo,Long actinstId,Long taskId,String logDescr){
            return  (List<TaskVO>)WSClient.getList(Codes.process_fallbackTask.name(),
                    new Object[]{
                            userInfo,
                            actinstId,
                            taskId,
                            logDescr
                    },
                    TaskVO.class
                    );
    }
    @Override
    public TaskVO delegateTask(UserInfoVO userInfo,Long taskId, UserInfoVO delegateUserInfo,String logDescr){
        return (TaskVO)WSClient.getObject(Codes.process_delegateTask.name(),
                new Object[]{
                        userInfo,
                        taskId,
                        delegateUserInfo,
                        logDescr
                },
                TaskVO.class
        );
    }

    @Override
    public List<TaskVO> startReaderTask(UserInfoVO userInfo,Long taskId,AssigneeVO[] taskReaders){
        return (List<TaskVO>)WSClient.getList(Codes.process_startReaderTask.name(),
                new Object[]{
                        userInfo,
                        taskId,
                        taskReaders
                },
                TaskVO.class
        );
    }

    @Override
    public List<TaskVO> getReaderTaskByProcInstId(UserInfoVO userInfo,Long procInstId){
        return (List<TaskVO>)WSClient.getList(Codes.process_getReaderTaskByProcInstId.name(),
                new Object[]{
                        userInfo,
                        procInstId
                },
                TaskVO.class
        );
    }
    @Override
    public Boolean completeReaderTask(UserInfoVO userInfo,Long taskId,String logDescr){
        return (Boolean)WSClient.getObject(Codes.process_completeReaderTask.name(),
                new Object[]{
                        userInfo,
                        taskId,
                        logDescr
                },
                Boolean.class
        );
    }

    @Override
    public Boolean claimTask(UserInfoVO userInfo,Long taskId){
        return (Boolean)WSClient.getObject(Codes.process_claimTask.name(),
                new Object[]{
                        userInfo,
                        taskId
                },
                Boolean.class
        );
    }
    @Override
    public Boolean unclaimTask(UserInfoVO userInfo,Long taskId){
        return (Boolean)WSClient.getObject(Codes.process_unclaimTask.name(),
                new Object[]{
                        userInfo,
                        taskId
                },
                Boolean.class
        );
    }
    @Override
    public List<TaskVO> getHistTaskByActInstId(UserInfoVO userInfo,Long procInstId,Long actInstId){
        return (List<TaskVO>)WSClient.getList(Codes.process_getHistTaskByActInstId.name(),
                new Object[]{
                        userInfo,
                        procInstId,
                        actInstId
                },
                TaskVO.class
                );
    }
}
