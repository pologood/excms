package cn.lonsun.process.controller;

import cn.lonsun.activemq.MessageSender;
import cn.lonsun.base.DealStatus;
import cn.lonsun.base.EngineCode;
import cn.lonsun.base.ProcessEngineConfig;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.common.vo.ReceiverVO;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.service.IBaseContentService;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.exception.util.Jacksons;
import cn.lonsun.core.util.ContextHolderUtils;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.SessionUtil;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.message.internal.entity.MessageSystemEO;
import cn.lonsun.process.entity.ActivityUserSetEO;
import cn.lonsun.process.entity.ProcessFormEO;
import cn.lonsun.process.handler.ButtonHandler;
import cn.lonsun.process.service.IActivityUserSetService;
import cn.lonsun.process.service.IProcessService;
import cn.lonsun.process.service.IProcessFormService;
import cn.lonsun.process.vo.*;
import cn.lonsun.rbac.internal.entity.PersonEO;
import cn.lonsun.rbac.internal.service.IPersonService;
import cn.lonsun.site.contentModel.internal.entity.ContentModelEO;
import cn.lonsun.site.contentModel.internal.entity.ModelTemplateEO;
import cn.lonsun.site.contentModel.internal.service.IContentModelService;
import cn.lonsun.site.contentModel.internal.service.IModelTemplateService;
import cn.lonsun.site.site.internal.entity.ColumnConfigEO;
import cn.lonsun.site.site.internal.entity.ColumnMgrEO;
import cn.lonsun.site.site.internal.service.IColumnConfigService;
import cn.lonsun.util.ColumnUtil;
import cn.lonsun.util.LoginPersonUtil;
import cn.lonsun.webservice.processEngine.ProcessEngineService;
import cn.lonsun.webservice.processEngine.enums.EParticipationType;
import cn.lonsun.webservice.processEngine.enums.TaskStatus;
import cn.lonsun.webservice.processEngine.vo.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by zhusy on 2016-7-18.
 */
@Controller
@RequestMapping("process")
public class ProcessController extends BaseController {


    @Autowired
    private IProcessService processService;

    @Autowired
    private IColumnConfigService columnConfigService;

    @Autowired
    private IContentModelService contentModelService;

    @Autowired
    private IModelTemplateService modelTemplateService;

    @Autowired
    private IProcessFormService processFormService;

    @Autowired
    ProcessEngineService processEngineService;

    @Autowired
    IActivityUserSetService activityUserSetService;

    @Autowired
    private IBaseContentService baseContentService;

    @Autowired
    private IPersonService personService;


    /**
     * 流程配置
     * @param request
     * @param model
     * @return
     */
    @RequestMapping("processConfig")
    public String processConfig(HttpServletRequest request,Model model){
        HttpSession session = request.getSession();
        String sessionId = session.getId();
        String engineHost = ProcessEngineConfig.getProcessEngineHost();
//        engineHost = "http://localhost:8003";
        String url = engineHost+"/processEngineSSO/login?markKey=EX&sessionId="+sessionId+"&toUrl=/processEngine/engine/"+ EngineCode.EX.toString();
        model.addAttribute("url", url);
        return "process/process_config";
    }


    /**
     * 流程人员配置
     * @param request
     * @param model
     * @return
     */
    @RequestMapping("processUserConfig")
    public String processUserConfig(HttpServletRequest request,Model model){
//        model.addAttribute("url", url);
        return "process/process_user_config";
    }


    /**
     * 流程活动列表页
     * @param request
     * @param model
     * @return
     */
    @RequestMapping("processActivityList")
    public String processActivityList(HttpServletRequest request,Model model,Long processId){
        model.addAttribute("processId", processId);
        return "process/process_activity_list";
    }

    /**
     * 流程列表
     * @return
     */
    @RequestMapping("getProcessListPagination")
    @ResponseBody
    public Object getProcessListPagination(ProcessFormQueryVO queryVO){
        //通过webservice查询出所有流程
        List<ProcessVO> processVOList =  processEngineService.getPackageProcessByThird(LoginPersonUtil.getSiteId(),EngineCode.EX.toString());
        Long pageIndex = queryVO.getPageIndex();
        Integer pageSize = queryVO.getPageSize();
        Pagination page = new Pagination();
        page.setData(processVOList);
        Long pageCount = (long)Math.ceil(processVOList.size()/(pageSize*1.0));
        page.setPageCount(pageCount);
        page.setTotal(processVOList.size()+0L);
        page.setPageIndex(pageIndex);
        page.setPageSize(pageSize);

        return getObject(page);
    }

