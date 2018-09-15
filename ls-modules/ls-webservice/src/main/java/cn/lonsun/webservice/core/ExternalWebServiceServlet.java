package cn.lonsun.webservice.core;

import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.axis2.AxisFault;

import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.core.exception.util.Jacksons;
import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.webservice.rbac.IPlatformService;
import cn.lonsun.webservice.to.WebServiceTO;
import cn.lonsun.webservice.vo.axis2.WebServiceVO;
import cn.lonsun.webservice.vo.rbac.PlatformVO;

/**
 * WebService初始化
 *  
 * @author xujh 
 * @date 2014年11月5日 上午8:44:30
 * @version V1.0
 */
public class ExternalWebServiceServlet  extends HttpServlet {

	
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -6508375032468999957L;

	@SuppressWarnings("unchecked")
	@Override
	public void init(ServletConfig config) throws ServletException {
		IPlatformService platformService = SpringContextHolder.getBean("indirectPlatfromService");
		// 返回值
		List<PlatformVO> platforms = platformService.getExternalPlatforms();
		if(platforms!=null&&platforms.size()>0){
			for(PlatformVO platform:platforms){
				String platformCode = platform.getCode();
				String url = platform.getWebserviceUrl();
				String nameSpace = platform.getNameSpace();
				String method = platform.getMethod();
				// 返回的对象类型
				Class<?> clazz = WebServiceTO.class;
				// 返回值
				WebServiceTO vo = null;
				Object[] res = null;
				try {
					res = WebServiceUtil.callNormalService(url, nameSpace, method,new Object[]{}, clazz);
				} catch (AxisFault ex) {
					ex.printStackTrace();
				}
				if (res != null && res.length > 0) {
					vo = (WebServiceTO) res[0];
					String json = vo.getJsonData();
					//通过Jacksons工具进行反转
					List<WebServiceVO> services = (List<WebServiceVO>)Jacksons.json().fromJsonToList(json, WebServiceVO.class);
					if(services!=null&&services.size()>0){
						for(WebServiceVO service:services){
							if(service!=null&&!StringUtils.isEmpty(service.getCode())){
								//存放到容器中
								WebServiceContainer.put(platformCode,service.getCode(), service);
							}
						}
					}
				}
			}
		}
	}
}
