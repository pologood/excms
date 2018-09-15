
package cn.lonsun.process.controller;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.util.SessionUtil;
import cn.lonsun.process.entity.ProcessAgentEO;
import cn.lonsun.process.service.IProcessAgentService;
import cn.lonsun.process.vo.ProcessAgentQueryVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 *控制层
 *@date 2015-1-7 9:36  <br/>
 *@author zhusy  <br/>
 *@version v1.0  <br/>
 */
@Controller
@RequestMapping(value= "/customForm/processAgent",produces = {"application/json;charset=UTF-8"})
public class ProcessAgentController extends BaseController {

    @Autowired
    private IProcessAgentService processAgentService;


    /**
     * 代填列表
     * @return
     */
    @RequestMapping("processAgentList")
    public ModelAndView processAgentList(Model model,String moduleCode,Long indicatorId){
        model.addAttribute("moduleCode",moduleCode);
       // model.addAttribute("location", RightUtil.getMenuLocation(AppContainer.getApp(CustomFormConfig.getValue("systemCode")), indicatorId));
        return new ModelAndView("app/pro/customForm/process/process_agent_list");
    }


    /**
     * 代填编辑
     * @return
     */
    @RequestMapping("processAgentEdit")
    public ModelAndView processAgentEdit(Model model,Long agentId,String moduleCode){
        if(null != agentId)
            model.addAttribute("agentId",agentId);
        model.addAttribute("moduleCode",moduleCode);
        return new ModelAndView("app/pro/customForm/process/process_agent_edit");
    }

    /**
     * 代填分页列表
     * @param request
     * @param queryVO
     * @return
     */
    @RequestMapping("getAgentPagination")
    @ResponseBody
    public Object getAgentPagination(HttpServletRequest request,ProcessAgentQueryVO queryVO){

        // 参数验证
        if (AppUtil.isEmpty(queryVO.getPageIndex())) {
            queryVO.setPageIndex(0L);
        }
        if (AppUtil.isEmpty(queryVO.getPageSize())) {
            queryVO.setPageSize(15);
        }
        String sortField = queryVO.getSortField();
        if (!AppUtil.isEmpty(sortField)) {
            if (!"createDate".equals(sortField))
                return ajaxParamsErr("sortField");
        }
        String sortOrder = queryVO.getSortOrder();
        if (!AppUtil.isEmpty(sortOrder)) {
            if (!("asc".equals(sortOrder) || "desc".equals(sortOrder)))
                return ajaxParamsErr(sortOrder);
        }
        Long unitId = SessionUtil.getLongProperty(request.getSession(true),"unitId");
        queryVO.setUnitId(unitId);
        return getObject(processAgentService.getAgentPagination(queryVO));
    }

    /**
     * 获取代填实体对象
     * @param agentId
     * @return
     */
    @RequestMapping("getAgent")
    @ResponseBody
    public Object getAgent(Long agentId){
        ProcessAgentEO processAgentEO = null;
        if(null == agentId)
            processAgentEO =  new ProcessAgentEO();
        else
            processAgentEO =  processAgentService.getEntity(ProcessAgentEO.class, agentId);
        return getObject(processAgentEO);
    }


    /**
     * 保存代填对象
     * @param processAgentEO
     * @return
     */
    @RequestMapping("saveAgent")
    @ResponseBody
    public Object saveAgent(HttpServletRequest request,ProcessAgentEO processAgentEO){
        /**
         * 参数验证
         */
        if(null == processAgentEO.getBeAgentOrganId() || null == processAgentEO.getBeAgentUserId() || AppUtil.isEmpty(processAgentEO.getBeAgentPersonName())){
            return ajaxErr("被代填人不能为空");
        }
        if(AppUtil.isEmpty(processAgentEO.getAgentUserIds()) || AppUtil.isEmpty(processAgentEO.getAgentOrganIds()) || AppUtil.isEmpty(processAgentEO.getAgentPersonNames())){
            return ajaxErr("代填人不能为空");
        }

        Long unitId = SessionUtil.getLongProperty(request.getSession(true),"unitId");
        processAgentService.saveAgent(processAgentEO,unitId);

        return getObject();
    }


    /**
     * 删除代填对象
     * @param agentId
     * @return
     */
    @RequestMapping("deleteAgent")
    @ResponseBody
    public Object deleteAgent(Long agentId){
        if(null == agentId)
            return ajaxParamsErr("agentId");
        processAgentService.delete(ProcessAgentEO.class,agentId);
        return getObject();
    }

}
