package cn.lonsun.wechatmgr.internal.dao.impl;

import org.springframework.stereotype.Repository;

import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.wechatmgr.internal.dao.IWeChatAccountsInfoDao;
import cn.lonsun.wechatmgr.internal.entity.WeChatAccountsInfoEO;

@Repository("weChatAccountsInfoDao")
public class WeChatAccountsInfoDaoImpl extends MockDao<WeChatAccountsInfoEO> implements
		IWeChatAccountsInfoDao {

	@Override
	public WeChatAccountsInfoEO getInfoByPrimitiveId(String primitiveId) {
		String hql="from WeChatAccountsInfoEO where primitiveId=? and recordStatus='Normal'";
		return getEntityByHql(hql, new Object[]{primitiveId});
	}

	@Override
	public WeChatAccountsInfoEO getInfoBySite(Long siteId) {
		String hql="from WeChatAccountsInfoEO where siteId=? and recordStatus='Normal'";
		return getEntityByHql(hql,new Object[]{siteId});
	}

	@Override
	public WeChatAccountsInfoEO getInfoByUid(String primitiveId) {
		String hql="from WeChatAccountsInfoEO where primitiveId=? and recordStatus='Normal'";
		return getEntityByHql(hql,new Object[]{primitiveId});
	}
	
}