    /**
     * 流程列表
     * @return
     */
    @RequestMapping("getProcessActivityListPagination")
    @ResponseBody
    public Object getProcessActivityListPagination(ProcessFormQueryVO queryVO,Long processId){
        //通过webservice查询出所有流程
        List<ActivityVO> activityVOList =  processEngineService.getActivitiesByProcessId(new UserInfoVO(),processId);
        Collections.reverse(activityVOList);//调整排序
        for(ActivityVO activityVO:activityVOList){
            Map<String,Object> map = new HashMap<String, Object>();
            map.put("siteId",LoginPersonUtil.getSiteId());
            map.put("activityId",activityVO.getElementId());
            map.put("processId",activityVO.getProcessId());
            ActivityUserSetEO activityUserSetEO =  activityUserSetService.getEntity(ActivityUserSetEO.class,map);
            if(activityUserSetEO!=null){
                //查询办理人员
                activityVO.setAssigneeName(activityUserSetEO.getUserNames());
                activityVO.setAssigneeId(activityUserSetEO.getUserIds());
                activityVO.setFormId(activityUserSetEO.getId());//人员配置表主键id
                activityVO.setAssigneeOrgId(activityUserSetEO.getOrganIds());
                activityVO.setAssigneeUnitId(activityUserSetEO.getUnitIds());
            }
        }
        Long pageIndex = queryVO.getPageIndex();
        Integer pageSize = queryVO.getPageSize();
        Pagination page = new Pagination();
        page.setData(activityVOList);
        Long pageCount = (long)Math.ceil(activityVOList.size()/(pageSize*1.0));
        page.setPageCount(pageCount);
        page.setTotal(activityVOList.size()+0L);
        page.setPageIndex(pageIndex);
        page.setPageSize(pageSize);

        return getObject(page);
    }

    /**
     * 跳转人员设置页面
     * @param processId
     * @param activityId
     * @param model
     * @return
     */
    @RequestMapping("toUserSettingEdit")
    public String toUserSettingEdit(Model model,Long processId,Long activityId,String activityName){
        model.addAttribute("processId", processId);
        model.addAttribute("activityId", activityId);
        model.addAttribute("activityName", activityName);
        return "process/userSettingEdit";
    }


    /**
     * 获取活动人员设置信息
     * @return
     */
    @RequestMapping("getUserSetInfo")
    @ResponseBody
    public Object getUserSetInfo(ActivityUserSetQueryVO queryVO){
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("siteId",queryVO.getSiteId());
        map.put("activityId",queryVO.getActivityId());
        map.put("processId",queryVO.getProcessId());
        ActivityUserSetEO activityUserSetEO =  activityUserSetService.getEntity(ActivityUserSetEO.class,map);
        return getObject(activityUserSetEO);
    }

    /**
     * 保存活动人员设置信息
     * @return
     */
    @RequestMapping("saveUserSetInfo")
    @ResponseBody
    public Object saveUserSetInfo(ActivityUserSetEO activityUserSetEO){
        if(AppUtil.isEmpty(activityUserSetEO.getId())){//新增
            activityUserSetService.saveEntity(activityUserSetEO);
        }else{
            activityUserSetService.updateEntity(activityUserSetEO);
        }
        return getObject();
    }


    /**
     * 流程列表
     * @return
     */
    @RequestMapping("processList")
    public ModelAndView processList(Model model,String moduleCode,String processBusinessType,Long dataId,Long columnId) {
        model.addAttribute("moduleCode", moduleCode);
        model.addAttribute("processBusinessType", processBusinessType);
        model.addAttribute("dataId", dataId);
        model.addAttribute("columnId", columnId);
        return new ModelAndView("process/process_list");
    }

    /**
     * 获取流程过程包列表
     * @return
     */
    @RequestMapping("getPackageList")
    @ResponseBody
    public Object  getPackageList(String moduleCode){
        Long siteId = LoginPersonUtil.getSiteId();
        return getObject(this.processService.getPackageList(siteId,moduleCode));
    }


    private ProcessFormEO getProcessForm(ProcessFormParamVO param){
        ProcessFormEO processForm = null;
        if(null != param.getProcessFormId()){
            processForm = processFormService.getEntity(ProcessFormEO.class,param.getProcessFormId());
        }else{
            processForm = new ProcessFormEO();
            processForm.setModuleCode(param.getModuleCode());
            processForm.setProcessId(param.getProcessId());
            processForm.setProcessName(param.getProcessName());
            processForm.setProcessBusinessType(param.getProcessBusinessTypeEnum());
            processForm.setDataId(param.getDataId());
        }
        return processForm;
    }


    private TaskStatus getTaskStatus(Integer formStatus){
        if(DealStatus.Dealing.getValue().equals(formStatus)){
            return TaskStatus.doneDeal;
        }else{
            return TaskStatus.end;
        }
    }

