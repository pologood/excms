package cn.lonsun.process.service.impl;

import cn.lonsun.activemq.MessageSender;
import cn.lonsun.base.DealStatus;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.common.vo.RecOrganVO;
import cn.lonsun.common.vo.RecRoleVO;
import cn.lonsun.common.vo.ReceiverVO;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.messageBoard.controller.internal.entity.MessageBoardEO;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.exception.util.Jacksons;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.form.FormReturnVO;
import cn.lonsun.form.IFormDataService;
import cn.lonsun.message.internal.entity.MessageSystemEO;
import cn.lonsun.process.container.ButtonsContainer;
import cn.lonsun.process.entity.ActivityUserSetEO;
import cn.lonsun.process.entity.ProcessAgentEO;
import cn.lonsun.process.invoke.BusinessHandlerFactory;
import cn.lonsun.process.invoke.IBusinessHandler;
import cn.lonsun.process.remind.enums.RemindWay;
import cn.lonsun.process.service.IActivityUserSetService;
import cn.lonsun.process.service.IProcessFormService;
import cn.lonsun.process.service.IProcessService;
import cn.lonsun.process.service.IProcessAgentService;
import cn.lonsun.process.vo.*;
import cn.lonsun.util.LoginPersonUtil;
import cn.lonsun.webservice.processEngine.ProcessEngineService;
import cn.lonsun.webservice.processEngine.ProcessHistService;
import cn.lonsun.webservice.processEngine.ProcessRuntimeService;
import cn.lonsun.webservice.processEngine.ProcessTaskService;
import cn.lonsun.webservice.processEngine.enums.EFieldControlType;
import cn.lonsun.webservice.processEngine.enums.EParticipationType;
import cn.lonsun.webservice.processEngine.enums.ETransactType;
import cn.lonsun.webservice.processEngine.enums.TaskStatus;
import cn.lonsun.webservice.processEngine.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 流程服务接口实现
 *@date 2014-12-15 15:27  <br/>
 *@author zhusy  <br/>
 *@version v1.0  <br/>
 */
@Service("processService")
public class ProcessServiceImpl implements IProcessService {

    @Autowired
    private ProcessEngineService processEngineService;

    @Autowired
    protected ProcessTaskService processTaskService;

    @Autowired
    private ProcessHistService processHistService;

    @Autowired
    private ProcessRuntimeService processRuntimeService;

    @Autowired
    private IProcessAgentService processAgentService;

    @Autowired
    private IActivityUserSetService activityUserSetService;

    @Autowired
    private IProcessFormService processFormService;


    /**
     * 获取流程过程包列表
     *
     * @param siteId
     * @param moduleCode
     * @return
     */
    @Override
    public List<PackageVO> getPackageList(Long siteId, String moduleCode) {

        // 从工作流引擎中获取单位设置的流程
        List<ProcessVO> processPackageList = processEngineService.getPackageProcessByThird(siteId, moduleCode);

        if (!(null != processPackageList && processPackageList.size() > 0))
            return null;

        // 过程包集合对象,用于返回数据
        List<PackageVO> packageList = new ArrayList<PackageVO>();

        PackageVO packageVO = null;
        ProcessVO processVO = null;

        for (Iterator<ProcessVO> iterator = processPackageList.iterator(); iterator.hasNext();) {

            processVO = iterator.next();

            // 根据流程获取所属过程包对象
            if ((packageVO = getPackageVO(packageList, processVO)) != null) {
                packageVO.getProcessList().add(processVO);
            } else {
                packageVO = new PackageVO();
                packageVO.setModuleId(processVO.getModuleId());
                packageVO.setPackageId(processVO.getPackageId());
                packageVO.setPackageName(processVO.getPackageName());
                List<ProcessVO> processList = new ArrayList<ProcessVO>();
                processList.add(processVO);
                packageVO.setProcessList(processList);
                packageList.add(packageVO);
            }
        }
        return packageList;
    }



    /**
     * 获取当前活动的后续活动
     *
     * @param userInfoVO
     * @param elementId
     * @return
     */
    @Override
    public List<ActivityVO> getNextActivities(UserInfoVO userInfoVO,Long elementId) {
        return this.processEngineService.getSuccessorActivities(userInfoVO,elementId);
    }


    /**
     * 获取当前活动的后续活动
     *
     * @param userInfoVO
     * @param paramMap
     * @return
     */
    @Override
    public List<ActivityVO> getNextActivities(UserInfoVO userInfoVO,Map<String,Object> paramMap) {
        Long elementId = AppUtil.getLong(paramMap.get("elementId"));
        return this.processEngineService.getSuccessorActivitiesByForm(userInfoVO, elementId, paramMap);
    }


    @Override
    public List<ActivityControlVO> getNextActivityControls(UserInfoVO userInfoVO, Long elementId, Long taskId) {
        List<ActivityVO> activities = getNextActivities(userInfoVO, elementId);
        if(null != activities && activities.size() > 0){
            ActivityControlVO vo = null;
            List<ActivityControlVO> activityControls = new ArrayList<ActivityControlVO>();
            ReceiverVO processStater = null;//流程发起人
            List<ReceiverVO> handlePersons = null;
            List<RecOrganVO> handleOrgans = null;
            List<RecRoleVO> handleRoles = null;
            EParticipationType participation = null;
            for(ActivityVO activity : activities){
                vo = new ActivityControlVO();
                participation = activity.getParticipation();
                vo.setElementId(activity.getElementId());
                vo.setName(activity.getName());
                vo.setIsLimit(activity.getIsLimit());
                vo.setParticipation(participation);
                vo.setTransactScope(activity.getTransactScope());
                vo.setAssigneeId(activity.getAssigneeId());
                vo.setAssigneeName(activity.getAssigneeName());
                vo.setAssigneeOrgId(activity.getAssigneeOrgId());
                vo.setAssigneeOrgName(activity.getAssigneeOrgName());
                vo.setCanAgent(Integer.valueOf(1).equals(activity.getCanAgent()));
                vo.setCanPeriodicAgent(activity.getCanPeriodicAgent());
                vo.setHasTransact(Integer.valueOf(1).equals(activity.getHasTransact()));
                vo.setIsEndActivity(Integer.valueOf(1).equals(activity.getEventType()));
                if(EParticipationType.promoter.equals(participation)){//参与者类型为发起人
                    handlePersons = new ArrayList<ReceiverVO>();
                    processStater = getProcessStarter(userInfoVO,taskId);
                    handlePersons.add(processStater);
                }else if(EParticipationType.candidate.equals(participation)){//指定办理人
                    handlePersons = getHandlePersons(activity);
                }else if(EParticipationType.dept.equals(participation)){//部门
                    handleOrgans = getHandleOrgans(activity);
                }else if(EParticipationType.role.equals(participation) || EParticipationType.roleUser.equals(participation)){//角色或角色用户
                    handleRoles = getHandleRoles(activity);
                }
                vo.setHandlePersons(handlePersons);
                vo.setHandleOrgans(handleOrgans);
                vo.setHandleRoles(handleRoles);
                activityControls.add(vo);
            }
            return activityControls;
        }
        return null;
    }

    /**
     * 流程启动服务
     *
     *
     * @param userInfoVO
     * @param paramMap
     * @return
     */
    @SuppressWarnings("unchecked")
    @Override
    public void processStart(UserInfoVO userInfoVO, Map<String, Object> paramMap) throws Exception{

        //业务数据处理
        String moduleCode = AppUtil.getValue(paramMap.get("moduleCode"));
        IBusinessHandler formHandler = BusinessHandlerFactory.getBusinessHandlerInstance(moduleCode);
        Map<String, Object> data = formHandler.start(userInfoVO,paramMap);//保存启动流程业务数据
        /**
         * 流程启动参数设置
         */
        StartProcessParam startProcessParam = StartProcessParam.newInstance();
        startProcessParam.setFormData(paramMap);
        startProcessParam.setNextActivityId(AppUtil.getLong(paramMap.get("elementId")));// 下一个活动ID
        startProcessParam.setTitle(AppUtil.getValue(data.get("title"))); // 流程标题
        startProcessParam.setRecordId(AppUtil.getLong(data.get("recordId"))); //业务实体ID
        if(null != data.get("businessData"))
            startProcessParam.setData((Map<String,String>)data.get("businessData"));
        /**
         * 设置办理方式
         */
        String transactType = AppUtil.getValue(paramMap.get("transactType"));
        startProcessParam.setTransactType(ETransactType.valueOf(transactType));
        /**
         * 设置是否限期
         */
        if(!AppUtil.isEmpty(paramMap.get("duedate"))){
            startProcessParam.setIsLimit(true);
            startProcessParam.setDuedate(AppUtil.formatStringToTime(AppUtil.getValue(paramMap.get("duedate")).toString(),"yyyy-MM-dd"));
        }
        /**
         * 设置办理人
         */
        Integer canPeriodicAgent = AppUtil.getInteger(paramMap.get("canPeriodicAgent"));
        UserInfoVO processStartUser = null;
        if(null != canPeriodicAgent && canPeriodicAgent.equals(1)){//是否流程发起人代理
            processStartUser = new UserInfoVO();
            processStartUser.setUserId(userInfoVO.getUserId());
            processStartUser.setOrgId(userInfoVO.getOrgId());
            processStartUser.setUserName(userInfoVO.getUserName());
        }
        List<AssigneeVO> taskAssigneeList = getTaskAssigneeList(paramMap,startProcessParam.getTransactType(),processStartUser);
        startProcessParam.setTaskAssigneeList(taskAssigneeList);
		/*
		 * 启动流程
		 */
        Long processId = AppUtil.getLong(paramMap.get("processId"));
        List<TaskVO> nextTasks = this.processEngineService.startProcessInstanceByIdOmitFristActi(userInfoVO, processId, startProcessParam);// 启动流程
		/*
		 * 流程启动后业务模块后续数据处理
		 */
        if(!(nextTasks != null && nextTasks.size()>0))
            throw new BaseRunTimeException(TipsMode.Message.toString(),"工作流引擎数据返回错误");
        //保存流程表单对象
        TaskVO task = nextTasks.get(0);
        formHandler.setProcessInfo(task.getProcinstId(),task.getActinstId(),task.getName(), task.getRecordId());

    }


