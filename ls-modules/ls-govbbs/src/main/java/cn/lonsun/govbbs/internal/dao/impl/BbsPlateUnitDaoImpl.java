package cn.lonsun.govbbs.internal.dao.impl;

import cn.lonsun.core.base.dao.impl.BaseDao;
import cn.lonsun.govbbs.internal.dao.IBbsPlateUnitDao;
import cn.lonsun.govbbs.internal.entity.BbsPlateUnitEO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class BbsPlateUnitDaoImpl extends BaseDao<BbsPlateUnitEO> implements IBbsPlateUnitDao {

	@Override
	public List<BbsPlateUnitEO> getUnits(Long plateId) {
		String hql = "from BbsPlateUnitEO where plateId = ? order by plateUnitId asc";
		return getEntitiesByHql(hql, new Object[]{plateId});
	}

	@Override
	public void deleteByPlateId(Long plateId) {
		String hql = "delete from BbsPlateUnitEO where plateId = ?";
		executeUpdateByHql(hql, new Object[]{plateId});
	}

}