    /**
     * 流程表单页
     *
     * @return
     */
    @RequestMapping("processForm")
    public ModelAndView processForm(HttpServletRequest request, Model model, ProcessFormParamVO param) {
        UserInfoVO userInfoVO = getProcessUserInfoVO(request);
        ProcessFormEO processForm = getProcessForm(param);//办理表单对象

        Long activityId = null; //当前活动ID
        String activityName = "";//当前活动名称
        Integer needSign = 0; //任务是否需要签收
        boolean isClaim = false;//任务是否需要办理(检出,原称为检出,后改为办理)
        Integer autoToOwner = 0;//是否是自动流转至发起人任务
        /**
         * 任务状态判断
         */
        TaskVO task = null;
        if (null != param.getTaskId()) {
            task = processService.getRunTaskById(userInfoVO, param.getTaskId());
            //任务为空,则该任务已经办理,只能查看表单
            if (null == task) {
                param.setTaskId(null);
                param.setViewForm(1);
            } else {
                autoToOwner = task.getCanAutoToOwner();
                activityId = task.getActivityId();
                activityName = task.getName();
                needSign = (task.getSignDate() == null && null != task.getAssigneeId()) ? 1 : 0;//签收时间为空且任务办理人不能为空则该任务需签收
                if (task.getAssignType() != null && (EParticipationType.role.equals(task.getAssignType()) || EParticipationType.dept.equals(task.getAssignType()))) {
                    if (null == task.getAssigneeId()) {
                        isClaim = true;
                    } else if (!task.getAssigneeId().equals(userInfoVO.getUserId()) || !task.getAssigneeOrgId().equals(userInfoVO.getOrgId())) {
                        isClaim = true;
                    }
                }
            }
        }
        if (Integer.valueOf(1).equals(param.getStartFlow())) {//流程开始
            ActivityVO firstActivity = processService.getFirstActivity(userInfoVO, processForm.getProcessId());//获取流程第一个活动
            if (null == firstActivity) {
                throw new BaseRunTimeException(TipsMode.Message.toString(), "当前流程未设置第一个活动");
            }
            activityId = firstActivity.getElementId();
            activityName = firstActivity.getName();
        }
        List<Button> buttons = null;//可操作按钮
        ActivityFieldControlVO activityFieldControl = null;//活动字段控制
        ButtonHandler buttonHandler = new ButtonHandler();
        if (Integer.valueOf(1).equals(param.getViewForm())) {
            buttons = buttonHandler.userInfoVO(userInfoVO).processId(processForm.getProcessId()).taskStatus(getTaskStatus(processForm.getFormStatus())).getButtons(ButtonHandler.GetButtonType.VIEW);
        } else if (isClaim) {
            buttons = buttonHandler.moduleCode(processForm.getModuleCode()).getButtons(ButtonHandler.GetButtonType.ClAIM);
        } else if (Integer.valueOf(1).equals(autoToOwner)) {
            buttons = buttonHandler.moduleCode(processForm.getModuleCode()).userInfoVO(userInfoVO).taskId(param.getTaskId()).getButtons(ButtonHandler.GetButtonType.AUTO_TO_OWNER);
        } else {
            buttons = buttonHandler.userInfoVO(userInfoVO).activityId(activityId).taskId(param.getTaskId()).getButtons(ButtonHandler.GetButtonType.HAND);
            activityFieldControl = processService.getActivityFieldControl(userInfoVO, activityId);
        }
        ProcessFormForwardDataVO formForwardData = new ProcessFormForwardDataVO();
        formForwardData.setActivityId(activityId);
        formForwardData.setActivityName(activityName);
        formForwardData.setAutoToOwner(autoToOwner);
        formForwardData.setNeedSign(needSign);
        formForwardData.setButtons(buttons);
        formForwardData.setActivityFieldControl(activityFieldControl);
        formForwardData.setParam(param);
        formForwardData.setProcessForm(processForm);
        model.addAttribute("formForwardData", formForwardData);

        ModelAndView modelAndView = new ModelAndView("process/process_form");
        return modelAndView;
    }


    /**
     * 流程下一步页面
     * @param model
     * @return
     */
    @RequestMapping(value="processNextStep",produces = {"application/json;charset=UTF-8"})
    public ModelAndView processNextStep(Model model,Long processId,Integer startFlow,Long elementId,Long taskId){
        model.addAttribute("processId", processId);//流程定义ID
        model.addAttribute("startFlow", startFlow);//流程开始标记
        model.addAttribute("elementId", elementId);//活动定义ID
        model.addAttribute("taskId", taskId);//任务ID
        return new ModelAndView("process/process_next_step");
    }






    /**
     * 收文分送弹层处理页面
     *
     * @param model
     * @param processId
     * @param startFlow
     * @param elementId
     * @param taskId
     * @return
     */
    @RequestMapping("processSendRead")
    public ModelAndView processSendRead(Model model,Long processId,Integer startFlow,Long elementId,Long taskId,String docTitle,String sendUnitName,Long procInstId){
        model.addAttribute("processId", processId);//流程定义ID
        model.addAttribute("procInstId", procInstId);//流程实例ID
        model.addAttribute("startFlow", startFlow);//流程开始标记
        model.addAttribute("elementId", elementId);//活动定义ID
        model.addAttribute("taskId", taskId);//任务ID
        try {
            docTitle = URLDecoder.decode(docTitle, "UTF-8");
            sendUnitName = URLDecoder.decode(sendUnitName, "UTF-8");
            model.addAttribute("docTitle", docTitle);
            model.addAttribute("sendUnitName", sendUnitName);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new BaseRunTimeException();
        }
        return new ModelAndView("process/process_send_read");
    }

    /**
     * 分送
     *
     * @param req
     * @param taskId
     * @param receivers
     * @return
     */
    @RequestMapping("createReadTasks")
    @ResponseBody
    public Object createReadTasks(HttpServletRequest req,Long taskId,String receivers,String moduleCode,Integer smsRemind){
        if(StringUtils.isEmpty(receivers)){
            throw new BaseRunTimeException(TipsMode.Message.toString(), "请选择接收人");
        }
       /* //PersonInfoVO[] persons = Jacksons.json().fromJsonToObject(receivers, PersonInfoVO[].class);
        AssigneeVO[] assignees = null;
        if(persons!=null&&persons.length>0){
            int length = persons.length;
            assignees = new AssigneeVO[length];
            for(int i=0;i<length;i++){
               *//* PersonInfoVO person = persons[i];
                AssigneeVO assignee = new AssigneeVO();
                assignee.setUserId(person.getUserId());
                assignee.setUserName(person.getPersonName());
                assignee.setUserOrgId(person.getOrganId());
                assignee.setUserOrgName(person.getOrganName());*//*
                assignees[i] = assignee;
            }
        }*/
        UserInfoVO user = getProcessUserInfoVO(req);
        processService.createReadTasks(user, taskId, null,moduleCode,smsRemind);
        return getObject();
    }

