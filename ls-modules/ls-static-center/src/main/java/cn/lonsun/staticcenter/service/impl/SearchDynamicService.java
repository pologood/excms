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

import org.springframework.stereotype.Service;

import cn.lonsun.cache.client.CacheGroup;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.message.internal.entity.MessageEnum;
import cn.lonsun.site.site.internal.entity.SiteConfigEO;
import cn.lonsun.site.template.internal.entity.TemplateConfEO;
import cn.lonsun.staticcenter.generate.GenerateException;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.service.DynamicService;
import cn.lonsun.staticcenter.service.HtmlEnum;
import cn.lonsun.staticcenter.util.GenerateUtil;

/**
 * 站点 <br/>
 *
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 * @date 2016年1月13日 <br/>
 */
@Service
public class SearchDynamicService implements DynamicService {

    @Override
    public String queryHtml(String action, Long id, Context context) throws GenerateException {
        // 获取站点配置信息
        SiteConfigEO siteConfigEO = CacheHandler.getEntity(SiteConfigEO.class, CacheGroup.CMS_INDICATORID, id);
        if (null == siteConfigEO) {
            return "站点id:" + id + ",不存在.";
        }
        // 上下文数据
        if (HtmlEnum.SEARCH.getValue().equals(action)) {// 全文检索首页

            Long tplId = null;
            //Wap 站读取wap首页模板
            if (context.getFrom().equals(Context.From.PC.toString())) {
                tplId = siteConfigEO.getSearchTempId();
            } else {
                tplId = siteConfigEO.getPhoneTempId();
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