    /**
     * 获取任务办理人
     * @param paramMap
     * @param eTransactType
     * @param processStartUser
     * @return
     */
    private List<AssigneeVO> getTaskAssigneeList(Map<String, Object> paramMap, ETransactType eTransactType, UserInfoVO processStartUser){
        List<AssigneeVO> taskAssigneeList = new ArrayList<AssigneeVO>();
        AssigneeVO assigneeVO = null;

        String participationStr = AppUtil.getValue(paramMap.get("participation"));//参与者类型
        EParticipationType participation = !"".equals(participationStr) ? EParticipationType.valueOf(participationStr) : null;
        if(EParticipationType.role.equals(participation)){//指定角色
            String recRolesStr = AppUtil.getValue(paramMap.get("recRoles"));
            RecRoleVO[] recRoles = null;
            if(!AppUtil.isEmpty(recRolesStr)){
                recRoles = Jacksons.json().fromJsonToObject(recRolesStr,RecRoleVO[].class);
                for(RecRoleVO recRole : recRoles){
                    assigneeVO = new AssigneeVO();
                    assigneeVO.setSwimlaneId(recRole.getRoleId());
                    assigneeVO.setSwimlaneName(recRole.getRoleName()+"("+recRole.getUnitName()+")");
                    assigneeVO.setSwimlaneUnitId(recRole.getUnitId());
                    assigneeVO.setHandletype(eTransactType);
                    assigneeVO.setType(participation);
                    taskAssigneeList.add(assigneeVO);
                }
            }else{
                throw new BaseRunTimeException(TipsMode.Message.toString(),"未选择办理角色");
            }
        }else if(EParticipationType.dept.equals(participation)){//指定部门办理
            String recOrganStr = AppUtil.getValue(paramMap.get("recOrgans"));
            RecOrganVO[] recOrgans = null;
            if(!AppUtil.isEmpty(recOrganStr)){
                recOrgans = Jacksons.json().fromJsonToObject(recOrganStr,RecOrganVO[].class);
                for(RecOrganVO recOrgan : recOrgans){
                    assigneeVO = new AssigneeVO();
                    assigneeVO.setSwimlaneId(recOrgan.getOrganId());
                    assigneeVO.setSwimlaneName(recOrgan.getOrganName());
                    assigneeVO.setHandletype(eTransactType);
                    assigneeVO.setType(participation);
                    taskAssigneeList.add(assigneeVO);
                }
            }else{
                throw new BaseRunTimeException(TipsMode.Message.toString(),"未选择办理部门");
            }
        }else{
            String receiverStr = AppUtil.getValue(paramMap.get("receivers"));
            ReceiverVO[] receivers = null;
            if (!AppUtil.isEmpty(receiverStr)) {
                receivers = Jacksons.json().fromJsonToObject(receiverStr, ReceiverVO[].class);
                int isAgency = AppUtil.getint(paramMap.get("isAgency"));//是否代填
                Map<String,ProcessAgentEO> agents = null;
                String moduleCode = AppUtil.getValue(paramMap.get("moduleCode"));
                if(1 == isAgency){
                    agents = processAgentService.getAgents(receivers,moduleCode);//获取所有办理人代填对象集合
                }
                ProcessAgentEO agentEO = null;
                int i = 0;
                for (ReceiverVO receiverVO : receivers) {
                    assigneeVO = new AssigneeVO();
                    assigneeVO.setUserId(receiverVO.getUserId());
                    assigneeVO.setUserName(receiverVO.getPersonName());
                    assigneeVO.setUserOrgId(receiverVO.getOrganId());
                    assigneeVO.setUserOrgName(receiverVO.getOrganName());
                    assigneeVO.setHandletype(eTransactType);// 办理方式
                    assigneeVO.setType(participation);//参与者类型
                    if(null != agents && agents.size() >0){ //设置代填人
                        if(null != (agentEO = agents.get(receiverVO.getUserId().toString()+receiverVO.getOrganId().toString()))){
                            assigneeVO.setAgentIds(agentEO.getAgentUserIds());
                            assigneeVO.setAgentNames(agentEO.getAgentPersonNames());
                            assigneeVO.setAgentOrgIds(agentEO.getAgentOrganIds());
                        }
                    }
                    if(null != processStartUser){//流程发起人代理
                        boolean isSameDealer = assigneeVO.getUserId().equals(processStartUser.getUserId()) && assigneeVO.getUserOrgId().equals(processStartUser.getOrgId());//办理人与流程发起人是否为同一人
                        boolean isContainAgent = false;//办理人的代填人中是否包含流程发起人
                        String agentIds = assigneeVO.getAgentIds();
                        String agentOrgIds = assigneeVO.getAgentOrgIds();
                        if(!AppUtil.isEmpty(agentIds) && !AppUtil.isEmpty(agentOrgIds)){
                            String[] agentIdArray = agentIds.split(",");
                            String[] agentOrgIdArray = agentOrgIds.split(",");
                            for(int j=0,length=agentIdArray.length;j<length;j++){
                                if(agentIdArray[j].equals(String.valueOf(processStartUser.getUserId())) && agentOrgIdArray[j].equals(String.valueOf(processStartUser.getOrgId()))){
                                    isContainAgent = true;
                                }
                            }
                        }
                        if(!isSameDealer && !isContainAgent){
                            assigneeVO.setAgentIds(AppUtil.isEmpty(agentIds) ? String.valueOf(processStartUser.getUserId()) : ","+processStartUser.getUserId());
                            assigneeVO.setAgentOrgIds(AppUtil.isEmpty(agentOrgIds) ? String.valueOf(processStartUser.getOrgId()) : ","+processStartUser.getOrgId());
                            assigneeVO.setAgentNames(AppUtil.isEmpty(assigneeVO.getAgentNames()) ? String.valueOf(processStartUser.getUserName()) : ","+processStartUser.getUserName());
                        }
                    }
                    taskAssigneeList.add(assigneeVO);
                }
            }else{
                throw new BaseRunTimeException(TipsMode.Message.toString(),"未选择办理人");
            }
        }
        return taskAssigneeList;
    }

