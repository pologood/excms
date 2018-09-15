package cn.lonsun.rbac.internal.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.SqlUtil;
import cn.lonsun.rbac.internal.dao.IResourceDao;
import cn.lonsun.rbac.internal.entity.ResourceEO;

@Repository("resourceDao")
public class ResourceDaoImpl extends MockDao<ResourceEO> implements
		IResourceDao {

	@Override
	public List<ResourceEO> getResources(Long businessTypeId) {
		String hql = "from ResourceEO r where r.businessTypeId=? and r.recordStatus=?";
		return getEntitiesByHql(hql, new Object[] { businessTypeId,
				AMockEntity.RecordStatus.Normal.toString() });
	}

	@Override
	public List<ResourceEO> getResourcesByResourceTypeId(Long resourceTypeId) {
		String hql = "from ResourceEO r where r.resourceTypeId=? and r.recordStatus=?";
		return getEntitiesByHql(hql, new Object[] { resourceTypeId,
				AMockEntity.RecordStatus.Normal.toString() });
	}

	@Override
	public Pagination getPagination(Long index, Integer size,
			Long businessTypeId, String subName, String subPath) {
		StringBuffer hql = new StringBuffer(
				"from ResourceEO r where r.recordStatus='Normal' ");
		List<Object> values = new ArrayList<Object>();
		if (businessTypeId != null) {
			hql.append(" and r.businessTypeId=?");
			values.add(businessTypeId);
		}
		if (!StringUtils.isEmpty(subName)) {
			String target = SqlUtil.prepareParam4Query(subName);
			hql.append(" and r.name like ? escape '\\'");
			values.add(target);
		}

		if (!StringUtils.isEmpty(subPath)) {
			String target = SqlUtil.prepareParam4Query(subPath);
			hql.append(" and r.path like ? escape '\\'");
			values.add(target);
		}
		Pagination page = getPagination(index, size, hql.toString(),
				values.toArray());
		return page;
	}

	@Override
	public List<ResourceEO> getResourcesByIndicatorId(Long indicatorId) {
		String hql = "from ResourceEO r where r.indicatorId=? and r.recordStatus=?";
		return getEntitiesByHql(hql, new Object[] { indicatorId,
				AMockEntity.RecordStatus.Normal.toString() });
	}

	@Override
	public List<ResourceEO> getResources(List<Long> indicatorIds) {
		List<Object> values = new ArrayList<Object>();
		StringBuffer hql = new StringBuffer("from ResourceEO r where 1=1");
		
		for(int i=0;i<indicatorIds.size();i++){
			Long indicatorId = indicatorIds.get(i);
			if(indicatorId==null||indicatorId<=0){
				continue;
			}
			if(i==0){
				hql.append(" and (r.indicatorId=?");
			}else{
				hql.append(" or r.indicatorId=?");
			}
			values.add(indicatorId);
			if(i==indicatorIds.size()-1){
				hql.append(" )");
			}
		}
		hql.append(" and r.recordStatus=?");
		values.add(AMockEntity.RecordStatus.Normal.toString());
		return getEntitiesByHql(hql.toString(), values.toArray());
	}

	@Override
	public boolean isUriExisted(String uri) {
		String hql = "from ResourceEO r where r.uri=? and r.recordStatus=?";
		Long count = getCount(hql, new Object[] { uri,
				AMockEntity.RecordStatus.Normal.toString() });
		return (count != null && count > 0) ? true : false;
	}

}
