package cn.lonsun.webservice.internal.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import cn.lonsun.core.base.dao.impl.BaseDao;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.SqlUtil;
import cn.lonsun.webservice.internal.dao.IWebserviceDao;
import cn.lonsun.webservice.internal.entity.WebServiceEO;
import cn.lonsun.webservice.vo.WebServiceQueryVO;

/**
 * WebService管理ORM接口实现
 * 
 * @author xujh
 * @date 2014年11月1日 下午3:34:37
 * @version V1.0
 */
@Repository("webServiceDao")
public class WebserviceDaoImpl extends BaseDao<WebServiceEO> implements
		IWebserviceDao {

	@Override
	public WebServiceEO getServiceByCode(String code) {
		String hql = "from WebServiceEO e where e.code=?";
		return getEntityByHql(hql, new Object[] { code });
	}

	@Override
	public List<WebServiceEO> getServicesBySystemCode(String[] systemCodes){
		String hql = "from WebServiceEO e where e.systemCode in :systemCodes";
		Map<String, Object> values = new HashMap<String, Object>(1);
		values.put("systemCodes", systemCodes);
		return getEntitiesByHql(hql, values);
	}
	
	@Override
	public List<WebServiceEO> getAccessedByExternalServices(){
		String hql = "from WebServiceEO e where e.isAccessedByExternal=(:isAccessedByExternal) ";
		Map<String, Object> values = new HashMap<String, Object>(1);
		values.put("isAccessedByExternal", Boolean.TRUE);
		return getEntitiesByHql(hql, values);
	}

	@Override
	public Pagination getPage(WebServiceQueryVO vo) {
		StringBuffer hql = new StringBuffer("from WebServiceEO e where 1=1");
		List<Object> values = new ArrayList<Object>();
		String systemCode = vo.getSystemCode();
		// 编码
		String code = vo.getCode();
		// 命名空间
		String nameSpace = vo.getNameSpace();
		// 系统名称
		String systemName = vo.getSystemName();
		// 服务
		String uri = vo.getUri();
		String description = vo.getDescription();
		if(!StringUtils.isEmpty(systemCode)){
			hql.append(" and e.systemCode=?");
			values.add(systemCode);
		}
		
		boolean hasSearchText = false;
		if (!StringUtils.isEmpty(code) || !StringUtils.isEmpty(nameSpace)
				|| !StringUtils.isEmpty(systemName) || !StringUtils.isEmpty(uri)|| !StringUtils.isEmpty(description)) {
			hasSearchText = true;
		}
		if(hasSearchText){
			hql.append(" and (1=0");
		}
		if (!StringUtils.isEmpty(code)) {
			hql.append(" or e.code like ?  escape '\\'");
			values.add("%".concat(SqlUtil.prepareParam4Query(code)).concat("%"));
		}
		if (!StringUtils.isEmpty(nameSpace)) {
			hql.append(" or e.nameSpace like ?  escape '\\'");
			values.add("%".concat(SqlUtil.prepareParam4Query(nameSpace))
					.concat("%"));
		}
		if (!StringUtils.isEmpty(systemName)) {
			hql.append(" or e.systemName like ?  escape '\\'");
			values.add("%".concat(SqlUtil.prepareParam4Query(systemName))
					.concat("%"));
		}
		if (!StringUtils.isEmpty(uri)) {
			hql.append(" or e.uri like ?  escape '\\'");
			values.add("%".concat(SqlUtil.prepareParam4Query(uri)).concat("%"));
		}
		if (!StringUtils.isEmpty(description)) {
			hql.append(" or e.description like ?  escape '\\'");
			values.add("%".concat(SqlUtil.prepareParam4Query(description)).concat("%"));
		}
		if(hasSearchText){
			hql.append(")");
		}
		// 排序字段
		String field = vo.getSortField();
		if (!StringUtils.isEmpty(field)) {
			hql.append(" order by e.").append(field);
			String sortOrder = vo.getSortOrder();
			if ("desc".equals(sortOrder)) {
				hql.append(" desc");
			}
		}
		return getPagination(vo.getPageIndex(), vo.getPageSize(), hql.toString(),
				values.toArray());
	}

}
