package cn.lonsun.webservice.internal.service;

import java.util.List;

import cn.lonsun.core.base.service.IBaseService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.webservice.internal.entity.WebServiceEO;
import cn.lonsun.webservice.vo.WebServiceQueryVO;

/**
 * WebService管理服务接口
 *  
 * @author xujh 
 * @date 2014年11月1日 下午3:32:24
 * @version V1.0
 */
public interface IWebserviceService extends IBaseService<WebServiceEO>{
	
	/**
	 * 保存
	 *
	 * @param webService
	 */
	public void save(WebServiceEO webService);
	
	/**
	 * 更新
	 *
	 * @param webService
	 */
	public void update(WebServiceEO webService);
	
	/**
	 * 删除
	 *
	 * @param webServiceIds
	 */
	public void delete(Long[] webServiceIds);
	
	/**
	 * 根据code获取服务对象
	 * @param code
	 * @return
	 */
	public WebServiceEO getServiceByCode(String code);
	
	/**
	 * 根据systemCode获取服务对象集合
	 * @param systemCodes
	 * @return
	 */
	public List<WebServiceEO> getServicesBySystemCode(String[] systemCodes);
	
	/**
	 * 获取外平台可访问的服务
	 * @return
	 */
	public List<WebServiceEO> getAccessedByExternalServices();
	
	/**
	 * 分页查询
	 * @param vo
	 * @return
	 */
	public Pagination getPage(WebServiceQueryVO vo);
	
	/**
	 * 异步通知各应用重新加载webservice
	 */
	public void asyncInitWebService();

}