    /**
     * 流程流转
     *
     *
     * @param userInfoVO
     * @param paramMap
     * @return
     */
    @Override
    public int processRun(UserInfoVO userInfoVO, Map<String, Object> paramMap) throws Exception{
        final Long taskId = AppUtil.getLong(paramMap.get("taskId"));// 当前任务ID
        TaskVO curTask = getRunTaskById(userInfoVO, taskId);
        if(null == curTask){
            throw new BaseRunTimeException(TipsMode.Message.toString(),"已经被办理,不可重复办理,请关闭或刷新当前页面");
        }
        validateTaskAssignee(curTask,userInfoVO);//任务办理人验证
        paramMap.put("taskVO",curTask);
        // 业务数据处理
        final String moduleCode = AppUtil.getValue(paramMap.get("moduleCode"));
        IBusinessHandler formHandler = BusinessHandlerFactory.getBusinessHandlerInstance(moduleCode);
        Map<String,Object> data = formHandler.run(userInfoVO,paramMap);// 保存业务后返回的数据
        // 任务办理参数对象
        TaskHandleParam taskHandleParam = new TaskHandleParam();
        taskHandleParam.setFormData(paramMap);
        taskHandleParam.setRecordId(Long.valueOf(data.get("recordId").toString())); // 业务实体ID
        taskHandleParam.setTitle(AppUtil.getValue(data.get("title")));// 任务标题
        taskHandleParam.setNextActivityId(AppUtil.getLong(paramMap.get("elementId")));// 下一个活动ID
        if(null != data.get("businessData"))
            taskHandleParam.setData((Map<String,String>)data.get("businessData"));
        /**
         * 设置任务办理方式
         */
        String transactType = AppUtil.getValue(paramMap.get("transactType"));
        taskHandleParam.setTransactType(ETransactType.valueOf(transactType));
        /**
         * 设置是否限期
         */
        if(!AppUtil.isEmpty(paramMap.get("duedate"))){
            taskHandleParam.setIsLimit(true);
            taskHandleParam.setDuedate(AppUtil.formatStringToTime(paramMap.get("duedate").toString(),"yyyy-MM-dd"));
        }
        /**
         * 设置办理人
         */
        Integer canPeriodicAgent = AppUtil.getInteger(paramMap.get("canPeriodicAgent"));
        UserInfoVO processStartUser = null;
        if(null != canPeriodicAgent && canPeriodicAgent.equals(1)){//是否流程发起人代理
            processStartUser = new UserInfoVO();
            processStartUser.setUserId(curTask.getCreateUserId());
            processStartUser.setOrgId(curTask.getCreateOrgId());
            processStartUser.setUserName(curTask.getCreateUser());
        }
        List<AssigneeVO> taskAssigneeList = getTaskAssigneeList(paramMap,taskHandleParam.getTransactType(), processStartUser);
        taskHandleParam.setTaskAssigneeList(taskAssigneeList);
        final List<TaskVO> nextTasks = this.processTaskService.complete(userInfoVO, taskId,taskHandleParam);
        if(null !=nextTasks && nextTasks.size() > 0){
            /**
             * 给业务设置当前活动信息
             */
            TaskVO task = nextTasks.get(0);
            Long actinstId = task.getActinstId();
            String activityName = task.getName();
            Long recordId = task.getRecordId();
            formHandler.setCurActivity(actinstId,activityName,recordId);
        }
        return 1;
    }


    /**
     * 验证任务办理人的合法性(针对任务移交操作后,任务办理人更换后,原任务办理人未关闭页面继续办理该任务)
     * @param task
     * @param userInfo
     */
    private void validateTaskAssignee(TaskVO task,UserInfoVO userInfo){
        Long assigneeId = task.getAssigneeId();
//        Long assigneeOrgId = task.getAssigneeOrgId();
        boolean isAssignee = userInfo.getUserId().equals(assigneeId) ;//&& userInfo.getOrgId().equals(assigneeOrgId);//当前用户是否是任务办理人
        boolean isAgent = false;//当前用户是否是任务代理人
        //boolean isPartTimeUser = userInfo.getUserId().equals(assigneeId) && !userInfo.getOrgId().equals(assigneeOrgId);//当前用户是否是任务办理人的兼职用户
        String agentIds = task.getAgentId();
        String agentOrgIds = task.getAgentOrgId();
        if(!AppUtil.isEmpty(agentIds) && !AppUtil.isEmpty(agentOrgIds)){
            String[] agentIdArray = agentIds.split(",");
            String[] agentOrgIdArray = agentOrgIds.split(",");
            for(int j=0,length=agentIdArray.length;j<length;j++){
                if(agentIdArray[j].equals(String.valueOf(userInfo.getUserId())) && agentOrgIdArray[j].equals(String.valueOf(userInfo.getOrgId()))){
                    isAgent = true;
                    break;
                }
            }
        }
//        if(isPartTimeUser){
//            throw new BaseRunTimeException(TipsMode.Message.toString(),"办理操作不合法,请切换至对应的兼职用户账号中进行办理");
//        }
        if(!isAssignee &&  !isAgent){
            throw new BaseRunTimeException(TipsMode.Message.toString(),"办理操作不合法,已被移交给"+task.getAssigneeName()+"办理");
        }
    }

    /**
     * 流程办结
     *
     * @param paramMap
     * @return
     */
    @Override
    public int processComplete(UserInfoVO userInfoVO,Map<String, Object> paramMap) throws Exception{
        Long taskId = AppUtil.getLong(paramMap.get("taskId"));// 当前任务ID
        TaskVO curTask = getRunTaskById(userInfoVO, taskId);
        if(null == curTask){
            throw new BaseRunTimeException(TipsMode.Message.toString(),"已经被办结,不可重复办结,请关闭或刷新当前页面");
        }
        validateTaskAssignee(curTask,userInfoVO);//任务办理人验证
        // 业务数据处理
        IBusinessHandler formHandler = BusinessHandlerFactory.getBusinessHandlerInstance(AppUtil.getValue(paramMap.get("moduleCode")));
        formHandler.complete(userInfoVO,paramMap);
        TaskHandleParam taskHandleParam = new TaskHandleParam();
        taskHandleParam.setFormData(paramMap);
        if(null != paramMap.get("elementId")){
            taskHandleParam.setNextActivityId(AppUtil.getLong(paramMap.get("elementId")));// 下一个活动ID
        }
        this.processTaskService.complete(userInfoVO,taskId,taskHandleParam);
        return 1;
    }


    /**
     * 获取流程待办任务分页列表
     *
     * @param userInfoVO
     * @param queryVO
     * @return
     */
    @Override
    public Pagination getTaskPagination(UserInfoVO userInfoVO, Long[] swimlaneIds, TaskQueryVO queryVO) {
        Pagination pagination = new Pagination();
        pagination.setPageIndex(queryVO.getPageIndex());
        pagination.setPageSize(queryVO.getPageSize());
        QueryTask queryTask = new QueryTask();
        queryTask.setTitle(queryVO.getTitle());
        queryTask.setName(queryVO.getDealStep());
        queryTask.setModuleCode(queryVO.getModuleCode());
        if(null != queryVO.getStartDate()){
            queryTask.setArriveStartDate(queryVO.getStartDate());
        }
        if(null != queryVO.getEndDate()){
            Calendar date = Calendar.getInstance();
            date.setTime(queryVO.getEndDate());
            date.set(Calendar.DATE, date.get(Calendar.DATE) + 1);//结束日期增加一天
            queryTask.setArriveEndDate(date.getTime());
        }
        pagination = this.processTaskService.getTaskByUser(userInfoVO,pagination,queryTask,userInfoVO.getUserId(),swimlaneIds);
        return  pagination;
    }


    /**
     * 根据流程获取所属过程包对象
     *
     * @param packageList
     * @param vo
     * @return
     */
    private PackageVO getPackageVO(List<PackageVO> packageList, ProcessVO vo) {
        for (PackageVO packageVO : packageList)
            if (packageVO.getPackageId().longValue() == vo.getPackageId().longValue())
                return packageVO;
        return null;
    }



    @Override
    public FormFieldControlVO getEditableFields4Activity(UserInfoVO userInfo,Long activityId) {
        // 可编辑字段类型枚举数组
        EFieldControlType[] fieldControlTypes = new EFieldControlType[] {EFieldControlType.writeable, EFieldControlType.comment,EFieldControlType.protectable };
        // 获取可编辑字段列表
        List<FieldVO> list = processTaskService.getFieldControls(userInfo,activityId,fieldControlTypes);

        FormFieldControlVO vo = new FormFieldControlVO();
        // 构造返系统的可编辑字段对象
        if (list != null && list.size() > 0) {
            vo = new FormFieldControlVO();
            //可写字段
            Map<String,String> writeFields = new HashMap<String, String>();
            //受保护可写字段
            Map<String,String> protectFields = new HashMap<String, String>();
            //批示意见字段
            Map<String,String> commentFields = new HashMap<String, String>();

            String controlType = "";
            for (FieldVO field : list) {
                controlType = field.getControlType().toString();
                //可写字段
                if(EFieldControlType.writeable.toString().equals(controlType)) {
                    writeFields.put(field.getFieldName(),controlType);
                    continue;
                }
                //受保护可写字段
                if(EFieldControlType.protectable.toString().equals(controlType)){
                    protectFields.put(field.getFieldName(),controlType);
                    continue;
                }
                //批示意见字段
                if(EFieldControlType.comment.toString().equals(controlType)){
                    commentFields.put(field.getFieldName(),controlType);
                }
            }
            if(protectFields.size() > 0){
                for(String key : protectFields.keySet()){
                    if(commentFields.containsKey(key)){
                        commentFields.put(key,EFieldControlType.protectable.toString());
                        if(!vo.isHasProtectFields()){
                            vo.setHasProtectFields(true);
                        }
                    }else if(writeFields.containsKey(key)){
                        writeFields.put(key,EFieldControlType.protectable.toString());
                        if(!vo.isHasProtectFields()){
                            vo.setHasProtectFields(true);
                        }
                    }
                }
            }
            vo.setEditableFields(writeFields);
            vo.setCommentFields(commentFields);

        }
        return vo;
    }
    @Override
    public List<Button> getButtons(UserInfoVO userInfo, Long activityId, Long taskId) {
        // 按钮属于-待办任务
        TaskStatus taskStatus = TaskStatus.waitDeal;

        Button button = null;
        /**
         * 构建流程提交按钮（下一步/办理完毕/办结/已阅）
         */
        if(null != taskId){
            Integer type = getNextStepType(userInfo, taskId);
            if(null == type)
                throw new BaseRunTimeException(TipsMode.Message.toString(),"获取下一步按钮类型异常");
            button = getButtonByNextStepType(type);
            if(2 == type.intValue()){
                taskStatus =  TaskStatus.waitRead;
            }
        }else{
            button = ButtonsContainer.BUTTONS_MAP.get("step_next");
        }

        // 获取活动配置按钮
        List<Button> buttons = processTaskService.getActivityOpertions(userInfo,activityId, taskStatus);

        //将button放置列表最前
        List<Button> result = new ArrayList<Button>(buttons.size()+1) ;
        result.add(button);
        result.addAll(buttons);
        return result;
    }