    /**
     * 已阅
     *
     * @param req
     * @param taskId
     * @return
     */
    @RequestMapping("updateReadTask")
    @ResponseBody
    public Object updateReadTask(HttpServletRequest req,Long taskId,Long recordId,String moduleCode,String readAdvice){
        UserInfoVO user = getProcessUserInfoVO(req);
        processService.updateReadTask(recordId,user,taskId,moduleCode,readAdvice);
        return getObject();
    }
    /**
     * 流程图查看页面
     * @param model
     * @param processId
     * @return
     */
    @RequestMapping("diagramView")
    public ModelAndView diagramView(Model model,Long processId,Long recordId,Long activityId){
        model.addAttribute("processId", processId);//流程定义ID
        model.addAttribute("recordId", recordId);//实体主键
        model.addAttribute("activityId", activityId);//当前活动定义ID
        return new ModelAndView("process/diagram/diagram_view");
    }

    /**
     * 查看办理日志
     * @param model
     * @param procInstId
     * @param recordId
     * @return
     */
    @RequestMapping("processHistLog")
    public ModelAndView processHistLog(HttpServletRequest request,Model model,Long procInstId,Long recordId){
        model.addAttribute("hisLogs", this.processService.getHistLogs(getProcessUserInfoVO(request),procInstId,recordId));
        return new ModelAndView("process/process_hist_log");
    }

    /**
     * 展示当前活动下的任务
     * @param request
     * @param model
     * @param procInstId
     * @param actinstId
     * @return
     */
    @RequestMapping("activityTaskList")
    public ModelAndView activityTaskList(HttpServletRequest request,Model model,Long procInstId,Long actinstId){
        if(null == procInstId || null == actinstId)
            throw new BaseRunTimeException(TipsMode.Message.toString(),"非法参数");
        model.addAttribute("taskList", this.processService.getActivityTaskList(getProcessUserInfoVO(request), procInstId, actinstId));
        return new ModelAndView("process/activity_task_list");
    }

    @RequestMapping("processFallBack")
    public ModelAndView processFallBack(Model model,Long taskId,String moduleCode,Long recordId){
        model.addAttribute("taskId", taskId);
        model.addAttribute("moduleCode", moduleCode);
        model.addAttribute("recordId", recordId);
        return new ModelAndView("process/process_fall_back");
    }

