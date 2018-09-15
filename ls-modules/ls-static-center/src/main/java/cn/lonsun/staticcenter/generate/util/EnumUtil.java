/*
 * EnumUtil.java         2016年8月5日 <br/>
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

import org.apache.commons.lang3.StringUtils;

/**
 * 枚举工具类 <br/>
 *
 * @date 2016年8月5日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
public class EnumUtil {

    /**
     * 字符串转枚举
     *
     * @author fangtinghua
     * @param clazz
     * @param string
     * @return
     */
    public static <T extends Enum<T>> T getEnumFromString(Class<T> clazz, String string) {
        if (StringUtils.isEmpty(string)) {
            return null;
        }
        try {
            return Enum.valueOf(clazz, string.trim());
        } catch (Exception e) {
            return null;
        }
    }
}