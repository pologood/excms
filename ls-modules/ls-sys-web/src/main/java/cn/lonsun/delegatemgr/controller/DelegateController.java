package cn.lonsun.delegatemgr.controller;

import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.delegatemgr.entity.DelegateEO;
import cn.lonsun.delegatemgr.internal.service.IDelegateService;
import cn.lonsun.delegatemgr.vo.DelegateQueryVO;
import cn.lonsun.system.datadictionary.vo.DataDictVO;
import cn.lonsun.util.DataDictionaryUtil;
import cn.lonsun.util.FileUploadUtil;
import cn.lonsun.util.LoginPersonUtil;
import com.ctc.wstx.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * 代表管理控制层<br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2017-6-6<br/>
 */
@Controller
@RequestMapping(value = "delegate")
public class DelegateController extends BaseController{
    @Autowired
    private IDelegateService delegateService;

    /**
     * 去往列表页面
     * @return
     */
    @RequestMapping("getList")
    public String getList(Model model){
        Long siteId=LoginPersonUtil.getSiteId();
        //获取届次
        List<DataDictVO> sessionList=DataDictionaryUtil.getItemList("delegate_session",siteId);
        model.addAttribute("sessionList",sessionList);
        //获取代表团
        List<DataDictVO> delegationlist=DataDictionaryUtil.getItemList("delegation",siteId);
        model.addAttribute("delegationList",delegationlist);
        return "delegatemgr/delegate_list";
    }

    /**
     * 获取分页信息
     * @param queryVO
     * @return
     */
    @RequestMapping("getPage")
    @ResponseBody
    public Object getPage(DelegateQueryVO queryVO){
        Long siteId = LoginPersonUtil.getSiteId();
        queryVO.setSiteId(siteId);
        Pagination page=delegateService.getPage(queryVO);
        List<DelegateEO> list=(List<DelegateEO>)page.getData();
        if(list!=null&&list.size()>0){
            for(DelegateEO eo:list){
                DataDictVO dictVO=DataDictionaryUtil.getItem("nation",eo.getNation());
                if(dictVO!=null){
                    eo.setNation(dictVO.getKey());
                }
                dictVO =DataDictionaryUtil.getItem("party",eo.getParty());
                if(dictVO!=null){
                    eo.setParty(dictVO.getKey());
                }
                dictVO =DataDictionaryUtil.getItem("delegation",eo.getDelegation());
                if(dictVO!=null){
                    eo.setDelegation(dictVO.getKey());
                }
                dictVO=DataDictionaryUtil.getItem("education",eo.getEducation());
                if(dictVO!=null){
                    eo.setEducation(dictVO.getKey());
                }
            }
        }
        page.setData(list);
        return page;
    }

    /**
     * 去往编辑页面
     * @param id
     * @param model
     * @return
     */
    @RequestMapping("toEdit")
    public String toEdit(Long id,Model model){
        model.addAttribute("delegateId",id);
        Long siteId=LoginPersonUtil.getSiteId();
        //获取民族
        List<DataDictVO> nationList = DataDictionaryUtil.getItemList("nation",siteId);
        model.addAttribute("nationList",nationList);
        //获取代表团
        List<DataDictVO> delegationlist=DataDictionaryUtil.getItemList("delegation",siteId);
        model.addAttribute("delegationList",delegationlist);
        //获取代表小组
        List<DataDictVO> groupList=DataDictionaryUtil.getItemList("dele_group",siteId);
        model.addAttribute("groupList",groupList);
        //获取届次
        List<DataDictVO> sessionList=DataDictionaryUtil.getItemList("delegate_session",siteId);
        model.addAttribute("sessionList",sessionList);
        //获取党派
        List<DataDictVO> partyList=DataDictionaryUtil.getItemList("party",siteId);
        model.addAttribute("partyList",partyList);
        //代表构成
        List<DataDictVO> deleCompList=DataDictionaryUtil.getItemList("dele_comp",siteId);
        model.addAttribute("deleCompList",deleCompList);
        //行业分类
        List<DataDictVO> industryList= DataDictionaryUtil.getItemList("industry_class",siteId);
        model.addAttribute("industryList",industryList);
        //职业构成
        List<DataDictVO> careerList=DataDictionaryUtil.getItemList("career_comp",siteId);
        model.addAttribute("careerList",careerList);
        //增加方式
        List<DataDictVO> addList=DataDictionaryUtil.getItemList("add_type",siteId);
        model.addAttribute("addList",addList);
        //学历
        List<DataDictVO> educationList=DataDictionaryUtil.getItemList("education",siteId);
        model.addAttribute("educationList",educationList);

        List<DataDictVO>  qualificationList=DataDictionaryUtil.getItemList("qualification",siteId);
        model.addAttribute("qualificationList",qualificationList);

        return "delegatemgr/delegate_edit";
    }

    /**
     * 获取实体类信息
     * @param id
     * @return
     */
    @RequestMapping("getEO")
    @ResponseBody
    public Object getEO(Long id){
        if(id==null){
            return getObject(new DelegateEO());
        }
        DelegateEO delegateEO=delegateService.getEntity(DelegateEO.class,id);
        return getObject(delegateEO);
    }

    /**
     * 保存代表信息
     * @param eo
     * @return
     */
    @RequestMapping("saveEO")
    @ResponseBody
    public Object saveEO(DelegateEO eo){
        if(null==eo.getId()){
            eo.setSiteId(LoginPersonUtil.getSiteId());
            delegateService.saveEntity(eo);
        }else{
            delegateService.updateEntity(eo);
        }
        if(!StringUtils.isEmpty(eo.getPicPath())){
            FileUploadUtil.saveFileCenterEO(eo.getPicPath());
        }
        return getObject();
    }

    /**
     * 删除代表信息
     * @param ids
     * @return
     */
    @RequestMapping("delEO")
    @ResponseBody
    public Object delEO(@RequestParam(value = "ids[]", required = false) Long[] ids){
        delegateService.delete(DelegateEO.class,ids);
        return getObject();
    }


}
