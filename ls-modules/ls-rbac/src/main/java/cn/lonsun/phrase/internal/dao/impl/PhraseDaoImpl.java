package cn.lonsun.phrase.internal.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import cn.lonsun.core.base.dao.impl.BaseDao;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.SqlUtil;
import cn.lonsun.phrase.internal.dao.IPhraseDao;
import cn.lonsun.phrase.internal.entity.PhraseEO;

/**
 * 常用语Dao接口实现
 *
 * @author xujh
 * @version 1.0
 * 2014年12月3日
 *
 */
@Repository("phraseDao")
public class PhraseDaoImpl extends BaseDao<PhraseEO> implements IPhraseDao {

	@Override
	public List<PhraseEO> getPhrases(Long organId,Long userId,String type) {
		String hql = "from PhraseEO p where p.createOrganId=? and p.createUserId=?";
		Object[] values = null;
		if(!StringUtils.isEmpty(type)){
			hql += " and p.type=?";
			values = new Object[]{organId,userId,type};
		}else{
			values = new Object[]{organId,userId};
		}
		return getEntitiesByHql(hql, values);
	}

	@Override
	public Pagination getPage(Long index,Integer size,Long organId,Long userId,String text) {
		String hql = "from PhraseEO p where p.createOrganId=? and p.createUserId=?";
		Object[] values = null;
		if(!StringUtils.isEmpty(text)){
			hql = hql.concat(" and p.text like ?");
			//模糊查询预处理
			String targetText = "%".concat(SqlUtil.prepareParam4Query(text)).concat("%");
			values = new Object[]{organId,userId,targetText};
		}else{
			values = new Object[]{organId,userId};
		}
		return getPagination(index, size, hql, values);
	}
}
