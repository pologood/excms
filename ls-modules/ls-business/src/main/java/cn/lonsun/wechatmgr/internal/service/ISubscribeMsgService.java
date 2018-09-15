package cn.lonsun.wechatmgr.internal.service;

import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.wechatmgr.internal.entity.SubscribeMsgEO;

public interface ISubscribeMsgService extends IMockService<SubscribeMsgEO> {

	/**
	 * 
	 * @Title: getMsgBySite
	 * @Description: 获取关注回复
	 * @param siteId
	 * @return   Parameter
	 * @return  SubscribeMsgEO   return type
	 * @throws
	 */
	SubscribeMsgEO getMsgBySite(Long siteId);

	/**
	 * map 中获取
	 * @param siteId
	 * @return
     */
	String getMsgCache(Long siteId);

	void save(SubscribeMsgEO subMsg);
}
