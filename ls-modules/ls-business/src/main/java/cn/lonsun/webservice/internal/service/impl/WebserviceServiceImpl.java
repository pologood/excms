package cn.lonsun.webservice.internal.service.impl;

import java.util.List;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import cn.lonsun.core.base.service.impl.BaseService;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.PropertiesReader;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.indicator.internal.service.IIndicatorService;
import cn.lonsun.webservice.internal.dao.IWebserviceDao;
import cn.lonsun.webservice.internal.entity.WebServiceEO;
import cn.lonsun.webservice.internal.service.IWebserviceService;
import cn.lonsun.webservice.vo.WebServiceQueryVO;

/**
 * WebService管理服务接口实现
 *  
 * @author xujh 
 * @date 2014年11月1日 下午3:38:52
 * @version V1.0
 */
@Service("webserviceService")
public class WebserviceServiceImpl extends BaseService<WebServiceEO> implements
		IWebserviceService {
	@Autowired
	private TaskExecutor taskExecutor;
	@Autowired
	private IIndicatorService indicatorService;
	
	@Autowired
	private IWebserviceDao webserviceDao;
	
	@Override
	public void save(WebServiceEO webService) {
		saveEntity(webService);
		asyncInitWebService();
	}

	@Override
	public void update(WebServiceEO webService) {
		updateEntity(webService);
		asyncInitWebService();
	}

	@Override
	public void delete(Long[] webServiceIds) {
		if(webServiceIds!=null&&webServiceIds.length>0){
			delete(WebServiceEO.class, webServiceIds);
		}
		asyncInitWebService();
	}
	
	/**
	 * 异步调用所有的服务重新初始化WebService
	 */
	public void asyncInitWebService(){
		//异步通知各应用重新初始化WebService
		taskExecutor.execute(new Runnable() {
			@Override
			public void run() {
				// 获取配置文件中的配置信息
				WebApplicationContext webApplicationContext = ContextLoader.getCurrentWebApplicationContext();
				ServletContext servletContext = webApplicationContext.getServletContext();
				String realPath = servletContext.getRealPath("/WEB-INF/classes/webservice.properties");
				PropertiesReader reader = PropertiesReader.getInstance(realPath);
				String uri = reader.getValue("webservice.initUri");
				String nameSpace = reader.getValue("webservice.initNameSpace");
				String method = reader.getValue("webservice.initMethod");
				// 返回的对象类型
//				Class<?> clazz = WebServiceTO.class;
				List<IndicatorEO> indicators = indicatorService.getAllShortcuts();
				if(indicators!=null&&indicators.size()>0){
					for(IndicatorEO indicator:indicators){
						//各应用的的地址
						String webServiceHost = indicator.getWebServiceHost();
						//如果服务地址为空，那么取应用host
						if(StringUtils.isEmpty(webServiceHost)){
							webServiceHost = indicator.getHost();
							if(StringUtils.isEmpty(webServiceHost)){
								continue;
							}
						}
						String url = webServiceHost+uri;
						//通知所有应用更新重新初始化WebService
//						try {
//							WebServiceUtil.callNormalService(url, nameSpace, method,new Object[] { }, clazz);
//						} catch (AxisFault ex) {
////							ex.printStackTrace();
//						}
					}
				}
			}
		});
	}


	@Override
	public WebServiceEO getServiceByCode(String code) {
		if(StringUtils.isEmpty(code)){
			throw new NullPointerException();
		}
		return webserviceDao.getServiceByCode(code);
	}

	@Override
	public List<WebServiceEO> getServicesBySystemCode(String[] systemCodes) {
		if(systemCodes==null||systemCodes.length<=0){
			throw new NullPointerException();
		}
		return webserviceDao.getServicesBySystemCode(systemCodes);
	}
	
	@Override
	public List<WebServiceEO> getAccessedByExternalServices(){
		return webserviceDao.getAccessedByExternalServices();
	}

	@Override
	public Pagination getPage(WebServiceQueryVO vo) {
		if(vo==null){
			throw new NullPointerException();
		}
		return webserviceDao.getPage(vo);
	}


}
