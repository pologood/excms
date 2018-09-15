/*
 * 2014-12-13 <br/>
 * 
 */
package cn.lonsun.webservice.processEngine.impl;

import cn.lonsun.webservice.processEngine.ProcessEngineService;
import cn.lonsun.webservice.processEngine.enums.EFieldControlType;
import cn.lonsun.webservice.processEngine.enums.TaskStatus;
import cn.lonsun.webservice.processEngine.util.AppValueUtil;
import cn.lonsun.webservice.processEngine.util.WSClient;
import cn.lonsun.webservice.processEngine.vo.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service("processEngineService")
public class ProcessEngineServiceImpl implements ProcessEngineService {
    private enum Codes{
        process_listQueryReProcess,
        process_getStartEventByProcessId,
        process_getEndEventByProcessId,
        process_getSuccessorActivities,
        process_startProcessInstanceById,
        process_getActivityOpertions,
        process_getFirstFieldControls,
        process_getExternalClassById,
        process_startProcessInstanceByIdOmitFristActi,
        process_getFirstActivity,
        process_getReElementById,
        process_getModules,
        process_getSuccessorActivitiesByForm,
        process_updateReProcessForFormVer,
        process_listQueryReProcessByThird,
        process_getActivitiesByProcessId,
        /**
         *   /services/managementWService
         *   /services/repositoryWService
         *   /services/runtimeWService
         *   /services/taskWService
         *   /services/historyWService
         *
         *   http://service.ws.processEngine.lonsun.cn
         */
    }

   private ActivityVO getStartActivityVO(UserInfoVO userInfo, Long processId){
       return  getStartEventByProcessId(userInfo, processId);
   }
    @Deprecated
    @Override
    public List<ProcessVO> getPackageProcess(Long unitId, String moduleCode) {
        UserInfoVO user =  new UserInfoVO(unitId);
       return  (List<ProcessVO>) WSClient.getList(Codes.process_listQueryReProcess.name(),
               new Object[]{
                       user,
                       moduleCode
               },ProcessVO.class);
    }

    @Override
    public List<ProcessVO> getPackageProcessByThird(Long thirdId, String moduleCode) {
        UserInfoVO user =  new UserInfoVO(thirdId);
        return  (List<ProcessVO>) WSClient.getList(Codes.process_listQueryReProcessByThird.name(),
                new Object[]{
                        user,
                        thirdId,
                        moduleCode
                },ProcessVO.class);
    }

    @Override
    public ActivityVO getStartEventByProcessId(UserInfoVO userInfo, Long processId) {
        return (ActivityVO) WSClient.getObject(Codes.process_getStartEventByProcessId.name(),
                new Object[]{
                        userInfo,
                        processId
                },ActivityVO.class);
    }

    @Override
    public ActivityVO getEndEventByProcessId(UserInfoVO userInfo, Long processId) {
        return (ActivityVO) WSClient.getObject(Codes.process_getEndEventByProcessId.name(),
                new Object[]{
                        userInfo,
                        processId
                },ActivityVO.class);
    }

    @Override
    public List<ActivityVO> getSuccessorActivities(UserInfoVO userInfo, Long elementId) {
        return (List<ActivityVO>) WSClient.getList(Codes.process_getSuccessorActivities.name(),
                new Object[]{
                        userInfo,
                        elementId
                },ActivityVO.class);
    }
    @Override
    public List<ActivityVO> getSuccessorActivitiesByForm(UserInfoVO userInfo, Long elementId, Map<String,Object> formData) {
        FormDataVO formDataVO = new FormDataVO();
        formDataVO.setElementId(elementId);
        formDataVO.setData(AppValueUtil.copyIndex0ByMap(formData));
        return (List<ActivityVO>) WSClient.getList(Codes.process_getSuccessorActivitiesByForm.name(),
                new Object[]{
                        userInfo,
                        formDataVO
                },ActivityVO.class);
    }
    @Override
    public List<ActivityVO> getSuccessorActivitiesByProcessId(UserInfoVO userInfo, Long processId) {
        ActivityVO startActivityVO= getStartActivityVO(userInfo,processId);
        if(null == startActivityVO || null == startActivityVO.getElementId()) return new ArrayList<ActivityVO>(0);
        return getSuccessorActivities(userInfo,startActivityVO.getElementId());
    }

    @Override
    public List<TaskVO> startProcessInstanceById(UserInfoVO userInfo, Long processId, StartProcessParam startProcessParam) {
        List<TaskVO> rest  = (List<TaskVO>) WSClient.getList(Codes.process_startProcessInstanceById.name(),
                new Object[]{
                        userInfo,
                        processId,
                        startProcessParam
                },
                TaskVO.class
                );
        return rest;
    }
    @Override
    public List<TaskVO> startProcessInstanceByIdOmitFristActi(UserInfoVO userInfo, Long processId, StartProcessParam startProcessParam) {
        List<TaskVO> rest  = (List<TaskVO>) WSClient.getList(Codes.process_startProcessInstanceByIdOmitFristActi.name(),
                new Object[]{
                        userInfo,
                        processId,
                        startProcessParam
                },
                TaskVO.class
        );
        return rest;
    }

    @Override
    public List<Button> getActivityOpertions(UserInfoVO userInfo, Long processId, TaskStatus taskStatus){
        return (List <Button>) WSClient.getList(Codes.process_getActivityOpertions.name(),
                new Object[]{
                        userInfo,
                        processId,
                        taskStatus
                },
                Button.class
                );
    }
    @Override
    public List<FieldVO> getFirstFieldControls(UserInfoVO userInfo, Long processId, EFieldControlType[] fieldControlTypes){
        return ( List<FieldVO>) WSClient.getList(Codes.process_getFirstFieldControls.name(),
                new Object[]{
                        userInfo,
                        processId,
                        fieldControlTypes
                },
                FieldVO.class
        );
    }
    @Override
    public String getExternalClassById(UserInfoVO userInfo, Long activityId){
        return (String) WSClient.getObject(Codes.process_getExternalClassById.name(),
                new Object[]{
                        userInfo,
                        activityId
                },
                String.class
                );
    }
    @Override
    public ActivityVO getFirstActivity(UserInfoVO userInfo, Long processId){
        return (ActivityVO) WSClient.getObject(Codes.process_getFirstActivity.name(),
                new Object[]{
                        userInfo,
                        processId
                },
                ActivityVO.class
                );
    }

    @Override
    public ActivityVO getActivity(UserInfoVO userInfo, Long activityId){
        return (ActivityVO) WSClient.getObject(Codes.process_getReElementById.name(),
                new Object[]{
                        userInfo,
                        activityId
                },
                ActivityVO.class
        );
    }

    public Boolean updateReProcessForFormVer(UserInfoVO userInfo, Long formId, Integer formVer){
        return (Boolean) WSClient.getObject(Codes.process_updateReProcessForFormVer.name(),
                new Object[]{
                        userInfo,
                        formId,
                        formVer
                },
                Boolean.class
                );
    }

    public List<ActivityVO> getActivitiesByProcessId(UserInfoVO userInfo, Long processId){
        return (List<ActivityVO>) WSClient.getList(Codes.process_getActivitiesByProcessId.name(),
                new Object[]{
                        userInfo,
                        processId
                },ActivityVO.class);
    }

}
