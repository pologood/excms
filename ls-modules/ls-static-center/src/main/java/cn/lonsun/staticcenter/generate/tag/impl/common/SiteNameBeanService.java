/*
 * SiteInfoBeanService.java         2015年9月22日 <br/>
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

import org.springframework.stereotype.Component;

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import cn.lonsun.util.ReflectionUtils;

import com.alibaba.fastjson.JSONObject;

/**
 * 获取站点名称 <br/>
 *
 * @date 2015年9月22日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
@Component
public class SiteNameBeanService extends AbstractBeanService {

    @Override
    public Object getObject(JSONObject paramObj) {
        Context context = ContextHolder.getContext();
        Long siteId = context.getSiteId();// 获取站点id
        // 获取站点信息
        return CacheHandler.getEntity(IndicatorEO.class, siteId);
    }

    @Override
    public String objToStr(String content, Object resultObj, JSONObject paramObj) {
        // 站点名称要特殊区分，根据name取值
        // String labelName = paramObj.getString(GenerateConstant.LABEL_NAME);
        return (String) ReflectionUtils.getValue(resultObj, "name");
    }
}