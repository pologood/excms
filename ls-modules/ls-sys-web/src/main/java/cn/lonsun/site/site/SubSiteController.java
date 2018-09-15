package cn.lonsun.site.site;

import cn.lonsun.cache.client.CacheGroup;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.site.site.internal.entity.SiteConfigEO;
import cn.lonsun.site.site.internal.entity.SiteMgrEO;
import cn.lonsun.site.site.internal.service.IColumnConfigService;
import cn.lonsun.site.site.internal.service.ISiteConfigService;
import cn.lonsun.site.template.internal.service.ITplConfService;
import cn.lonsun.util.LoginPersonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

/**
 * <br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2016-4-5<br/>
 */
@Controller
@RequestMapping("subSite")
public class SubSiteController extends BaseController {

    @Autowired
    private ISiteConfigService siteConfigService;

    @Autowired
    private ITplConfService tplConfService;


    @Autowired
    private IColumnConfigService columnConfigService;


    /**
     * 去往站点管理页面
     *
     * @return
     */
    @RequestMapping("listSiteTree")
    public String listSiteTree( Model model) {
        Long siteId= LoginPersonUtil.getSiteId();
        IndicatorEO eo=CacheHandler.getEntity(IndicatorEO.class,siteId);
        Object tplObj=tplConfService.getVrtpls();
        model.addAttribute("tplObj",tplObj);
        if(IndicatorEO.Type.SUB_Site.toString().equals(eo.getType())){
            return "site/site/sub_siteInfo";
        }
        return "site/site/sub_site";
    }

    /**
     * 获取站点树的结构（异步加载）
     * 1、给站点管理提供站点树
     *
     * @return String JSON格式
     */
    @RequestMapping("getSiteTree")
    @ResponseBody
    public Object getSiteTree(@RequestParam(value = "indicatorId", required = false, defaultValue = "") Long indicatorId) {

         if(indicatorId==null){
             return getObject(new ArrayList<SiteMgrEO>());
         }
        List<SiteMgrEO> list= CacheHandler.getList(SiteMgrEO.class, CacheGroup.CMS_PARENTID, indicatorId);
        List<SiteMgrEO> newList=new ArrayList<SiteMgrEO>();
        if(list!=null&&list.size()>0){
            for(SiteMgrEO eo:list){
                if(IndicatorEO.Type.SUB_Site.toString().equals(eo.getType())){
                    eo.setIsParent(0);
                    newList.add(eo);
                }
            }
        }
        return getObject(newList);
    }

    /**
     * 新增或者修改站点信息
     *
     * @param siteVO
     * @return
     */
    @RequestMapping("saveSiteConfigEO")
    @ResponseBody
    public Object saveSiteConfigEO(SiteMgrEO siteVO) {
        if (siteVO.getName() == null || siteVO.getName().trim() == "") {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "站点名称不能为空");
        }
        if (siteVO.getSortNum() == null) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "序号不能为空");
        }
        if(StringUtils.isEmpty(siteVO.getUnitIds())||StringUtils.isEmpty(siteVO.getUnitNames())){
            throw new BaseRunTimeException(TipsMode.Message.toString(), "绑定单位不能为空");
        }
        if(siteVO.getComColumnId()==null){
            throw new BaseRunTimeException(TipsMode.Message.toString(), "绑定公共栏目不能为空");
        }
        if(siteVO.getSiteTempId()==null){
            throw new BaseRunTimeException(TipsMode.Message.toString(), "站点模板不能为空");
        }

        if(!StringUtils.isEmpty(siteVO.getKeyWords())&&siteVO.getKeyWords().length()>300){
            throw new BaseRunTimeException(TipsMode.Message.toString(), "关键词的长度应小于300");
        }

        SiteConfigEO eo=siteConfigService.saveSubEO(siteVO);
        return getObject(eo);
    }

    @RequestMapping("getFirstSiteEO")
    @ResponseBody
    public SiteMgrEO getFirstSiteEO(Long indicatorId) {
        SiteMgrEO siteMgrEO = new SiteMgrEO();
        List<SiteMgrEO> list = CacheHandler.getList(SiteMgrEO.class, CacheGroup.CMS_PARENTID, indicatorId);
        if (list != null && list.size() > 0) {
                for(SiteMgrEO eo:list){
                    if(IndicatorEO.Type.SUB_Site.toString().equals(eo.getType())){
                        AppUtil.copyProperties(siteMgrEO,eo);
                        break;
                    }
                }
        }
        return siteMgrEO;
    }

    @RequestMapping("getComTemplate")
    @ResponseBody
    public Object getComTemplate(){
        return tplConfService.getVrtpls();
    }

}
