/*
 * AjaxRequestUtil.java         2016年6月22日 <br/>
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

package cn.lonsun.shiro.util;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;

/**
 * ajax请求判断 <br/>
 *
 * @date 2016年6月22日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
public class AjaxRequestUtil {

    /**
     * 判断是否ajax请求
     *
     * @author fangtinghua
     * @param request
     * @return
     */
    public static boolean isAjax(ServletRequest request) {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String requestType = httpRequest.getHeader("X-Requested-With");
        return !StringUtils.isEmpty(requestType) && "XMLHttpRequest".equals(requestType);
    }

    /**
     * 输出内容
     *
     * @author fangtinghua
     * @param response
     * @param object
     */
    public static boolean printAjax(ServletResponse response, Object object) {
        PrintWriter out = null;
        try {
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse.setHeader("Content-type", "application/json;charset=UTF-8");
            httpResponse.setCharacterEncoding("UTF-8");
            out = httpResponse.getWriter();
            out.write(JSON.toJSONString(object));
            return false;
        } catch (IOException e) {
            return false;
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    /**
     * 输出内容
     *
     * @author fangtinghua
     * @param response
     * @param string
     */
    public static boolean printString(ServletResponse response, String string) {
        PrintWriter out = null;
        try {
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse.setHeader("Content-type", "text/html;charset=UTF-8");
            httpResponse.setCharacterEncoding("UTF-8");
            out = httpResponse.getWriter();
            out.write(string);
            return false;
        } catch (IOException e) {
            return false;
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }
}