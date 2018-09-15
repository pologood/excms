package cn.lonsun.content.ideacollect.internal.dao.impl;

import java.util.ArrayList;
import java.util.List;

import cn.lonsun.system.role.internal.util.RoleAuthUtil;
import cn.lonsun.util.LoginPersonUtil;
import org.springframework.stereotype.Repository;

import cn.lonsun.content.ideacollect.internal.dao.ICollectInfoDao;
import cn.lonsun.content.ideacollect.internal.entity.CollectInfoEO;
import cn.lonsun.content.ideacollect.vo.CollectInfoVO;
import cn.lonsun.content.vo.QueryVO;
import cn.lonsun.core.base.dao.impl.BaseDao;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.core.util.Pagination;

@Repository
public class CollectInfoDaoImpl extends BaseDao<CollectInfoEO> implements ICollectInfoDao{

	@Override
	public Pagination getPage(QueryVO query) {
		List<Object> values = new ArrayList<Object>();
		StringBuffer hql = new StringBuffer("select b.id as contentId,b.title as title,b.columnId as columnId,b.siteId as siteId,"
				+ "b.num as sortNum,b.isPublish as isIssued,b.publishDate as issuedTime,b.imageLink as picUrl,"
				+ "s.collectInfoId as collectInfoId,s.startTime as startTime,s.endTime as endTime,s.content as content,s.desc as desc,s.ideaCount as ideaCount,s.linkUrl as linkUrl"
				+ " from BaseContentEO b,CollectInfoEO s where b.id = s.contentId and b.recordStatus= ? and b.columnId = ? and b.siteId = ? and b.typeCode = ?");
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
		if(!query.getIsMobile()) {
			if (!RoleAuthUtil.isCurUserColumnAdmin(query.getColumnId()) && !LoginPersonUtil.isSiteAdmin() && !LoginPersonUtil.isSuperAdmin()) {
				if (null != LoginPersonUtil.getOrganId()) {
					hql.append(" and b.createOrganId=" + LoginPersonUtil.getOrganId());
				}
			}
		}
		hql.append(" order by b.num desc,s.createDate desc");
		return getPagination(query.getPageIndex(),query.getPageSize(), hql.toString(), values.toArray(),CollectInfoVO.class);
	}

	@Override
	public CollectInfoEO getCollectInfoByContentId(Long contentId) {
		String hql  = "from CollectInfoEO where contentId  = ?";
		return getEntityByHql(hql, new Object[]{contentId});
	}


	@Override
	public List<CollectInfoVO> getCollectInfoVOS(String code) {
		List<Object> values = new ArrayList<Object>();
		StringBuffer hql = new StringBuffer("select b.id as contentId,b.title as title,b.columnId as columnId,b.siteId as siteId,"
				+ "b.num as sortNum,b.isPublish as isIssued,b.publishDate as issuedTime,b.imageLink as picUrl,"
				+ "s.collectInfoId as collectInfoId,s.startTime as startTime,s.endTime as endTime,s.content as content,s.desc as desc,s.ideaCount as ideaCount"
				+ " from BaseContentEO b,CollectInfoEO s where b.id = s.contentId and b.recordStatus= ? and b.typeCode = ? and b.isPublish = 1 order by s.createDate desc");
		values.add(AMockEntity.RecordStatus.Normal.toString());
		values.add(code);
		return (List<CollectInfoVO>)getBeansByHql(hql.toString(),values.toArray(),CollectInfoVO.class);
	}
}
