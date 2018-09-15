package cn.lonsun.site.siteManage;

import cn.lonsun.cache.client.CacheGroup;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.site.serverInfo.internal.entity.SiteControlEO;
import cn.lonsun.site.serverInfo.internal.service.ISiteControlService;
import cn.lonsun.site.site.internal.entity.SiteMgrEO;
import cn.lonsun.system.role.internal.service.ISiteRightsService;
import cn.lonsun.util.LoginPersonUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.*;

import static cn.lonsun.cache.client.CacheHandler.getEntity;

/**
 * @author gu.fei
 * @version 2017-08-01 8:14
 * 服务器信息
 */
@Controller
@RequestMapping(value = "siteControl")
public class SiteControlController extends BaseController {

    private static final String FILE_BASE = "/siteControl/";

    @Resource
    private ISiteControlService siteControlService;

    @Resource
    private ISiteRightsService siteRightsService;

    /**
     * 主页
     * @return
     */
    @RequestMapping(value = "index")
    public String index() {
        return FILE_BASE + "index";
    }

    /**
     * 获取配置信息
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "getSiteControl")
    public Object getSiteControl() {
        // 获取站点列表
        List<IndicatorEO> siteList = getCurUserSiteList();
        List<SiteControlEO> ceos = new ArrayList<SiteControlEO>();
        if(null != siteList && !siteList.isEmpty()) {
            Map<String,Object> map = new HashMap<String,Object>();
            for(IndicatorEO site : siteList) {
                SiteMgrEO siteVO = getEntity(SiteMgrEO.class, site.getIndicatorId());
                map.put("siteId", site.getIndicatorId());
                SiteControlEO ceo = siteControlService.getEntity(SiteControlEO.class,map);
                if(null == ceo) {
                    ceo = new SiteControlEO();
                }
                ceo.setSiteId(site.getIndicatorId());
                ceo.setSiteName(site.getName());
                if(null != siteVO) {
                    ceo.setDomian(siteVO.getUri());
                }
                ceos.add(ceo);
            }
        }

        return getObject(ceos);
    }

    /**
     * 更新配置信息
     * @param siteIds
     * @param status
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "updateSiteControl")
    public Object updateSiteControl(Long[] siteIds,Integer status) {
        siteControlService.updateSiteControl(siteIds,status);
        return ajaxOk();
    }

    /**
     * 获取当前用户站点列表
     *
     * @return
     * @author fangtinghua
     */
    private List<IndicatorEO> getCurUserSiteList() {
        if (LoginPersonUtil.isRoot()) {
            return Collections.emptyList();
        }
        List<IndicatorEO> siteList = new ArrayList<IndicatorEO>();
        if (LoginPersonUtil.isSuperAdmin()) {// 超级管理员获取所有站点
            List<IndicatorEO> list = CacheHandler.getList(IndicatorEO.class, CacheGroup.CMS_TYPE, IndicatorEO.Type.CMS_Site.toString());
            if (null != list && !list.isEmpty()) {
                siteList.addAll(list);
            }
            list = CacheHandler.getList(IndicatorEO.class, CacheGroup.CMS_TYPE, IndicatorEO.Type.SUB_Site.toString());
            if (null != list && !list.isEmpty()) {
                siteList.addAll(list);
            }
        } else {
            Long[] siteIds = siteRightsService.getCurUserSiteIds(true);
            if (null != siteIds && siteIds.length > 0) {
                for (Long id : siteIds) {
                    IndicatorEO indicatorEO = getEntity(IndicatorEO.class, id);
                    if (indicatorEO != null) {
                        siteList.add(indicatorEO);
                    }
                }
            }
        }
        return siteList;
    }
}