    /**
     * 获取下一步办理类型
     *
     *
     * @param userInfoVO
     * @param taskId        当前任务ID
     * @return (0: 下一步 1:办理完毕 100:办结)
     */
    @Override
    public Integer getNextStepType(UserInfoVO userInfoVO,  Long taskId) {
        return this.processTaskService.getSuccessorTypeByTaskId(userInfoVO,taskId);
    }

    @Override
    public Integer getNextStepType(UserInfoVO userInfoVO, Map<String, Object> paramMap) {
        Long taskId = AppUtil.getLong(paramMap.get("taskId"));
        return this.processTaskService.getSuccessorTypeByTaskIdByForm(userInfoVO, taskId, paramMap);
    }

    /**
     * 任务办理完毕
     *
     * @param userInfoVO
     * @param paramMap
     * @return
     */
    @Override
    public int taskFinish(UserInfoVO userInfoVO, Map<String, Object> paramMap) throws Exception{

        Long taskId = AppUtil.getLong(paramMap.get("taskId"));// 当前任务ID
        TaskVO curTask = getRunTaskById(userInfoVO, taskId);
        if(null == curTask){
            throw new BaseRunTimeException(TipsMode.Message.toString(),"已经被办理,不可重复办理。请关闭或刷新当前页面");
        }
        validateTaskAssignee(curTask,userInfoVO);//任务办理人验证
        paramMap.put("taskVO",curTask);
        // 业务数据处理
        final String moduleCode = AppUtil.getValue(paramMap.get("moduleCode"));
        IBusinessHandler formHandler = BusinessHandlerFactory.getBusinessHandlerInstance(moduleCode);
        formHandler.run(userInfoVO,paramMap);
        TaskHandleParam taskHandleParam = new TaskHandleParam();
        List<TaskVO> nextTasks = this.processTaskService.complete(userInfoVO, taskId,taskHandleParam);
        return 1;

    }

    /**
     * 获取已办流程按钮
     *
     * @param userInfoVO
     * @param processId
     * @return
     */
    @Override
    public List<Button> getButtonsByTaskStatus(UserInfoVO userInfoVO, Long processId,TaskStatus taskStatus) {
        if(null == taskStatus) return new ArrayList<Button>(0);
        return this.processEngineService.getActivityOpertions(userInfoVO,processId,taskStatus);
    }

    /**
     * 获取流程
     *
     * @param siteId
     * @param moduleCode
     * @return
     */
    @Override
    public List<ProcessVO> getProcessList(Long siteId, String moduleCode) {

        // 从工作流引擎中获取设置的流程
        List<ProcessVO> list = processEngineService.getPackageProcessByThird(siteId, moduleCode);
        return null != list ? list : new ArrayList<ProcessVO>(0);
    }

    /**
     * 获取手工登记按钮
     *
     * @return
     */
    @Override
    public List<Button> getButtons4ManulRegister() {
        List<Button> list =  new ArrayList<Button>();
        list.add(ButtonsContainer.BUTTONS_MAP.get("step_next"));//下一步
        list.add(ButtonsContainer.BUTTONS_MAP.get("edit_doc"));//编辑正文
        list.add(ButtonsContainer.BUTTONS_MAP.get("edit_attachment"));//编辑附件
        list.add(ButtonsContainer.BUTTONS_MAP.get("print"));//打印
        list.add(ButtonsContainer.BUTTONS_MAP.get("register_finish"));//登记办结
        list.add(ButtonsContainer.BUTTONS_MAP.get("save_form"));//保存
        return list;
    }

    /**
     * 获取登记办结查看表单按钮
     *
     * @return
     */
    @Override
    public List<Button> getButtons4RegisterFinishViewForm() {
        List<Button> list =  new ArrayList<Button>();
        list.add(ButtonsContainer.BUTTONS_MAP.get("view_doc"));//查看正文
        list.add(ButtonsContainer.BUTTONS_MAP.get("view_attachment"));//查看附件
        list.add(ButtonsContainer.BUTTONS_MAP.get("print"));//打印
        return list;
    }

    /**
     * 获取流程图
     * @param userInfoVO
     * @param processId
     * @param recordId
     * @param activityId
     * @return
     */
    public DiagramVO getDiagram(UserInfoVO userInfoVO,Long processId,Long recordId,Long activityId){
        return this.processHistService.getDiagram(userInfoVO,processId,recordId,activityId);
    }

    /**
     * 任务签收
     *
     * @param userInfoVO
     * @param taskId
     * @return
     */
    @Override
    public Boolean taskSign(UserInfoVO userInfoVO, Long taskId) {
        return this.processTaskService.signTask(userInfoVO,taskId);
    }



    /**
     * 获取办理日志
     *
     * @param userInfoVO
     * @param procInstId
     * @param recordId
     * @return
     */
    @Override
    public List<HistLogVO> getHistLogs(UserInfoVO userInfoVO, Long procInstId, Long recordId) {
        if(null == procInstId || null == recordId)
            return null;
        return processHistService.getHistLogByRecordId(userInfoVO,procInstId,recordId);
    }

    /**
     * 获取退回列表操作项
     *
     * @param userInfoVO
     * @param procInstId
     *@param taskId  @return
     */
    @Override
    public FallbackVO getFallbackOptions(UserInfoVO userInfoVO, Long procInstId, Long taskId){
        if(null == procInstId || null == taskId)
            return null;
        return this.processHistService.getFallbackOptions(userInfoVO,procInstId,taskId);
    }

    /**
     * 提交退回操作
     *
     * @param userInfoVO
     * @return
     */
    @Override
    public int fallBack(UserInfoVO userInfoVO,Map<String,Object> paramMap){
        Long recordId = AppUtil.getLong(paramMap.get("recordId"));
        String moduleCode = AppUtil.getValue(paramMap.get("moduleCode"));
        IBusinessHandler processFormHandler = BusinessHandlerFactory.getBusinessHandlerInstance(moduleCode);
        processFormHandler.backBusinessValidate(recordId);//退回操作业务验证


        Long actinstId = AppUtil.getLong(paramMap.get("actinstId"));
        Long taskId = AppUtil.getLong(paramMap.get("taskId"));
        String backReason = AppUtil.getValue(paramMap.get("backReason"));
        List<TaskVO> nextTasks = this.processHistService.fallbackTask(userInfoVO,actinstId,taskId,backReason);//调用工作服务进行退回操作
        if(nextTasks != null && nextTasks.size()>0){
            TaskVO task = nextTasks.get(0);
            paramMap.put("recordId",task.getRecordId());
            paramMap.put("nextActinstId",task.getActinstId());
            paramMap.put("nextActivityName",task.getName());
            processFormHandler.back(userInfoVO,paramMap);//退回后业务处理
            //customFormRemindService.asynchronousSendRemindAndDealMessage(nextTasks, moduleCode, taskId, new RemindWay[]{RemindWay.Message});
        }
        processFormHandler.saveSystemLog(recordId,"退回");
        return 1;
    }


    /**
     * 分送
     * @param user
     * @param taskId
     * @param assignees
     * @param moduleCode
     * @param smsRemind
     * @return
     */
    @Override
    public boolean createReadTasks(UserInfoVO user, Long taskId, AssigneeVO[] assignees, String moduleCode, Integer smsRemind) {
        List<TaskVO> nextTasks = processHistService.startReaderTask(user, taskId, assignees);
        //发送消息
        if(nextTasks != null && nextTasks.size()>0){
            RemindWay[] remindWays;
            if(null != smsRemind && smsRemind.equals(1)){
                remindWays = new RemindWay[]{RemindWay.Message,RemindWay.SMS};
            }else{
                remindWays = new RemindWay[]{RemindWay.Message};
            }
            //customFormRemindService.asynchronousSendRemind(nextTasks,moduleCode,remindWays);
        }
        return true;
    }

