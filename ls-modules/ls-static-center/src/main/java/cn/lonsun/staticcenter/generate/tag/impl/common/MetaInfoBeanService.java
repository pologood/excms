/*
 * NavBeanService.java         2015年9月15日 <br/>
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

package cn.lonsun.staticcenter.generate.tag.impl.common;

import cn.lonsun.cache.client.CacheGroup;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.message.internal.entity.MessageEnum;
import cn.lonsun.publicInfo.internal.entity.PublicCatalogEO;
import cn.lonsun.publicInfo.internal.service.IPublicContentService;
import cn.lonsun.publicInfo.vo.PublicContentQueryVO;
import cn.lonsun.publicInfo.vo.PublicContentVO;
import cn.lonsun.site.site.internal.entity.ColumnConfigEO;
import cn.lonsun.site.site.internal.entity.ColumnMgrEO;
import cn.lonsun.site.site.internal.entity.ColumnTypeEO;
import cn.lonsun.site.site.internal.entity.SiteConfigEO;
import cn.lonsun.site.site.internal.service.IColumnTypeService;
import cn.lonsun.staticcenter.generate.GenerateConstant;
import cn.lonsun.staticcenter.generate.tag.BeanService;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.tag.impl.article.ArticleNewsDetailBeanService;
import cn.lonsun.staticcenter.generate.tag.impl.publicInfo.PublicConstant;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import cn.lonsun.staticcenter.generate.util.PathUtil;
import cn.lonsun.system.datadictionary.internal.entity.DataDictItemEO;
import cn.lonsun.util.PublicCatalogUtil;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 头部信息公用标签 <br/>
 *
 * @author liuk <br/>
 * @version v1.0 <br/>
 * @date 2017年6月28日 <br/>
 */
@Component
public class MetaInfoBeanService extends AbstractBeanService {

    @Resource
    private IPublicContentService publicContentService;
    @Autowired
    private IColumnTypeService icts;

