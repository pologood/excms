package cn.lonsun.staticcenter.controller;

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.onlineDeclaration.internal.entity.OnlineDeclarationEO;
import cn.lonsun.content.onlineDeclaration.internal.service.IOnlineDeclarationService;
import cn.lonsun.content.onlineDeclaration.vo.OnlineDeclarationVO;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.util.ContextHolderUtils;
import cn.lonsun.net.service.entity.CmsTableResourcesEO;
import cn.lonsun.net.service.service.ITableResourcesService;
import cn.lonsun.rbac.internal.entity.OrganEO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * <br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2016-6-14<br/>
 */
@Controller
@RequestMapping(value = "/frontDeclaration")
public class FrontDeclarationController extends BaseController {

    @Autowired
    private IOnlineDeclarationService declarationService;

    @Autowired
    private ITableResourcesService resourcesService;

    @RequestMapping(value = "saveVO")
    @ResponseBody
    public Object saveVO(OnlineDeclarationVO vo, String checkCode) {

        if (StringUtils.isEmpty(vo.getPersonName())) {
            return ajaxErr("申请人不能为空！");
        }
        if (StringUtils.isEmpty(vo.getUnitName())) {
            return ajaxErr("单位不能为空！");
        }
        if (StringUtils.isEmpty(vo.getTitle())) {
            return ajaxErr("事项名称不能为空！");
        }
        if (StringUtils.isEmpty(vo.getAddress())) {
            return ajaxErr("地址不能为空！");
        }
        if (vo.getPhoneNum() == null) {
            return ajaxErr("联系电话不能为空！");
        }

        if (StringUtils.isEmpty(checkCode)) {
            return ajaxErr("验证码不能为空！");
        }
        String webCode = (String) ContextHolderUtils.getSession().getAttribute("webCode");
        if (!checkCode.trim().toLowerCase().equals(webCode.toLowerCase())) {
            return ajaxErr("验证码不正确，请重新输入！");
        }
        OnlineDeclarationEO eo = declarationService.saveVO(vo);
        return getObject(eo);
    }

    @RequestMapping("searchByCode")
    public String searchByCode(String docNum,String randomCode,Model model,String ids,Long siteId){
        if (StringUtils.isEmpty(docNum) || StringUtils.isEmpty(randomCode)) {
            model.addAttribute("msg","<div>查询编号或查询编码不能为空</div>");
        }
        if(siteId==null){
            model.addAttribute("msg","<div>站点ID为空</div>");
        }
        OnlineDeclarationVO vo=declarationService.searchByCode(randomCode,docNum,siteId);
        if(vo==null){
            model.addAttribute("msg","<div>查询编号或查询编码错误</div>");
        }else{
            if(vo.getRecUnitId()!=null){
                OrganEO organEO = CacheHandler.getEntity(OrganEO.class, vo.getRecUnitId());
                if (organEO != null) {
                    vo.setRecUnitName(organEO.getName());
                }
            }
            if(!StringUtils.isEmpty(ids)){
                Long[] idArr= AppUtil.getLongs(ids,",");
                List<CmsTableResourcesEO> tableList=resourcesService.getEntities(CmsTableResourcesEO.class,idArr);
                model.addAttribute("tableList",tableList);
            }
            model.addAttribute("l",vo);
        }
        return "onlineDeclarationInfo";

    }

}