    /**
     * 流程终止
     * @param model
     * @param procInstId
     * @param recordId
     * @param moduleCode
     * @return
     */
    @RequestMapping("processTerminate")
    public ModelAndView processTerminate(Model model,Long procInstId,Long recordId,String moduleCode,String title,Long dataId){
        model.addAttribute("procInstId", procInstId);
        model.addAttribute("recordId", recordId);
        model.addAttribute("moduleCode", moduleCode);
        model.addAttribute("dataId",dataId);
        try {
            model.addAttribute("title",URLDecoder.decode(title, "UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ModelAndView("process/process_terminate");
    }

    /**
     * 任务移交
     * @param model
     * @param taskId
     * @param moduleCode
     * @return
     */
    @RequestMapping("taskTransfer")
    public ModelAndView taskTransfer(Model model,Long taskId,String moduleCode){
        model.addAttribute("taskId", taskId);
        model.addAttribute("moduleCode", moduleCode);
        TaskVO task = processService.getRunTaskById(new UserInfoVO(),taskId);
        if(null != task){
            model.addAttribute("title",task.getTitle());
            model.addAttribute("oldAssigneeId",task.getAssigneeId());
            model.addAttribute("oldAssigneeName",task.getAssigneeName());
        }
        return new ModelAndView("process/task_transfer");
    }

    /**
     * 任务催办
     * @param model
     * @param taskId
     * @param moduleCode
     * @param docTitle
     * @param pressedName
     * @return
     */
    @RequestMapping("taskPress")
    public ModelAndView taskPress(Model model,Long taskId,String moduleCode,String docTitle,String pressedName){
        model.addAttribute("taskId", taskId);
        model.addAttribute("moduleCode", moduleCode);
        try {
            model.addAttribute("docTitle",URLDecoder.decode(docTitle, "UTF-8"));
            if(!AppUtil.isEmpty(pressedName))
                model.addAttribute("pressedName",URLDecoder.decode(pressedName, "UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ModelAndView("process/task_press");
    }




    /**
     * 移交列表
     * @param model
     * @return
     */
    @RequestMapping("taskTransferList")
    public ModelAndView taskTransferList(Model model,String moduleCode,String docTitle){
        model.addAttribute("docTitle",docTitle);
        model.addAttribute("moduleCode",moduleCode);
        return new ModelAndView("process/task_transfer_list");
    }

    /**
     * 催办列表
     * @param model
     * @return
     */
    @RequestMapping("taskPressList")
    public ModelAndView taskPressList(Model model,String moduleCode,String docTitle){
        model.addAttribute("docTitle",docTitle);
        model.addAttribute("moduleCode",moduleCode);
        return new ModelAndView("process/task_press_list");
    }



    /**
     * 获取流程列表
     * @return
     */
    @RequestMapping("getProcessList")
    @ResponseBody
    public Object  getProcessList(HttpServletRequest request,String moduleCode){
        try{
            EngineCode.valueOf(moduleCode);
        }catch (Exception e){
            return ajaxParamsErr("moduleCode");
        }
        Long siteId = LoginPersonUtil.getSiteId();
        List<ProcessVO> list = new ArrayList<ProcessVO>();
        try{
            list = processService.getProcessList(siteId,moduleCode);
        }catch (Exception e){}
        return getObject(list);
    }

    /**
     * 获取当前活动的后续活动
     * @param request
     * @return
     */
    @RequestMapping("getNextActivities")
    @ResponseBody
    public Object getNextActivities(HttpServletRequest request){
        Map<String,Object> paramMap = AppUtil.parseRequestToMap(request);
        return getObject(processService.getNextActivityControls(getProcessUserInfoVO(request),paramMap));
    }

    /**
     * 保存表单数据
     * @param request
     * @return
     */
    @RequestMapping("saveFormData")
    @ResponseBody
    private Object saveFormData(HttpServletRequest request) throws Exception{
        Map<String,Object> map = AppUtil.parseRequestToMap(request);
        Map<String,Object> result =  processService.saveFormData(getProcessUserInfoVO(request),map);
        return getObject(result);
    }


    /**
     * 流程启动
     * @param request
     * @return
     */
    @RequestMapping("processStart")
    @ResponseBody
    public Object processStart(HttpServletRequest request) throws Exception{
        Map<String,Object> map = AppUtil.parseRequestToMap(request);
        processService.processStart(getProcessUserInfoVO(request),map);

        sendMessageAndSetStatus(getProcessUserInfoVO(request),map,BaseContentEO.STATUS_HANDLING);

        return getObject();
    }

    private void sendMessageAndSetStatus(UserInfoVO userInfoVO, Map<String, Object> paramMap,Integer status){
        Object dataId = paramMap.get("dataId");
        if(!AppUtil.isEmpty(dataId)){
            BaseContentEO baseContentEO = baseContentService.getEntity(BaseContentEO.class,Long.parseLong(dataId.toString()));
            baseContentEO.setWorkFlowStatus(status);//办理中
            baseContentService.updateEntity(baseContentEO);


            //发送消息
            if(!AppUtil.isEmpty(paramMap.get("nextActivityName"))){
                MessageSystemEO message = new MessageSystemEO();
                message.setSiteId(baseContentEO.getSiteId());
                message.setTitle("待办事项");
                String columnName = ColumnUtil.getColumnName(baseContentEO.getColumnId(), baseContentEO.getSiteId());
                message.setContent(paramMap.get("nextActivityName").toString()+":"+columnName+":"+baseContentEO.getTitle());
                message.setRecUserIds(String.valueOf(userInfoVO.getUserId()));
                message.setMessageType(MessageSystemEO.TIP);
                message.setMessageStatus(MessageSystemEO.MessageStatus.success.toString());
                message.setCreateUserId(LoginPersonUtil.getUserId());
                message.setLink("/process/taskList");
                message.setResourceId(AppUtil.getLong(dataId));

                MessageSender.sendMessage(message);
            }
        }
    }

    /**
     * 流程流转
     */
    @RequestMapping("processRun")
    @ResponseBody
    public Object processRun(HttpServletRequest request) throws Exception{
        Map<String,Object> map = AppUtil.parseRequestToMap(request);
        processService.processRun(getProcessUserInfoVO(request),map);

        sendMessageAndSetStatus(getProcessUserInfoVO(request),map,BaseContentEO.STATUS_HANDLING);
        return getObject();
    }


    /**
     * 流程结束
     * @param request
     * @return
     */
    @RequestMapping("processComplete")
    @ResponseBody
    public Object  processComplete(HttpServletRequest request) throws Exception{
        Map<String,Object> map = AppUtil.parseRequestToMap(request);
        processService.processComplete(getProcessUserInfoVO(request),map);

        sendMessageAndSetStatus(getProcessUserInfoVO(request),map,BaseContentEO.STATUS_BANJIE);

        return getObject();
    }


    /**
     * 任务办理完毕
     * @param request
     * @return
     */
    @RequestMapping("taskFinish")
    @ResponseBody
    public Object  taskFinish(HttpServletRequest request) throws Exception{
        Map<String,Object> map = AppUtil.parseRequestToMap(request);
        processService.taskFinish(getProcessUserInfoVO(request), map);
        return getObject();
    }

    /**
     * 任务签收
     * @param request
     * @param taskId
     * @return
     */
    @RequestMapping("taskSign")
    @ResponseBody
    public Object taskSign(HttpServletRequest request,Long taskId){
        if(null == taskId)
            return ajaxParamsErr("taskId");
        processService.taskSign(getProcessUserInfoVO(request),taskId);
        return getObject();
    }

    /**
     * 任务检出
     * @param request
     * @param taskId
     * @return
     */
    @RequestMapping("claimTask")
    @ResponseBody
    public Object claimTask(HttpServletRequest request,Long taskId){
        if(null == taskId)
            return ajaxParamsErr("taskId");
        processService.claimTask(getProcessUserInfoVO(request), taskId);
        return getObject();
    }

    /**
     * 任务取消检出
     * @param request
     * @param taskId
     * @return
     */
    @RequestMapping("unClaimTask")
    @ResponseBody
    public Object unClaimTask(HttpServletRequest request,Long taskId){
        if(null == taskId) {
            return ajaxParamsErr("taskId");
        }
        processService.unClaimTask(getProcessUserInfoVO(request), taskId);
        return getObject();
    }

    private UserInfoVO getProcessUserInfoVO(HttpServletRequest request){
        HttpSession session = request.getSession(true);
        Long userId = SessionUtil.getLongProperty(session, "userId");
        String personName = SessionUtil.getStringProperty(session, "personName");
        Long organId = SessionUtil.getLongProperty(session, "organId");
        String organName = SessionUtil.getStringProperty(session, "organName");
        Long unitId = SessionUtil.getLongProperty(session, "unitId");
        String unitName = SessionUtil.getStringProperty(session, "unitName");
        return new UserInfoVO(personName,organId,organName,unitId,unitName,userId);
    }

    /**
     * 获取待办任务的按钮
     *
     * @param req
     * @param activityId
     * @param taskId
     * @return
     */
    @RequestMapping("getButtons")
    @ResponseBody
    public Object getButtons(HttpServletRequest req,Long activityId,Long taskId){
        //活动主键验证
        activetyIdIllegal(activityId);
        return getObject(processService.getButtons(getProcessUserInfoVO(req),activityId,taskId));
    }

    /**
     * 查看流程图
     * @param request
     * @param processId
     * @return
     */
    @RequestMapping("getDiagram")
    @ResponseBody
    public Object getDiagram(HttpServletRequest request,Long processId,Long recordId,Long activityId){
        if(null == processId)
            return ajaxParamsErr("processId");
        return getObject(processService.getDiagram(getProcessUserInfoVO(request),processId,recordId,activityId).getXml());
    }

    /**
     * 获取退回列表操作项
     * @param request
     * @param taskId
     * @return
     */
    @RequestMapping("getFallbackOptions")
    @ResponseBody
    public Object getFallbackOptions(HttpServletRequest request,Long procInstId,Long taskId){
        if(null == procInstId){
            return ajaxParamsErr("procInstId");
        }
        if(null == taskId){
            return ajaxParamsErr("taskId");
        }
        return getObject(processService.getFallbackOptions(getProcessUserInfoVO(request), procInstId, taskId));
    }

    /**
     * 提交退回操作
     * @param request
     * @return
     */
    @RequestMapping("fallBack")
    @ResponseBody
    public Object fallBack(HttpServletRequest request){
        Map<String,Object> map = AppUtil.parseRequestToMap(request);
        int flag = processService.fallBack(getProcessUserInfoVO(request), map);

        return getObject(flag);
    }



    /**
     * 流程终止
     * @param procInstId
     * @param recordId
     * @param reason
     * @return
     */
    @RequestMapping("terminate")
    @ResponseBody
    public Object terminate(HttpServletRequest request,Long procInstId,Long recordId,String reason,String moduleCode){
        if(null == procInstId)
            return ajaxParamsErr("procInstId");
        if(null == recordId)
            return ajaxParamsErr("recordId");
        if(AppUtil.isEmpty(reason))
            return ajaxErr("终止原因不能为空");
        else if(reason.length() > 100)
            return ajaxErr("终止原因不能超过100字");
        if(AppUtil.isEmpty(moduleCode))
            return ajaxParamsErr("moduleCode");
        processService.saveProcessTerminate(getProcessUserInfoVO(request),procInstId,recordId,reason,moduleCode);

        Object dataId = AppUtil.parseRequestToMap(request).get("dataId");
        if(!AppUtil.isEmpty(dataId)){
            BaseContentEO baseContentEO = baseContentService.getEntity(BaseContentEO.class,Long.parseLong(dataId.toString()));
            baseContentEO.setWorkFlowStatus(BaseContentEO.STATUS_STOP);//终止
            baseContentService.updateEntity(baseContentEO);
        }

        return getObject();
    }

    /**
     * 任务移交
     * @param request
     * @param taskId
     * @param moduleCode
     * @param reason
     * @param assigneeStr
     * @return
     */
    @RequestMapping("transfer")
    @ResponseBody
    public Object transfer(HttpServletRequest request,Long taskId,String moduleCode,String reason,String assigneeStr,Integer isSms){
        ReceiverVO assignee = null;//办理人
        if(null == taskId)
            return ajaxParamsErr("taskId");
        if(AppUtil.isEmpty(moduleCode))
            return ajaxParamsErr("moduleCode");
        if(AppUtil.isEmpty(reason))
            return ajaxErr("移交原因不能为空");
        else if(reason.length() > 100)
            return ajaxErr("移交原因不能超过100字");
        if(AppUtil.isEmpty(assigneeStr))
            return ajaxErr("办理人不能为空");
        else{
            ReceiverVO[] assignees = Jacksons.json().fromJsonToObject(assigneeStr,ReceiverVO[].class);
            if(assignees !=null && assignees.length>0){
                assignee = assignees[0];
            }else{
                return ajaxErr("办理人不能为空");
            }
        }

        processService.saveTransfer(getProcessUserInfoVO(request), moduleCode, taskId, reason, assignee, isSms);
        return getObject();
    }

    /**
     * 根据流程实例ID获取正在运行的任务列表
     * @param request
     * @param procInstId
     * @return
     */
    @RequestMapping("getRuExecutionTaskList")
    @ResponseBody
    public Object getRuExecutionTaskList(HttpServletRequest request,Long procInstId){
        if(null == procInstId)
            return ajaxParamsErr("procInstId");
        return getObject(processService.getRuExecutionTaskList(getProcessUserInfoVO(request),procInstId));
    }

    /**
     * 催办
     * @param request
     * @param moduleCode
     * @param taskId
     * @param pressReason
     * @param isSms
     * @return
     */
    @RequestMapping("press")
    @ResponseBody
    public Object press(HttpServletRequest request,String moduleCode,Long taskId,String pressReason,Integer isSms){
        if(AppUtil.isEmpty(moduleCode)){
            return ajaxParamsErr("moduleCode");
        }else{
            try {
                //ModuleCode.valueOf(moduleCode);
            } catch (Exception e){
                return ajaxParamsErr("moduleCode");
            }
        }
        if(null == taskId){
            return ajaxParamsErr("taskId");
        }
        processService.savePress(getProcessUserInfoVO(request),moduleCode,taskId,pressReason,isSms);
        return getObject();
    }

    @RequestMapping("getReaderTaskList")
    @ResponseBody
    public Object getReaderTaskList(HttpServletRequest request,Long procInstId){
        return getObject(processService.getReaderTaskList(getProcessUserInfoVO(request), procInstId));
    }

    /**
     * 获取下一步类型
     * @param request
     * @return
     */
    @RequestMapping("getNextStepType")
    @ResponseBody
    public Object getNextStepType(HttpServletRequest request){
        Map<String,Object> paramMap = AppUtil.parseRequestToMap(request);
        Integer type = processService.getNextStepType(getProcessUserInfoVO(request), paramMap);
        return getObject(type);
    }

    /**
     * 判断任务是否办理完毕
     * @param request
     * @param taskId
     * @return
     */
    @RequestMapping("getTaskIsComplete")
    @ResponseBody
    public Object getTaskIsComplete(HttpServletRequest request,Long taskId){
        if(null == taskId){
            return ajaxErr("参数不合法");
        }
        boolean isComplete = processService.getTaskIsComplete(getProcessUserInfoVO(request), taskId);
        return getObject(isComplete);
    }

    /**
     * 活动ID验证
     *
     * @param activityId
     */
    private void activetyIdIllegal(Long activityId){
        if(activityId==null||activityId<=0){
            throw new IllegalArgumentException();
        }
    }

    /**
     * 流程自动流转到发起人
     * @param request
     * @return
     */
    @RequestMapping("processAutoToOwner")
    @ResponseBody
    public Object processAutoToOwner(HttpServletRequest request) throws Exception{
        Map<String,Object> map = AppUtil.parseRequestToMap(request);
        processService.processAutoToOwner(getProcessUserInfoVO(request), map);
        return getObject();
    }

    /**
     * 获取活动定义
     * @param activityId
     * @return
     */
    @RequestMapping("getActivity")
    @ResponseBody
    public Object getActivity(HttpServletRequest request,Long activityId){
        if(null == activityId){
            return ajaxErr("参数不合法");
        }
        return getObject(processService.getActivity(getProcessUserInfoVO(request),activityId));
    }

    /**
     * 获取下一个活动默认办理人
     * @return
     */
    @RequestMapping("getDefaultHandler4NextActivity")
    @ResponseBody
    public Object getDefaultHandler4NextActivity(HttpServletRequest request){
        Map<String,Object> paramMap = AppUtil.parseRequestToMap(request);
        Map<String,Object> result = processService.getDefaultHandler4NextActivity(getProcessUserInfoVO(request),paramMap);
        return getObject(result);
    }

    /**
     * 获取流程发起人
     * @param taskId
     * @return
     */
    @RequestMapping("getProcessStarter")
    @ResponseBody
    public Object getProcessStarter(HttpServletRequest request,Long taskId){
        return getObject(processService.getProcessStarter(getProcessUserInfoVO(request),taskId));
    }

    /**
     *  已阅
     * @param model
     * @return
     */
    @RequestMapping("taskRead")
    public ModelAndView taskRead(Model model,Long taskId,Long recordId,String moduleCode){
        model.addAttribute("taskId",taskId);
        model.addAttribute("recordId",recordId);
        model.addAttribute("moduleCode",moduleCode);
        return new ModelAndView("process/task_read");
    }

    /**
     * 流程待办列表
     * @param model
     * @return
     */
    @RequestMapping("taskList")
    public ModelAndView taskList(Model model,String moduleCode){
        model.addAttribute("moduleCode",moduleCode);
        return new ModelAndView("process/task_list");
    }

    @RequestMapping(value="getTaskPagination")
    @ResponseBody
    public Object getTaskPagination(HttpServletRequest request,TaskQueryVO queryVO){
        if (AppUtil.isEmpty(queryVO.getPageIndex())){
            queryVO.setPageIndex(0L);
        }
        if (AppUtil.isEmpty(queryVO.getPageSize())) {
            queryVO.setPageSize(15);
        }
        Long[] swimlaneIds = getSwimlaneIds(request);
        return getObject(processService.getTaskPagination(getProcessUserInfoVO(request), swimlaneIds, queryVO));
    }

    private Long[] getSwimlaneIds(HttpServletRequest request){
        HttpSession session = request.getSession(false);
        Long[] swimlaneIds = null;
        if(null == session.getAttribute("swimlaneIds")){
            Long userId = SessionUtil.getLongProperty(session, "userId");
            Long organId = SessionUtil.getLongProperty(session,"organId");
            /**
             * 获取流程任务查询时的泳道参数(泳道由用户的角色ID和部门ID组成)
             */
            Long[] roleIds = null;//customFormModuleService.getRoleIds(organId,userId);
            if(null != roleIds){
                swimlaneIds = new Long[roleIds.length+1];
                for(int i=0,length = roleIds.length;i<length;i++){
                    swimlaneIds[i] = roleIds[i];
                }
                swimlaneIds[swimlaneIds.length-1] = organId;
            }else{
                swimlaneIds = new Long[]{organId};
            }
            session.setAttribute("swimlaneIds",swimlaneIds);
        }else{
            swimlaneIds = (Long[])session.getAttribute("swimlaneIds");
        }

        return swimlaneIds;
    }


    public String getFormHtml(ProcessFormForwardDataVO processForm) {
        /*if(null == processForm.getProcessForm().getFormId()){
            return "";
        }
        IForm formAPI = SpringContextHolder.getBean(IForm.class);
        ProcessFormEO processFormEO = processForm.getProcessForm();
        Long formId = null;
        Long recordId = null;
        String writableFields = null;
        String protectFields = null;
        String commentFields = null;
        ActivityFieldControlVO activityFieldControlVO = null;
        if(null !=processFormEO){
            formId = processFormEO.getFormId();
            recordId = processFormEO.getRecordId();
        }
        activityFieldControlVO = processForm.getActivityFieldControl();
        if(null !=activityFieldControlVO){
            writableFields = activityFieldControlVO.getWritableFields();
            protectFields = activityFieldControlVO.getProtectFields();
            commentFields = activityFieldControlVO.getCommentFields();
        }
        FormTemplateVO formTemplate = formAPI.getTemplate(formId);
        Map<String,FormFieldVO> formData = null;
        formData = formAPI.queryFormFieldMapForProcess(formId,
                recordId,
                writableFields,
                protectFields,
                commentFields);*/

        return null;//formAPI.inflaterTemplate(formData,formTemplate);
    }

    /**
     * 编辑正文
     * @param model
     * @param mainBodyId
     * @param organGenerationWordId
     * @param hasTemplate
     * @param hasSeal
     * @param editType(1:编辑正文 2:批阅征文)
     * @param viewBak（查看备份文件）
     * @return
     */
    @RequestMapping("editDocFile")
    public ModelAndView editDocFile(Model model,String mainBodyId,Long organGenerationWordId,Integer hasTemplate,Integer hasSeal,Integer editType,Integer viewBak) {
        if (!AppUtil.isEmpty(mainBodyId))
            model.addAttribute("recordId", mainBodyId);
        if (!AppUtil.isEmpty(hasTemplate)){//套红标记
            if(AppUtil.isEmpty(organGenerationWordId))
                throw new BaseRunTimeException(TipsMode.Key.toString(),"illegalArgumentException");
            model.addAttribute("hasTemplate", hasTemplate);
        }
        if (!AppUtil.isEmpty(hasSeal))//用印标记
            model.addAttribute("hasSeal", hasSeal);
        if(null != viewBak)
            model.addAttribute("viewBak", viewBak);
        model.addAttribute("editType",editType);
        String title = "";
        if(null != editType && editType.equals(1)){
            title = "编辑正文";
        }
        if(null != editType && editType.equals(2)){
            title = "批阅正文";
        }
        if(null != viewBak && viewBak.equals(1)){
            title = "查看原文";
        }
        if(null != hasTemplate && hasTemplate.equals(1) && null != hasSeal && hasSeal.equals(1)){
            title = "套红用印";
        }else if(null != hasTemplate && hasTemplate.equals(1)){
            title = "套红";
        }else if(null != hasSeal && hasSeal.equals(1)){
            title = "用印";
        }
        model.addAttribute("title",title);
        return new ModelAndView("process/edit_doc_file");
    }

    /**
     * 获取流程启动数据
     * @return
     */
    @RequestMapping("getProcessStartData")
    @ResponseBody
    public Object getProcessStartData(HttpServletRequest request,Long processId,String processName){
        ProcessVO vo = new ProcessVO();
        vo.setProcessId(processId);
        vo.setName(processName);
        ProcessStartVO processStart = processService.getProcessStartData(getProcessUserInfoVO(request),vo);
        Map<String,Object> result = new HashMap<String, Object>();
        result.put("processStartData",processStart);
        return getObject(result);
    }

    @RequestMapping("getProcessConfig")
    @ResponseBody
    public Object getProcessConfig(String contentModelCode){
        Map<String,Object> result = new HashMap<String, Object>();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("indicatorId",Long.parseLong(contentModelCode));//contentModelCode是栏目id
        params.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
//        ContentModelEO contentModelEO = contentModelService.getEntity(ContentModelEO.class,params);
        ColumnConfigEO columnConfigEO = columnConfigService.getEntity(ColumnConfigEO.class,params);
        if(columnConfigEO!=null){
            result.put("processId",columnConfigEO.getProcessId());
            result.put("processName",columnConfigEO.getProcessName());
        }
        return getObject(result);
    }
}
