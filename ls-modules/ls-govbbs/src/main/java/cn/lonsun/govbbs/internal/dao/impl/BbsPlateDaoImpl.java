package cn.lonsun.govbbs.internal.dao.impl;

import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.govbbs.internal.dao.IBbsPlateDao;
import cn.lonsun.govbbs.internal.entity.BbsPlateEO;
import cn.lonsun.govbbs.internal.vo.PlateShowVO;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class BbsPlateDaoImpl extends MockDao<BbsPlateEO> implements IBbsPlateDao {

	@Override
	public List<BbsPlateEO> getPlates(Long parentId, Long siteId) {
		List<Object> values = new ArrayList<Object>();
		StringBuffer hql = new StringBuffer("from BbsPlateEO o where siteId = ? and  recordStatus='Normal'");
		values.add(siteId);
		if (parentId == null || parentId <= 0) {
			hql.append(" and o.parentId is null");
		} else {
			hql.append(" and o.parentId=?");
			values.add(parentId);
		}
		hql.append(" order by o.sortNum");
		return getEntitiesByHql(hql.toString(), values.toArray());
	}

	@Override
	public Long getMaxSortNum(Long parentId,Long siteId) {
		Long maxSortNum = null;
		StringBuffer sb = new StringBuffer(
				"select max(o.sortNum) from BbsPlateEO as o where siteId = ? and  recordStatus='Normal'");
		if (parentId == null || parentId <= 0) {
			sb.append(" and o.parentId is null");
		} else {
			sb.append(" and o.parentId = "+parentId+"");
		}
		Query query = getCurrentSession().createQuery(sb.toString()).setParameter(0, siteId);
		@SuppressWarnings("rawtypes")
		List list = query.list();
		if (list != null && list.size() > 0) {
			maxSortNum = Long.valueOf(list.get(0) == null ? "0" : list.get(0)
					.toString());
		}
		return maxSortNum;
	}

	@Override
	public List<PlateShowVO> getPlatesByPids(String topPlateParent, Long siteId) {
		String hql = "select p.plateId as plateId,p.siteId as siteId,p.parentId as parentId,p.parentIds as parentIds,p.name as name from BbsPlateEO p where p.parentIds like ? and p.siteId = ? and p.status = 1 and  recordStatus='Normal' order by length(p.parentIds) asc,p.sortNum asc";
		return (List<PlateShowVO>)getBeansByHql(hql,new Object[]{topPlateParent+"%",siteId},PlateShowVO.class);
	}

	@Override
	public BbsPlateEO getMaxBbsPlate(Long parentId) {
		List<Object> values = new ArrayList<Object>();
		String hql = "from BbsPlateEO where 1 = 1";
		if(parentId == null){
			hql  += " and parentId is null";
		}else{
			hql  += " and parentId = ?";
			values.add(parentId);
		}
		hql +=" and  recordStatus='Normal'";
		hql +=" order by plateId desc";
		List<BbsPlateEO> plates = getEntitiesByHql(hql,values.toArray());
		if(plates != null && plates.size()> 0){
			return plates.get(0);
		}
		return null;
	}
}
