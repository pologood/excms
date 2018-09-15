package cn.lonsun.rbac.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.ldap.internal.entity.ConfigEO;
import cn.lonsun.ldap.internal.exception.CommunicationRuntimeException;
import cn.lonsun.ldap.internal.service.IConfigService;
import cn.lonsun.ldap.internal.util.LDAPUtil;

/**
 * 初始化本地线程变量
 * 
 * @author 徐建华
 * 
 */
public class LdapFilter implements Filter {
	
	private Logger logger = LoggerFactory.getLogger(LdapFilter.class);

	@Override
	public void doFilter(ServletRequest arg0, ServletResponse arg1,
			FilterChain chain) throws IOException, ServletException {
		int times = 1;
		try {
			chain.doFilter(arg0, arg1);
		} catch (Exception e) {
			Throwable t = e.getCause();
			if(t instanceof CommunicationRuntimeException){
				doFilter(arg0, arg1,chain,times);
			}
		}
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

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

}
