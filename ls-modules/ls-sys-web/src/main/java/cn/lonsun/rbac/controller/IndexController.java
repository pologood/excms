/*
 * IndexController.java         2015年8月20日 <br/>
 *
 * Copyright (c) 1994-1999 AnHui LonSun, Inc. <br/>
 * All rights reserved.	<br/>
 *
 * This software is the confidential and proprietary information of AnHui	<br/>
 * LonSun, Inc. ("Confidential Information").  You shall not	<br/>
 * disclose such Confidential Information and shall use it only in	<br/>
 * accordance with the terms of the license agreement you entered into	<br/>
 * with Sun. <br/>
 */

package cn.lonsun.rbac.controller;

import cn.lonsun.cache.client.CacheGroup;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.publicInfo.internal.service.IPublicCatalogUpdateService;
import cn.lonsun.rbac.login.InternalAccount;
import cn.lonsun.site.site.internal.entity.SiteConfigEO;
import cn.lonsun.site.site.internal.service.ISiteConfigService;
import cn.lonsun.system.role.internal.service.ISiteRightsService;
import cn.lonsun.system.role.internal.site.service.IUserSiteOptService;
import cn.lonsun.system.role.internal.util.RoleAuthUtil;
import cn.lonsun.system.systemlogo.util.SystemLogoUtil;
import cn.lonsun.util.LoginPersonUtil;
import cn.lonsun.util.SysLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * TODO <br/>
 *
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 * @date 2015年8月20日 <br/>
 */
@Controller
@RequestMapping("/")
public class IndexController extends BaseController {
    @Resource
    private ISiteRightsService siteRightsService;

    @Autowired
    private IUserSiteOptService userSiteOptService;

    @Resource(name = "ex_8_IndicatorServiceImpl")
    private cn.lonsun.rbac.indicator.service.IIndicatorService indicatorService;
    @Resource
    private IPublicCatalogUpdateService publicCatalogUpdateService;
    //@Resource
    //private IBaseContentUpdateService baseContentUpdateService;
    @Resource
    private ISiteConfigService siteConfigService;// 站点配置

    /**
     * 保存站点id
     *
     * @return
     */
    @ResponseBody
    @RequestMapping("saveSiteId")
    public Object saveSiteId(Long siteId) {
        LoginPersonUtil.setAttribute(InternalAccount.USER_SITEID, siteId);
        LoginPersonUtil.setAttribute(InternalAccount.PRE_USER_SITEID, siteId);
        LoginPersonUtil.setAttribute(InternalAccount.USER_ISSITEADMIN, RoleAuthUtil.confirmUserSiteAdmin(siteId));
        return getObject();
    }

    /**
     * 首页
     *
     * @return
     */
    @RequestMapping("index")
    public String index(ModelMap map, Model m) {
        boolean isRoot = LoginPersonUtil.isRoot();
        map.put("userName", isRoot ? "厂商管理员" : LoginPersonUtil.getPersonName());
        // 当为站点管理员或者为超级管理员时需要切换站点
        if (!LoginPersonUtil.isRoot()) {
            map.put("uid", LoginPersonUtil.getUserName());
            // 获取站点列表
            List<IndicatorEO> siteList = getCurUserSiteList();
            // 当前选中站点id
            Long siteId = LoginPersonUtil.getSiteId();
            // 获取站点信息
            if (null != siteId) {
                map.put("site", CacheHandler.getEntity(IndicatorEO.class, siteId));
            } else if (!siteList.isEmpty()) {
                IndicatorEO indicatorEO = null;
                // 这里根据用户找到用户所在单位对应的站点id
                Long unitId = LoginPersonUtil.getUnitId();
                if (null != unitId && !LoginPersonUtil.isSuperAdmin()) { // 不是超管的用户
                    Map<String, Object> paramMap = new HashMap<String, Object>();
                    paramMap.put("unitIds", String.valueOf(unitId));
                    paramMap.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
                    SiteConfigEO siteConfigEO = siteConfigService.getEntity(SiteConfigEO.class, paramMap);
                    if (null != siteConfigEO) {
                        siteId = siteConfigEO.getIndicatorId();
                        indicatorEO = CacheHandler.getEntity(IndicatorEO.class, siteId);
                    }
                }
                if (null == indicatorEO) {
                    indicatorEO = siteList.get(0);
                    siteId = siteList.get(0).getIndicatorId();
                }
                // 设置默认的站点id
                LoginPersonUtil.setAttribute(InternalAccount.USER_SITEID, siteId);
                LoginPersonUtil.setAttribute(InternalAccount.PRE_USER_SITEID, siteId);
                map.put("site", indicatorEO);
            }

            // 获取站点列表
            map.put("siteList", siteList);
        }
        map.put("isIndex", true);
        map.put("rightsCode", LoginPersonUtil.getAttribute(String.class, InternalAccount.USER_RIGHTSCODE));
        SystemLogoUtil.setLogo(m);
        //baseContentUpdateService.sendMessageByCurrentUser(LoginPersonUtil.getUserId());
        publicCatalogUpdateService.sendMessageByCurrentUser(LoginPersonUtil.getUserId());
        return "index";
    }

