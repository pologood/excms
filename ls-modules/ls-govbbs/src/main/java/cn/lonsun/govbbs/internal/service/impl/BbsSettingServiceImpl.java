package cn.lonsun.govbbs.internal.service.impl;

import cn.lonsun.core.base.service.impl.BaseService;
import cn.lonsun.govbbs.internal.dao.IBbsSettingDao;
import cn.lonsun.govbbs.internal.entity.BbsSettingEO;
import cn.lonsun.govbbs.internal.service.IBbsSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("bbsSettingService")
public class BbsSettingServiceImpl extends BaseService<BbsSettingEO> implements IBbsSettingService {

	@Autowired
	private IBbsSettingDao bbsSettingDao;

	@Override
	public BbsSettingEO getBbsSeting(Long siteId) {
		//		BbsSettingEO bbsSetting = BbsSettingUitl.getSiteBbsSetting(siteId);
		BbsSettingEO bbsSetting = null;
		if(bbsSetting == null){
			Map<String,Object> params = new HashMap<String,Object>();
			params.put("siteId", siteId);
			bbsSetting = getEntity(BbsSettingEO.class, params);
			//			if(bbsSetting != null){
			//				BbsSettingUitl.put(siteId, bbsSetting);
			//			}
		}
		return bbsSetting;
	}

	@Override
	public BbsSettingEO getByCaseId(Long id) {
		BbsSettingEO bbs = null;
		if(null != id){
			Map<String,Object> params = new HashMap<String,Object>();
			params.put("caseId", id);
			List<BbsSettingEO> bbsSettings = getEntities(BbsSettingEO.class, params);
			if(bbsSettings != null && bbsSettings.size()>0){
				bbs = bbsSettings.get(0);
			}
		}
		return bbs;
	}


}
