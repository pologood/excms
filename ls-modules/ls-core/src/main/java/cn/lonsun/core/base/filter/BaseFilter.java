package cn.lonsun.core.base.filter;

import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.exception.handler.IExceptionHandler;
import cn.lonsun.core.exception.util.ExceptionTipsMessage;
import cn.lonsun.core.exception.util.Jacksons;
import cn.lonsun.core.util.*;

import com.fasterxml.jackson.databind.util.JSONPObject;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * 初始化本地线程变量
 * 
 * @author 徐建华
 * 
 */
public class BaseFilter implements Filter {

    private Logger logger = LoggerFactory.getLogger(BaseFilter.class);

    private IExceptionHandler ormExceptionHandler;

    /**
     * 向MDC中设置的key，用于日志记录 MDC（Mapped Diagnostic Context，映射调试上下文）是 log4j
     * 提供的一种方便在多线程条件下记录日志的功能
     * 
     * @author 徐建华
     * 
     */
    private enum MDCItems {
        /** 当前操作用户的用户ID */
        UserID,
        /** 当前操作用户的用户名 */
        UserName,
        /** 客户端的IP地址 */
        IP
    }

    @Override
    public void doFilter(ServletRequest arg0, ServletResponse arg1, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) arg0;
        HttpServletResponse response = (HttpServletResponse) arg1;
        String accessKey = null;
        try {
            // Long userId =
            // SessionUtil.getLongProperty(request.getSession(),"userId");
            // if (userId == null) {
            // String requestType = request.getHeader("X-Requested-With");
            // if (StringUtils.isEmpty(requestType)) {
            // // ajax不做处理
            // response.sendRedirect("/login");
            // }
            // }
            // 1.初始化日志参数
            initMDC(request);

            // 2.过滤资源访问请求
            // if (isRepeatedAccess(request)) {
            // return;
            // }
            // 3.初始化线程参数
            initThreadLocal(request);
            // 4.用自定义的Request对象，该对象复写了获取入参的两个方法，对参数中的特殊字符串进行了处理
            // HttpServletRequest requestWrapper = new
            // LonsunRequestWrapper(request);
            chain.doFilter(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            String requestType = request.getHeader("X-Requested-With");
            if (!StringUtils.isEmpty(requestType) && requestType.equals("XMLHttpRequest")) {
                // 异常处理
                processException(request, response, e);
            } else {
                /*
                 * Throwable throwable = e.getCause(); if( null != throwable &&
                 * throwable instanceof
                 * BaseRunTimeException){//如果是系统自定义异常则跳转至指定的错误页面
                 * request.setAttribute("errorMsg",((BaseRunTimeException)
                 * throwable).getTipsMessage());
                 * request.getRequestDispatcher("/app/error.jsp"
                 * ).forward(request,response); }else{
                 */
                response.sendError(500);// 响应给客户端500错误(服务器内部错误)
                /* } */
            }
        } finally {
            // 请求结束时移除用户对某资源发起请求的标记,对应——3.过滤资源访问请求
            if (accessKey != null) {
                request.getSession().removeAttribute(accessKey);
            }
        }
    }

    /**
     * 日志信息初始化
     *
     * @param request
     */
    private void initMDC(HttpServletRequest request) {
        Long userId = SessionUtil.getLongProperty(request.getSession(), "userId");
        MDC.put(MDCItems.UserID.toString(), userId == null ? "null" : userId.toString());
        String userName = SessionUtil.getStringProperty(request.getSession(), "uid");
        MDC.put(MDCItems.UserName.toString(), (userName == null || "".equals(userName.trim())) ? "null" : userName);
        String ip = RequestUtil.getIpAddr(request);
        MDC.put(MDCItems.IP.toString(), ip);
    }

