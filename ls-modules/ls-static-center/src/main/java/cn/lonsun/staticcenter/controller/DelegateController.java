package cn.lonsun.staticcenter.controller;

import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.delegatemgr.entity.AdviceEO;
import cn.lonsun.delegatemgr.entity.DelegateEO;
import cn.lonsun.delegatemgr.entity.ProposalEO;
import cn.lonsun.delegatemgr.internal.service.IAdviceService;
import cn.lonsun.delegatemgr.internal.service.IDelegateService;
import cn.lonsun.delegatemgr.internal.service.IProposalService;
import cn.lonsun.delegatemgr.vo.DelegateQueryVO;
import cn.lonsun.system.datadictionary.vo.DataDictVO;
import cn.lonsun.util.DataDictionaryUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;

/**
 * 代表管理控制层
 * <br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2017-6-13<br/>
 */
@Controller
@RequestMapping(value = "delegate")
public class DelegateController  extends BaseController {
    @Resource
    private IDelegateService delegateService;
    @Resource
    private IAdviceService adviceService;
    @Resource
    private IProposalService proposalService;

    /**
     * 根据ID获取代表信息
     * @param id
     * @param model
     * @return
     */
    @RequestMapping("getDelegateById")
    public String getDelegateById(Long id,ModelMap model){
        DelegateEO delegateEO=delegateService.getEntity(DelegateEO.class,id);
        model.put("eo", delegateEO);
        List<DataDictVO> nationList = DataDictionaryUtil.getItemList("nation",delegateEO.getSiteId());
        model.put("nationList",nationList);
        List<DataDictVO> delegationList = DataDictionaryUtil.getItemList("delegation",delegateEO.getSiteId());
        model.put("delegationList",delegationList);
        List<DataDictVO> educationList = DataDictionaryUtil.getItemList("education",delegateEO.getSiteId());
        model.put("educationList",educationList);
        return "/delegateDetail";
    }

    /**
     * 获取建议管理分页列表
     * @param queryVO
     * @return
     */
    @RequestMapping("getAdvicePage")
    @ResponseBody
    public Object getAdvicePage(DelegateQueryVO queryVO){
        Pagination page=adviceService.getPage(queryVO);
        return page;
    }

    /**
     * 根据ID获取建议详情
     * @param id
     * @param model
     * @return
     */
    @RequestMapping("getAdviceById")
    public String getAdviceById(Long id,ModelMap model){
        AdviceEO adviceEO=adviceService.getEntity(AdviceEO.class,id);
        model.put("eo", adviceEO);
        List<DataDictVO> sessionList = DataDictionaryUtil.getItemList("advice_session",adviceEO.getSiteId());
        model.put("sessionList",sessionList);
        return "/adviceDetail";
    }

    /**
     * 根据code值获取数据字典列表项
     * @param code
     * @param siteId
     * @return
     */
    @RequestMapping("getDicVO")
    @ResponseBody
    public Object getDicVO(String code,Long siteId){
        List<DataDictVO> list = DataDictionaryUtil.getItemList(code,siteId);
        return getObject(list);
    }

    /**
     * 获取建议议案分页列表
     * @param queryVO
     * @return
     */
    @RequestMapping("getProposalPage")
    @ResponseBody
    public Object getProposalPage(DelegateQueryVO queryVO){
        Pagination page=proposalService.getPage(queryVO);
        return page;
    }

    /**
     * 根据ID获取建议议案详情
     * @param id
     * @param model
     * @return
     */
    @RequestMapping("getProposalById")
    public String getProposalById(Long id,ModelMap model){
        ProposalEO proposalEO=proposalService.getEntity(ProposalEO.class,id);
        model.put("eo", proposalEO);
        List<DataDictVO> sessionList = DataDictionaryUtil.getItemList("proposal_session",proposalEO.getSiteId());
        model.put("sessionList",sessionList);
        return "/proposalDetail";
    }


}