    @Override
    public boolean updateReadTask(Long recordId, UserInfoVO user, Long taskId, String moduleCode, String readAdvice) {
        IBusinessHandler formHandler = BusinessHandlerFactory.getBusinessHandlerInstance(moduleCode);
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("recordId",recordId);
        map.put("taskId",taskId);
        map.put("activityName","传阅");
        formHandler.saveReadLog(map);
        boolean result = processHistService.completeReaderTask(user,taskId,readAdvice);
        if(result){
            //customFormRemindService.asynchronousDealMessage(taskId);//消息置为已办
        }
        return result;
    }

    /**
     * 保存流程终止操作
     *  @param userInfoVO
     * @param procInstId
     * @param recordId
     * @param reason
     * @param moduleCode
     */
    @Override
    public void saveProcessTerminate(UserInfoVO userInfoVO, Long procInstId, Long recordId, String reason, String moduleCode) {

        this.processRuntimeService.terminateProcessInstanceById(userInfoVO,procInstId,reason);//调用工作流引擎服务终止流程
        /**
         * 业务数据处理
         */
        IBusinessHandler formHandler = BusinessHandlerFactory.getBusinessHandlerInstance(moduleCode);
        formHandler.terminate(recordId,reason);
    }

    /**
     * 根据流程实例ID获取正在运行的任务列表
     *
     * @param userInfoVO
     * @param procInstId
     * @return
     */
    @Override
    public List<TaskVO> getRuExecutionTaskList(UserInfoVO userInfoVO, Long procInstId) {
        return processRuntimeService.getRuExecutionTaskListById(userInfoVO,procInstId);
    }

    /**
     * 任务移交
     *  @param userInfoVO
     * @param moduleCode
     * @param taskId
     * @param reason
     * @param assignee
     * @param isSms
     */
    @Override
    public int saveTransfer(UserInfoVO userInfoVO, String moduleCode, Long taskId, String reason, ReceiverVO assignee, Integer isSms) {

        TaskVO task = getRunTaskById(userInfoVO,taskId);

        //调用流程引擎移交服务
        UserInfoVO delegateUserInfo = new  UserInfoVO(
                assignee.getPersonName(),
                assignee.getOrganId(),
                assignee.getOrganName(),
                assignee.getUnitId(),
                assignee.getUnitName(),
                assignee.getUserId());
        this.processHistService.delegateTask(userInfoVO,taskId,delegateUserInfo,reason);

        task.setAssigneeId(assignee.getUserId());
        task.setAssigneeOrgId(assignee.getOrganId());
        task.setAgentId("");
        task.setAgentOrgId("");
        RemindWay[] remindWays = null;
        if(null != isSms && isSms.equals(1)){
            remindWays = new RemindWay[]{RemindWay.Message,RemindWay.SMS};
        }else{
            remindWays = new RemindWay[]{RemindWay.Message};
        }
        //customFormRemindService.asynchronousSendRemindAndDealMessage(task, moduleCode,taskId,remindWays);
        BusinessHandlerFactory.getBusinessHandlerInstance(moduleCode).saveSystemLog(task.getRecordId(), "移交操作");
        return 1;
    }


    /**
     * 根据TaskId获取未办理的任务
     * @param userInfoVO
     * @param taskId
     * @return
     */
    @Override
    public TaskVO getRunTaskById(UserInfoVO userInfoVO,Long taskId){
        TaskVO task = null;
        try{
            task = this.processTaskService.getRuTaskById(userInfoVO,taskId);
        }catch(Exception e){
            return null;
        }
        return task;
    }

    /**
     * 催办
     * @param userInfoVO
     * @param moduleCode
     * @param taskId
     * @param pressReason
     * @param isSms
     */
    @Override
    public void savePress(UserInfoVO userInfoVO, String moduleCode, Long taskId, String pressReason, Integer isSms) {
        /*TaskVO task = getRunTaskById(userInfoVO,taskId);
        if(null == task)
            throw new BaseRunTimeException(TipsMode.Message.toString(),"该任务已办理完毕");
        *//**
         * 增加催办记录
         *//*
        ProcessPressEO eo = new ProcessPressEO();
        eo.setProcInstId(task.getProcinstId());
        eo.setActInstId(task.getActinstId());
        eo.setActivityName(task.getName());
        eo.setTaskId(taskId);
        eo.setPressReason(pressReason);
        eo.setCreatePersonName(ContextHolderUtils.getPersonName());
        boolean isRole =  task.getAssignType() != null ? EParticipationType.role.equals(task.getAssignType()) && task.getAssigneeId() == null : false;
        boolean isDept =  task.getAssignType() != null ? EParticipationType.dept.equals(task.getAssignType()) && task.getAssigneeId() == null : false;
        if(isRole){//任务办理人为空则为指定角色办理
            eo.setPressedId(task.getSwimlaneId());
            eo.setPressedOrganId(task.getSwimlaneUnitId());
            eo.setPressedName(task.getSwimlaneName());
            eo.setPressedType(ProcessPressEO.PressedType.Role.getValue());//催办对象为指定角色
        }else if(isDept){
            eo.setPressedId(task.getSwimlaneId());
            eo.setPressedOrganId(task.getSwimlaneUnitId());
            eo.setPressedName(task.getSwimlaneName());
            eo.setPressedType(ProcessPressEO.PressedType.Dept.getValue());//催办对象为指定部门
        }else{
            eo.setPressedId(task.getAssigneeId());
            eo.setPressedOrganId(task.getAssigneeOrgId());
            eo.setPressedName(task.getAssigneeName());
            eo.setPressedType(ProcessPressEO.PressedType.User.getValue());//催办对象为指定用户
        }
        this.processPressService.saveEntity(eo);
        RemindWay[] remindWays = null;
        if(null != isSms && isSms.equals(1)){
            remindWays = new RemindWay[]{RemindWay.Message,RemindWay.SMS};
        }else{
            remindWays = new RemindWay[]{RemindWay.Message};
        }
        docRemindService.asynchronousSendRemind(task, moduleCode, true,remindWays);
        FormHandlerFactory.getBusinessHandlerInstance(moduleCode).saveSystemLog(task.getRecordId(), "催办");*/
    }

    /**
     * 保存任务检出
     *
     * @param userInfoVO
     * @param taskId
     * @return
     */
    @Override
    public Boolean claimTask(UserInfoVO userInfoVO, Long taskId) {
        return this.processHistService.claimTask(userInfoVO, taskId);
    }

    /**
     * 任务取消检出
     *
     * @param userInfoVO
     * @param taskId
     * @return
     */
    @Override
    public Boolean unClaimTask(UserInfoVO userInfoVO, Long taskId) {
        return this.processHistService.unclaimTask(userInfoVO,taskId);
    }

    /**
     * 获取任务检出操作时显示的按钮
     *
     * @param moduleCode
     * @return
     */
    @Override
    public List<Button> getClaimButtons(String moduleCode) {
        List<Button> buttons = new ArrayList<Button>();
        buttons.add(ButtonsContainer.BUTTONS_MAP.get("claim"));//检出
        /*if(ModuleCode.sendDoc.equals(moduleCode)){
            buttons.add(ButtonsContainer.BUTTONS_MAP.get("view_doc"));//查看正文
            buttons.add(ButtonsContainer.BUTTONS_MAP.get("view_attachment"));//查看附件
            buttons.add(ButtonsContainer.BUTTONS_MAP.get("view_processHistLog"));//查看办理日志
        }else if(ModuleCode.receiveDoc.equals(moduleCode)){
            buttons.add(ButtonsContainer.BUTTONS_MAP.get("view_doc"));//查看正文
            buttons.add(ButtonsContainer.BUTTONS_MAP.get("view_attachment"));//查看附件
            buttons.add(ButtonsContainer.BUTTONS_MAP.get("view_processHistLog"));//查看办理日志
        }*/
        return buttons;
    }

    /**
     * 获取指定活动下的任务
     *
     * @param userInfoVO
     * @param procInstId
     * @param actinstId
     * @return
     */
    @Override
    public List<TaskVO> getActivityTaskList(UserInfoVO userInfoVO, Long procInstId, Long actinstId) {
        return this.processHistService.getHistTaskByActInstId(userInfoVO, procInstId, actinstId);
    }

    /**
     * 获取指定流程的第一个活动
     *
     * @param userInfo
     * @param processId
     * @return
     */
    @Override
    public ActivityVO getFirstActivity(UserInfoVO userInfo, Long processId) {
        return this.processEngineService.getFirstActivity(userInfo, processId);
    }

    @Override
    public List<TaskVO> getWaitTaskList(UserInfoVO userInfo,QueryTask queryTask,Long[] roleIds,int limit) {
        List<TaskVO> list = this.processTaskService.getTaskListByUser(userInfo, queryTask, userInfo.getUserId(), limit, roleIds);
        return list;
    }

