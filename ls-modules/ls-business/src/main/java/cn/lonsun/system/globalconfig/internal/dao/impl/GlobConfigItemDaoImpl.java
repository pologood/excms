package cn.lonsun.system.globalconfig.internal.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import cn.lonsun.core.base.dao.impl.BaseDao;
import cn.lonsun.system.globalconfig.internal.dao.IGlobConfigItemDao;
import cn.lonsun.system.globalconfig.internal.entity.GlobConfigItemEO;

/**
 * 
 * @ClassName: GlobConfigItemDaoImpl
 * @Description: 全局配置数据访问层
 * @author Hewbing
 * @date 2015年10月15日 上午11:29:35
 *
 */
@Repository("globConfigItemDao")
public class GlobConfigItemDaoImpl extends BaseDao<GlobConfigItemEO> implements
		IGlobConfigItemDao {

	@Override
	public List<GlobConfigItemEO> getListByCateId(Long cateId) {
		String hql="from GlobConfigItemEO where cateId=?";
		return getEntitiesByHql(hql, new Object[]{cateId});
	}

	@Override
	public List<GlobConfigItemEO> getListByCateKey(String cateKey) {
		String hql="from GlobConfigItemEO where cateKey=?";
		return getEntitiesByHql(hql, new Object[]{cateKey});
	}

	@Override
	public GlobConfigItemEO getEOByKey(String key) {
		String hql="from GlobConfigItemEO where key=?";
		return getEntityByHql(hql, new Object[]{key});
	}

	@Override
	public List<GlobConfigItemEO> getEOByKey2(String key, String cateKey) {
		String hql="from GlobConfigItemEO where cateKey=? and key=?";
		return getEntitiesByHql(hql, new Object[]{cateKey,key});
	}

}