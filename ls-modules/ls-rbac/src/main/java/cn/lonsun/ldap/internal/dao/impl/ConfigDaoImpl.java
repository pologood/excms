package cn.lonsun.ldap.internal.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import cn.lonsun.common.vo.PageQueryVO;
import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.ldap.internal.dao.IConfigDao;
import cn.lonsun.ldap.internal.entity.ConfigEO;

@Repository("configDao")
public class ConfigDaoImpl extends MockDao<ConfigEO> implements IConfigDao{

	@Override
	public List<ConfigEO> getConfigs() {
		String hql = "from ConfigEO c where c.recordStatus=? order by c.sortNum";
		return getEntitiesByHql(hql, new Object[]{AMockEntity.RecordStatus.Normal.toString()});
	}
	
	@Override
	public Pagination getPagination(PageQueryVO query) {
		StringBuffer hql = new StringBuffer("from ConfigEO c where c.recordStatus=?");
		if(!StringUtils.isEmpty(query.getSortField())){
			hql.append(" order by c.").append(query.getSortField());
			hql.append(" ").append(query.getSortOrder());
		}
		return getPagination(query.getPageIndex(), query.getPageSize(), hql.toString(), new Object[]{AMockEntity.RecordStatus.Normal.toString()});
	}
	
	@Override
	public boolean isUrlExistedExceptSelf(ConfigEO config){
		String hql = "from ConfigEO c where c.recordStatus=? and c.url=?";
		List<Object> values = new ArrayList<Object>();
		values.add(AMockEntity.RecordStatus.Normal.toString());
		values.add(config.getUrl());
		Long configId = config.getConfigId();
		if(configId!=null&&configId>0){
			hql += " and c.configId != ?";
			values.add(configId);
		}
		Long count = getCount(hql, values.toArray());
		return count!=null&&count>0;
	}
	
	@Override
	public Integer getMaxSortNum(){
		String hql = "select max(c.sortNum) from ConfigEO c where c.recordStatus=?";
		Object object = getObject(hql, new Object[]{AMockEntity.RecordStatus.Normal.toString()});
		Integer maxSortNum = 0;
		if(object!=null){
			maxSortNum = (Integer)object;
		}
		return maxSortNum;
	}

	@Override
	public String getPassword(Long configId) {
		String hql = "select c.password from ConfigEO c where c.configId=? and c.recordStatus=? order by c.sortNum";
		Object object = getObject(hql, new Object[]{configId,AMockEntity.RecordStatus.Normal.toString()});
		return object!=null?(String)object:null;
	}
}
