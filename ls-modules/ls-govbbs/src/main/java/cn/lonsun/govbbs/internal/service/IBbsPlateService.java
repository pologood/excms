package cn.lonsun.govbbs.internal.service;

import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.govbbs.internal.entity.BbsPlateEO;
import cn.lonsun.govbbs.internal.vo.PlateShowVO;

import java.util.List;

public interface IBbsPlateService extends IMockService<BbsPlateEO> {

	List<BbsPlateEO> getPlates(Long parentId, Long siteId);

	Long getMaxSortNum(Long parentId, Long siteId);

	void updateHasChildren(Long parentId);

	void delete(Long plateId);

	List<PlateShowVO> getPlatesByPids(String topPlateParent, Long siteId);

	String getParentIds(Long parentId);
}
