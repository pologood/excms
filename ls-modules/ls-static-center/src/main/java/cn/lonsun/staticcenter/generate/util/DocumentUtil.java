/*
 * DocumentUtil.java         2016年3月7日 <br/>
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
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.lonsun.staticcenter.generate.GenerateException;
import cn.lonsun.staticcenter.generate.GenerateRecord;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;

/**
 * 解析参数工具类 <br/>
 *
 * @date 2016年3月7日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
public class DocumentUtil {
    // 日志
    private static final Logger logger = LoggerFactory.getLogger(DocumentUtil.class);

    /**
     * 解析参数，针对相同参数做缓存处理
     *
     * @author fangtinghua
     * @param param
     * @param labelName
     * @return
     * @throws GenerateException
     */
    public static Map<String, String> parseText(String param) throws GenerateException {
        Map<String, String> map = new HashMap<String, String>();// 参数map
        // 判断空
        if (StringUtils.isEmpty(param)) {
            return map;
        }
        GenerateRecord generateRecord = ContextHolder.getContext().getGenerateRecord();// 生成记录
        Map<String, Map<String, String>> paramMap = generateRecord.getParamMap();
        if (paramMap.containsKey(param)) {
            return paramMap.get(param);
        }
        try {
            Document document = DocumentHelper.parseText("<Regex " + param.replaceAll("&", "&amp;") + "></Regex>");
            Element element = (Element) document.selectSingleNode("Regex");
            Iterator<?> it = element.attributeIterator();
            while (it.hasNext()) {
                Attribute attr = (Attribute) it.next();
                map.put(attr.getName(), attr.getValue());
            }
            paramMap.put(param, map);
            return map;
        } catch (Throwable e) {
            logger.error("参数[" + param + "]解析错误.", e);
            throw new GenerateException("参数[" + param + "]解析错误.", e);
        }
    }
}