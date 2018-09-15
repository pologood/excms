package cn.lonsun.govbbs.internal.dao;

import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.govbbs.internal.entity.BbsPlateEO;
import cn.lonsun.govbbs.internal.vo.PlateShowVO;

import java.util.List;

public interface IBbsPlateDao extends IMockDao<BbsPlateEO> {

	List<BbsPlateEO> getPlates(Long parentId, Long siteId);

	Long getMaxSortNum(Long parentId, Long siteId);

	List<PlateShowVO>  getPlatesByPids(String topPlateParent, Long siteId);

	BbsPlateEO getMaxBbsPlate(Long parentId);
}
