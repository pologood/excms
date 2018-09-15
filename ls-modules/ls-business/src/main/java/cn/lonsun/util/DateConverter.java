/*
 * DateConverter.java         2016年8月24日 <br/>
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

import java.util.Date;

import org.apache.commons.beanutils.converters.DateTimeConverter;
import org.apache.commons.lang3.StringUtils;

/**
 * 时间转换 <br/>
 *
 * @date 2016年8月24日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
public class DateConverter extends DateTimeConverter {

    public DateConverter() {
    }

    public DateConverter(Object defaultValue) {
        super(defaultValue);
    }

    @Override
    protected Class<?> getDefaultType() {
        return Date.class;
    }

    @Override
    @SuppressWarnings("rawtypes")
    public Object convert(Class type, Object value) {
        if (value == null) {
            return null;
        }
        String v = value.toString();
        return StringUtils.isEmpty(v) ? null : super.convert(type, value);
    }
}