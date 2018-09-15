package cn.lonsun.process.handler;

import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.process.service.IProcessService;
import cn.lonsun.webservice.processEngine.enums.TaskStatus;
import cn.lonsun.webservice.processEngine.vo.Button;
import cn.lonsun.webservice.processEngine.vo.UserInfoVO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhu124866 on 2015-12-18.
 */
public class ButtonHandler {


    private IProcessService processService = SpringContextHolder.getBean("processService");


    public enum GetButtonType{
        HAND,//办理
        VIEW,//查看
        ClAIM,//检出
        AUTO_TO_OWNER;//自动流转至发起人
    }

    private UserInfoVO userInfoVO;//用户信息

    private TaskStatus taskStatus;//任务状态

    private String moduleCode;//模块编码

    private Long processId;//流程ID

    private Long activityId;//活动ID

    private Long taskId;//任务ID

    private String processBusinessType;//流程业务类型

    private Long dataId;//数据ID




    public ButtonHandler userInfoVO(UserInfoVO userInfoVO){
        this.userInfoVO = userInfoVO;
        return this;
    }

    public ButtonHandler taskStatus(TaskStatus taskStatus){
        this.taskStatus = taskStatus;
        return this;
    }

    public ButtonHandler moduleCode(String moduleCode){
        this.moduleCode = moduleCode;
        return this;
    }

    public ButtonHandler processId(Long processId){
        this.processId = processId;
        return this;
    }

    public ButtonHandler activityId(Long activityId){
        this.activityId = activityId;
        return this;
    }

    public ButtonHandler taskId(Long taskId){
        this.taskId = taskId;
        return this;
    }

    public ButtonHandler processBusinessType(String processBusinessType){
        this.processBusinessType = processBusinessType;
        return this;
    }

    public ButtonHandler dataId(Long dataId){
        this.dataId = dataId;
        return this;
    }

    public List<Button> getButtons(GetButtonType type){
        Map<String,Object> formData = new HashMap<String, Object>();
        if(GetButtonType.HAND.equals(type)){
            return processService.getButtons(userInfoVO,activityId,taskId,formData);
        }else if(GetButtonType.VIEW.equals(type)){
            return processService.getButtonsByTaskStatus(userInfoVO,processId,taskStatus);
        }else if(GetButtonType.ClAIM.equals(type)){
            return processService.getClaimButtons(moduleCode);
        }else if(GetButtonType.AUTO_TO_OWNER.equals(type)){
            return processService.getAutoToOwnerButtons(moduleCode,taskId,userInfoVO,formData);
        }
        return null;
    }






}
