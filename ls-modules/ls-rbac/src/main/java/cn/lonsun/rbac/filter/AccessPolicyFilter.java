package cn.lonsun.rbac.filter;

import java.io.IOException;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import cn.lonsun.core.util.IpUtil;
import cn.lonsun.rbac.internal.entity.AccessPolicyEO;
import cn.lonsun.rbac.internal.service.IAccessPolicyService;


/**
 * 系统访问策略过滤器
 *  
 * @author xujh 
 * @date 2014年9月25日 下午3:05:39
 * @version V1.0
 */
public class AccessPolicyFilter implements Filter{
	
	private IAccessPolicyService accessPolicyService;

	@Override
	public void destroy() {
		
	}

	@Override
	public void doFilter(ServletRequest arg0, ServletResponse arg1,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) arg0;
		HttpServletResponse response = (HttpServletResponse) arg1;
		//如果ip策略不允许，那么直接返回
		List<AccessPolicyEO> policys = accessPolicyService.getPolicys(true);
		if(policys==null||policys.size()<=0){
			return;
		}else{
			String ip = IpUtil.getIpAddr(request);
			if(ip.equals("127.0.0.1")){
				ip = "192.168.1.11";
			}
			String[] subs = ip.split("\\.");
			boolean isIllegal = false;
			for(AccessPolicyEO policy:policys){
				String startIp = policy.getStartIp();
				String[] sub1s = startIp.split("\\.");
				String endIp = policy.getEndIp();
				String[] sub2s = endIp.split("\\.");
				if(subs[0].equals(sub1s[0])&&subs[1].equals(sub1s[1])&&subs[2].equals(sub1s[2])){
					String start = sub1s[3];
					String end = sub2s[3];
					if(Integer.valueOf(start)<Integer.valueOf(subs[3])&&Integer.valueOf(subs[3])<Integer.valueOf(end)){
						isIllegal = true;
						break;
					}
				}
			}
			if(!isIllegal){
				//返回到403页面
				response.sendError(403);
				return;
			}
			chain.doFilter(arg0, response);
		}
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		ServletContext context = arg0.getServletContext();
		ApplicationContext ctx = WebApplicationContextUtils
				.getWebApplicationContext(context);
		accessPolicyService = (IAccessPolicyService) ctx.getBean("accessPolicyService");
	}

}
