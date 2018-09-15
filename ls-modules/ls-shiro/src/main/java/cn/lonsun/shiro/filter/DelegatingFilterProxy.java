/*
 * DelegatingFilterProxy.java         2016年6月30日 <br/>
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

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import cn.lonsun.shiro.util.LegalAccessUrlUtil;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.util.WebUtils;

import cn.lonsun.security.web.filters.XSSWrapperRequest;

/**
 * 重写代理拦截器，预先处理session上传问题 <br/>
 * 
 * @date 2016年6月30日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
public class DelegatingFilterProxy extends org.springframework.web.filter.DelegatingFilterProxy {

    public static String LEGAL_ACCESS_RESOURCES_PATH = "/WEB-INF/classes/legalAccessUrls.properties";
    // 日志
    private static final Logger logger = LoggerFactory.getLogger(DelegatingFilterProxy.class);
    private static final String BEAN_NAME = DispatcherServlet.MULTIPART_RESOLVER_BEAN_NAME;
    private static final String FILTER_XSS = "filterXss";
    // private static final String FILTER_SQL_INJECTION = "filterSqlInjection";
    // 是否上传请求标记
    public static final String REQUEST_ATTRIBUTE_ISMULTIPART = DelegatingFilterProxy.class.getName() + ".isMultipart";
    // 针对文件上传
    private MultipartResolver multipartResolver = null;
    private boolean filterXss = false;// 针对XSS攻击

    @Override
    protected Filter initDelegate(WebApplicationContext wac) throws ServletException {
        filterXss = BooleanUtils.toBoolean(this.getFilterConfig().getInitParameter(FILTER_XSS));
        multipartResolver = wac.getBean(BEAN_NAME, MultipartResolver.class);
        return super.initDelegate(wac);
    }

    @Override
    protected void invokeDelegate(Filter delegate, ServletRequest request, ServletResponse response, FilterChain filterChain) throws ServletException,
            IOException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        // HttpServletResponse httpResponse = (HttpServletResponse) response;
        if (filterXss) {// 包装
            // httpRequest = new XssHttpServletRequestWrapper(httpRequest);
            if (!LegalAccessUrlUtil.containsValue(httpRequest.getRequestURI())) {
                httpRequest = new XSSWrapperRequest(httpRequest);// antisamy
            }
        }
        if (null != multipartResolver && multipartResolver.isMultipart(httpRequest)) {// 是上传请求
            request.setAttribute(REQUEST_ATTRIBUTE_ISMULTIPART, true);// 标记上传请求
            if (WebUtils.getNativeRequest(httpRequest, MultipartHttpServletRequest.class) != null) {
                logger.debug("Request is already a MultipartHttpServletRequest - if not in a forward, "
                        + "this typically results from an additional MultipartFilter in web.xml");
            } else if (httpRequest.getAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE) instanceof MultipartException) {
                logger.debug("Multipart resolution failed for current request before - " + "skipping re-resolution for undisturbed error rendering");
            } else {
                httpRequest = multipartResolver.resolveMultipart(httpRequest);
            }
        }
        super.invokeDelegate(delegate, httpRequest, response, filterChain);
    }
}