package cn.lonsun.webservice.processEngine;

import cn.lonsun.webservice.processEngine.vo.TaskVO;
import cn.lonsun.webservice.processEngine.vo.UserInfoVO;

import java.util.List;

/**
 * Created by lonsun on 2015-1-14.
 */
public interface ProcessRuntimeService {
    /**
     * 流程实例运行任务查询
     * @param userInfo
     * @param processInstanceId
     * @return
     */
    public List<TaskVO> getRuExecutionTaskListById(UserInfoVO userInfo,Long processInstanceId);

    /**
     * 流程实例终止
     * @param userInfo
     * @param processInstanceId
     * @return
     */
    public Boolean terminateProcessInstanceById(UserInfoVO userInfo,Long processInstanceId,String logDesc);


}
