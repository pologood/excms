package cn.lonsun.govbbs.internal.dao;

import cn.lonsun.core.base.dao.IBaseDao;
import cn.lonsun.govbbs.internal.entity.BbsPlateUnitEO;

import java.util.List;

public interface IBbsPlateUnitDao extends IBaseDao<BbsPlateUnitEO> {

	List<BbsPlateUnitEO> getUnits(Long plateId);

	void deleteByPlateId(Long plateId);

}
