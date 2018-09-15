/*
 * WebServiceUtil.java         2016年1月13日 <br/>
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

package cn.lonsun.util;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import cn.lonsun.base.util.HttpClientUtils;
import cn.lonsun.cache.client.CacheGroup;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.webservice.internal.entity.WebServiceEO;

import com.alibaba.fastjson.JSON;

/**
 * http调用接口 <br/>
 *
 * @date 2016年1月13日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
public class HttpServiceUtil {

    /**
     * get方法调用
     *
     * @author fangtinghua
     * @param clazz
     * @param code
     * @param paramMap
     * @return
     */
    public static <T> T call(Class<T> clazz, String code, Map<String, String> paramMap) {
        WebServiceEO webServiceEO = CacheHandler.getEntity(WebServiceEO.class, CacheGroup.CMS_CODE, code);
        if (webServiceEO == null) {
            return null;
        }
        String url = webServiceEO.getUri();
        String path = webServiceEO.getMethod();
        String json = HttpClientUtils.get(url + path, paramMap);
        return StringUtils.isEmpty(json) ? null : JSON.parseObject(json, clazz);
    }
}