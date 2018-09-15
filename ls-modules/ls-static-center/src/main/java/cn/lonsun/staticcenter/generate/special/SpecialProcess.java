/*
 * SpecialProcess.java         2015年9月11日 <br/>
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

package cn.lonsun.staticcenter.generate.special;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.staticcenter.generate.GenerateException;

/**
 * 对属性字段值进行特殊处理类 <br/>
 * 
 * @date 2015年9月11日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
public class SpecialProcess {
    // 日志
    private static final Logger LOGGER = LoggerFactory.getLogger(SpecialProcess.class);
    // 特殊处理字段
    private static final List<String> SPECIAL = Arrays.asList(new String[] { "link" });
    // 处理veloctiy处理不了的字符
    private static final Map<String, String> SPECIAL_MAP = new HashMap<String, String>();

    /**
     * 初始化特殊字符替换
     */
    static {
        SPECIAL_MAP.put("#", "HASH-" + UUID.randomUUID().toString());// #替换
        SPECIAL_MAP.put("\\$", "DOLLARS-" + UUID.randomUUID().toString());// $替换
        SPECIAL_MAP.put("\"", "DOUBLE-" + UUID.randomUUID().toString());// "替换
        SPECIAL_MAP.put("'", "SINGLE-" + UUID.randomUUID().toString());// '替换
    }

    /**
     * 获取字段值
     * 
     * @author fangtinghua
     * @param key
     * @param obj
     * @return
     * @throws GenerateException
     */
    public static String getBeanValue(String key, Object obj) throws GenerateException {
        if (StringUtils.isEmpty(key)) {
            return StringUtils.EMPTY;
        }
        String value = StringUtils.EMPTY;// 值
        try {
            if (SPECIAL.contains(key)) {
                Process process = SpringContextHolder.getBean(key + "Process");
                value = process.getValue(obj);
            } else {
                value = BeanUtils.getProperty(obj, key);
            }
            // 替换影响veloctiy解析的字符串#$"'等
            if (StringUtils.isNotEmpty(value)) {
                for (Entry<String, String> entry : SPECIAL_MAP.entrySet()) {
                    value = value.replaceAll(entry.getKey(), entry.getValue());
                }
            }
        } catch (Throwable e) {
            LOGGER.error("获取字段[" + key + "]值错误.", e);
            throw new GenerateException("获取字段[" + key + "]值错误.", e);
        }
        return StringUtils.defaultString(value);
    }

    /**
     * 恢复内容
     * 
     * @author fangtinghua
     * @param content
     * @return
     */
    public static String recoverContent(String content) {
        if (StringUtils.isEmpty(content)) {
            return StringUtils.EMPTY;
        }
        for (Entry<String, String> entry : SPECIAL_MAP.entrySet()) {
            content = content.replaceAll(entry.getValue(), entry.getKey());
        }
        return content;
    }
}