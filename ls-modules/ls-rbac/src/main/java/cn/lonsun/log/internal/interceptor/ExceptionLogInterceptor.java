package cn.lonsun.log.internal.interceptor;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import cn.lonsun.core.base.service.impl.BaseService;
import cn.lonsun.core.util.BrowserUtils;
import cn.lonsun.core.util.ContextHolderUtils;
import cn.lonsun.core.util.IpUtil;
import cn.lonsun.log.internal.dao.IExceptionLogDao;
import cn.lonsun.log.internal.entity.ExceptionLogEO;

/**
 * 系统异常拦截器
 * @author 
 */
public class ExceptionLogInterceptor extends BaseService<ExceptionLogEO> implements HandlerInterceptor {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    
    @Autowired
    private static IExceptionLogDao exceptionLogDao;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, 
			Object handler) throws Exception {
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, 
			ModelAndView modelAndView) throws Exception {
/*		if(modelAndView!=null) {
			String viewName = modelAndView.getViewName();
			UserAgent userAgent = UserAgent.parseUserAgentString(request.getHeader("User-Agent")); 
			if(viewName.startsWith("modules/") && DeviceType.MOBILE.equals(userAgent.getOperatingSystem().getDeviceType())){
				modelAndView.setViewName(viewName.replaceFirst("modules", "mobile"));
			}
		}*/
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, 
			Object handler, Exception ex) throws Exception {
        HttpSession session = ContextHolderUtils.getSession();
        Long userId = session.getAttribute("userId")==null?Long.valueOf(1):Long.valueOf(1); // TODO
		if (ex!=null){
		    
			StringBuilder params = new StringBuilder();
			int index = 0;
			for (Object param : request.getParameterMap().keySet()){ 
				params.append((index++ == 0 ? "" : "&") + param + "=");
			}
			
			ExceptionLogEO logEO = new ExceptionLogEO();
	        logEO.setBroswer(BrowserUtils.checkBrowse(request));
	        //logEO.setCaseType(caseType);
	        //logEO.setDescription(description);
	        logEO.setOperationIp(IpUtil.getIpAddr(request));
	        //logEO.setOperation(operation);
	        logEO.setCreateUserId(userId);
	        logEO.setCreateDate(new Date());
	        logEO.setUserAgent(request.getHeader("user-agent"));
	        logEO.setRequestUri(request.getRequestURI());
	        logEO.setMethod(request.getMethod());
	        logEO.setParams(params.toString());
	        logEO.setException(ex != null ? ex.toString() : "");
	        
	        exceptionLogDao.save(logEO);
			
		}
		
//		logger.debug("最大内存: {}, 已分配内存: {}, 已分配内存中的剩余空间: {}, 最大可用内存: {}", 
//				Runtime.getRuntime().maxMemory(), Runtime.getRuntime().totalMemory(), Runtime.getRuntime().freeMemory(), 
//				Runtime.getRuntime().maxMemory()-Runtime.getRuntime().totalMemory()+Runtime.getRuntime().freeMemory()); 
		
	}

}