    @Override
    public Object getObject(JSONObject paramObj) {
        Context context = ContextHolder.getContext();
        Long siteId = context.getSiteId();// 站点id
        Long columnId = context.getColumnId();// 栏目或部门id
        Long source = context.getSource();// 来源 1.内容协同 2.信息公开
        Long scope = context.getScope();// 1.首页 2.栏目页 3.文章页 针对全站生成情况

        Map<String, Object> resultMap = new HashMap<String, Object>();
        IndicatorEO siteInfo = CacheHandler.getEntity(IndicatorEO.class, siteId);
        SiteConfigEO siteConfigEO = CacheHandler.getEntity(SiteConfigEO.class, CacheGroup.CMS_INDICATORID, siteId);
        if (siteInfo != null) {
            resultMap.put("SiteName", siteConfigEO.getSiteTitle());
            resultMap.put("SiteDomain", siteInfo.getUri());
            resultMap.put("SiteIDCode", siteConfigEO.getSiteIDCode());
        }
        if (MessageEnum.CONTENTINFO.value().equals(source)) {// 内容协同
            if (MessageEnum.COLUMN.value().equals(scope)) {// 栏目页
                ColumnMgrEO columnMgrEO = CacheHandler.getEntity(ColumnMgrEO.class, columnId);
                ColumnConfigEO columnConfigEO = CacheHandler.getEntity(ColumnConfigEO.class, CacheGroup.CMS_INDICATORID, columnId);
                setColumnInfo(resultMap, columnMgrEO.getName(), columnMgrEO.getName(), columnMgrEO.getDescription(), columnMgrEO.getKeyWords(), columnConfigEO.getColumnClassCode());
            } else if (MessageEnum.CONTENT.value().equals(scope)) {// 文章页
                BaseContentEO bce = CacheHandler.getEntity(BaseContentEO.class, context.getContentId());
                ColumnMgrEO columnMgrEO = CacheHandler.getEntity(ColumnMgrEO.class, bce.getColumnId());
                ColumnConfigEO columnConfigEO = CacheHandler.getEntity(ColumnConfigEO.class, CacheGroup.CMS_INDICATORID, bce.getColumnId());
                setColumnInfo(resultMap, columnMgrEO.getName(), columnMgrEO.getName(), columnMgrEO.getDescription(), columnMgrEO.getKeyWords(), columnConfigEO.getColumnClassCode());
                if (bce != null) {
                    resultMap.put("ArticleTitle", bce.getTitle());
                    resultMap.put("PubDate", bce.getPublishDate());
                    resultMap.put("ContentSource", bce.getResources());
                    resultMap.put("Keywords", "");
                    resultMap.put("Author", bce.getAuthor());
                    resultMap.put("Description", bce.getRemarks());
                    resultMap.put("Url", PathUtil.getLinkPath(columnId, context.getContentId()));
                }
                String typeCode = context.getTypeCode();// 文章类型
                // 拿到处理的beanservice
                BeanService service = null;
                try {
                    service = SpringContextHolder.getBean(typeCode + "DetailBeanService");
                } catch (Throwable e) {
                    // 当没有找到其他类型文章页预处理逻辑时，默认采用文章新闻
                    service = SpringContextHolder.getBean(ArticleNewsDetailBeanService.class);
                }
                Object resultObj = service.getObject(null);
                resultMap.put("ContentInfo", resultObj);
            }

        } else if (MessageEnum.PUBLICINFO.value().equals(source)) {// 信息公开
            if (MessageEnum.COLUMN.value().equals(scope)) {
                if (!context.getParamMap().get("type").equals("4")) {
                    if (context.getParamMap().get("type").equals("1")) {
                        resultMap.put("ColumnName", "政府信息公开制度");
                        resultMap.put("ColumnType", "信息公开制度");
                        resultMap.put("ColumnDescription", "政府信息公开专栏、人民政府信息公开指南");
                        resultMap.put("ColumnKeywords", "信息公开制度");
                    } else if (context.getParamMap().get("type").equals("2")) {
                        PublicContentQueryVO queryVO = new PublicContentQueryVO();
                        queryVO.setSiteId(siteId);
                        queryVO.setIsPublish(1);// 查询已发布的文章
                        queryVO.setOrganId(columnId);
                        queryVO.setType(PublicConstant.PublicTypeEnum.getType("2"));// 设置类型
                        DataDictItemEO dict = CacheHandler.getEntity(DataDictItemEO.class, CacheGroup.CMS_CODE, PublicConstant.PublicTypeEnum.PUBLIC_GUIDE.name());
                        if(null != dict){
                            queryVO.setCatId(dict.getItemId());// 设置目录id
                            PublicContentVO vo = publicContentService.getPublicContent(queryVO);
                            if(null != vo){
                                resultMap.put("ColumnType", vo.getTitle());
                            }
                        }
                        resultMap.put("ColumnName", "政府信息公开指南");
                        resultMap.put("ColumnDescription", "政府信息公开专栏、人民政府信息公开指南");
                        resultMap.put("ColumnKeywords", "信息公开指南");
                    } else if (context.getParamMap().get("type").equals("3")) {
                        resultMap.put("ColumnName", "政府信息公开年报");
                        resultMap.put("ColumnType", "信息公开年报");
                        resultMap.put("ColumnDescription", "政府信息公开专栏、人民政府信息公开年报");
                        resultMap.put("ColumnKeywords", "信息公开年报");
                    } else if (context.getParamMap().get("type").equals("5")) {
                        resultMap.put("ColumnName", "依申请公开");
                        resultMap.put("ColumnType", "依申请公开");
                        resultMap.put("ColumnDescription", "政府信息公开依申请公开");
                        resultMap.put("ColumnKeywords", "政府信息公开申请、依申请公开");
                    }
                    return resultMap;
                }
                if (!StringUtils.isEmpty(context.getParamMap().get("catId"))) {
                    PublicCatalogEO publicCatalogEO = PublicCatalogUtil.getPrivateCatalogByOrganId(columnId, Long.parseLong(context.getParamMap().get("catId")));
                    if (publicCatalogEO != null) {
                        setColumnInfo(resultMap, "信息公开目录", publicCatalogEO.getName(), publicCatalogEO.getDescription(), publicCatalogEO.getKeyWords(), publicCatalogEO.getColumnTypeIds());
                    }
                } else {// 没有目录id 为政府信息公开目录首页
                    resultMap.put("ColumnName", "政府信息公开目录首页");
                    resultMap.put("ColumnType", "政府信息公开目录首页");
                    resultMap.put("ColumnDescription", "政府信息公开目录首页");
                    resultMap.put("ColumnKeywords", "政府信息公开目录首页");
                }
            } else if (MessageEnum.CONTENT.value().equals(scope)) {
                Long contentId = context.getContentId();// 根据文章id查询文章
                PublicContentVO vo = publicContentService.getPublicContentByBaseContentId(contentId);
                //如果两张表中有一张表的数据已删除，则会出现找不到数据的情况
                if(vo != null){
                    Long catId = vo.getCatId();
                    PublicCatalogEO publicCatalogEO = PublicCatalogUtil.getPrivateCatalogByOrganId(vo.getOrganId(), catId);
                    if (publicCatalogEO != null) {
                        setColumnInfo(resultMap, "信息公开目录", publicCatalogEO.getName(), publicCatalogEO.getDescription(), publicCatalogEO.getKeyWords(), publicCatalogEO.getColumnTypeIds());
                    }
                    resultMap.put("ArticleTitle", vo.getTitle());
                    resultMap.put("PubDate", vo.getPublishDate());
                    resultMap.put("ContentSource", vo.getResources());
                    resultMap.put("Keywords", vo.getKeyWords());
                    resultMap.put("Author", vo.getAuthor());
                    resultMap.put("Description", "");
                    resultMap.put("Url", PathUtil.getLinkPath(catId, contentId));
                }
                String typeCode = context.getTypeCode();// 文章类型
                // 拿到处理的beanservice
                BeanService service = null;
                try {
                    service = SpringContextHolder.getBean(typeCode + "DetailBeanService");
                } catch (Throwable e) {
                    // 当没有找到其他类型文章页预处理逻辑时，默认采用文章新闻
                    service = SpringContextHolder.getBean(ArticleNewsDetailBeanService.class);
                }
                Object resultObj = service.getObject(null);
                resultMap.put("PublicContentInfo", resultObj);
            }
        }

        return resultMap;
    }

