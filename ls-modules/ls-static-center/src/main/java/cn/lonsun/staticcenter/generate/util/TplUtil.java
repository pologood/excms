/*
 * TplUtil.java         2016年5月13日 <br/>
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

package cn.lonsun.staticcenter.generate.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import cn.lonsun.cache.client.CacheGroup;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.message.internal.entity.MessageEnum;
import cn.lonsun.publicInfo.internal.entity.OrganConfigEO;
import cn.lonsun.site.contentModel.internal.entity.ContentModelEO;
import cn.lonsun.site.contentModel.internal.entity.ModelTemplateEO;
import cn.lonsun.site.site.internal.entity.ColumnConfigEO;
import cn.lonsun.site.site.internal.entity.SiteConfigEO;
import cn.lonsun.site.template.internal.entity.TemplateConfEO;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ThreadHolder;

/**
 * 模板工具类 <br/>
 * 
 * @date 2016年5月13日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
public class TplUtil {

    private static Map<Long, String> scopeMap = null;

    /**
     * 初始化模板类型关系
     */
    static {
        scopeMap = new HashMap<Long, String>();
        scopeMap.put(MessageEnum.INDEX.value(), "index");
        scopeMap.put(MessageEnum.COLUMN.value(), "column");
        scopeMap.put(MessageEnum.CONTENT.value(), "content");
    }

    /**
     * 获取首页模板
     * 
     * @author fangtinghua
     * @param source
     * @return
     */
    public static Map<String, TemplateConfEO> getIndexTemplate(Long source) {
        return getTemplate(null, source, MessageEnum.INDEX.value(), null);
    }

    /**
     * 获取栏目页模板
     * 
     * @author fangtinghua
     * @param columnId
     * @param source
     * @param process
     * @return
     */
    public static Map<String, TemplateConfEO> getColumnTemplate(Long columnId, Long source) {
        return getTemplate(columnId, source, MessageEnum.COLUMN.value(), null);
    }

    /**
     * 获取文章页模板
     * 
     * @author fangtinghua
     * @param columnId
     * @param source
     * @param typeCode
     * @return
     */
    public static Map<String, TemplateConfEO> getContentTemplate(Long columnId, Long source, String typeCode) {
        return getTemplate(columnId, source, MessageEnum.CONTENT.value(), typeCode);
    }

    /**
     * 获取模板信息
     * 
     * @author fangtinghua
     * @param columnId
     * @param source
     * @param scope
     * @param process
     * @param typeCode
     * @return
     */
    @SuppressWarnings("unchecked")
    private static Map<String, TemplateConfEO> getTemplate(Long columnId, Long source, Long scope, String typeCode) {
        Map<String, TemplateConfEO> tempMap = new HashMap<String, TemplateConfEO>();// 模板列表
        IndicatorEO indicatorEO = ThreadHolder.getContext(IndicatorEO.class, ThreadHolder.LocalParamsKey.site.toString());// 站点信息
        SiteConfigEO siteConfigEO = ThreadHolder.getContext(SiteConfigEO.class, ThreadHolder.LocalParamsKey.siteConfig.toString());
        if (IndicatorEO.Type.CMS_Site.toString().equals(indicatorEO.getType())) {// 站点
            Map<String, Long> tempIdMap = new HashMap<String, Long>();// 模板id列表
            if (MessageEnum.INDEX.value().equals(scope)) {// 首页
                if (MessageEnum.CONTENTINFO.value().equals(source)) {// 内容协同
                    tempIdMap.put(Context.From.PC.toString(), siteConfigEO.getIndexTempId());// pc
                    if (siteConfigEO.getIsWap() == 1) {
                        tempIdMap.put(Context.From.WAP.toString(), siteConfigEO.getWapTempId());// wap
                    }
                } else if (MessageEnum.PUBLICINFO.value().equals(source)) {// 信息公开
                    tempIdMap.put(Context.From.PC.toString(), siteConfigEO.getPublicTempId());// pc
                    if (siteConfigEO.getIsWap() == 1) {
                        tempIdMap.put(Context.From.WAP.toString(), siteConfigEO.getWapPublicTempId());// wap
                    }
                }
            } else {// 栏目页和文章页
                String contentModeCode = getContentModeCode(columnId, source);// 获取栏目类容模型
                if (StringUtils.isEmpty(contentModeCode)) {// 内容模型code
                    return tempMap;
                }
                String key = ThreadHolder.LocalParamsKey.contentMode + "_" + contentModeCode;
                Map<String, ModelTemplateEO> columnSourceTplMap = ThreadHolder.getContext(Map.class, key);
                if (null == columnSourceTplMap) {
                    ContentModelEO contentModelEO = CacheHandler.getEntity(ContentModelEO.class, CacheGroup.CMS_CODE, contentModeCode);
                    if (null == contentModelEO) {
                        return tempMap;
                    }
                    // 获取模板配置
                    List<ModelTemplateEO> modelTemplateList = CacheHandler.getList(ModelTemplateEO.class, CacheGroup.CMS_MODEL_ID, contentModelEO.getId());
                    if (null != modelTemplateList && !modelTemplateList.isEmpty()) {
                        columnSourceTplMap = new HashMap<String, ModelTemplateEO>();
                        for (ModelTemplateEO tpl : modelTemplateList) {
                            columnSourceTplMap.put(tpl.getModelTypeCode(), tpl);
                        }
                        ThreadHolder.setContext(key, columnSourceTplMap);
                    }
                }
                if (MessageEnum.COLUMN.value().equals(scope)) {// 栏目页，默认取第一个
                    ModelTemplateEO modelTemplateEO = columnSourceTplMap.get(CollectionUtils.get(columnSourceTplMap.keySet(), 0));
                    // 设置当前栏目类型
                    ThreadHolder.setContext(ThreadHolder.LocalParamsKey.modelTypeCode.toString(), modelTemplateEO.getModelTypeCode());
                    tempIdMap.put(Context.From.PC.toString(), modelTemplateEO.getColumnTempId());// pc
                    if (siteConfigEO.getIsWap() == 1) {
                        tempIdMap.put(Context.From.WAP.toString(), modelTemplateEO.getWapColumnTempId());// wap
                    }
                } else if (MessageEnum.CONTENT.value().equals(scope) && !StringUtils.isEmpty(typeCode)) {// 文章页
                    ModelTemplateEO modelTemplateEO = columnSourceTplMap.get(typeCode);
                    if (null != modelTemplateEO) {
                        tempIdMap.put(Context.From.PC.toString(), modelTemplateEO.getArticalTempId());// pc
                        if (siteConfigEO.getIsWap() == 1) {
                            tempIdMap.put(Context.From.WAP.toString(), modelTemplateEO.getWapArticalTempId());// wap
                        }
                    }
                }
            }
            if (!tempIdMap.isEmpty()) {// 循环
                for (Map.Entry<String, Long> entry : tempIdMap.entrySet()) {
                    if (null == entry.getValue()) {// 模板id为空
                        // wap站，如果模板id为空，则不加入判断
                        if (Context.From.WAP.toString().equals(entry.getKey())) {
                            continue;
                        } else {
                            tempMap.put(entry.getKey(), null);
                        }
                    } else {
                        String tplKey = ThreadHolder.LocalParamsKey.template + "_" + entry.getValue();
                        TemplateConfEO templateConfEO = ThreadHolder.getContext(TemplateConfEO.class, tplKey);
                        if (null == templateConfEO) {
                            templateConfEO = CacheHandler.getEntity(TemplateConfEO.class, entry.getValue());
                            ThreadHolder.setContext(tplKey, templateConfEO);
                        }
                        tempMap.put(entry.getKey(), templateConfEO);
                    }
                }
            }
        } else if (IndicatorEO.Type.SUB_Site.toString().equals(indicatorEO.getType())) {// 虚拟子站
            Long siteTempId = siteConfigEO.getSiteTempId();// 站点模板套
            if (null != siteTempId) {
                String key = ThreadHolder.LocalParamsKey.virtual + "_" + siteTempId;
                Map<String, TemplateConfEO> columnSourceTplMap = ThreadHolder.getContext(Map.class, key);
                if (null == columnSourceTplMap) {
                    List<TemplateConfEO> subList = CacheHandler.getList(TemplateConfEO.class, CacheGroup.CMS_PARENTID, siteTempId);
                    if (null != subList && !subList.isEmpty()) {
                        columnSourceTplMap = new HashMap<String, TemplateConfEO>();
                        for (TemplateConfEO eo : subList) {
                            columnSourceTplMap.put(eo.getTempType(), eo);
                        }
                        ThreadHolder.setContext(key, columnSourceTplMap);
                    }
                }
                // 虚拟子站没有手机版模板
                tempMap.put(Context.From.PC.toString(), columnSourceTplMap.get(scopeMap.get(scope)));
            }
        }
        return tempMap;
    }

    /**
     * 获取内容模型
     * 
     * @author fangtinghua
     * @param columnId
     * @param source
     * @return
     */
    private static String getContentModeCode(Long columnId, Long source) {
        if (MessageEnum.CONTENTINFO.value().equals(source)) {// 内容协同
            ColumnConfigEO columnConfigEO = CacheHandler.getEntity(ColumnConfigEO.class, CacheGroup.CMS_INDICATORID, columnId);
            return null == columnConfigEO ? null : columnConfigEO.getContentModelCode();
        } else if (MessageEnum.PUBLICINFO.value().equals(source)) {// 信息公开
            OrganConfigEO organConfigEO = CacheHandler.getEntity(OrganConfigEO.class, CacheGroup.CMS_ORGAN_ID, columnId);
            return null == organConfigEO ? null : organConfigEO.getContentModelCode();
        }
        return null;
    }
}