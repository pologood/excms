package cn.lonsun.content.leaderwin.internal.dao.impl;

import java.util.*;

import cn.lonsun.core.base.query.*;
import cn.lonsun.system.role.internal.util.RoleAuthUtil;
import cn.lonsun.util.LoginPersonUtil;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.leaderwin.internal.dao.ILeaderInfoDao;
import cn.lonsun.content.leaderwin.internal.entity.LeaderInfoEO;
import cn.lonsun.content.leaderwin.vo.LeaderInfoVO;
import cn.lonsun.content.vo.QueryVO;
import cn.lonsun.core.base.dao.impl.BaseDao;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.core.util.Pagination;

@Repository
public class LeaderInfoDaoImpl extends BaseDao<LeaderInfoEO> implements ILeaderInfoDao{

	@Override
	public Pagination getPage(QueryVO query) {
		List<Object> values = new ArrayList<Object>();
		StringBuffer hql = new StringBuffer("select b.id as contentId,b.title as name,b.columnId as columnId,b.siteId as siteId,"
				+ "b.num as sortNum,b.isPublish as issued,b.publishDate as issuedTime,b.imageLink as picUrl,"
				+ "s.leaderInfoId as leaderInfoId,s.leaderTypeId as leaderTypeId,s.positions as positions,s.work as work,s.jobResume as jobResume"
				+ " from BaseContentEO b,LeaderInfoEO s where b.id = s.contentId and b.recordStatus= ? and b.columnId = ? and b.siteId = ? and b.typeCode = ?");
		values.add(AMockEntity.RecordStatus.Normal.toString());
		values.add(query.getColumnId());
		values.add(query.getSiteId());
		values.add(query.getTypeCode());
		if(!StringUtils.isEmpty(query.getSearchText())){
			hql.append(" and b.title like ?");
			values.add("%".concat(query.getSearchText()).concat("%"));
		}
		if(query.getIsPublish() != null){
			hql.append(" and b.isPublish = ?");
			values.add(query.getIsPublish());
		}
		if (!RoleAuthUtil.isCurUserColumnAdmin(query.getColumnId()) && !LoginPersonUtil.isSiteAdmin() && !LoginPersonUtil.isSuperAdmin()) {
			if(null != LoginPersonUtil.getOrganId()) {
				hql.append(" and b.createOrganId=" + LoginPersonUtil.getOrganId());
			}
		}
		hql.append(" order by b.num desc");
		return getPagination(query.getPageIndex(),query.getPageSize(), hql.toString(), values.toArray(),LeaderInfoVO.class);
	}


	@SuppressWarnings("unchecked")
	@Override
	public List<LeaderInfoVO> getList(Long siteId,Long columnId) {
		List<Object> values = new ArrayList<Object>();
		StringBuffer hql = new StringBuffer("select b.id as contentId,b.title as name,b.columnId as columnId,b.siteId as siteId,"
				+ "b.num as sortNum,b.isPublish as issued,b.publishDate as issuedTime,b.imageLink as picUrl,"
				+ "s.leaderInfoId as leaderInfoId,s.leaderTypeId as leaderTypeId,s.positions as positions,s.work as work,s.jobResume as jobResume"
				+ " from BaseContentEO b,LeaderInfoEO s where b.id = s.contentId and b.recordStatus= ? and b.columnId = ? and b.typeCode = ? and b.isPublish = 1");
		values.add(AMockEntity.RecordStatus.Normal.toString());
		values.add(columnId);
		values.add(BaseContentEO.TypeCode.leaderInfo.toString());
		if(siteId != null){
			hql.append(" and b.siteId = ?");
			values.add(siteId);
		}
		hql.append(" order by b.num desc");
		return (List<LeaderInfoVO>) getBeansByHql(hql.toString(), values.toArray(), LeaderInfoVO.class);
	}


	@Override
	public LeaderInfoEO getLeaderInfoByContentId(Long contentId) {
		String hql  = "from LeaderInfoEO where contentId  = ?";
		return getEntityByHql(hql, new Object[]{contentId});
	}

	@Override
	public List<LeaderInfoVO> getLeaderInfoVOS(String code) {
		List<Object> values = new ArrayList<Object>();
		StringBuffer hql = new StringBuffer("select b.id as contentId,b.title as name,b.columnId as columnId,b.siteId as siteId,"
				+ "b.num as sortNum,b.isPublish as issued,b.publishDate as issuedTime,b.imageLink as picUrl,"
				+ "s.leaderInfoId as leaderInfoId,s.leaderTypeId as leaderTypeId,s.positions as positions,s.work as work,s.jobResume as jobResume"
				+ " from BaseContentEO b,LeaderInfoEO s where b.id = s.contentId and b.recordStatus= ?  and b.typeCode = ? and b.isPublish = 1 order by b.num desc");
		values.add(AMockEntity.RecordStatus.Normal.toString());
		values.add(code);
		return (List<LeaderInfoVO>) getBeansByHql(hql.toString(), values.toArray(), LeaderInfoVO.class);
	}

