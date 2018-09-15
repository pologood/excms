/*
 * PublicAnalysisController.java         2016年9月6日 <br/>
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

package cn.lonsun.publicInfo.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import cn.lonsun.cache.client.CacheGroup;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.publicInfo.internal.entity.PublicCatalogEO;
import cn.lonsun.publicInfo.internal.service.IPublicContentService;
import cn.lonsun.publicInfo.vo.OrganVO;
import cn.lonsun.publicInfo.vo.PublicContentQueryVO;
import cn.lonsun.rbac.internal.entity.OrganEO;
import cn.lonsun.rbac.internal.service.IOrganService;
import cn.lonsun.site.site.internal.entity.SiteConfigEO;
import cn.lonsun.util.LoginPersonUtil;
import cn.lonsun.util.PublicCatalogUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.publicInfo.internal.service.IPublicAnalysisService;
import cn.lonsun.publicInfo.vo.PublicAnalysisQueryVO;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 信息公开统计分析 <br/>
 * 
 * @date 2016年9月6日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
@Controller
@RequestMapping("/public/analysis")
public class PublicAnalysisController extends BaseController {

    @Resource
    private IPublicAnalysisService publicAnalysisService;

    @Resource
    private IOrganService organService;

    @Resource
    private IPublicContentService publicContentService;

    @RequestMapping("index")
    public String index() {
        return "/public/analysis/index";
    }

    @RequestMapping("catalogIndex")
    public String catalogIndex(Model model) {
        SiteConfigEO siteConfigEO = CacheHandler.getEntity(SiteConfigEO.class, CacheGroup.CMS_INDICATORID, LoginPersonUtil.getSiteId());
        List<OrganEO> organList = organService.getPublicOrgans(Long.valueOf(siteConfigEO.getUnitIds()));
        model.addAttribute("organList",organList);
        return "/public/analysis/catalog_index";
    }

    @RequestMapping("catalogDetail")
    public String catalogDetail(Model model) {
        SiteConfigEO siteConfigEO = CacheHandler.getEntity(SiteConfigEO.class, CacheGroup.CMS_INDICATORID, LoginPersonUtil.getSiteId());
        List<OrganEO> organList = organService.getPublicOrgans(Long.valueOf(siteConfigEO.getUnitIds()));
        model.addAttribute("organList",organList);
        return "/public/analysis/catalog_detail";
    }

    @ResponseBody
    @RequestMapping("getOrganRanking")
    public Object getOrganRanking(PublicAnalysisQueryVO queryVO) {
        return getObject(publicAnalysisService.getRanking(queryVO, IPublicAnalysisService.PublicAnalysisType.organRanking));
    }

    @ResponseBody
    @RequestMapping("getApplyRanking")
    public Object getApplyRanking(PublicAnalysisQueryVO queryVO) {
        return getObject(publicAnalysisService.getRanking(queryVO, IPublicAnalysisService.PublicAnalysisType.applyRanking));
    }

    @ResponseBody
    @RequestMapping("getReplyRanking")
    public Object getReplyRanking(PublicAnalysisQueryVO queryVO) {
        return getObject(publicAnalysisService.getRanking(queryVO, IPublicAnalysisService.PublicAnalysisType.replyRanking));
    }

    @ResponseBody
    @RequestMapping("getReplyStatusRanking")
    public Object getReplyStatusRanking(PublicAnalysisQueryVO queryVO) {
        return getObject(publicAnalysisService.getRanking(queryVO, IPublicAnalysisService.PublicAnalysisType.replyStatusRanking));
    }

    @ResponseBody
    @RequestMapping("getCatalogRanking")
    public Object getCatalogRanking(PublicAnalysisQueryVO queryVO) {
        return getObject(publicAnalysisService.getRanking(queryVO, IPublicAnalysisService.PublicAnalysisType.catalogRanking));
    }

    @RequestMapping("exportOrganRanking")
    public void exportOrganRanking(PublicAnalysisQueryVO queryVO, HttpServletResponse response) {
        publicAnalysisService.exportRanking(queryVO, IPublicAnalysisService.PublicAnalysisType.organRanking, response);
    }

    @RequestMapping("exportApplyRanking")
    public void exportApplyRanking(PublicAnalysisQueryVO queryVO, HttpServletResponse response) {
        publicAnalysisService.exportRanking(queryVO, IPublicAnalysisService.PublicAnalysisType.applyRanking, response);
    }

    @RequestMapping("exportReplyRanking")
    public void exportReplyRanking(PublicAnalysisQueryVO queryVO, HttpServletResponse response) {
        publicAnalysisService.exportRanking(queryVO, IPublicAnalysisService.PublicAnalysisType.replyRanking, response);
    }

    @RequestMapping("exportReplyStatusRanking")
    public void exportReplyStatusRanking(PublicAnalysisQueryVO queryVO, HttpServletResponse response) {
        publicAnalysisService.exportRanking(queryVO, IPublicAnalysisService.PublicAnalysisType.replyStatusRanking, response);
    }

    @RequestMapping("exportCatalogRanking")
    public void exportCatalogRanking(PublicAnalysisQueryVO queryVO, HttpServletResponse response) {
        publicAnalysisService.exportRanking(queryVO, IPublicAnalysisService.PublicAnalysisType.catalogRanking, response);
    }

    @ResponseBody
    @RequestMapping("getPublicContentStatis")
    public Object getPublicContentStatis(PublicAnalysisQueryVO vo) {
        PublicContentQueryVO queryVO = new PublicContentQueryVO();
        queryVO.setSiteId(LoginPersonUtil.getSiteId());

        List<OrganVO> organList = (List<OrganVO>) getOrgansBySiteId();//信息公开单位
        List<Long> organIds = new ArrayList<Long>();
        if(null != organList && organList.size()>0){
            for(OrganVO organVO : organList){
                organIds.add(organVO.getOrganId());
            }
            queryVO.setOrganIds(organIds);//信息公开单位ids
        }

        List<Long> catalogIds = new ArrayList<Long>();
        // 查询出子目录信息
        List<PublicCatalogEO> childList = CacheHandler.getList(PublicCatalogEO.class, CacheGroup.CMS_PARENTID, vo.getParentId());
        if(null == childList){
            return getObject();
        }
        // 过滤出显示的
//        PublicCatalogUtil.filterCatalogList(childList, queryVO.getOrganId(), true);
        for (Iterator<PublicCatalogEO> it = childList.iterator(); it.hasNext(); ){
            PublicCatalogEO eo = it.next();
            if (!eo.getIsShow()) {
                it.remove();
                continue;
            }
        }
        // 排序
        PublicCatalogUtil.sortCatalog(childList);
        if(null != childList && childList.size()>0){
            Long[] catIds = new Long[childList.size()];
            for(PublicCatalogEO catalogEO : childList){
                catalogIds.add(catalogEO.getId());
            }
            catalogIds.toArray(catIds);//信息公开目录
            queryVO.setCatIds(catIds);//信息公开目录ids
        }

        List<Map<String,Object>> mapList = publicContentService.getPublicContentStatisByCatalogAndOrganId(queryVO);



        List<Long> catIds = new ArrayList<Long>();
        if(null != mapList && mapList.size()>0){
            for(Map<String,Object> map :mapList){
                if(map.get("ISPARENT").toString().equals(String.valueOf(1)) ){
                    sumSubCount(map,mapList,organIds,catIds);
                }
            }
        }

        List<Map<String,Object>> resultList = new ArrayList<Map<String, Object>>();
        for(Map<String,Object> map : mapList){
            String catId = map.get("CATID").toString();
            if(catalogIds.indexOf(Long.valueOf(catId)) > -1){
                if(null == map.get("TOTALCOUNT")){
                    map.put("TOTALCOUNT",0);
                }
                for(Long organId : organIds){
                    if(null == map.get("C_"+organId)){
                        map.put("C_"+organId,0);
                    }
                }
                if(map.get("ISPARENT").toString().equals(String.valueOf(1)) ){
                    map.put("isLeaf",false);
                }else {
                    map.put("isLeaf",true);
                }
                map.put("expanded",false);
                resultList.add(map);
            }

        }
        return resultList;
    }

    public void sumSubCount(Map<String,Object> map,List<Map<String,Object>> mapList,List<Long> organIds,List<Long> catIds){
        String catId = map.get("CATID").toString();
        if(catIds.indexOf(Long.valueOf(catId)) > -1){
            return;
        }
        for(int i = 0; i < mapList.size(); i++){
            Map<String,Object> subMap  = mapList.get(i);
            String parentId = subMap.get("PARENTID").toString();
            String subCatId = subMap.get("CATID").toString();

            if(Long.valueOf(parentId).equals(Long.valueOf(catId))){

                if(subMap.get("ISPARENT").toString().equals(String.valueOf(1)) ){
                    sumSubCount(subMap,mapList,organIds,catIds);
                }
                Integer totalcount = 0;
                if(null != map.get("TOTALCOUNT")){
                    totalcount = Integer.valueOf(map.get("TOTALCOUNT").toString());
                }
                for(Long organId : organIds){
                    Integer count = 0;
                    if(null != map.get("C_"+organId)){
                        count = Integer.valueOf(map.get("C_"+organId).toString());
                    }
                    Integer subCount = 0;
                    if(null != subMap.get("C_"+organId)){
                        subCount = Integer.valueOf(subMap.get("C_"+organId).toString());
                    }
                    totalcount += subCount;
                    map.put("C_"+organId,count+subCount);
                }
                map.put("TOTALCOUNT",totalcount);

                if(!subMap.get("ISPARENT").toString().equals(String.valueOf(1)) && catIds.indexOf(Long.valueOf(catId)) < 0){
                    catIds.add(Long.valueOf(catId));
                }
            }
        }
    }

    /**
     * 公开目录的组织
     *
     * @return
     * @author fangtinghua
     */
    @ResponseBody
    @RequestMapping("getOrgansBySiteId")
    public Object getOrgansBySiteId() {
        Long siteId = LoginPersonUtil.getSiteId();
        SiteConfigEO siteConfigEO = CacheHandler.getEntity(SiteConfigEO.class, CacheGroup.CMS_INDICATORID, siteId);
        List<OrganVO> voList = new ArrayList<OrganVO>();
        if (StringUtils.isEmpty(siteConfigEO.getUnitIds())) {
            return voList;
        }
        List<OrganEO> organList = organService.getPublicOrgans(Long.valueOf(siteConfigEO.getUnitIds()));
        PublicCatalogUtil.filterOrgan(organList, voList);// 过滤单位信息
        PublicCatalogUtil.sortOrgan(voList);// 排序
        return voList;
    }
}