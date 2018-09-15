/*
 * AssertUtil.java         2016年7月21日 <br/>
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

import cn.lonsun.staticcenter.generate.GenerateException;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.List;

/**
 * 异常判断 <br/>
 * 
 * @date 2016年7月21日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
public class AssertUtil {

    private AssertUtil() {
    }

    /**
     * 判断空
     * 
     * @author fangtinghua
     * @param expression
     * @param message
     */
    public static void orEmpty(Object expression, Object orExpression, String message) {
        if (isEmpty(expression) || isEmpty(orExpression)) {
            throw new GenerateException(message);
        }
    }

    /**
     * 判断空
     * 
     * @author fangtinghua
     * @param expression
     * @param message
     */
    public static void andEmpty(Object expression, Object andExpression, String message) {
        if (isEmpty(expression) && isEmpty(andExpression)) {
            throw new GenerateException(message);
        }
    }

    /**
     * 判断空
     * 
     * @author fangtinghua
     * @param expression
     * @param message
     */
    public static void isEmpty(Object expression, String message) {
        if (isEmpty(expression)) {
            throw new GenerateException(message);
        }
    }
    /**
     * 判断空
     *
     * @author fangtinghua
     * @param expression
     * @param message
     */
    public static void isEmpty(Object expression, String message, Object... param) {
        if (isEmpty(expression)) {
            StringBuilder sb = new StringBuilder();
            throw new GenerateException(message, param);
        }
    }

    /**
     * 判断空
     * 
     * @author fangtinghua
     * @param expression
     * @return
     */
    private static boolean isEmpty(Object expression) {
        if (null == expression) {
            return true;
        } else if (expression.getClass().isArray()) {
            if (ArrayUtils.isEmpty((Object[]) expression)) {
                return true;
            }
        } else if (expression instanceof List) {
            if (((List<?>) expression).isEmpty()) {
                return true;
            }
        } else if (NumberUtils.isNumber(expression.toString())) {
            Long number = NumberUtils.toLong(expression.toString(), 0L);
            if (0L == number) {
                return true;
            }
        } else if (StringUtils.isBlank(expression.toString())) {
            return true;
        }
        return false;
    }

    /**
     * 判断boolean
     * 
     * @author fangtinghua
     * @param result
     * @param message
     */
    public static void isTrue(boolean result, String message) {
        if (result) {
            throw new GenerateException(message);
        }
    }
}