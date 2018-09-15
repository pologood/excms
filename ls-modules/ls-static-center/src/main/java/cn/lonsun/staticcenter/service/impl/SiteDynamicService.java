/*
 * SiteDynamicService.java         2016年1月13日 <br/>
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

package cn.lonsun.staticcenter.service.impl;

import cn.lonsun.cache.client.CacheGroup;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.util.XSSFilterUtil;
import cn.lonsun.message.internal.entity.MessageEnum;
import cn.lonsun.site.label.internal.entity.LabelEO;
import cn.lonsun.site.site.internal.entity.SiteConfigEO;
import cn.lonsun.site.template.internal.entity.TemplateConfEO;
import cn.lonsun.staticcenter.generate.GenerateException;
import cn.lonsun.staticcenter.generate.GenerateRecord;
import cn.lonsun.staticcenter.generate.tag.BeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.util.ContextUtil;
import cn.lonsun.staticcenter.generate.util.LabelUtil;
import cn.lonsun.staticcenter.generate.util.MapUtil;
import cn.lonsun.staticcenter.generate.util.RegexUtil;
import cn.lonsun.staticcenter.service.DynamicService;
import cn.lonsun.staticcenter.service.HtmlEnum;
import cn.lonsun.staticcenter.util.GenerateUtil;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 站点 <br/>
 *
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 * @date 2016年1月13日 <br/>
 */
@Service
public class SiteDynamicService implements DynamicService {

    @Override
    public Object queryHtml(String action, Long id, Context context) throws GenerateException {

        Map<String, String> paramMap = context.getParamMap();

        if (HtmlEnum.TPL.getValue().equals(action)) {// 根据模板直接访问
            TemplateConfEO templateConfEO = CacheHandler.getEntity(TemplateConfEO.class, id);
            if (null == templateConfEO) {
                return "模板ID(" + id + ")：ID不存在或已被删除.";
            }
            return GenerateUtil.generate(context.setSiteId(templateConfEO.getSiteId()).setTemplateConfEO(templateConfEO));
        } else if (HtmlEnum.LABEL.getValue().equals(action)) {// 根据标签直接解析
            if (Context.From.WAP.toString().equals(context.getFrom())) {
                //context.setModule("public");
            }
            String labelName = paramMap.get("labelName");
            BeanService service = null;
            LabelEO labelEO = null;// 标签
            if (StringUtils.isEmpty(labelName)) {
                labelEO = CacheHandler.getEntity(LabelEO.class, id);
                if (null == labelEO) {
                    return "标签ID(" + id + ")：不存在或已被删除.";
                }
                service = LabelUtil.getBeanServiceByLabel(labelEO.getLabelName());
            } else {
                service = LabelUtil.getBeanServiceByLabel(labelName);
                labelEO = CacheHandler.getEntity(LabelEO.class, CacheGroup.CMS_NAME, labelName);
                if (null == labelEO) {
                    labelName = XSSFilterUtil.filterSqlInject(labelName);//sql注入过滤
                    labelName = XSSFilterUtil.stripXSS(labelName);//特殊字符过滤
                    return "标签名称(" + labelName + ")：不存在或已被删除.";
                }
            }
            labelName = labelEO.getLabelName();
            // 设置生成全局变量
            context.setGenerateRecord(new GenerateRecord());
            // 设置站点信息
            ContextUtil.setContextUri();
            // 获取beanservice
//            BeanService service = LabelUtil.getBeanServiceByLabel(labelName);
            String html = paramMap.remove("html");// 标签中间名称
            boolean isJson = BooleanUtils.toBoolean(paramMap.get("isJson"));
            JSONObject unionObj = MapUtil.unionMapToLabel(labelEO, paramMap);// 参数合并
            if (isJson) {
                return RegexUtil.parseLabelObject(service, labelName, unionObj);
            }
            String result = RegexUtil.parseLabelHtml(service, labelName, html, unionObj);
            result = StringUtils.isEmpty(result) ? unionObj.getString("result") : result;
            return StringUtils.isEmpty(result) ? "正在更新中..." : result;
        } else if (HtmlEnum.INDEX.getValue().equals(action)) {// 站点首页

            // 获取站点配置信息
            SiteConfigEO siteConfigEO = CacheHandler.getEntity(SiteConfigEO.class, CacheGroup.CMS_INDICATORID, id);
            if (null == siteConfigEO) {
                return "站点ID(" + id + ")：ID不存在或已被删除.";
            }

            Long tplId = null;
            // Wap 站读取wap首页模板
            if (context.getFrom().equals(Context.From.PC.toString())) {
                tplId = siteConfigEO.getIndexTempId();
            } else {
                tplId = siteConfigEO.getWapTempId();
            }

            TemplateConfEO templateConfEO = CacheHandler.getEntity(TemplateConfEO.class, tplId);

            // 设置站点id
            context.setSiteId(id).setScope(MessageEnum.INDEX.value());
            context.setTemplateConfEO(templateConfEO);
            return GenerateUtil.generate(context);
        } else if (HtmlEnum.SEARCH.getValue().equals(action)) {// 全站搜索

            String searchType = paramMap.get("searchType");
            String searchTplId = paramMap.get("searchTplId");

            if(!StringUtils.isEmpty(searchType)) {
                searchType = XSSFilterUtil.stripXSS(searchType);
            }
            if(!StringUtils.isEmpty(searchTplId)) {
                searchTplId = XSSFilterUtil.stripXSS(searchTplId);
            }

            Long tplId = null;
            //专题搜索
            if ("1".equals(searchType) && AppUtil.isNumeric(searchTplId)) {
                tplId = Long.valueOf(searchTplId);
            } else {
                // 获取站点配置信息
                SiteConfigEO siteConfigEO = CacheHandler.getEntity(SiteConfigEO.class, CacheGroup.CMS_INDICATORID, id);
                if (null == siteConfigEO) {
                    return "站点ID(" + id + ")：ID不存在或已被删除.";
                }

                // Wap 站读取wap首页模板
                if (context.getFrom().equals(Context.From.PC.toString())) {
                    tplId = siteConfigEO.getSearchTempId();
                } else {
                    tplId = siteConfigEO.getPhoneTempId();
                }
            }

            TemplateConfEO templateConfEO = CacheHandler.getEntity(TemplateConfEO.class, tplId);
            // 设置站点id
            context.setSiteId(id).setScope(MessageEnum.INDEX.value());
            context.setTemplateConfEO(templateConfEO);
            return GenerateUtil.generate(context);
        }
        return null;
    }
}