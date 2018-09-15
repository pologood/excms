package cn.lonsun.process.invoke.impl;

import cn.lonsun.base.DealStatus;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.service.IBaseContentService;
import cn.lonsun.core.util.ContextHolderUtils;
import cn.lonsun.process.entity.ProcessFormEO;
import cn.lonsun.process.invoke.IBusinessHandler;
import cn.lonsun.process.service.IFormHandleRecordService;
import cn.lonsun.process.service.IProcessFormService;
import cn.lonsun.site.site.internal.entity.ColumnMgrEO;
import cn.lonsun.util.LoginPersonUtil;
import cn.lonsun.webservice.processEngine.vo.UserInfoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 表单业务数据处理
 * Created by zhu124866 on 2015-12-22.
 */
@Component("exFormHandler")
public class EXFormHandler implements IBusinessHandler {

    @Autowired
    private IProcessFormService processFormService;

    @Autowired
    private IFormHandleRecordService formHandleRecordService;

    @Autowired
    private IBaseContentService baseContentService;

    /**
     * 流程启动表单数据处理
     *
     * @param map
     * @return
     */
    @Override
    public Map<String, Object> start(UserInfoVO userInfo,Map<String,Object> map) throws Exception{
        /**
         * 保存流程表单对象
         */
        Long dataId = Long.parseLong(AppUtil.getValue(map.get("dataId")));
        BaseContentEO baseContentEO = baseContentService.getEntity(BaseContentEO.class,dataId);
        Long columnId = baseContentEO.getColumnId();
        ColumnMgrEO columnMgrEO = CacheHandler.getEntity(ColumnMgrEO.class, columnId);
        map.put("columnId",columnId);
        map.put("columnName",columnMgrEO.getName());

        String personName = LoginPersonUtil.getPersonName();
        Long unitId = LoginPersonUtil.getUnitId();
        map.put("createPersonName",personName);
        map.put("unitId",unitId);
        ProcessFormEO processForm = processFormService.saveProcessFormProcessStart(map);
        /**
         * 保存办理记录对象
         */
        map.put("processFormId",processForm.getProcessFormId());
        formHandleRecordService.saveFormHandleRecord(userInfo,map);
        /**
         * 构造返回数据
         */
        Map<String,Object> data = new HashMap<String,Object>();
        data.put("recordId",processForm.getProcessFormId());
        data.put("title",processForm.getTitle());
        Map<String,String> businessData = new HashMap<String, String>();
        businessData.put("processStarter",personName);
        businessData.put("processBusinessType",AppUtil.getValue(map.get("processBusinessType")));
        businessData.put("columnId",columnId.toString());
        businessData.put("columnName",columnMgrEO.getName());
        data.put("businessData",businessData);
        return data;
    }

    /**
     * 流程流转表单数据处理
     *
     * @param map
     * @return
     */
    @Override
    public Map<String, Object> run(UserInfoVO userInfo,Map<String, Object> map) throws Exception{

        Long dataId = Long.parseLong(AppUtil.getValue(map.get("dataId")));
        BaseContentEO baseContentEO = baseContentService.getEntity(BaseContentEO.class,dataId);
        Long columnId = baseContentEO.getColumnId();
        ColumnMgrEO columnMgrEO = CacheHandler.getEntity(ColumnMgrEO.class, columnId);
        map.put("columnId",columnId);
        map.put("columnName",columnMgrEO.getName());

        /**
         * 保存流程表单对象
         */
        ProcessFormEO processForm = processFormService.saveProcessForm(map,null);
        /**
         * 保存办理记录对象
         */
        formHandleRecordService.saveFormHandleRecord(userInfo,map);
        /**
         * 构造返回数据
         */
        Map<String,Object> data = new HashMap<String,Object>();
        data.put("recordId",processForm.getProcessFormId());
        data.put("title",processForm.getTitle());

        Map<String,String> businessData = (Map<String,String>)data.get("businessData");
        if(businessData!=null){
            if(AppUtil.isEmpty(businessData.get(columnId))){
                businessData.put("columnId",columnId.toString());
                businessData.put("columnName",columnMgrEO.getName());
            }
            data.put("businessData",businessData);
        }

        return data;
    }



