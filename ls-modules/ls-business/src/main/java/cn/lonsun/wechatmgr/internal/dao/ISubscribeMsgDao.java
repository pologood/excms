package cn.lonsun.wechatmgr.internal.dao;

import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.wechatmgr.internal.entity.SubscribeMsgEO;

public interface ISubscribeMsgDao extends IMockDao<SubscribeMsgEO> {

	/**
	 * 
	 * @Title: getMsgBySite
	 * @Description: 关注回复信息
	 * @param siteId
	 * @return   Parameter
	 * @return  SubscribeMsgEO   return type
	 * @throws
	 */
	SubscribeMsgEO getMsgBySite(Long siteId);
}
