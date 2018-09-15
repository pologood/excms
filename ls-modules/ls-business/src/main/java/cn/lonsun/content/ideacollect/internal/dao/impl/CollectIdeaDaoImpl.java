package cn.lonsun.content.ideacollect.internal.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import cn.lonsun.content.ideacollect.internal.dao.ICollectIdeaDao;
import cn.lonsun.content.ideacollect.internal.entity.CollectIdeaEO;
import cn.lonsun.content.ideacollect.vo.IdeaQueryVO;
import cn.lonsun.core.base.dao.impl.BaseDao;
import cn.lonsun.core.util.Pagination;

@Repository
public class CollectIdeaDaoImpl extends BaseDao<CollectIdeaEO> implements ICollectIdeaDao{

	@Override
	public void deleteByCollectInfoId(Long[] ids) {
		String hql = "delete from CollectIdeaEO where collectInfoId in (:ids)";
		getCurrentSession().createQuery(hql).setParameterList("ids", ids).executeUpdate();
	}

	@Override
	public Pagination getPage(IdeaQueryVO query) {
		List<Object> values = new ArrayList<Object>();
		StringBuffer hql = new StringBuffer("from CollectIdeaEO where collectInfoId = ?");
		values.add(query.getCollectInfoId());
		if(query != null){
			if(query.getIssued()!= null){
				hql.append(" and isIssued = ?");
				values.add(query.getIssued());
			}
		}
		hql.append(" order by createDate desc");
		return getPagination(query.getPageIndex(),query.getPageSize(), hql.toString(), values.toArray());
	}

}
