package cn.lonsun.system.globalconfig.internal.dao.impl;

import org.springframework.stereotype.Repository;

import cn.lonsun.core.base.dao.impl.BaseDao;
import cn.lonsun.system.globalconfig.internal.dao.IGlobConfigCateDao;
import cn.lonsun.system.globalconfig.internal.entity.GlobConfigCateEO;

/**
 * 
 * @ClassName: GlobConfigCateDaoImpl
 * @Description: 全局配置项数据访问层
 * @author Hewbing
 * @date 2015年10月15日 上午11:28:46
 *
 */
@Repository("globConfigCateDao")
public class GlobConfigCateDaoImpl extends BaseDao<GlobConfigCateEO> implements
		IGlobConfigCateDao {

	@Override
	public GlobConfigCateEO getGlobConfigCateByCode(String code) {
		String hql="from GlobConfigCateEO where code=?";
		return getEntityByHql(hql, new Object[]{code});
	}

	@Override
	public void updateKeyByCode(String code, String key) {
		String hql="update GlobConfigCateEO set key=? where code=?";
		executeUpdateByHql(hql, new Object[]{key,code});
	}

	@Override
	public GlobConfigCateEO getGlobConfigCateByKey(String key) {
		String hql="from GlobConfigCateEO where key=?";
		return getEntityByHql(hql, new Object[]{key});
	}
	
}