	@Override
	public List<LeaderInfoVO> getLeaderInfoVOSByTypeId(Long leaderTypeId) {
		List<Object> values = new ArrayList<Object>();
		StringBuffer hql = new StringBuffer("select b.id as contentId,b.title as name,b.columnId as columnId,b.siteId as siteId,"
				+ "b.num as sortNum,b.isPublish as issued,b.publishDate as issuedTime,b.imageLink as picUrl,"
				+ "s.leaderInfoId as leaderInfoId,s.leaderTypeId as leaderTypeId,s.positions as positions,s.work as work,s.jobResume as jobResume"
				+ " from BaseContentEO b,LeaderInfoEO s where b.id = s.contentId and b.recordStatus= ? and b.typeCode = ? and s.leaderTypeId = ? order by b.num desc");
		values.add(AMockEntity.RecordStatus.Normal.toString());
		values.add(BaseContentEO.TypeCode.leaderInfo.toString());
		values.add(leaderTypeId);
		return (List<LeaderInfoVO>) getBeansByHql(hql.toString(), values.toArray(), LeaderInfoVO.class);
	}

	@Override
	public List<LeaderInfoVO> getLeaderInfo(Long siteId) {
		List<Object> values = new ArrayList<Object>();
		StringBuffer hql = new StringBuffer("select b.id as contentId,b.title as name,b.columnId as columnId,b.siteId as siteId,"
				+ "b.num as sortNum,b.isPublish as issued,b.publishDate as issuedTime,b.imageLink as picUrl,"
				+ "s.leaderInfoId as leaderInfoId,s.leaderTypeId as leaderTypeId,s.positions as positions,s.work as work,s.jobResume as jobResume"
				+ " from BaseContentEO b,LeaderInfoEO s where b.id = s.contentId and b.recordStatus= ? and b.siteId = ?  and b.typeCode = ? and b.isPublish = 1 order by b.num desc");
		values.add(AMockEntity.RecordStatus.Normal.toString());
		values.add(siteId);
		values.add(BaseContentEO.TypeCode.leaderInfo.toString());
		return (List<LeaderInfoVO>) getBeansByHql(hql.toString(), values.toArray(), LeaderInfoVO.class);
	}



	@Override
	public void batchCompletelyDelete(Long[] contentIds) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("contentIds", contentIds);
		String hql = "delete from LeaderInfoEO where contentId in (:contentIds)";
		executeUpdateByJpql(hql, map);

	}

	// 以下为参数处理的私有方法

	private int executeUpdateByJpql(final String hql, final Map<String, Object> params){
		// 创建一个JpqlQuery对象
		JpqlQuery jpqlQuery = new JpqlQuery(hql);
		// 添加参数
		jpqlQuery.setParameters(params);
		// 构造Query对象,并处理参数
		Query query = getQuery(jpqlQuery);
		return query.executeUpdate();

	}

	private Query getQuery(JpqlQuery jpqlQuery) {
		Query query = getCurrentSession().createQuery(jpqlQuery.getJpql());
		processQuery(query, jpqlQuery);
		return query;
	}

	@SuppressWarnings("rawtypes")
	private void processQuery(Query query, BaseQuery originQuery) {
		processQuery(query, originQuery.getParameters(),
				originQuery.getFirstResult(), originQuery.getMaxResults());
		fillParameters(query, originQuery.getParameters());
		query.setFirstResult(originQuery.getFirstResult());
		if (originQuery.getMaxResults() > 0) {
			query.setMaxResults(originQuery.getMaxResults());
		}
	}

	private void processQuery(Query query, QueryParameters parameters,
							  int firstResult, int maxResults) {
		fillParameters(query, parameters);
		query.setFirstResult(firstResult);
		if (maxResults > 0) {
			query.setMaxResults(maxResults);
		}
	}

	private void fillParameters(Query query, QueryParameters params) {
		if (params == null) {
			return;
		}
		if (params instanceof PositionalParameters) {
			fillParameters(query, (PositionalParameters) params);
		} else if (params instanceof NamedParameters) {
			// logger.info("use NamedParameters >>> ");
			// System.out.println("use NamedParameters >>> ");
			fillParameters(query, (NamedParameters) params);
		} else {
			throw new UnsupportedOperationException("不支持的参数形式");
		}
	}

	private void fillParameters(Query query, PositionalParameters params) {
		Object[] paramArray = params.getParams();
		for (int i = 0; i < paramArray.length; i++) {
			query = query.setParameter(i, paramArray[i]);
		}
	}

	@SuppressWarnings("rawtypes")
	private void fillParameters(Query query, NamedParameters params) {
		// logger.info("start fillParameters ... ");
		// System.out.println("start fillParameters ... ");
		for (Map.Entry<String, Object> entry : params.getParams().entrySet()) {
			Object value = entry.getValue();
			if (value instanceof Collection) {
				query.setParameterList(entry.getKey(), (Collection) value);
			} else if (value.getClass().isArray()) {
				query.setParameterList(entry.getKey(), (Object[]) value);
			} else {
				query.setParameter(entry.getKey(), value);
			}
		}
	}

}
