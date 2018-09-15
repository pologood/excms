package cn.lonsun.rbac.servlet;

import java.io.File;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import cn.lonsun.core.util.PropertiesReader;
import cn.lonsun.ldap.internal.cache.InternalLdapCache;
import cn.lonsun.rbac.internal.service.IOrganService;
import cn.lonsun.rbac.internal.service.IPersonService;
import cn.lonsun.rbac.utils.PropertiesContainer;
import cn.lonsun.rbac.utils.PropertiesContainer.FileNames;
import cn.lonsun.rbac.utils.PropertiesFilePaths;

/**
 * 角色权限树初始化Servlet
 *
 * @author xujh
 * @date 2014年10月11日 上午9:43:05
 * @version V1.0
 */
public class InitServlet extends HttpServlet {
	/**
	 *
	 */
	private static final long serialVersionUID = 1237851506671541450L;
	//初始化的时候回保留文件的路径
	public static String organsJsPath = null;
	public static String personsJsPath = null;

	private IPersonService personService;

	private IOrganService organService;

	@Override
	public void init(ServletConfig config) throws ServletException {
		// 项目跟路径
		String rootPath = config.getServletContext().getRealPath("/");
		// 设置项目根路径
		InitServlet.organsJsPath = rootPath.concat("assets")
				.concat(File.separator).concat("common").concat(File.separator)
				.concat("js").concat(File.separator)
				.concat("organs.js");
		InitServlet.personsJsPath = rootPath.concat("assets")
				.concat(File.separator).concat("common").concat(File.separator)
				.concat("js").concat(File.separator)
				.concat("persons.js");
//		String realPath = config.getServletContext().getRealPath(
//				PropertiesFilePaths.SWITCH_PATH);
//		PropertiesReader reader = PropertiesReader.getInstance(realPath);
//		String ldapCacheOn = reader.getValue("ldapCacheOn");
//		if (!StringUtils.isEmpty(ldapCacheOn) && ldapCacheOn.equals("1")) {
//			InternalLdapCache.isCacheOn = true;
//		}
//		PropertiesContainer container = PropertiesContainer.getInstance();
//		// 如果没有取到，那么到配置文件中读取，并保存到容器中
//		String configRealPath = config.getServletContext().getRealPath(
//				PropertiesFilePaths.SYSTEM_ENVIRONMENT_PATH);
//		Map<String, String> map = PropertiesReader.getInstance(configRealPath)
//				.getValues();
//		container.put(FileNames.SystemProperties.getValue(), map);
		// 初始化选人界面对应的Cache
		ApplicationContext ctx = WebApplicationContextUtils
				.getWebApplicationContext(config.getServletContext());
		this.personService = (IPersonService) ctx.getBean("personService");

		this.organService = (IOrganService) ctx.getBean("organService");

		try{
			// 初始化组织Cache
			//organService.initSimpleOrgansCache();
			// 初始化人员Cache
			//personService.initSimplePersonsCache();
		}catch(Exception e){
			e.printStackTrace();
			System.out.println(" 初始化组织Cache失败");
		}
//		organService.initOrganNamesWithDn();
	}
}
