package cn.lonsun.content.interview.internal.dao.impl;

import java.util.ArrayList;
import java.util.List;

import cn.lonsun.system.role.internal.util.RoleAuthUtil;
import cn.lonsun.util.LoginPersonUtil;
import org.springframework.stereotype.Repository;

import cn.lonsun.content.interview.internal.dao.IInterviewInfoDao;
import cn.lonsun.content.interview.internal.entity.InterviewInfoEO;
import cn.lonsun.content.interview.vo.InterviewInfoVO;
import cn.lonsun.content.vo.QueryVO;
import cn.lonsun.core.base.dao.impl.BaseDao;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.core.util.Pagination;

@Repository
public class InterviewInfoDaoImpl extends BaseDao<InterviewInfoEO> implements IInterviewInfoDao{

	@Override
	public Pagination getPage(QueryVO query) {
		List<Object> values = new ArrayList<Object>();
		StringBuffer hql = new StringBuffer("select b.id as contentId,b.title as title,b.columnId as columnId,b.siteId as siteId,"
				+ "b.num as sortNum,b.isPublish as issued,b.publishDate as issuedTime,b.imageLink as picUrl,b.videoStatus as videoStatus,"
				+ "s.interviewId as interviewId,s.presenter as presenter,s.userNames as userNames,s.time as time,s.liveLink as liveLink,"
				+ "s.outLink as outLink,s.content as content,s.summary as summary,s.desc as desc,s.isOpen as isOpen,s.openTime as openTime,s.startTime as startTime,s.endTime as endTime,s.type as type"
				+ " from BaseContentEO b,InterviewInfoEO s where b.id = s.contentId and b.recordStatus= ? and b.columnId = ? and b.siteId = ? and b.typeCode = ?");
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
		if(query.getIsOpen() != null){
			hql.append(" and s.isOpen = ?");
			values.add(query.getIsOpen());
		}
		if(query.getType() != null){
			hql.append(" and s.type = ?");
			values.add(query.getType());
		}
		if(!query.getIsMobile()){
			if (!RoleAuthUtil.isCurUserColumnAdmin(query.getColumnId()) && !LoginPersonUtil.isSiteAdmin() && !LoginPersonUtil.isSuperAdmin()) {
				if(null != LoginPersonUtil.getOrganId()) {
					hql.append(" and b.createOrganId=" + LoginPersonUtil.getOrganId());
				}
			}
		}
		hql.append(" order by b.num desc");
		return getPagination(query.getPageIndex(),query.getPageSize(), hql.toString(), values.toArray(),InterviewInfoVO.class);
	}

	@Override
	public InterviewInfoEO getInterviewInfoByContentId(Long contentId) {
		String hql  = "from InterviewInfoEO where contentId  = ?";
		return getEntityByHql(hql, new Object[]{contentId});
	}

	@Override
	public List<InterviewInfoVO> getInterviewInfoVOS(String code) {
		List<Object> values = new ArrayList<Object>();
		StringBuffer hql = new StringBuffer("select b.id as contentId,b.title as title,b.columnId as columnId,b.siteId as siteId,"
				+ "b.num as sortNum,b.isPublish as issued,b.publishDate as issuedTime,b.imageLink as picUrl,"
				+ "s.interviewId as interviewId,s.presenter as presenter,s.userNames as userNames,s.time as time,s.liveLink as liveLink,"
				+ "s.outLink as outLink,s.summary as summary,s.content as content,s.desc as desc,s.isOpen as isOpen,s.openTime as openTime,s.startTime as startTime,s.endTime as endTime,s.type as type"
				+ " from BaseContentEO b,InterviewInfoEO s where b.id = s.contentId and b.recordStatus= ? and b.typeCode = ? and b.isPublish = 1 order by b.num desc");
		values.add(AMockEntity.RecordStatus.Normal.toString());
		values.add(code);
		return (List<InterviewInfoVO>)getBeansByHql(hql.toString(),values.toArray(),InterviewInfoVO.class);
	}
}