    /**
     * 过滤器的异常处理
     *
     * @param request
     * @param response
     * @param e
     */
    private void processException(HttpServletRequest request, HttpServletResponse response, Exception e) {
        String callback = request.getParameter("callback");
        String json = getExceptionJsonString(e, callback);
        logger.error("异常描述信息>>>>>" + json);
        PrintWriter writer = null;
        try {
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);
            response.setContentType("application/json;charset=UTF-8");
            response.setCharacterEncoding("utf-8");
            writer = response.getWriter();
            writer.print(json);
        } catch (Exception e2) {
        } finally {
            if (writer != null)
                writer.close();
        }
    }

    @Override
    public void destroy() {
    }

    /**
     * 获取异常返回对象json串
     *
     * @param e
     * @param callback
     * @return
     */
    private String getExceptionJsonString(Exception e, String callback) {
        Throwable throwable = e.getCause();
        BaseRunTimeException exception = getOrmExceptionHandler().process(throwable);
        // 获取提示信息
        String description = null;
        if (!StringUtils.isEmpty(exception.getTipsMessage())) {
            description = exception.getTipsMessage();
        } else {
            description = ExceptionTipsMessage.getInstance().get(exception);
        }
        ResultVO ro = new ResultVO();
        ro.setStatus(ResultVO.Status.Failure.getValue());
        ro.setDesc(description);
        // ro.setData(data);
        // 返回json或jsonp串
        String json = null;
        if (callback != null && !"".equals(callback)) {
            json = Jacksons.json().fromObjectToJson(new JSONPObject(callback, ro));
        } else {
            json = Jacksons.json().fromObjectToJson(ro);
        }
        return json;
    }

    /**
     * 初始化线程线程本地变量
     *
     */
    private void initThreadLocal(HttpServletRequest request) {
        HttpSession session = request.getSession(true);
        Map<String, Object> map = new HashMap<String, Object>();
        Long userId = SessionUtil.getLongProperty(session, "userId");
        map.put(ThreadUtil.LocalParamsKey.UserId.toString(), userId);
        String uid = SessionUtil.getStringProperty(session, "uid");
        map.put(ThreadUtil.LocalParamsKey.Uid.toString(), uid);
        String personName = SessionUtil.getStringProperty(session, "personName");
        map.put(ThreadUtil.LocalParamsKey.PersonName.toString(), personName);
        Long organId = SessionUtil.getLongProperty(session, "organId");
        map.put(ThreadUtil.LocalParamsKey.OrganId.toString(), organId);
        String organName = SessionUtil.getStringProperty(session, "organName");
        map.put(ThreadUtil.LocalParamsKey.OrganName.toString(), organName);
        String callback = RequestUtil.getStringParameter(request, "callback");
        map.put(ThreadUtil.LocalParamsKey.Callback.toString(), callback);
        Integer dataFlag = RequestUtil.getIntegerParameter(request, "dataFlag");
        map.put(ThreadUtil.LocalParamsKey.DataFlag.toString(), dataFlag);
        String ip = IpUtil.getIpAddr(request);
        map.put(ThreadUtil.LocalParamsKey.IP.toString(), ip);

        // 放入菜单id
        Long currentMenuId = SessionUtil.getLongProperty(session, "menuId");
        Object menuId = request.getParameter("menuId");
        if (null != menuId && !menuId.equals(currentMenuId)) {
            session.setAttribute("menuId", menuId);
        }
        ThreadUtil.set(map);
    }

    /**
     * 对资源的重复请求进行拦截 </br> 当一个用户对某个资源发起请求时，如果用户再次对该资源进行访问的请求将被忽略，
     * 需要等用户释放当前资源后才可以进行下一次访问，这样可以避免用户对同一个资源的重复请求;</br>
     * 
     * @return
     */
    // private boolean isRepeatedAccess(HttpServletRequest request) {
    // boolean isRepeated = false;
    // String accessKey = RequestUtil.getAccessKey(request);
    // if (accessKey != null) {
    // Object token = request.getSession().getAttribute(accessKey); // 用户不在请求中
    // if (token == null) { // 初始化用户对某资源发起请求的标记
    // request.getSession(true).setAttribute(accessKey,
    // new Date().getTime());
    // } else {
    // isRepeated = true;
    // }
    // if (isRepeated) {
    // // 判断请求时间是否超过1秒，超过1秒允许发起请求:只有在异常框架出现问题或者请求的执行时间超过5s才被执行 long
    // Long times = Long.valueOf(request.getSession()
    // .getAttribute(accessKey).toString());
    // Long.valueOf(token.toString());
    // int seconds = (int) (new Date().getTime() - times);
    // // 500毫秒内不允许重复请求，否则直接返回
    // if (seconds < 500) {
    // isRepeated = true;
    // } else {
    // isRepeated = false;
    // }
    // }
    // }
    // if (isRepeated) {
    // logger.info("The resource \"".concat(request.getRequestURI())
    // .concat("\" can not be accessed."));
    // }
    // return isRepeated;
    // }

    @Override
    public void init(FilterConfig arg0) throws ServletException {
        ServletContext context = arg0.getServletContext();
        ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(context);
        ormExceptionHandler = (IExceptionHandler) ctx.getBean("ormExceptionHandler");
    }

    public IExceptionHandler getOrmExceptionHandler() {
        return ormExceptionHandler;
    }

}
