package cn.lonsun.ldap.internal.servlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import cn.lonsun.ldap.internal.context.DirContextContainerFactory;
import cn.lonsun.ldap.internal.context.impl.DirContextPool;

/**
 * LDAP连接池初始化Servlet
 *  
 * @author xujh 
 * @date 2014年10月11日 上午9:41:20
 * @version V1.0
 */
public class DirContextPoolServlet extends HttpServlet {
	
	public static boolean isPoolOn = false;
	
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		try {
//			String realPath = config.getServletContext().getRealPath(PropertiesFilePaths.SWITCH_PATH);
//			PropertiesReader reader = PropertiesReader.getInstance(realPath);
//			String poolOn = reader.getValue("ldapConnectionPoolOn");
//			String ldapCacheOn = reader.getValue("ldapCacheOn");
//			if(!StringUtils.isEmpty(ldapCacheOn)&&ldapCacheOn.equals("1")){
//				PersonServiceImpl.isCacheOn = Boolean.TRUE; 
//				OrganServiceImpl.isCacheOn = Boolean.TRUE; 
//			}
//			String developerOn = reader.getValue("developerOn");
//			if(!StringUtils.isEmpty(developerOn)&&developerOn.equals("1")){
//				IndicatorController.isDeveloperOn = true;
//			}
			//如果未取到或值为0，那么不初始化连接池
			if(!isPoolOn){
				return;
			}
			DirContextPool pool = DirContextContainerFactory.getPool();
			pool.setSize(50);
			pool.init();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Error();
		}
		super.init(config);
	}

	
}