    /**
     * 流程办结表单数据处理
     *
     * @param map
     * @return
     */
    @Override
    public Map<String, Object> complete(UserInfoVO userInfo,Map<String, Object> map) throws Exception{
        /**
         * 保存流程表单对象
         */
        processFormService.saveProcessForm(map, DealStatus.Completed);
        /**
         * 保存办理记录对象
         */
        formHandleRecordService.saveFormHandleRecord(userInfo,map);
        return new HashMap<String, Object>();
    }

    /**
     * 为业务实体设置流程信息
     *
     * @param procInstId
     * @param actinstId
     * @param activityName
     * @param recordId     @return
     */
    @Override
    public void setProcessInfo(Long procInstId, Long actinstId, String activityName, Long recordId) {
        ProcessFormEO processForm = processFormService.getEntity(ProcessFormEO.class,recordId);
        processForm.setProcInstId(procInstId);
        processForm.setCurActinstId(actinstId);
        processForm.setCurActivityName(activityName);
        processFormService.updateEntity(processForm);
    }

    /**
     * 为业务实体设置当前活动实例
     *
     * @param actinstId
     * @param activityName
     * @param recordId
     * @return
     */
    @Override
    public void setCurActivity(Long actinstId, String activityName, Long recordId) {
        ProcessFormEO processForm = processFormService.getEntity(ProcessFormEO.class,recordId);
        processForm.setCurActinstId(actinstId);
        processForm.setCurActivityName(activityName);
        processFormService.updateEntity(processForm);
    }

    /**
     * 流程终止业务数据处理
     *
     * @param recordId
     * @param reason
     * @return
     */
    @Override
    public void terminate(Long recordId, String reason) {
        processFormService.processTerminate(recordId,reason);
        ProcessFormEO processForm = processFormService.getEntity(ProcessFormEO.class,recordId);
        processForm.setFormStatus(DealStatus.Terminate.getValue());
        processForm.setCurActivityName("已终止");
        processFormService.updateEntity(processForm);
    }

    /**
     * 保存阅件办理记录
     *
     * @param map
     */
    @Override
    public void saveReadLog(Map<String, Object> map) {

    }

    /**
     * 退回
     *
     * @param map
     */
    @Override
    public void back(UserInfoVO userInfo,Map<String, Object> map) {
        Long processFormId = AppUtil.getLong(map.get("recordId"));
        /**
         * 保存办理记录对象
         */
        map.put("activityName","退回");
        map.put("processFormId",processFormId);
        formHandleRecordService.saveFormHandleRecord(userInfo,map);
        //设置公文当前所处活动信息
        Long nextActinstId = AppUtil.getLong(map.get("nextActinstId"));
        String nextActivityName = AppUtil.getValue(map.get("nextActivityName"));
        setCurActivity(nextActinstId,nextActivityName,processFormId);
    }

    /**
     * 退回操作业务验证
     *
     * @param recordId
     */
    @Override
    public void backBusinessValidate(Long recordId) {

    }

    /**
     * 保存表单数据
     *
     * @param map
     */
    @Override
    public Map<String, Object> saveFormData(Map<String, Object> map) throws Exception{
        processFormService.saveProcessForm(map,null);
        return new HashMap<String, Object>();
    }

    /**
     * 记录系统操作日志
     *
     * @param recordId
     * @param title
     */
    @Override
    public void saveSystemLog(Long recordId, String title) {
        /*if(null != recordId){
            ProcessFormEO processForm = processFormService.getEntity(ProcessFormEO.class,recordId);
            if(null != processForm){
                LogUtil.saveLog("自定义表单" + title + "：编号(" + processForm.getProcessFormId() + "),标题(" + processForm.getTitle() + ")", "ProcessFormEO", LogVO.Operation.Update.toString());
            }
        }*/
    }

}
