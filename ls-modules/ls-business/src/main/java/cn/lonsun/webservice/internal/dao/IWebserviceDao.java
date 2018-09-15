package cn.lonsun.webservice.internal.dao;

import java.util.List;

import cn.lonsun.core.base.dao.IBaseDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.webservice.internal.entity.WebServiceEO;
import cn.lonsun.webservice.vo.WebServiceQueryVO;

/**
 * WebService管理ORM接口
 *  
 * @author xujh 
 * @date 2014年11月1日 下午3:33:37
 * @version V1.0
 */
public interface IWebserviceDao extends IBaseDao<WebServiceEO>{
	
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
	 * 获取外平台服务对象集合
	 * @return
	 */
	public List<WebServiceEO> getAccessedByExternalServices();
	
	/**
	 * 分页查询
	 * @param vo
	 * @return
	 */
	public Pagination getPage(WebServiceQueryVO vo);

}
