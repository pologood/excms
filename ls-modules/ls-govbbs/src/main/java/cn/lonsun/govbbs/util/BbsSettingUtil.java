package cn.lonsun.govbbs.util;


import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.govbbs.internal.entity.BbsSettingEO;

public class BbsSettingUtil {

	public static BbsSettingEO getSiteBbsSetting(Long siteId){
		if(siteId == null){
			return null;
		}
		BbsSettingEO sett= CacheHandler.getEntity(BbsSettingEO.class,siteId);
		return sett == null?new BbsSettingEO():sett;
	}

}