    /**
     * 根据流程实例ID获取当前流程下所有的阅件任务
     *
     * @param userInfo
     * @param procInstId
     * @return
     */
    @Override
    public List<TaskVO> getReaderTaskList(UserInfoVO userInfo, Long procInstId) {
        List<TaskVO> list = this.processHistService.getReaderTaskByProcInstId(userInfo,procInstId);
        return list;
    }

    /**
     * 获取快速发文查看表单按钮
     *
     * @return
     */
    @Override
    public List<Button> getQuickSendViewButtons() {
        List<Button> buttons = new ArrayList<Button>();
        buttons.add(ButtonsContainer.BUTTONS_MAP.get("view_doc"));//查看正文
        buttons.add(ButtonsContainer.BUTTONS_MAP.get("view_attachment"));//查看附件
        buttons.add(ButtonsContainer.BUTTONS_MAP.get("print"));//打印
        return buttons;
    }

    /**
     * 获取快速发文可编辑字段
     *
     * @return
     */
    @Override
    public FormFieldControlVO getQuickSendEditableFields() {

        /*Map<String,String> editableFields = FieldsContainer.COMMON_FIELDS_MAP.get(ModuleCode.sendDoc.toString());
        Map<String,String> commentFields = FieldsContainer.COMMENT_FIELDS_MAP.get(ModuleCode.sendDoc.toString());
        for(String key : editableFields.keySet()){
            editableFields.put(key,EFieldControlType.writeable.toString());
        }
        for(String key : commentFields.keySet()){
            commentFields.put(key,EFieldControlType.comment.toString());
        }
        FormFieldControlVO formFieldControlVO = new FormFieldControlVO();
        formFieldControlVO.setEditableFields(editableFields);
        formFieldControlVO.setCommentFields(commentFields);
        return formFieldControlVO;*/
        return null;

    }

    /**
     * 获取快速发文可编辑按钮
     *
     * @return
     */
    @Override
    public List<Button> getQuickSendButtons() {

        List<Button> buttons = new ArrayList<Button>();
        buttons.add(ButtonsContainer.BUTTONS_MAP.get("edit_doc"));//编辑正文
        buttons.add(ButtonsContainer.BUTTONS_MAP.get("edit_attachment"));//编辑附件
        buttons.add(ButtonsContainer.BUTTONS_MAP.get("distribution_complete"));//分发办结
        buttons.add(ButtonsContainer.BUTTONS_MAP.get("direct_complete"));//直接办结
        buttons.add(ButtonsContainer.BUTTONS_MAP.get("print"));//打印
        return buttons;

    }

    /**
     * 获取手工登记可编辑字段
     *
     * @return
     */
    @Override
    public FormFieldControlVO getManulRegisterEditableFields() {
       /* Map<String,String> editableFields = FieldsContainer.COMMON_FIELDS_MAP.get(ModuleCode.receiveDoc.toString());
        Map<String,String> commentFields = FieldsContainer.COMMENT_FIELDS_MAP.get(ModuleCode.receiveDoc.toString());
        for(String key : editableFields.keySet()){
            editableFields.put(key,EFieldControlType.writeable.toString());
        }
        for(String key : commentFields.keySet()){
            commentFields.put(key,EFieldControlType.comment.toString());
        }
        FormFieldControlVO formFieldControlVO = new FormFieldControlVO();
        formFieldControlVO.setEditableFields(editableFields);
        formFieldControlVO.setCommentFields(commentFields);
        return formFieldControlVO;*/
        return null;
    }

    /**
     * 保存表单数据
     *
     * @param userInfoVO
     * @param paramMap
     */
    @Override
    public Map<String,Object> saveFormData(UserInfoVO userInfoVO, Map<String, Object> paramMap) throws Exception{
        // 业务数据处理
        final String moduleCode = AppUtil.getValue(paramMap.get("moduleCode"));
        IBusinessHandler formHandler = BusinessHandlerFactory.getBusinessHandlerInstance(moduleCode);
        Map<String,Object> result = formHandler.saveFormData(paramMap);
        formHandler.saveSystemLog(AppUtil.getLong(paramMap.get("recordId")),AppUtil.getValue(paramMap.get("activityName"))+"保存");
        return result;
    }

    /**
     * 判断任务是否办理完毕
     *
     * @param userInfoVO
     * @param taskId
     * @return
     */
    @Override
    public boolean getTaskIsComplete(UserInfoVO userInfoVO, Long taskId) {
        TaskVO task = getRunTaskById(userInfoVO,taskId);
        return null == task;
    }

    /**
     * 流程自动流转到发起人
     * @return
     */
    @Override
    public void processAutoToOwner(UserInfoVO userInfoVO,  Map<String,Object> paramMap) throws Exception{
        Long taskId = AppUtil.getLong(paramMap.get("taskId"));// 当前任务ID
        TaskVO curTask = getRunTaskById(userInfoVO, taskId);
        if(null == curTask){
            throw new BaseRunTimeException(TipsMode.Message.toString(),"该公文已经被办理,不可重复办理。请关闭或刷新当前页面");
        }
        validateTaskAssignee(curTask,userInfoVO);//任务办理人验证
        paramMap.put("taskVO",curTask);
        // 业务数据处理
        final String moduleCode = AppUtil.getValue(paramMap.get("moduleCode"));
        IBusinessHandler formHandler = BusinessHandlerFactory.getBusinessHandlerInstance(moduleCode);
        Map<String,Object> data = formHandler.run(userInfoVO,paramMap);// 保存业务后返回的数据
        // 任务办理参数对象
        TaskHandleParam taskHandleParam = new TaskHandleParam();
        taskHandleParam.setRecordId(Long.valueOf(data.get("recordId").toString())); // 业务实体ID
        taskHandleParam.setTitle(AppUtil.getValue(data.get("title")));// 任务标题
        if(null != data.get("businessData"))
            taskHandleParam.setData((Map<String,String>)data.get("businessData"));
        List<AssigneeVO> taskAssigneeList = new ArrayList<AssigneeVO>();
        //将流程发起人设置为办理人
        AssigneeVO assignee = new AssigneeVO();
        assignee.setUserId(curTask.getCreateUserId());
        assignee.setUserOrgId(curTask.getCreateOrgId());
        assignee.setUserName(curTask.getCreateUser());
        assignee.setUserOrgName(curTask.getCreateOrg());
        taskAssigneeList.add(assignee);
        taskHandleParam.setTaskAssigneeList(taskAssigneeList);
        List<TaskVO> nextTasks = processTaskService.autoSerialComplete(userInfoVO, taskId, taskHandleParam);
        if(nextTasks != null && nextTasks.size()>0){
            //customFormRemindService.asynchronousSendRemindAndDealMessage(nextTasks, moduleCode, taskId, new RemindWay[]{RemindWay.Message});
        }
    }

    /**
     * 获取自动流转至发起人时可操作按钮
     *
     * @param moduleCode
     * @param taskId
     * @param userInfo
     * @return
     */
    @Override
    public List<Button> getAutoToOwnerButtons(String moduleCode, Long taskId, UserInfoVO userInfo) {
        List<Button> buttons = new ArrayList<Button>();
        if(null != taskId){
            Integer type = getNextStepType(userInfo, taskId);
            if(null == type)
                throw new BaseRunTimeException(TipsMode.Message.toString(),"获取下一步按钮类型异常");
            Button button = getButtonByNextStepType(type);
            if(null != button){
                buttons.add(button);
            }
        }
        buttons.add(ButtonsContainer.BUTTONS_MAP.get("view_flowchart"));//查看流程图
        buttons.add(ButtonsContainer.BUTTONS_MAP.get("view_processHistLog"));//查看办理日志
        return buttons;
    }

    /**
     * 获取活动定义
     * @param userInfoVO
     * @param activityId
     * @return
     */
    @Override
    public ActivityVO getActivity(UserInfoVO userInfoVO, Long activityId) {
        return processEngineService.getActivity(userInfoVO,activityId);
    }

    /**
     * 获取下一步默认办理人
     * @param userInfoVO
     * @param activityId
     * @param taskId
     * @return
     */
    @Override
    public Map<String,Object> getDefaultHandler4NextActivity(UserInfoVO userInfoVO, Long activityId, Long taskId) {
        //默认办理人获取策略
        //1、下一步活动只有一个且活动参与者类型为指定用户且只有一个指定用户,则该用户为默认办理用户
        //2、下一步活动只有一个且活动参与者类型为发起人,流程发起人为默认办理用户
        List<ActivityVO> list = getNextActivities(userInfoVO,activityId);
        Map<String,Object> result = new HashMap<String, Object>();
        if(null != list && list.size() == 1){
            ActivityVO activity = list.get(0);
            //EX8只读取人员设置里面配置的人员
//            if(EParticipationType.candidate.equals(activity.getParticipation())){//活动参与者类型为候选人
                List<ReceiverVO> receivers = getSettingUsers(activity.getElementId());//getHandlePersons(activity);
                if(null != receivers && receivers.size() == 1){
                    result.put("receiver",receivers);
                    result.put("nextActivity",activity);
                }
//            }else if(EParticipationType.promoter.equals(activity.getParticipation())){
//                List<ReceiverVO> receivers = new ArrayList<ReceiverVO>();
//                receivers.add(getProcessStarter(userInfoVO, taskId));
//                result.put("receiver",receivers);
//                result.put("nextActivity",activity);
//            }
        }
        return result;
    }

