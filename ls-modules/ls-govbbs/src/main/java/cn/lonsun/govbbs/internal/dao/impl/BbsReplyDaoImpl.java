package cn.lonsun.govbbs.internal.dao.impl;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.dao.impl.BaseDao;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.govbbs.internal.dao.IBbsReplyDao;
import cn.lonsun.govbbs.internal.entity.BbsReplyEO;
import cn.lonsun.govbbs.internal.vo.*;
import cn.lonsun.util.LoginPersonUtil;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Repository
public class BbsReplyDaoImpl extends BaseDao<BbsReplyEO> implements IBbsReplyDao {

	@Override
	public Pagination getVoPage(PostQueryVO query) {
		List<Object> values = new ArrayList<Object>();
		StringBuffer hql = new StringBuffer("select p.postId as postId,p.plateId as plateId,p.title as title,p.memberName as postMemberName,p.createDate as postCreateTime,p.ip as postIp,"
				+ "r.replyId as replyId,r.isPublish as isPublish,r.content as replyContent,r.memberName as replyMemberName,r.ip as replyIp,r.createDate as replyCreateTime," +
				"r.auditUserId as auditUserId,r.auditUserName as auditUserName,r.auditTime as auditTime,r.isColse as isColse,r.colseDesc as colseDesc" +
				" from BbsReplyEO r,BbsPostEO p" +
				" where r.postId = p.postId and r.recordStatus = ? and p.recordStatus = ?");
		values.add(AMockEntity.RecordStatus.Normal.toString());
		values.add(AMockEntity.RecordStatus.Normal.toString());
		if(query.getSiteId() != null){
			hql.append(" and b.siteId = ? and r.siteId= ?");
			values.add(query.getSiteId());
			values.add(query.getSiteId());
		}
		if(!StringUtils.isEmpty(query.getParentIds())){
			hql.append(" and r.parentIds like ?");
			values.add(query.getParentIds().concat("%"));
		}
		if(query.getPlateId() != null){
			hql.append(" and r.plateId = ?");
			values.add(query.getPlateId());
		}
		if(query.getIsPublish() !=null){
			hql.append(" and r.isPublish = ?");
			values.add(query.getIsPublish());
		}
		if(!StringUtils.isEmpty(query.getInfoKey())){
			hql.append(" and p.infoKey = ?");
			values.add(query.getInfoKey());
		}
		if(!query.getAdmin()){
			hql.append(" and p.acceptUnitId = ?");
			values.add(query.getUnitId());
		}
		if(!StringUtils.isEmpty(query.getSearchText())){
			hql.append(" and (p.title like ? or r.memberName like ?)");
			values.add("%".concat(query.getSearchText()).concat("%"));
			values.add("%".concat(query.getSearchText()).concat("%"));
		}
		// 发送开始
		if (!AppUtil.isEmpty(query.getStartTime())) {
			Date start = query.getStartTime();
			hql.append(" and r.createDate >= ?");
			values.add(start);
		}
		if(query.getIsColse() != null){
			hql.append(" and r.isColse = ?");
			values.add(query.getIsColse());
		}
		// 结束时间
		if (!AppUtil.isEmpty(query.getEndTime())) {
			Date end = query.getEndTime();
			Calendar date = Calendar.getInstance();
			date.setTime(end);
			date.set(Calendar.DATE, date.get(Calendar.DATE) + 1);// 结束日期增加一天
			end = date.getTime();
			hql.append(" and r.createDate <= ?");
			values.add(end);
		}
		hql.append(" order by r.createDate desc");
		return getPagination(query.getPageIndex(), query.getPageSize(), hql.toString(), values.toArray(),BbsReplyVO.class);
	}


