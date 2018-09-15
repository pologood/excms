/*
 * UrlAutherticationFilter.java         2016年7月27日 <br/>
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

package cn.lonsun.shiro.filter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * url权限控制 <br/>
 *
 * @date 2016年7月27日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
public interface UrlAutherticationFilter {

    /**
     * 判断url权限
     *
     * @author fangtinghua
     * @param request
     * @param response
     * @return
     */
    boolean isUrlAllowed(ServletRequest request, ServletResponse response);
}