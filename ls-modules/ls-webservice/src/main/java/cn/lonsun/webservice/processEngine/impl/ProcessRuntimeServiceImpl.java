package cn.lonsun.webservice.processEngine.impl;

import cn.lonsun.webservice.processEngine.ProcessRuntimeService;
import cn.lonsun.webservice.processEngine.util.WSClient;
import cn.lonsun.webservice.processEngine.vo.TaskVO;
import cn.lonsun.webservice.processEngine.vo.UserInfoVO;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by lonsun on 2015-1-14.
 */
@Service("processRuntimeService")
public class ProcessRuntimeServiceImpl implements ProcessRuntimeService {
    private enum Codes {
        process_getRuExecutionTaskListById,
        process_terminateProcessInstanceById
    }

    @Override
    public List<TaskVO> getRuExecutionTaskListById(UserInfoVO userInfo, Long processInstanceId) {
        return (List<TaskVO>) WSClient.getList(Codes.process_getRuExecutionTaskListById.name(),
                new Object[]{
                        userInfo,
                        processInstanceId
                },
                TaskVO.class
        );
    }
    @Override
    public Boolean terminateProcessInstanceById(UserInfoVO userInfo,Long processInstanceId,String logDesc) {
        return (Boolean) WSClient.getObject(Codes.process_terminateProcessInstanceById.name(),
                new Object[]{
                        userInfo,
                        processInstanceId,
                        logDesc
                },
                Boolean.class
        );
    }
}
