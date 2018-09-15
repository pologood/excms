package cn.lonsun.site.site.internal.dao.impl;

import org.springframework.stereotype.Repository;

import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.site.site.internal.dao.IWaterMarkConfigDao;
import cn.lonsun.site.site.internal.entity.WaterMarkConfigEO;

@Repository("waterMarkConfigDao")
public class WaterMarkConfigDaoImpl extends MockDao<WaterMarkConfigEO> implements
		IWaterMarkConfigDao {

	@Override
	public WaterMarkConfigEO getConfigBySiteId(Long siteId) {
		String hql="from WaterMarkConfigEO where siteId=? and recordStatus='Normal'";
		return getEntityByHql(hql, new Object[]{siteId});
	}
}
