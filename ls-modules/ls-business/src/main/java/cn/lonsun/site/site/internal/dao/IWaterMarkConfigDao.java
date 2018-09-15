package cn.lonsun.site.site.internal.dao;

import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.site.site.internal.entity.WaterMarkConfigEO;

public interface IWaterMarkConfigDao extends IMockDao<WaterMarkConfigEO> {

	public WaterMarkConfigEO getConfigBySiteId(Long siteId);
}
