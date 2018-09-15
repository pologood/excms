package cn.lonsun.rbac.filter;

import java.io.IOException;
import java.io.PrintWriter;
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
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.exception.util.Jacksons;
import cn.lonsun.core.util.LsSessionContext;
import cn.lonsun.core.util.PropertiesReader;
import cn.lonsun.core.util.ResultVO;
import cn.lonsun.core.util.SessionUtil;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.rbac.internal.service.IPermissionService;
import cn.lonsun.rbac.utils.AdminUidVO;
import cn.lonsun.rbac.utils.PropertiesFilePaths;

import com.fasterxml.jackson.databind.util.JSONPObject;

/**
 * 访问权限控制过滤器
 * 
 * @author xujh
 * @date 2014年9月26日 上午11:09:18
 * @version V1.0
 */
public class ResourceFilter implements Filter {

	private Logger logger = LoggerFactory.getLogger(getClass());
	private IPermissionService permissionService;

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest arg0, ServletResponse arg1,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) arg0;
		HttpServletResponse response = (HttpServletResponse) arg1;
		HttpSession session = request.getSession();
		/*
		 * fix SWFUload在firefox,chorme等非IE浏览器下上传session丢失的BUG 需要在登录成功时注入一下
		 * LsSessionContext.AddSession(session); 退出登录时删除一下
		 * LsSessionContext.DelSession(session);
		 */
		if (request.getParameter("jsessionId") != null) {
			//session = LsSessionContext.getSession(request.getParameter("jsessionId"));
		}
		// 2.jsp页面禁止访问
		if (isIllegalAccess(request, response)) {
			return;
		}
		String uri = request.getRequestURI();
		// 带"."的静态资源都无需进行权限验证
		boolean isContinue = true;
		if (!uri.contains(".")) {
			String realPath = request.getSession().getServletContext().getRealPath(PropertiesFilePaths.LEGAL_ACCESS_RESOURCES_PATH);
			PropertiesReader reader = PropertiesReader.getInstance(realPath);
			// 需要跳过权限验证的资源直接忽略
			if (!reader.containsValue(uri)) {
				Long userId = SessionUtil.getLongProperty(session, "userId");
				if (userId == null) {
					if (!((request.getHeader("X-Requested-With") != null && request
							.getHeader("X-Requested-With").contains("XMLHttpRequest")))) {
						popLoginPageCode(request,response);
						return;
					} else {// JSON格式返回
						throw new BaseRunTimeException(TipsMode.Message.toString(), "请重新登录");
					}
				} else {
					String uid = SessionUtil.getStringProperty(session, "uid");
					//开发商管理账号和超管跳过
					if(!uid.equals(AdminUidVO.developerUid)&&!uid.equals(AdminUidVO.superAdminUid)){
						@SuppressWarnings("unchecked")
						List<Long> roleIds = (List<Long>) session.getAttribute("roleIds");
						if (!permissionService.hasPermission(roleIds, uri)) {
							throw new BaseRunTimeException(TipsMode.Message.toString(),"您的权限不足，请联系管理员");
						}
					}
				}
			}
		}
		if(isContinue){
			chain.doFilter(arg0, arg1);
		}
	}
	
	private void popLoginPageCode(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// 如果session中不存在登录者实体，则弹出框提示重新登录
		// 设置request和response的字符集，防止乱码
		request.setCharacterEncoding("UTF-8");
		response.reset();
		PrintWriter out = response.getWriter();
		StringBuilder sb = new StringBuilder();
		sb.append("<!doctype html><html><head><meta charset=\"utf-8\">");
		sb.append("<script src=\"/assets/core/jquery.min.js\"></script>");
		sb.append("<script src=\"/app/login/js/miniLogin.js\"></script>");
		sb.append("<script src=\"/assets/core/boot.js\" debug=\"0\"></script>");
		sb.append("<script src=\"/app/common/js/config.js\"></script>");
		sb.append("<script>");
		sb.append("jQuery(document).ready(function(){");
		sb.append("Ls.openWin('/login/miniLogin',{winType:1,title:'',width:400,height:320});");
		sb.append("});");
		sb.append("</script>");
		sb.append("");
		sb.append("</head><body></body></html>");
		out.print(sb.toString());
		out.flush();

	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		ServletContext context = arg0.getServletContext();
		ApplicationContext ctx = WebApplicationContextUtils
				.getWebApplicationContext(context);
		permissionService = (IPermissionService) ctx
				.getBean("permissionService");
	}

	public void redirectToLoginPage(HttpServletRequest request,
			HttpServletResponse response) {
		ResultVO ro = new ResultVO();
		ro.setStatus(ResultVO.Status.Failure.getValue());
		ro.setDesc("请重新登录");
		ro.setData(-9);
		// 返回json或jsonp串
		String json = null;
		String callback = request.getParameter("callback");
		if (callback != null && !"".equals(callback)) {
			json = Jacksons.json().fromObjectToJson(
					new JSONPObject(callback, ro));
		} else {
			json = Jacksons.json().fromObjectToJson(ro);
		}
		PrintWriter writer = null;
		try {
			response.setHeader("Pragma", "no-cache");
			response.setHeader("Cache-Control", "no-cache");
			response.setDateHeader("Expires", 0);
			response.setContentType("application/json;charset=UTF-8");
			writer = response.getWriter();
			writer.print(json);
		} catch (Exception e2) {
		} finally {
			if (writer != null)
				writer.close();
		}
	}

	/**
	 * 判断请求是否合法，如果是直接访问jsp页面，返回不合法fasle
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	private boolean isIllegalAccess(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		boolean isIllegal = false;
		String path = request.getServletPath();
		try {
			// jsp返回403
			if (path.endsWith(".jsp")) {
				logger.error(path + " error!");
				response.sendError(403);
			}
		} catch (IOException e) {
			throw new RuntimeException();
		}
		return isIllegal;
	}

}