    @ResponseBody
    @RequestMapping("getSiteList")
    public Object getSiteList() {
        return getObject(getCurUserSiteList());
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
                    IndicatorEO indicatorEO = CacheHandler.getEntity(IndicatorEO.class, id);
                    if (indicatorEO != null) {
                        siteList.add(indicatorEO);
                    }
                }
            }
        }
        return siteList;
    }

    @RequestMapping("desktop")
    public String desktop(HttpServletRequest request, HttpServletResponse response, Model model) {
        return "/desktop/index";
    }

    @RequestMapping("ie_update")
    public String ie_update() {
        return "ie_update";
    }

    @ResponseBody
    @RequestMapping("clientLogs")
    public Object collectLogs(String desc) {
        SysLog.log(desc, "clientLogs", "collect");
        return getObject();
    }

    /**
     * 全局搜索-框架页
     *
     * @return
     */
    @RequestMapping("search")
    public String globalSearch(ModelMap map, HttpServletRequest request) {
        String columnTypeCode = request.getParameter("columnTypeCode");
        map.put("columnTypeCode", columnTypeCode);
        return "/content/global_search";
    }

    /**
     * 全局搜索-内容页
     *
     * @return
     */
    @RequestMapping("searchBody")
    public String globalSearch_Body(ModelMap map, HttpServletRequest request) {
        String columnTypeCode = request.getParameter("columnTypeCode");
        map.put("columnTypeCode", columnTypeCode);
        return "/content/global_search_body";
    }

    /**
     * lonsun_root 获取所有站点
     *
     * @return
     */
    @ResponseBody
    @RequestMapping("getRootSiteList")
    public Object getRootSiteList() {
        List<IndicatorEO> siteList = new ArrayList<IndicatorEO>();
        List<IndicatorEO> list = CacheHandler.getList(IndicatorEO.class, CacheGroup.CMS_TYPE, IndicatorEO.Type.CMS_Site.toString());
        if (null != list && !list.isEmpty()) {
            siteList.addAll(list);
        }
        list = CacheHandler.getList(IndicatorEO.class, CacheGroup.CMS_TYPE, IndicatorEO.Type.SUB_Site.toString());
        if (null != list && !list.isEmpty()) {
            siteList.addAll(list);
        }
        return getObject(siteList);
    }

    /**
     * 点击菜单时，看是否有绑定子站点
     *
     * @param indicatorId 菜单id
     * @return siteId
     */
    @ResponseBody
    @RequestMapping("getMenuSiteId")
    public Object getMenuSiteId(Long indicatorId) {
        if (LoginPersonUtil.isRoot()) {
            return getObject();
        }
        //默认登录或者自行切换的站点id
        Long siteId = LoginPersonUtil.getPreSiteId();
        if (indicatorId != null) {
            IndicatorEO cacheEO = CacheHandler.getEntity(IndicatorEO.class, indicatorId);
            if (cacheEO != null && cacheEO.getSiteId() != null) {
                siteId = cacheEO.getSiteId();
            }
        }
        //更新session的siteId
        LoginPersonUtil.setAttribute(InternalAccount.USER_SITEID, siteId);
        IndicatorEO site = CacheHandler.getEntity(IndicatorEO.class, siteId);
        return getObject(site);
    }

    /**
     * 站点切换列表
     *
     * @param map
     * @return
     */
    @RequestMapping("changeSite")
    public Object changeSite(ModelMap map) {
        return "/content/site_change";
    }
}