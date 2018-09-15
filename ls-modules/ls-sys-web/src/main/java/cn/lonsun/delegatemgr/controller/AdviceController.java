package cn.lonsun.delegatemgr.controller;

import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.delegatemgr.entity.AdviceEO;
import cn.lonsun.delegatemgr.entity.DelegateEO;
import cn.lonsun.delegatemgr.internal.service.IAdviceService;
import cn.lonsun.delegatemgr.vo.DelegateQueryVO;
import cn.lonsun.system.datadictionary.vo.DataDictVO;
import cn.lonsun.util.DataDictionaryUtil;
import cn.lonsun.util.LoginPersonUtil;
import org.apache.activemq.leveldb.replicated.dto.Login;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * 建议管理控制层<br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2017-6-6<br/>
 */
@Controller
@RequestMapping("advice")
public class AdviceController extends BaseController {

    @Autowired
    private IAdviceService adviceService;

    /**
     * 去往列表页面
     * @return
     */
    @RequestMapping("getList")
    public String getList(Model model){
        Long siteId=LoginPersonUtil.getSiteId();
        //获取届次
        List<DataDictVO> sessionList= DataDictionaryUtil.getItemList("advice_session",siteId);
        model.addAttribute("sessionList",sessionList);
        return "delegatemgr/advice_list";
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
        Pagination page=adviceService.getPage(queryVO);
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
        model.addAttribute("adviceId",id);
        Long siteId=LoginPersonUtil.getSiteId();
        //获取届次
        List<DataDictVO> sessionList= DataDictionaryUtil.getItemList("advice_session",siteId);
        model.addAttribute("sessionList",sessionList);
        return "delegatemgr/advice_edit";
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
            return getObject(new AdviceEO());
        }
        AdviceEO adviceEO=adviceService.getEntity(AdviceEO.class,id);
        return getObject(adviceEO);
    }

    /**
     * 保存建议信息
     * @param eo
     * @return
     */
    @RequestMapping("saveEO")
    @ResponseBody
    public Object saveEO(AdviceEO eo){
        if(null==eo.getId()){
            eo.setSiteId(LoginPersonUtil.getSiteId());
            adviceService.saveEntity(eo);
        }else{
            adviceService.updateEntity(eo);
        }

        return getObject();
    }

    /**
     * 删除建议信息
     * @param ids
     * @return
     */
    @RequestMapping("delEO")
    @ResponseBody
    public Object delEO(@RequestParam(value = "ids[]", required = false) Long[] ids){
        adviceService.delete(AdviceEO.class,ids);
        return getObject();
    }


}
