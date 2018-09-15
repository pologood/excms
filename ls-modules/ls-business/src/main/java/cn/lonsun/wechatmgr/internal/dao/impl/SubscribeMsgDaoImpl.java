package cn.lonsun.wechatmgr.internal.dao.impl;

import org.springframework.stereotype.Repository;

import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.wechatmgr.internal.dao.ISubscribeMsgDao;
import cn.lonsun.wechatmgr.internal.entity.SubscribeMsgEO;

@Repository("subscribeMsgDao")
public class SubscribeMsgDaoImpl extends MockDao<SubscribeMsgEO> implements ISubscribeMsgDao {

	@Override
	public SubscribeMsgEO getMsgBySite(Long siteId) {
		String hql="from SubscribeMsgEO where siteId=? and recordStatus='Normal'";
		return getEntityByHql(hql, new Object[]{siteId});
	}
	
}