    /**
     * 如果有栏目配置了描述，使用栏目配置，如果没有，查询配置的标签，如果没有配置标签，设置为栏目名称
     * @param resultMap
     * @param name
     * @param descr
     * @param keywords
     * @param ctids
     */
    public void setColumnInfo(Map<String, Object> resultMap, String type, String name, String descr, String keywords, String ctids){
        resultMap.put("ColumnName", name);
        resultMap.put("ColumnType", "");
        resultMap.put("ColumnDescription", descr==null?"":descr);
        resultMap.put("ColumnKeywords", keywords==null?"":keywords);
        //==如果栏目没有配置，则查询配置的标签
        if ((StringUtils.isEmpty(descr) || StringUtils.isEmpty(keywords)) && !StringUtils.isEmpty(ctids)) {
            String buf[] = ctids.split(",");
            List<Long> buff = new ArrayList<Long>();
            for (int i = 0; i < buf.length; i++) {
                if(StringUtils.isEmpty(buf[i])){
                    continue;
                }
                buff.add(Long.parseLong(buf[i]));
            }
            List<ColumnTypeEO> ctes = icts.getCtsByIds(buff.toArray(new Long[]{}));
            if (ctes != null && !ctes.isEmpty()) {
                String s1 = "", s2 = "", s3 = "";
                for (ColumnTypeEO cte : ctes) {
                    s1 += cte.getTypeName() + "，";
                    s2 += cte.getDescription() + "，";
                    s3 += cte.getKeyWords() + "，";
                }
                if(StringUtils.isEmpty(String.valueOf(resultMap.get("ColumnType")))){
                    resultMap.put("ColumnType", s1.substring(0, s1.length() - 1));
                }
                if(StringUtils.isEmpty(String.valueOf(resultMap.get("ColumnDescription")))){
                    resultMap.put("ColumnDescription", s2.substring(0, s2.length() - 1));
                }
                if(StringUtils.isEmpty(String.valueOf(resultMap.get("ColumnKeywords")))){
                    resultMap.put("ColumnKeywords", s3.substring(0, s3.length() - 1));
                }
            }
        }
        //==标签配置查询完毕，如果也没有配置标签，则设置为栏目名称
        if(StringUtils.isEmpty(String.valueOf(resultMap.get("ColumnType")))){
            resultMap.put("ColumnType", type);
        }
        if(StringUtils.isEmpty(String.valueOf(resultMap.get("ColumnDescription")))){
            resultMap.put("ColumnDescription", name);
        }
        if(StringUtils.isEmpty(String.valueOf(resultMap.get("ColumnKeywords")))){
            resultMap.put("ColumnKeywords", name);
        }
    }

}