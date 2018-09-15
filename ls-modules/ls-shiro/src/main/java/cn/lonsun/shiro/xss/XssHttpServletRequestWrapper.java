/*
 * XssHttpServletRequestWrapper.java         2016年8月9日 <br/>
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

package cn.lonsun.shiro.xss;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.lang3.StringUtils;

/**
 * xss请求过滤 <br/>
 *
 * @date 2016年8月9日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
public class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {

    public XssHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    @Override
    public String getHeader(String name) {
        return cleanXssValue(super.getHeader(name));
    }

    @Override
    public String getParameter(String name) {
        return cleanXssValue(super.getParameter(name));
    }

    @Override
    public String[] getParameterValues(String name) {
        String[] params = super.getParameterValues(name);
        if (null == params) {
            return null;
        }
        int length = params.length;
        String[] process = new String[length];
        for (int i = 0; i < length; i++) {
            process[i] = cleanXssValue(params[i]);
        }
        return process;
    }

    private String cleanXssValue(String value) {
        if (StringUtils.isEmpty(value)) {
            return value;
        }
        value = value.replaceAll("<", "＜").replaceAll(">", "＞");
        value = value.replaceAll("'", "‘").replaceAll("\"", "”");
        value = value.replaceAll("&", "＆").replaceAll("\\\\", "＼");
        value = value.replaceAll("#", "＃");
        return value;
    }
}