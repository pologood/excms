package cn.lonsun.site.site.internal.service;

import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.site.site.internal.entity.WaterMarkConfigEO;

public interface IWaterMarkConfigService extends
		IMockService<WaterMarkConfigEO> {
	
	public WaterMarkConfigEO getConfigBySiteId(Long siteId);

	public void saveConfigEO(WaterMarkConfigEO eo);
}
