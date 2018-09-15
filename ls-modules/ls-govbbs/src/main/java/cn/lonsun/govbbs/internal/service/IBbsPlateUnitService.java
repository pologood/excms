package cn.lonsun.govbbs.internal.service;

import cn.lonsun.core.base.service.IBaseService;
import cn.lonsun.govbbs.internal.entity.BbsPlateUnitEO;

import java.util.List;

public interface IBbsPlateUnitService extends IBaseService<BbsPlateUnitEO> {

	List<BbsPlateUnitEO> getUnits(Long plateId);

	void deleteByPlateId(Long plateId);

	void deleteByPlateId(Long plateId, String unitIds, String unitNames);

	void savePlateUnits(Long columnConfigId, String unitIds, String unitNames);

}
