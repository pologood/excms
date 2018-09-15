package cn.lonsun.govbbs.internal.service;

import cn.lonsun.core.base.service.IBaseService;
import cn.lonsun.govbbs.internal.entity.BbsSettingEO;
import org.springframework.stereotype.Service;

@Service("bbsSettingService")
public interface IBbsSettingService extends IBaseService<BbsSettingEO> {

	BbsSettingEO getBbsSeting(Long siteId);
	
	BbsSettingEO getByCaseId(Long id);
	

}
