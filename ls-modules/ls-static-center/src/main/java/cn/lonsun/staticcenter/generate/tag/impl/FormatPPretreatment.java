/*
 * FormatPPretreatment.java         2015年8月18日 <br/>
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

package cn.lonsun.staticcenter.generate.tag.impl;

import java.util.Date;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import cn.lonsun.staticcenter.generate.GenerateException;
import cn.lonsun.staticcenter.generate.tag.PPretreatment;

/**
 * 日期格式化 <br/>
 *
 * @date 2015年8月18日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
@Component
public class FormatPPretreatment implements PPretreatment {

    private static final Logger logger = LoggerFactory.getLogger(FormatPPretreatment.class);

    private String year = "yyyy-MM-dd HH:mm:ss.S";
    private String e = "EEE MMM dd HH:mm:ss zzz yyyy";

    @Override
    public String getValue(String value, String param) throws GenerateException {
        if (StringUtils.isEmpty(value) || StringUtils.isEmpty(param)) {
            return "";
        }
        try {
            Date date = DateUtils.parseDate(value, Locale.US, year, e);
            return null == date ? "" : DateFormatUtils.format(date, param);
        } catch (Throwable e) {
            logger.error("日期格式化错误！", e);
            throw new GenerateException("日期格式化错误！", e);
        }
    }
}