	@Override
	public Pagination getPage(PostQueryVO query) {
		List<Object> values = new ArrayList<Object>();
		StringBuffer hql = new StringBuffer("select r.replyId as replyId,r.postId as postId,r.postTile as postTile,r.plateId as plateId," +
				"r.memberName as memberName,r.createDate as createDate,r.isPublish as isPublish,r.isHandle as isHandle,r.auditUserName as auditUserName," +
				"r.auditTime as auditTime" +
				" from BbsReplyEO r where r.recordStatus = ? ");
		values.add(AMockEntity.RecordStatus.Normal.toString());
		if(query.getSiteId() != null){
			hql.append(" and r.siteId = ?");
			values.add(query.getSiteId());
		}
		if(!StringUtils.isEmpty(query.getParentIds())){
			hql.append(" and r.parentIds like ?");
			values.add(query.getParentIds().concat("%"));
		}
		if(query.getPlateId() != null){
			hql.append(" and r.plateId = ?");
			values.add(query.getPlateId());
		}
		if(query.getIsPublish() !=null){
			hql.append(" and r.isPublish = ?");
			values.add(query.getIsPublish());
		}
		if(query.getAcceptUnitId() != null){
			hql.append(" and r.handleUnitId = ? and r.isHandle = 1");
			values.add(query.getAcceptUnitId());
		}
		if(!StringUtils.isEmpty(query.getSearchText())){
			hql.append(" and (r.postTile like ? or r.memberName like ?)");
			values.add("%".concat(query.getSearchText()).concat("%"));
			values.add("%".concat(query.getSearchText()).concat("%"));
		}
		// 发送开始
		if (!AppUtil.isEmpty(query.getStartTime())) {
			Date start = query.getStartTime();
			hql.append(" and r.createDate >= ?");
			values.add(start);
		}
		// 结束时间
		if (!AppUtil.isEmpty(query.getEndTime())) {
			Date end = query.getEndTime();
			Calendar date = Calendar.getInstance();
			date.setTime(end);
			date.set(Calendar.DATE, date.get(Calendar.DATE) + 1);// 结束日期增加一天
			end = date.getTime();
			hql.append(" and r.createDate <= ?");
			values.add(end);
		}
		if(query.getIsColse() != null){
			hql.append(" and r.isColse = ?");
			values.add(query.getIsColse());
		}
		if(query.getMemberId() != null){
			hql.append(" and r.memberId = ?");
			values.add(query.getMemberId());
		}
		hql.append(" order by r.createDate desc");
		return getPagination(query.getPageIndex(), query.getPageSize(), hql.toString(), values.toArray(),ReplyVO.class);
	}

	@Override
	public void delByPostIds(Long[] postIds) {
		String hql = "delete from BbsReplyEO where postId in (:ids)";
		getCurrentSession().createQuery(hql).setParameterList("ids", postIds).executeUpdate();
	}

	@Override
	public void updateRecordStatus(Long[] postIds) {
		String hql = "update BbsReplyEO r set r.recordStatus = :recordStatus where postId in (:ids)";
		getCurrentSession().createQuery(hql).setParameter("recordStatus", AMockEntity.RecordStatus.Removed.toString()).
				setParameterList("ids", postIds).executeUpdate();
	}

	@Override
	public List<BbsReplyEO> getBbsReplyEOs(Long postId) {
		String hql = "from BbsReplyEO r where r.recordStatus = 'Normal' and r.isPublish = 1 and r.postId = ? order by r.createDate desc";
		return getEntitiesByHql(hql,new Object[]{postId});
	}

	@Override
	public List<MemberCountVO> getReplysByMemberIds(List<Long> memberIds) {
		List<Object> values = new ArrayList<Object>();
		StringBuffer hql = new StringBuffer("select b.memberId as memberId,count(*) as postCount " +
				"from BbsReplyEO b where b.recordStatus = 'Normal' and b.isPublish = 1 and b.siteId = ?");
		values.add(LoginPersonUtil.getSiteId());
		hql.append(" and (1=0");
		if(memberIds != null) {
			for (Long memberId : memberIds) {
				if(memberId != null){
					hql.append(" or b.memberId = ?");
					values.add(memberId);
				}
			}
		}
		hql.append(")");
		hql.append(" group by b.memberId");
		return (List<MemberCountVO>)getBeansByHql(hql.toString(),values.toArray(),MemberCountVO.class);
	}

	@Override
	public Long getReplyCount(String type) {
		StringBuffer hql = new StringBuffer(" from BbsReplyEO b where 1=1");
		List<Object> values = new ArrayList<Object>();

		if (!AppUtil.isEmpty(type)) {
			hql.append(" and b.type=?");
			values.add(type);
		}

		Long count = getCount(hql.toString(), values.toArray());
		if (count == null) {
			count = 0L;
		}
		return count;
	}

	@Override
	public List<ExportReplyVO> getBbsReplys() {
		String sql = "select r.old_id as oldId,r.reply_id as replyId,r.post_id as postId,r.plate_Id as plateId from CMS_BBS_REPLY r";
		return (List<ExportReplyVO>) getBeansBySql(sql.toString(), new Object[]{}, ExportReplyVO.class, null);
	}

}
