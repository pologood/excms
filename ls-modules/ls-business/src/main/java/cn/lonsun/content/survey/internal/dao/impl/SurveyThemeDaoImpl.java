package cn.lonsun.content.survey.internal.dao.impl;

import java.util.ArrayList;
import java.util.List;

import cn.lonsun.system.role.internal.util.RoleAuthUtil;
import cn.lonsun.util.LoginPersonUtil;
import org.springframework.stereotype.Repository;

import cn.lonsun.content.survey.internal.dao.ISurveyThemeDao;
import cn.lonsun.content.survey.internal.entity.SurveyThemeEO;
import cn.lonsun.content.survey.vo.SurveyThemeVO;
import cn.lonsun.content.vo.QueryVO;
import cn.lonsun.core.base.dao.impl.BaseDao;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.core.util.Pagination;

@Repository
public class SurveyThemeDaoImpl extends BaseDao<SurveyThemeEO> implements ISurveyThemeDao{

	@Override
	public Pagination getPage(QueryVO query) {
		List<Object> values = new ArrayList<Object>();
		StringBuffer hql = new StringBuffer("select b.id as contentId,b.title as title,b.columnId as columnId,b.siteId as siteId,"
				+ "b.num as sortNum,b.isPublish as isPublish,b.publishDate as issuedTime,"
				+ "s.themeId as themeId,s.options as options,s.ipLimit as ipLimit,s.ipDayCount as ipDayCount,s.isVisible as isVisible,s.startTime as startTime,"
				+ "s.endTime as endTime,s.content as content,s.isLink as isLink,s.linkUrl as linkUrl,s.typeIds as typeIds,s.objectIds as objectIds"
				+ " from BaseContentEO b,SurveyThemeEO s where b.id = s.contentId and b.recordStatus= ? and b.columnId = ? and b.siteId = ? and b.typeCode = ?");
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
		hql.append(" order by b.num desc");
		return getPagination(query.getPageIndex(),query.getPageSize(), hql.toString(), values.toArray(),SurveyThemeVO.class);
	}

	@Override
	public SurveyThemeEO getSurveyThemeByContentId(Long contentId) {
		String hql  = "from SurveyThemeEO where contentId  = ?";
		return getEntityByHql(hql, new Object[]{contentId});
	}

	@Override
	public List<SurveyThemeVO> getSurveyThemeVOS(String code) {
		List<Object> values = new ArrayList<Object>();
		StringBuffer hql = new StringBuffer("select b.id as contentId,b.title as title,b.columnId as columnId,b.siteId as siteId,"
				+ "b.num as sortNum,b.isPublish as isPublish,b.publishDate as issuedTime,"
				+ "s.themeId as themeId,s.options as options,s.ipLimit as ipLimit,s.ipDayCount as ipDayCount,s.isVisible as isVisible,s.startTime as startTime,"
				+ "s.endTime as endTime,s.content as content,s.isLink as isLink,s.linkUrl as linkUrl,s.typeIds as typeIds,s.objectIds as objectIds"
				+ " from BaseContentEO b,SurveyThemeEO s where b.id = s.contentId and b.recordStatus= ? and b.typeCode = ? and b.isPublish = 1 order by b.num desc");
		values.add(AMockEntity.RecordStatus.Normal.toString());
		values.add(code);
		return (List<SurveyThemeVO>)getBeansByHql(hql.toString(),values.toArray(),SurveyThemeVO.class);
	}
}
