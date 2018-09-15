/*
 * ShiroExceptionUtil.java         2016年7月29日 <br/>
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

import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.exception.handler.IExceptionHandler;
import cn.lonsun.core.exception.util.ExceptionTipsMessage;
import cn.lonsun.core.util.ResultVO;
import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.rbac.login.InternalAccount;
import com.alibaba.fastjson.JSONPObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 异常处理工具类 <br/>
 *
 * @date 2016年7月29日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
public class ShiroExceptionUtil {
    // 日志
    private static final Logger logger = LoggerFactory.getLogger(ShiroExceptionUtil.class);
    // 异常处理handler
    private static IExceptionHandler ormExceptionHandler = SpringContextHolder.getApplicationContext().getBean("ormExceptionHandler", IExceptionHandler.class);

    /**
     * 异常处理
     *
     * @author fangtinghua
     * @param request
     * @param response
     * @param ex
     */
    public static void processException(HttpServletRequest request, HttpServletResponse response, Exception ex) {
        if (null == ex) {// 判断异常
            return;
        }
        if (!AjaxRequestUtil.isAjax(request)) {
            try {
                if (ex instanceof AuthorizationException) {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED);// 没有权限
                } else {
                    logger.error("服务器异常！", ex); // 打印异常
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);// 服务器内部错误
                }
            } catch (IOException e) {
                ex = e;
                logger.error("服务器异常！", e);
            }
        } else {// 判断ajax请求
            String description = null;// 异常提示
            String dataType = WebUtils.getCleanParam(request, InternalAccount.USER_DATATYPE);// 请求类型
            String callback = WebUtils.getCleanParam(request, InternalAccount.USER_CALLBACK);// 回调函数
            if (ex instanceof AuthorizationException) {// 没有权限
                description = "没有操作权限！";
            } else {
                logger.error("服务器异常！", ex); // 打印异常
                BaseRunTimeException exception = ormExceptionHandler.process(ex);
                if (!StringUtils.isEmpty(exception.getTipsMessage())) {
                    description = exception.getTipsMessage();
                } else {
                    description = ExceptionTipsMessage.getInstance().get(exception);
                }
            }
            if ("html".equalsIgnoreCase(dataType)) {// 请求的是html
                String script = "<script>Ls.tipsErr('" + description + "')</script>";
                AjaxRequestUtil.printString(response, script);
            } else {
                ResultVO vo = new ResultVO();
                vo.setStatus(ResultVO.Status.Failure.getValue());
                vo.setDesc(description);
                if (StringUtils.isEmpty(callback)) {
                    AjaxRequestUtil.printAjax(response, vo);
                } else {
                    JSONPObject function = new JSONPObject(callback);
                    function.addParameter(vo);
                    AjaxRequestUtil.printAjax(response, function);
                }
            }
        }
    }
}