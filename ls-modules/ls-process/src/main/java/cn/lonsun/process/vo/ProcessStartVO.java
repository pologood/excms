package cn.lonsun.process.vo;

import cn.lonsun.webservice.processEngine.vo.ActivityVO;

import java.util.Map;

/**
 * 流程启动VO
 * Created by zhusy on 2016-8-2.
 */
public class ProcessStartVO {

    private Long processId;//流程ID

    private String processName;//流程名称

    private ActivityVO firstActivity;//第一个活动

    private Map<String,Object> nextActivity;//下一个活动


    public Long getProcessId() {
        return processId;
    }

    public void setProcessId(Long processId) {
        this.processId = processId;
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public ActivityVO getFirstActivity() {
        return firstActivity;
    }

    public void setFirstActivity(ActivityVO firstActivity) {
        this.firstActivity = firstActivity;
    }

    public Map<String, Object> getNextActivity() {
        return nextActivity;
    }

    public void setNextActivity(Map<String, Object> nextActivity) {
        this.nextActivity = nextActivity;
    }
}
