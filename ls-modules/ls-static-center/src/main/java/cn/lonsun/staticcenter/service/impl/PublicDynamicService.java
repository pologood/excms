/*
 * PublicDynamicService.java         2016年1月13日 <br/>
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
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.core.util.ContextHolderUtils;
import cn.lonsun.message.internal.entity.MessageEnum;
import cn.lonsun.publicInfo.internal.entity.OrganConfigEO;
import cn.lonsun.site.contentModel.internal.entity.ContentModelEO;
import cn.lonsun.site.contentModel.internal.entity.ModelTemplateEO;
import cn.lonsun.site.site.internal.entity.SiteConfigEO;
import cn.lonsun.site.template.internal.entity.TemplateConfEO;
import cn.lonsun.staticcenter.generate.GenerateException;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.util.AssertUtil;
import cn.lonsun.staticcenter.service.DynamicService;
import cn.lonsun.staticcenter.service.HtmlEnum;
import cn.lonsun.staticcenter.util.GenerateUtil;
import cn.lonsun.util.LoginPersonUtil;
import cn.lonsun.util.ModelConfigUtil;
import cn.lonsun.util.PropertiesHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 信息公开 <br/>
 *
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 * @date 2016年1月13日 <br/>
 */
@Service
public class PublicDynamicService implements DynamicService {

    private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    @Resource
    private PropertiesHelper properties;

    @Override
    public String queryHtml(String action, Long id, Context context) throws GenerateException {
        context.setSource(MessageEnum.PUBLICINFO.value());// 信息公开
        if (HtmlEnum.INDEX.getValue().equals(action)) {// 信息公开首页

            // 获取站点配置信息
            SiteConfigEO siteConfigEO = CacheHandler.getEntity(SiteConfigEO.class, CacheGroup.CMS_INDICATORID, id);
            if (null == siteConfigEO) {
                return "站点id:" + id + ",不存在.";
            }

            //Wap 站读取wap首页模板
            Long tplId = null;
            if (context.getFrom().equals(Context.From.PC.toString())) {
                tplId = siteConfigEO.getPublicTempId();
            } else {
                tplId = siteConfigEO.getWapPublicTempId();
            }

            //读取模板
            TemplateConfEO templateConfEO = CacheHandler.getEntity(TemplateConfEO.class, tplId);

            // 设置站点id
            context.setSiteId(id).setScope(MessageEnum.INDEX.value());
            context.setTemplateConfEO(templateConfEO);
            return GenerateUtil.generate(context);
        } else if (HtmlEnum.COLUMN.getValue().equals(action)) {// 信息公开栏目页

            OrganConfigEO organConfigEO = CacheHandler.getEntity(OrganConfigEO.class, CacheGroup.CMS_ORGAN_ID, id);
            if (null != organConfigEO) {
                ModelTemplateEO eo = ModelConfigUtil.getTemplateByCode(organConfigEO.getContentModelCode(), LoginPersonUtil.getSiteId());
                if (null != eo) {
                    //Wap 站读取wap首页模板
                    Long tplId = null;
                    if (context.getFrom().equals(Context.From.PC.toString())) {
                        tplId = eo.getColumnTempId();
                    } else {
                        tplId = eo.getWapColumnTempId();
                    }
                    TemplateConfEO templateConfEO = CacheHandler.getEntity(TemplateConfEO.class, tplId);

                    // 设置站点id和部门id
                    context.setSiteId(templateConfEO.getSiteId()).setColumnId(id);
                    context.setTemplateConfEO(templateConfEO);
                    context.setUserData(organConfigEO);
                    context.setScope(MessageEnum.COLUMN.value());
                    return GenerateUtil.generate(context);
                }
            }

        } else if (HtmlEnum.CONTENT.getValue().equals(action)) {// 信息公开文章页

            BaseContentEO baseContentEO = CacheHandler.getEntity(BaseContentEO.class, id);
            AssertUtil.isEmpty(baseContentEO, "文章id:" + id + ",不存在.");
            if (!accessCheck(baseContentEO)) {
                AssertUtil.isEmpty(baseContentEO, "文章id:" + id + ",未发布.");
            }
            OrganConfigEO organConfigEO = CacheHandler.getEntity(OrganConfigEO.class, CacheGroup.CMS_ORGAN_ID, baseContentEO.getColumnId());
            AssertUtil.isEmpty(organConfigEO, "单位id:" + baseContentEO.getColumnId() + ",没有单位配置信息.");
            ContentModelEO contentModel = CacheHandler.getEntity(ContentModelEO.class, CacheGroup.CMS_CODE, organConfigEO.getContentModelCode());
            AssertUtil.isEmpty(contentModel, "单位id:" + baseContentEO.getColumnId() + ",沒有模板配置信息.");
            List<ModelTemplateEO> modeList = CacheHandler.getList(ModelTemplateEO.class, CacheGroup.CMS_MODEL_ID, contentModel.getId());
            AssertUtil.isEmpty(modeList, "单位id:" + baseContentEO.getColumnId() + ",沒有模板配置信息.");
            
            // 模板id
            Long tplId = null;
            //Wap 站读取wap首页模板
            if (context.getFrom().equals(Context.From.PC.toString())) {
                tplId = modeList.get(0).getArticalTempId();
            } else {
                tplId = modeList.get(0).getWapArticalTempId();
            }
            // 文章类型
            String typeCode = baseContentEO.getTypeCode();
            /*// 文章模板根据类型来判断
            for (ModelTemplateEO tplEO : modeList) {
                if (typeCode.equals(tplEO.getModelTypeCode())) {
                    tplId = tplEO.getArticalTempId();
                    break;
                }
            }*/
            AssertUtil.isEmpty(tplId, "文章id:" + id + ",沒有配置文章页模板.");
            // 模板数据
            TemplateConfEO templateConfEO = CacheHandler.getEntity(TemplateConfEO.class, tplId);
            AssertUtil.isEmpty(tplId, "文章id:" + id + ",沒有配置文章页模板.");
            // 设置站点id
            context.setSiteId(baseContentEO.getSiteId()).setColumnId(baseContentEO.getColumnId()).setContentId(id);
            context.setTemplateConfEO(templateConfEO).setTypeCode(typeCode);
            context.setScope(MessageEnum.CONTENT.value());
            return GenerateUtil.generate(context);
        }
        return "";
    }

    /**
     * 判断是否让访问动态地址
     * @param content
     * @return
     */
    private boolean accessCheck(BaseContentEO content) {
        if(content.getIsPublish() == 1) {
            return true;
        }
        HttpServletRequest request = ContextHolderUtils.getRequest();
        String url = request.getHeader("Referer");
        logger.info("当前请求来源:{}",url);
        if(null == url) {
            return false;
        }
        String sysDomain = properties.getSysDomain();
        logger.info("允许的域名地址:{}",sysDomain);
        if(null == sysDomain) {
            logger.error("请在配置文件中配置sysDomain属性");
            return true;
        }
        if(!url.contains(sysDomain)) {
            logger.error("当前信息[{}]未发布，前台[{}]禁止访问动态地址",content.getTitle(),url);
            return false;
        }
        return true;
    }
}