package cn.lonsun.wechatmgr.internal.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.wechatmgr.internal.dao.IWeChatMenuDao;
import cn.lonsun.wechatmgr.internal.entity.WeChatMenuEO;

@Repository("weChatMenuDao")
public class WeChatMenuDaoImpl extends MockDao<WeChatMenuEO> implements IWeChatMenuDao {

	@Override
	public List<WeChatMenuEO> get1Leve(Long siteId) {
		String hql="from WeChatMenuEO where siteId=? and leves=1  and recordStatus='Normal' order by sort asc";
		return getEntities(hql, new Object[]{siteId}, 3);
	}

	@Override
	public List<WeChatMenuEO> get2Leve(Long pId) {
		String hql="from WeChatMenuEO where pId=? and leves=2 and recordStatus='Normal' order by sort asc";
		return getEntitiesByHql(hql, new Object[]{pId});
	}

	@Override
	public List<WeChatMenuEO> getMenuBySite(Long siteId) {
		String hql="from WeChatMenuEO where siteId=? and recordStatus='Normal' order by sort asc";
		return getEntitiesByHql(hql, new Object[]{siteId});
	}

	@Override
	public void deleteMenu(Long id) {
		String hql="update WeChatMenuEO set recordStatus='Removed' where id=? or pId=?";
		executeUpdateByHql(hql, new Object[]{id,id});
	}
	
}
