/*
 * LengthPPretreatment.java         2015年8月18日 <br/>
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

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import cn.lonsun.staticcenter.generate.GenerateException;
import cn.lonsun.staticcenter.generate.tag.PPretreatment;

/**
 * 长度截取 <br/>
 *
 * @date 2015年8月18日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
@Component
public class LengthPPretreatment implements PPretreatment {

    private static final Logger logger = LoggerFactory.getLogger(LengthPPretreatment.class);

    @Override
    public String getValue(String value, String param) throws GenerateException {
        if (StringUtils.isEmpty(value) || StringUtils.isEmpty(param)) {
            return value;
        }
        try {
            int len = Integer.valueOf(param);
            return len > value.length() ? value : value.substring(0, len);
        } catch (Throwable e) {
            logger.error("字符串长度截取传参错误！", e);
            throw new GenerateException("字符串长度截取传参错误！", e);
        }
    }
}