    /**
     * 获取流程发起人
     * @param userInfoVO
     * @param taskId
     * @return
     */
    @Override
    public ReceiverVO getProcessStarter(UserInfoVO userInfoVO, Long taskId) {
        ReceiverVO receiver = null;
        if(null == taskId){//任务ID为空,则流程未启动,即当前用户为流程发起人
            receiver = new ReceiverVO();
            receiver.setUserId(userInfoVO.getUserId());
            receiver.setOrganId(userInfoVO.getOrgId());
            receiver.setUnitId(userInfoVO.getUnitId());
            receiver.setPersonName(userInfoVO.getUserName());
        }else{
            TaskVO task = getRunTaskById(userInfoVO,taskId);
            if(null == task){
                throw new BaseRunTimeException(TipsMode.Message.toString(),"当前任务已办理,获取流程发起人失败");
            }
            receiver = new ReceiverVO();
            receiver.setUserId(task.getCreateUserId());
            receiver.setOrganId(task.getCreateOrgId());
            receiver.setUnitId(task.getCreateUnitId());
            receiver.setPersonName(task.getCreateUser());
        }
        return receiver;
    }

    /**
     * 删除待办任务
     * @param recordId
     */
    @Override
    public void deleteTaskByRecordId(Long recordId) {
        if(null != recordId){
            UserInfoVO userInfoVO = new UserInfoVO();
            processTaskService.deleteRuTaskByRecordId(userInfoVO,recordId);
        }
    }

    /**
     * 获取活动字段控制
     * @param userInfoVO
     * @param activityId
     * @return
     */
    @Override
    public ActivityFieldControlVO getActivityFieldControl(UserInfoVO userInfoVO, Long activityId) {
        // 可编辑字段类型枚举数组
        EFieldControlType[] fieldControlTypes = new EFieldControlType[] {EFieldControlType.writeable, EFieldControlType.comment,EFieldControlType.protectable };
        // 获取可编辑字段列表
        List<FieldVO> list = processTaskService.getFieldControls(userInfoVO,activityId,fieldControlTypes);
        ActivityFieldControlVO vo = null;
        // 构造返系统的可编辑字段对象
        if (list != null && list.size() > 0) {
            vo = new ActivityFieldControlVO();
            //可写字段
            StringBuilder writeFields = new StringBuilder();
            //受保护可写字段
            StringBuilder protectFields = new StringBuilder();
            //批示意见字段
            StringBuilder commentFields = new StringBuilder();

            String controlType = "";
            for (FieldVO field : list) {
                controlType = field.getControlType().toString();
                //可写字段
                if(EFieldControlType.writeable.toString().equals(controlType)) {
                    writeFields.append(field.getFieldName()).append(",");
                    continue;
                }
                //受保护可写字段
                if(EFieldControlType.protectable.toString().equals(controlType)){
                    protectFields.append(field.getFieldName()).append(",");
                    continue;
                }
                //批示意见字段
                if(EFieldControlType.comment.toString().equals(controlType)){
                    commentFields.append(field.getFieldName()).append(",");
                }
            }
            if(writeFields.toString().endsWith(",")){
                vo.setWritableFields(writeFields.toString().substring(0,writeFields.toString().length()-1));
            }
            if(protectFields.toString().endsWith(",")){
                vo.setProtectFields(protectFields.toString().substring(0, protectFields.toString().length() - 1));
            }
            if(commentFields.toString().endsWith(",")){
                vo.setCommentFields(commentFields.toString().substring(0, commentFields.toString().length() - 1));
            }
        }
        return vo;
    }

    @Override
    public List<ActivityControlVO> getNextActivityControls(UserInfoVO userInfoVO, Map<String, Object> paramMap) {
        Long taskId = AppUtil.getLong(paramMap.get("taskId"));
        List<ActivityVO> activities = getNextActivities(userInfoVO, paramMap);
        if(null != activities && activities.size() > 0){
            ActivityControlVO vo = null;
            List<ActivityControlVO> activityControls = new ArrayList<ActivityControlVO>();
            ReceiverVO processStater = null;//流程发起人
            List<ReceiverVO> handlePersons = null;
            List<RecOrganVO> handleOrgans = null;
            List<RecRoleVO> handleRoles = null;
            EParticipationType participation = null;
            for(ActivityVO activity : activities){
                vo = new ActivityControlVO();
                participation = activity.getParticipation();
                vo.setElementId(activity.getElementId());
                vo.setName(activity.getName());
                vo.setIsLimit(activity.getIsLimit());
                vo.setParticipation(participation);
                vo.setTransactScope(activity.getTransactScope());
                vo.setAssigneeId(activity.getAssigneeId());
                vo.setAssigneeName(activity.getAssigneeName());
                vo.setAssigneeOrgId(activity.getAssigneeOrgId());
                vo.setAssigneeOrgName(activity.getAssigneeOrgName());
                vo.setCanAgent(Integer.valueOf(1).equals(activity.getCanAgent()));
                vo.setCanPeriodicAgent(activity.getCanPeriodicAgent());
                vo.setHasTransact(Integer.valueOf(1).equals(activity.getHasTransact()));
                vo.setIsEndActivity(Integer.valueOf(1).equals(activity.getEventType()));
//                if(EParticipationType.promoter.equals(participation)){//参与者类型为发起人
//                    handlePersons = new ArrayList<ReceiverVO>();
//                    processStater = getProcessStarter(userInfoVO,taskId);
//                    handlePersons.add(processStater);
//                }else if(EParticipationType.candidate.equals(participation)){//指定办理人
                    //获取人员配置里面的人员信息
                    handlePersons = getSettingUsers(activity.getElementId());
                    //没有则查询默认的人员
                    if(handlePersons.size()==0){
                        handlePersons = getHandlePersons(activity);
                    }
//                }else if(EParticipationType.dept.equals(participation)){//部门
//                    handleOrgans = getHandleOrgans(activity);
//                }else if(EParticipationType.role.equals(participation) || EParticipationType.roleUser.equals(participation)){//角色或角色用户
//                    handleRoles = getHandleRoles(activity);
//                }
                vo.setHandlePersons(handlePersons);
                vo.setHandleOrgans(handleOrgans);
                vo.setHandleRoles(handleRoles);
                activityControls.add(vo);
            }
            return activityControls;
        }
        return null;
    }

    /**
     * 查询流程活动的办理人员
     * @param activityId
     * @return
     */
    private List<ReceiverVO> getSettingUsers(Long activityId){
        //查询人员配置里面设置的人员
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("siteId",LoginPersonUtil.getSiteId());
        map.put("activityId",activityId);
        ActivityUserSetEO activityUserSetEO =  activityUserSetService.getEntity(ActivityUserSetEO.class,map);

        List<ReceiverVO> handlePersons = new ArrayList<ReceiverVO>();
        if(activityUserSetEO!=null){
            String userIds = activityUserSetEO.getUserIds();
            String userNames = activityUserSetEO.getUserNames();
            if(!AppUtil.isEmpty(userIds)){
                String[] userId = userIds.split(",");
                String[] userName = userNames.split(",");
                for(int i=0;i<userId.length;i++){
                    if(!AppUtil.isEmpty(userId[i])){
                        ReceiverVO receiverVO = new ReceiverVO();
                        //userid跟username是一一对应的
                        receiverVO.setUserId(Long.parseLong(userId[i]));
                        receiverVO.setPersonName(userName[i]);
                        handlePersons.add(receiverVO);
                    }
                }
            }
        }
        return handlePersons;
    }


    @Override
    public Map<String, Object> getDefaultHandler4NextActivity(UserInfoVO userInfoVO, Map<String, Object> paramMap) {
        Long activityId = AppUtil.getLong(paramMap.get("activityId"));
        Long taskId = AppUtil.getLong(paramMap.get("taskId"));
        //默认办理人获取策略
        //1、下一步活动只有一个且活动参与者类型为指定用户且只有一个指定用户,则该用户为默认办理用户
        //2、下一步活动只有一个且活动参与者类型为发起人,流程发起人为默认办理用户
        paramMap.put("elementId",activityId);
        List<ActivityVO> list = getNextActivities(userInfoVO,paramMap);
        Map<String,Object> result = new HashMap<String, Object>();
        if(null != list && list.size() == 1){
            ActivityVO activity = list.get(0);
            if(EParticipationType.candidate.equals(activity.getParticipation())){//活动参与者类型为候选人
                List<ReceiverVO> receivers = getHandlePersons(activity);
                if(null != receivers && receivers.size() == 1){
                    result.put("receiver",receivers);
                    result.put("nextActivity",activity);
                }
            }else if(EParticipationType.promoter.equals(activity.getParticipation())){
                List<ReceiverVO> receivers = new ArrayList<ReceiverVO>();
                receivers.add(getProcessStarter(userInfoVO, taskId));
                result.put("receiver",receivers);
                result.put("nextActivity",activity);
            }
        }
        return result;
    }

