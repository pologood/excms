/*
 * PublicAnalysisServiceImpl.java         2016年9月6日 <br/>
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

package cn.lonsun.publicInfo.internal.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import cn.lonsun.cache.client.CacheGroup;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.publicInfo.vo.OrganVO;
import cn.lonsun.rbac.internal.service.IOrganService;
import cn.lonsun.site.site.internal.entity.SiteConfigEO;
import cn.lonsun.util.PublicCatalogUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.CSVUtils;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.publicInfo.internal.entity.OrganConfigEO;
import cn.lonsun.publicInfo.internal.entity.PublicCatalogEO;
import cn.lonsun.publicInfo.internal.entity.PublicContentEO;
import cn.lonsun.publicInfo.internal.service.IOrganConfigService;
import cn.lonsun.publicInfo.internal.service.IPublicAnalysisService;
import cn.lonsun.publicInfo.internal.service.IPublicApplyService;
import cn.lonsun.publicInfo.internal.service.IPublicCatalogService;
import cn.lonsun.publicInfo.internal.service.IPublicContentService;
import cn.lonsun.publicInfo.vo.PublicAnalysisQueryVO;
import cn.lonsun.publicInfo.vo.PublicContentQueryVO;
import cn.lonsun.publicInfo.vo.PublicTjVO;
import cn.lonsun.rbac.internal.entity.OrganEO;
import cn.lonsun.system.datadictionary.vo.DataDictVO;
import cn.lonsun.util.DataDictionaryUtil;
import cn.lonsun.util.LoginPersonUtil;

/**
 * TODO <br/>
 *
 * @date 2016年9月6日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
@Service
public class PublicAnalysisServiceImpl extends MockService<PublicContentEO> implements IPublicAnalysisService {

    @Resource
    private IPublicContentService publicContentService;
    @Resource
    private IPublicApplyService publicApplyService;
    @Resource
    private IOrganConfigService organConfigService;
    @Resource
    private IPublicCatalogService publicCatalogService;
    @Resource
    private IOrganService organService;

    @Override
    public Object getRanking(PublicAnalysisQueryVO queryVO, PublicAnalysisType type) {
        Object obj = null;// 返回值
        PublicContentQueryVO vo = new PublicContentQueryVO();
        if(AppUtil.isEmpty(queryVO.getSiteId())) {
            vo.setSiteId(LoginPersonUtil.getSiteId());
        }else {
            vo.setSiteId(queryVO.getSiteId());
        }
        vo.setStartDate(queryVO.getStartDate());
        vo.setEndDate(queryVO.getEndDate());
        vo.setReplyStatus(queryVO.getReplyStatus());
        Boolean isShowOrgans = false;
        if(queryVO.getIsOrgans() != null && queryVO.getIsOrgans() == 1){
            isShowOrgans = true;
        }
        if (!type.equals(PublicAnalysisType.applyRanking)) {// 依申请公开排行是统计所有的
            vo.setIsPublish(1);// 统计已发布的文章数
        }
        switch (type) {
            case organRanking:// 单位排行，查询所有单位的主动公开发文数的排行
                List<PublicTjVO> voList = publicContentService.getPublicTjList(vo);
                //如果显示所有单位的
                if(isShowOrgans){
                    voList = getPublicOrgans(voList,vo.getSiteId(),null);
                }else{
                    for (PublicTjVO tj : voList) {
                        OrganEO organEO = CacheHandler.getEntity(OrganEO.class, tj.getOrganId());
                        if (null != organEO) {
                            tj.setOrganName(organEO.getName());
                        }
                    }
                }
                return voList;
            case applyRanking:// 依申请公开排行、依申请公开回复排行
            case replyRanking:
                List<PublicTjVO> list = publicApplyService.getPublicTjList(vo);
                if(isShowOrgans){
                    list = getPublicOrgans(list,vo.getSiteId(),null);
                }else {
                    for (PublicTjVO tj : list) {
                        OrganEO organEO = CacheHandler.getEntity(OrganEO.class, tj.getOrganId());
                        if (null != organEO) {
                            tj.setOrganName(organEO.getName());
                        }
                    }
                }
                return list;
            case replyStatusRanking:
                list = publicApplyService.getPublicTjByApplyStatus(vo);
//                if (null == list || list.isEmpty()) {
//                    return resultList;
//                }
                List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
                if (StringUtils.isNotEmpty(vo.getReplyStatus())) {
                    //如果是显示所有
                    if(isShowOrgans){
                        list = getPublicOrgans(list,vo.getSiteId(),vo.getReplyStatus());
                    }
                    for (PublicTjVO tj : list) {
                        Map<String, Object> map = new LinkedHashMap<String, Object>();
                        if(!isShowOrgans){
                            Long organId = tj.getOrganId();
                            OrganEO organEO = CacheHandler.getEntity(OrganEO.class, organId);
                            if (null != organEO) {
                                map.put("organName", organEO.getName());
                            }
                        }else{
                            map.put("organName", tj.getOrganName());
                        }
                        map.put(tj.getReplyStatus(), tj.getTotal());
                        map.put("statusTotal", tj.getTotal());
                        resultList.add(map);
                    }
                } else {
                    Map<Long, List<PublicTjVO>> organStatusMap = new LinkedHashMap<Long, List<PublicTjVO>>();
                    for (PublicTjVO tj : list) {
                        Long organId = tj.getOrganId();
                        if (organStatusMap.containsKey(organId)) {
                            organStatusMap.get(organId).add(tj);
                        } else {
                            List<PublicTjVO> tempList = new ArrayList<PublicTjVO>();
                            tempList.add(tj);
                            organStatusMap.put(organId, tempList);
                        }
                    }
                    //如果是显示所有
                    if(isShowOrgans) {
                        resultList = getPublicOrgansStatus(organStatusMap, vo.getSiteId());
                    }else{
                        for (Map.Entry<Long, List<PublicTjVO>> entry : organStatusMap.entrySet()) {
                            Map<String, Object> map = new LinkedHashMap<String, Object>();
                            OrganEO organEO = CacheHandler.getEntity(OrganEO.class, entry.getKey());
                            if (null != organEO) {
                                map.put("organName", organEO.getName());
                            }
                            Long total = 0L;
                            List<PublicTjVO> tempList = entry.getValue();
                            for (PublicTjVO v : tempList) {
                                total += v.getTotal();
                                map.put(v.getReplyStatus(), v.getTotal());
                            }
                            map.put("statusTotal", total);
                            resultList.add(map);
                        }
                    }

                }
                return resultList;
            case catalogRanking:
                Long catId = queryVO.getCatId();
                String catIds = queryVO.getCatIds();
                if (null == catId || StringUtils.isEmpty(catIds)) {
                    throw new BaseRunTimeException(TipsMode.Message.toString(), "请选择信息公开目录！");
                }
                List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
                // 处理父子关系，方便在后续的数值中计算父目录的发文条数
                List<Long> catIdList = new ArrayList<Long>();
                Map<Long, String[]> idChildrenArrayMap = new HashMap<Long, String[]>();
                if (StringUtils.isNotEmpty(catIds)) {
                    int index = 0;
                    String[] catIdArr = catIds.split(",");
                    String childrenIds = queryVO.getChildrenIds();
                    String[] childIdArr = childrenIds.split(",");
                    for (String id : catIdArr) {
                        catIdList.add(Long.valueOf(id));
                        String childrenId = childIdArr[index++];
                        if (!"-9999".equals(childrenId)) {// 页面使用特殊数值来替换
                            idChildrenArrayMap.put(Long.valueOf(id), childrenId.split("-"));
                        }
                    }
                }
                // 查询出所有绑定该目录的单位列表
                Map<String, Object> paramsMap = new HashMap<String, Object>();
                paramsMap.put("catId", catId);
                List<OrganConfigEO> organConfigList = organConfigService.getEntities(OrganConfigEO.class, paramsMap);
                if (null != organConfigList && !organConfigList.isEmpty()) {
                    vo.setCatIds(catIdList.toArray(new Long[] {}));
                    for (OrganConfigEO organConfig : organConfigList) {
                        vo.setOrganId(organConfig.getOrganId());
                        voList = publicContentService.getPublicTjByCatIdList(vo);
                        // 结果按照cat分组
                        Map<Long, PublicTjVO> catMap = new HashMap<Long, PublicTjVO>();
                        if (null != voList && !voList.isEmpty()) {
                            for (PublicTjVO tj : voList) {
                                catMap.put(tj.getCatId(), tj);
                            }
                        }
                        int index = 0;// 序列
                        Map<String, Object> resultMap = new LinkedHashMap<String, Object>();
                        // 放入单位名称
                        OrganEO organEO = CacheHandler.getEntity(OrganEO.class, organConfig.getOrganId());
                        if (null != organEO) {
                            resultMap.put("organName", organEO.getName());
                        }
                        resultMap.put("catTotal",0L);//第二行设置，导出时
                        Long catTotal = 0L;
                        // 递归合并结果
                        for (Long id : catIdList) {
                            Long total = 0L;
                            if (catMap.containsKey(id)) {
                                total += catMap.get(id).getTotal();
                            }
                            if (idChildrenArrayMap.containsKey(id)) {
                                total += getTotalLong(idChildrenArrayMap.get(id), idChildrenArrayMap, catMap);
                            }
                            resultMap.put("value" + index++, total);
                            catTotal = catTotal + total;
                        }
                        if(catTotal != 0){
                            resultMap.put("catTotal",catTotal);
                        }
                        listMap.add(resultMap);
                    }
                }
                return listMap;
            default:
                break;
        }
        return obj;
    }

    /**
     * 构造数据
     * @param organStatusMap
     * @param siteId
     * @return
     */
    private List<Map<String, Object>> getPublicOrgansStatus(Map<Long, List<PublicTjVO>> organStatusMap, Long siteId) {
        SiteConfigEO siteConfigEO = CacheHandler.getEntity(SiteConfigEO.class, CacheGroup.CMS_INDICATORID, siteId);
        List<OrganEO> organList = organService.getPublicOrgans(Long.valueOf(siteConfigEO.getUnitIds()));
        if (null == organList || organList.isEmpty()) {
            return null;
        }
        List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
        List<OrganVO> organVOList = new ArrayList<OrganVO>();
        PublicCatalogUtil.filterOrgan(organList, organVOList);// 过滤单位列表
        PublicCatalogUtil.sortOrgan(organVOList);// 排序
        PublicTjVO pt = null;
        for(OrganVO organ:organVOList){
            Map<String, Object> map = new LinkedHashMap<String, Object>();
            if(organStatusMap != null && organStatusMap.containsKey(organ.getOrganId())){
                map.put("organName", organ.getName());
                Long total = 0L;
                List<PublicTjVO> tempList = organStatusMap.get(organ.getOrganId());
                for (PublicTjVO v : tempList) {
                    total += v.getTotal();
                    map.put(v.getReplyStatus(), v.getTotal());
                }
                map.put("statusTotal", total);
            }else{
                map.put("organName", organ.getName());
                map.put("info_not_exist",0L);
                map.put("statusTotal", 0L);
            }
            resultList.add(map);
        }
        return resultList;
    }

    /**
     * 处理所有单位数
     * @param voList
     * @param siteId
     * @return
     */
    private List<PublicTjVO> getPublicOrgans(List<PublicTjVO> voList,Long siteId,String replyStatus) {
        SiteConfigEO siteConfigEO = CacheHandler.getEntity(SiteConfigEO.class, CacheGroup.CMS_INDICATORID, siteId);
        List<OrganEO> organList = organService.getPublicOrgans(Long.valueOf(siteConfigEO.getUnitIds()));
        if (null == organList || organList.isEmpty()) {
            return null;
        }
        List<OrganVO> organVOList = new ArrayList<OrganVO>();
        PublicCatalogUtil.filterOrgan(organList, organVOList);// 过滤单位列表
        PublicCatalogUtil.sortOrgan(organVOList);// 排序
        Map<Long,PublicTjVO> organsMap = null;
        if(voList != null && voList.size() > 0){
            organsMap =  new HashMap<Long, PublicTjVO>();
            for(PublicTjVO pt:voList){
                organsMap.put(pt.getOrganId(),pt);
            }
        }
        List<PublicTjVO> pts = new ArrayList<PublicTjVO>();
        PublicTjVO pt = null;
        for(OrganVO organ:organVOList){
            pt = new PublicTjVO();
            pt.setReplyStatus(replyStatus);
            if(organsMap != null){
                PublicTjVO vo  = organsMap.get(organ.getOrganId());
                if(vo != null){
                    BeanUtils.copyProperties(vo, pt);
                }
            }
            pt.setOrganId(organ.getOrganId());
            pt.setOrganName(organ.getName());
            pts.add(pt);
        }
        return pts;
    }


    private Long getTotalLong(String[] childrenList, Map<Long, String[]> idChildrenArrayMap, Map<Long, PublicTjVO> catMap) {
        Long total = 0L;
        for (String childrenId : childrenList) {
            Long id = Long.valueOf(childrenId);
            if (catMap.containsKey(id)) {
                total += catMap.get(id).getTotal();
            }
            if (idChildrenArrayMap.containsKey(id)) {
                total += getTotalLong(idChildrenArrayMap.get(id), idChildrenArrayMap, catMap);
            }
        }
        return total;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void exportRanking(PublicAnalysisQueryVO queryVO, PublicAnalysisType type, HttpServletResponse response) {
        Object obj = this.getRanking(queryVO, type);
        Boolean isShowOrgans = false;
        if(queryVO.getIsOrgans() != null && queryVO.getIsOrgans() == 1){
            isShowOrgans = true;
        }
        switch (type) {
            case organRanking:// 单位排行，查询所有单位的主动公开发文数的排行
                String[] titles = new String[] { "部门名称","已发布数","未发布数", "总数" };
                List<String[]> datas = new ArrayList<String[]>();
                List<PublicTjVO> voList = (List<PublicTjVO>) obj;
                if (null != voList && !voList.isEmpty()) {
                    for (PublicTjVO tj : voList) {
                        String[] row = new String[4];
                        if(!isShowOrgans){
                            OrganEO organEO = CacheHandler.getEntity(OrganEO.class, tj.getOrganId());
                            if (null != organEO) {
                                row[0] = organEO.getName();
                            }
                        }else{
                            row[0] = tj.getOrganName();
                        }
                        row[1] = tj.getPublishCount() + "";
                        row[2] = tj.getNotPublishCount() + "";
                        row[3] = tj.getTotal() + "";
                        datas.add(row);
                    }
                }
                // 导出
                String name = "部门排行";
                try {
                    CSVUtils.download(name, titles, datas, response);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case applyRanking:// 依申请公开排行、依申请公开回复排行
            case replyRanking:
                titles = new String[] { "部门名称","已发布数","未发布数","已回复数","总数" };
                datas = new ArrayList<String[]>();
                List<PublicTjVO> list = (List<PublicTjVO>) obj;
                for (PublicTjVO tj : list) {
                    String[] row = new String[5];
                    if(!isShowOrgans) {
                        OrganEO organEO = CacheHandler.getEntity(OrganEO.class, tj.getOrganId());
                        if (null != organEO) {
                            row[0] = organEO.getName();
                        }
                    }else {
                        row[0] = tj.getOrganName();
                    }
                    row[1] = tj.getPublishCount()+"";
                    row[2] = tj.getNotPublishCount()+"";
                    row[3] = tj.getReplyCount()+"";
                    row[4] = tj.getApplyCount()+"";
//                    if (PublicAnalysisType.applyRanking == type) {
//                        row[1] = tj.getApplyCount() + "";
//                    } else {
//                        row[1] = tj.getReplyCount() + "";
//                    }
                    datas.add(row);
                }
                // 导出
                name = PublicAnalysisType.applyRanking == type ? "依申请公开排行" : "依申请公开回复排行";
                try {
                    CSVUtils.download(name, titles, datas, response);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case replyStatusRanking:
                String status = queryVO.getReplyStatus();
                List<String> forList = new ArrayList<String>();
                if (StringUtils.isNotEmpty(status)) {
                    DataDictVO dictVO = DataDictionaryUtil.getItem("public_apply_reply_status", status);
                    if (null == dictVO) {
                        throw new BaseRunTimeException(TipsMode.Message.toString(), "回复状态不存在！");
                    }
                    forList.add(dictVO.getCode());
                    titles = new String[] { "部门名称", dictVO.getKey(), "总数" };
                } else {
                    List<DataDictVO> dictVOList = DataDictionaryUtil.getDDList("public_apply_reply_status");
                    if (null == dictVOList || dictVOList.isEmpty()) {
                        throw new BaseRunTimeException(TipsMode.Message.toString(), "回复状态不存在！");
                    }
                    List<String> titlesList = new ArrayList<String>();
                    titlesList.add("部门名称");
                    for (DataDictVO vo : dictVOList) {
                        forList.add(vo.getCode());
                        titlesList.add(vo.getKey());
                    }
                    titlesList.add("总数");
                    titles = titlesList.toArray(new String[] {});
                }
                datas = new ArrayList<String[]>();
                List<Map<String, Object>> resultList = (List<Map<String, Object>>) obj;
                for (Map<String, Object> map : resultList) {
                    List<String> rowList = new ArrayList<String>();
                    rowList.add(map.get("organName").toString());
                    for (String code : forList) {
                        rowList.add(null == map.get(code) ? "0" : map.get(code).toString());
                    }
                    rowList.add(map.get("statusTotal").toString());
                    datas.add(rowList.toArray(new String[] {}));
                }
                // 导出
                try {
                    CSVUtils.download("依申请公开回复状态排行", titles, datas, response);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case catalogRanking:
                String[] columns = null;
                String catIds = queryVO.getCatIds();
                Long[] catIdArray = cn.lonsun.core.base.util.StringUtils.getArrayWithLong(catIds, ",");
                List<PublicCatalogEO> catalogList = publicCatalogService.getEntities(PublicCatalogEO.class, catIdArray);
                if (null != catalogList && !catalogList.isEmpty()) {
                    columns = new String[catalogList.size() + 2];
                    int index = 2;
                    columns[0] = "单位名称";
                    columns[1] = "总数";
                    for (PublicCatalogEO eo : catalogList) {
                        columns[index++] = eo.getName();
                    }
                }
                datas = new ArrayList<String[]>();
                List<Map<String, Object>> listMap = (List<Map<String, Object>>) obj;
                for (Map<String, Object> map : listMap) {
                    List<String> strList = new ArrayList<String>();
                    for (Object o : map.values()) {
                        strList.add(String.valueOf(o));
                    }
                    datas.add(strList.toArray(new String[] {}));
                }
                name = "重点信息统计";
                try {
                    CSVUtils.download(name, columns, datas, response);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }
}