package cn.lonsun.rbac.filter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.exception.handler.IExceptionHandler;
import cn.lonsun.core.exception.util.ExceptionTipsMessage;
import cn.lonsun.core.exception.util.Jacksons;
import cn.lonsun.core.util.IpUtil;
import cn.lonsun.core.util.RequestUtil;
import cn.lonsun.core.util.ResultVO;
import cn.lonsun.core.util.SessionUtil;
import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.core.util.ThreadUtil;
import cn.lonsun.ldap.internal.entity.ConfigEO;
import cn.lonsun.ldap.internal.exception.CommunicationRuntimeException;
import cn.lonsun.ldap.internal.service.IConfigService;
import cn.lonsun.ldap.internal.util.LDAPUtil;

import com.fasterxml.jackson.databind.util.JSONPObject;

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
	public void doFilter(ServletRequest arg0, ServletResponse arg1,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) arg0;
		HttpServletResponse response = (HttpServletResponse) arg1;
		String accessKey = null;
		int times = 1;
		try {
			//初始化日志参数
			initMDC(request);
			initThreadLocal(request);
			//对Request的重写，对参数中的尖括号"<"和">"进行转义处理
			HttpServletRequest requestWrapper = new LonsunRequestWrapper(request);
			chain.doFilter(requestWrapper, response);
		} catch (Exception e) {
			Throwable t = e.getCause();
			if(t instanceof CommunicationRuntimeException){
				doFilter(arg0, arg1,chain,times);
			}else{
				e.printStackTrace();
				String requestType = request.getHeader("X-Requested-With");
				if (!StringUtils.isEmpty(requestType)
						&& requestType.equals("XMLHttpRequest")) {
					// 异常处理
					processException(request, response, e);
				} else {
					response.sendError(400);
				}
			}
		} finally {
			// 请求结束时移除用户对某资源发起请求的标记,对应——3.过滤资源访问请求
			if (accessKey != null) {
				request.getSession(false).removeAttribute(accessKey);
			}
		}
	}

	/**
	 * 过滤器的异常处理
	 * 
	 * @param request
	 * @param response
	 * @param e
	 */
	private void processException(HttpServletRequest request,
			HttpServletResponse response, Exception e) {
		String callback = request.getParameter("callback");
		String json = getExceptionJsonString(e, callback);
		logger.error("异常描述信息>>>>>"+json);
		PrintWriter writer = null;
		try {
			response.reset();
			response.setContentType("text/xml;charset=UTF-8"); 
			response.setHeader("Pragma", "no-cache");
			response.setHeader("Cache-Control", "no-cache");
			response.setDateHeader("Expires", 0);
			writer = response.getWriter();
			writer.print(json);
		} catch (Exception e2) {
		} finally {
			if (writer != null)
				writer.close();
		}
	}

	/**
	 * 日志信息初始化
	 * 
	 * @param request
	 */
	private void initMDC(HttpServletRequest request) {
		Long userId = SessionUtil.getLongProperty(request.getSession(false),"userId");
		MDC.put(MDCItems.UserID.toString(),userId == null ? "null" : userId.toString());
		String userName = SessionUtil.getStringProperty(request.getSession(false),"userName");
		MDC.put(MDCItems.UserName.toString(), (userName == null || "".equals(userName.trim())) ? "null" : userName);
		String ip = RequestUtil.getIpAddr(request);
		MDC.put(MDCItems.IP.toString(), ip);
	}

	@Override
	public void destroy() {
	}

	/**
	 * 初始化线程线程本地变量
	 * 
	 * @param session
	 */
	private void initThreadLocal(HttpServletRequest request) {
		HttpSession session = request.getSession(true);
		Map<String, Object> map = new HashMap<String, Object>();
		Long userId = SessionUtil.getLongProperty(session, "userId");
		map.put(ThreadUtil.LocalParamsKey.UserId.toString(), userId);
		String uid = SessionUtil.getStringProperty(session, "uid");
		map.put(ThreadUtil.LocalParamsKey.Uid.toString(), uid);
		String personName = SessionUtil
				.getStringProperty(session, "personName");
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
		ThreadUtil.set(map);
	}

	/**
	 * 获取异常返回对象json串
	 * 
	 * @param e
	 * @param callback
	 * @return
	 */
	private String getExceptionJsonString(Exception e, String callback) {
		BaseRunTimeException exception = null;
		if (!(e instanceof BaseRunTimeException)) {
			Throwable throwable = e.getCause();
			if(!(throwable instanceof BaseRunTimeException)){
				exception = getOrmExceptionHandler().process(throwable);
			}else{
				exception = (BaseRunTimeException)throwable;
			}
		}else{
			exception = (BaseRunTimeException)e;
		}
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
		// 返回json或jsonp串
		String json = null;
		if (callback != null && !"".equals(callback)) {
			json = Jacksons.json().fromObjectToJson(new JSONPObject(callback, ro));
		} else {
			json = Jacksons.json().fromObjectToJson(ro);
		}
		return json;
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		ServletContext context = arg0.getServletContext();
		ApplicationContext ctx = WebApplicationContextUtils
				.getWebApplicationContext(context);
		ormExceptionHandler = (IExceptionHandler) ctx
				.getBean("ormExceptionHandler");
	}

	public IExceptionHandler getOrmExceptionHandler() {
		return ormExceptionHandler;
	}
	
	public void doFilter(ServletRequest arg0, ServletResponse arg1,FilterChain chain,int times){
		//尝试第times次请求
		times++;
		try {
			logger.info("LDAP连接失败，因此进行第"+times+"次请求");
			chain.doFilter(arg0, arg1);
		} catch (Exception e) {
			//LDAP连接异常，重新初始化LDAP的Conig
			Throwable t = e.getCause();
			if(t instanceof CommunicationRuntimeException){
				logger.error("LDAP连接失败，尝试切换LDAP");
				IConfigService configService = SpringContextHolder.getBean("configService");
				ConfigEO config = configService.getEffectiveConfig();
				if(config!=null){
					logger.info("切换LDAP到："+config.getUrl());
					LDAPUtil.effectiveConfig = config;
					if(times>=3){
						logger.info("3次请求后仍无法连接上LDAP，放弃请求");
						throw new BaseRunTimeException();
					}else{
						//重新请求
						doFilter(arg0, arg1,chain,times);
					}
				}
			}
		}
	}
}