    @Override
    public List<Button> getButtons(UserInfoVO userInfoVO, Long activityId, Long taskId, Map<String, Object> formData) {
        // 按钮属于-待办任务
        TaskStatus taskStatus = TaskStatus.waitDeal;

        Button button = null;
        /**
         * 构建流程提交按钮（下一步/办理完毕/办结/已阅）
         */
        if(null != taskId){
            formData.put("taskId",taskId);
            Integer type = getNextStepType(userInfoVO,formData);
            if(null == type)
                throw new BaseRunTimeException(TipsMode.Message.toString(),"获取下一步按钮类型异常");
            button = getButtonByNextStepType(type);
            if(2 == type.intValue()){
                taskStatus =  TaskStatus.waitRead;
            }
        }else{
            button = ButtonsContainer.BUTTONS_MAP.get("step_next");
        }
        // 获取活动配置按钮
        List<Button> buttons = processTaskService.getActivityOpertions(userInfoVO,activityId, taskStatus);

        //将button放置列表最前
        List<Button> result = new ArrayList<Button>(buttons.size()+1) ;
        result.add(button);
        result.addAll(buttons);
        return result;
    }

    @Override
    public List<Button> getAutoToOwnerButtons(String moduleCode, Long taskId, UserInfoVO userInfoVO, Map<String, Object> formData) {
        List<Button> buttons = new ArrayList<Button>();
        if(null != taskId){
            formData.put("taskId",taskId);
            Integer type = getNextStepType(userInfoVO, formData);
            if(null == type)
                throw new BaseRunTimeException(TipsMode.Message.toString(),"获取下一步按钮类型异常");
            Button button = getButtonByNextStepType(type);
            if(null != button){
                buttons.add(button);
            }
        }
        buttons.add(ButtonsContainer.BUTTONS_MAP.get("view_flowchart"));//查看流程图
        buttons.add(ButtonsContainer.BUTTONS_MAP.get("view_processHistLog"));//查看办理日志
        return buttons;
    }

    @Override
    public ProcessStartVO getProcessStartData(UserInfoVO userInfo,ProcessVO processVO) {
        ProcessStartVO processStart = new ProcessStartVO();
        processStart.setProcessName(processVO.getName());
        processStart.setProcessId(processVO.getProcessId());
        ActivityVO firstActivity = getFirstActivity(userInfo,processVO.getProcessId());
        if(null == firstActivity){
            throw new BaseRunTimeException(TipsMode.Message.toString(),"流程未设置活动,请检查流程图设置");
        }
        processStart.setFirstActivity(firstActivity);
        Map<String,Object> nextActivity = getDefaultHandler4NextActivity(userInfo,firstActivity.getElementId(),null);
        processStart.setNextActivity(nextActivity);
        return processStart;
    }

    public List<ReceiverVO> getHandlePersons(ActivityVO activity){
        String userIds = activity.getAssigneeId();
        String personNames = activity.getAssigneeName();
        String organIds = activity.getAssigneeOrgId();
        if(!AppUtil.isEmpty(userIds) && !AppUtil.isEmpty(personNames) && !AppUtil.isEmpty(organIds)){
            String[] userIdArray = userIds.split(",");
            String[] personNameArray = personNames.split(",");
            String[] organIdArray = organIds.split(",");
            if(userIdArray.length == personNameArray.length && userIdArray.length == organIdArray.length){
                List<ReceiverVO> receivers = new ArrayList<ReceiverVO>(userIdArray.length);
                ReceiverVO vo = null;
                for(int i=0,length=userIdArray.length;i<length;i++){
                    vo = new ReceiverVO();
                    vo.setUserId(AppUtil.getLong(userIdArray[i]));
                    vo.setOrganId(AppUtil.getLong(organIdArray[i]));
                    vo.setPersonName(personNameArray[i]);
                    receivers.add(vo);
                }
                return receivers;
            }else{
                throw new BaseRunTimeException(TipsMode.Message.toString(),activity.getName()+"指定办理人设置存在错误");
            }
        }
        return null;
    }

    public List<RecOrganVO> getHandleOrgans(ActivityVO activity){
        String organIds =  activity.getAssigneeId();
        String organNames = activity.getAssigneeName();
        String unitIds = activity.getAssigneeOrgId();
        String unitNames = activity.getAssigneeOrgName();
        if(!AppUtil.isEmpty(organIds) && !AppUtil.isEmpty(organNames) && !AppUtil.isEmpty(unitIds) && !AppUtil.isEmpty(unitNames)){
            String[] organIdArray = organIds.split(",");
            String[] organNameArray = organNames.split(",");
            String[] unitIdArray = unitIds.split(",");
            String[] unitNameArray = unitNames.split(",");
            if(organIdArray.length == organNameArray.length && unitIdArray.length == organIdArray.length && unitNameArray.length == unitIdArray.length){
                List<RecOrganVO> handleOrgans = new ArrayList<RecOrganVO>(organIdArray.length);
                RecOrganVO vo = null;
                for(int i=0,length=organIdArray.length;i<length;i++){
                    vo = new RecOrganVO();
                    vo.setOrganId(AppUtil.getLong(organIdArray[i]));
                    vo.setOrganName(organNameArray[i]);
                    vo.setUnitId(AppUtil.getLong(unitIdArray[i]));
                    vo.setUnitName(unitNameArray[i]);
                    handleOrgans.add(vo);
                }
                return handleOrgans;
            }else{
                throw new BaseRunTimeException(TipsMode.Message.toString(),activity.getName()+"指定的部门设置存在错误");
            }

        }
        return null;
    }


    public List<RecRoleVO> getHandleRoles(ActivityVO activity){
        String roleIds =  activity.getAssigneeId();
        String roleNames = activity.getAssigneeName();
        String unitIds = activity.getAssigneeOrgId();
        String unitNames = activity.getAssigneeOrgName();
        if(!AppUtil.isEmpty(roleIds) && !AppUtil.isEmpty(roleNames)){
            String[] roleIdArray = roleIds.split(",");
            String[] roleNameArray = roleNames.split(",");
            if(!AppUtil.isEmpty(unitIds) && !AppUtil.isEmpty(unitNames)){
                String[] unitIdArray = unitIds.split(",");
                String[] unitNameArray = unitNames.split(",");
                if(roleIdArray.length == roleNameArray.length && unitIdArray.length == roleIdArray.length && unitNameArray.length == unitIdArray.length){
                    List<RecRoleVO> handleRoles = new ArrayList<RecRoleVO>(roleIdArray.length);
                    RecRoleVO vo = null;
                    for(int i=0,length=roleIdArray.length;i<length;i++){
                        vo = new RecRoleVO();
                        vo.setRoleId(AppUtil.getLong(roleIdArray[i]));
                        vo.setRoleName(roleNameArray[i]);
                        vo.setUnitId(AppUtil.getLong(unitIdArray[i]));
                        vo.setUnitName(unitNameArray[i]);
                        handleRoles.add(vo);
                    }
                    return handleRoles;
                }else{
                    throw new BaseRunTimeException(TipsMode.Message.toString(),activity.getName()+"指定的角色设置存在错误");
                }
            }else{
                if(roleIdArray.length == roleNameArray.length){
                    List<RecRoleVO> handleRoles = new ArrayList<RecRoleVO>(roleIdArray.length);
                    RecRoleVO vo = null;
                    for(int i=0,length=roleIdArray.length;i<length;i++){
                        vo = new RecRoleVO();
                        vo.setRoleId(AppUtil.getLong(roleIdArray[i]));
                        vo.setRoleName(roleNameArray[i]);
                        handleRoles.add(vo);
                    }
                    return handleRoles;
                }else{
                    throw new BaseRunTimeException(TipsMode.Message.toString(),activity.getName()+"指定的角色设置存在错误");
                }
            }
        }
        return null;
    }


    private Button getButtonByNextStepType(Integer type){
        if (0 == type.intValue()) {//下一步
            return ButtonsContainer.BUTTONS_MAP.get("step_next");
        } else if (1 == type.intValue()) {
            return ButtonsContainer.BUTTONS_MAP.get("finish");
        } else if(2 == type.intValue()){
            return ButtonsContainer.BUTTONS_MAP.get("has_read");
        } if (100 == type.intValue()) {
            return ButtonsContainer.BUTTONS_MAP.get("complete");
        }
        return null;
    }


}
