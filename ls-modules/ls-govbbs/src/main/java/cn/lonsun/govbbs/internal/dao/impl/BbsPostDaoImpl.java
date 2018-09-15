package cn.lonsun.govbbs.internal.dao.impl;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.dao.impl.BaseDao;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.govbbs.internal.dao.IBbsPostDao;
import cn.lonsun.govbbs.internal.entity.BbsPostEO;
import cn.lonsun.govbbs.internal.vo.*;
import cn.lonsun.govbbs.util.PlateUtil;
import cn.lonsun.rbac.internal.entity.OrganEO;
import cn.lonsun.util.LoginPersonUtil;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Repository
public class BbsPostDaoImpl extends BaseDao<BbsPostEO> implements IBbsPostDao {

	@Override
	public Pagination getPage(PostQueryVO query) {
		List<Object> values = new ArrayList<Object>();
		StringBuffer hql = new StringBuffer("select b.postId as postId,b.plateId as plateId,b.title as title,b.isHeadTop as isHeadTop,b.isTop as isTop," +
				"b.isEssence as isEssence,b.isLock as isLock,b.isAccept as isAccept,b.acceptUnitId as acceptUnitId,b.acceptUnitName as acceptUnitName," +
				"b.auditUserName as auditUserName,b.auditTime as auditTime,b.memberName as memberName,b.createDate as createDate," +
				"b.isPublish as isPublish,b.yellowTimes as yellowTimes,b.redTimes as redTimes" +
				" from BbsPostEO b where b.recordStatus = ?");
		values.add(query.getRecordStatus());
		if(query.getSiteId() != null){
			hql.append(" and b.siteId = ?");
			values.add(query.getSiteId());
		}
		if(!StringUtils.isEmpty(query.getParentIds())){
			hql.append(" and b.parentIds like ?");
			values.add(query.getParentIds().concat("%"));
		}
//		if(query.getPlateId() != null){
//			hql.append(" and b.plateId = ?");
//			values.add(query.getPlateId());
//		}
		if(!StringUtils.isEmpty(query.getSearchText())){
			hql.append(" and (b.title like ? or b.memberName like ?)");
			values.add("%".concat(query.getSearchText()).concat("%"));
			values.add("%".concat(query.getSearchText()).concat("%"));
		}
		if(!StringUtils.isEmpty(query.getTitle())){
			hql.append(" and b.title like ?");
			values.add("%".concat(query.getTitle()).concat("%"));
		}
		if(!query.getAdmin()){
			hql.append(" and b.acceptUnitId = ?");
			values.add(query.getUnitId());
		}
		// 发送开始
		if (!AppUtil.isEmpty(query.getStartTime())) {
			Date start = query.getStartTime();
			hql.append(" and b.createDate >= ?");
			values.add(start);
		}
		// 结束时间
		if (!AppUtil.isEmpty(query.getEndTime())) {
			Date end = query.getEndTime();
			Calendar date = Calendar.getInstance();
			date.setTime(end);
			date.set(Calendar.DATE, date.get(Calendar.DATE) + 1);// 结束日期增加一天
			end = date.getTime();
			hql.append(" and b.createDate <= ?");
			values.add(end);
		}
		if(query.getAcceptUnitId() != null){
			hql.append(" and b.acceptUnitId = ?");
			values.add(query.getAcceptUnitId());
		}
		if(!StringUtils.isEmpty(query.getInfoKey())){
			hql.append(" and b.infoKey = ?");
			values.add(query.getInfoKey());
		}
		if(query.getMemberId() != null){
			hql.append(" and b.memberId = ?");
			values.add(query.getMemberId());
		}
		if(query.getIsAccept() != null){
			hql.append(" and b.isAccept = ?");
			values.add(query.getIsAccept());
		}
		if(query.getIsPublish() !=null){
			hql.append(" and b.isPublish = ?");
			values.add(query.getIsPublish());
		}
		if(query.getIsColse() != null){
			hql.append(" and b.isColse = ?");
			values.add(query.getIsColse());
		}
		if(query.getLevel() != null){
			Long times = new Date().getTime()/1000;
			hql.append(" and b.acceptUnitId is not null and isAccept = 0");
			if(query.getLevel() == 1){//正常的
				hql.append(" and (b.yellowTimes > ? or b.yellowTimes is null)");
				values.add(times);
			}else if(query.getLevel() == 2){//黄牌
				hql.append(" and b.yellowTimes <= ?");
				hql.append(" and b.redTimes > ?");
				values.add(times);
				values.add(times);
			}else if(query.getLevel() == 3){//红牌
				hql.append(" and b.redTimes <= ?");
				values.add(times);
			}
		}
//		hql.append(" order by b.isHeadTop desc,b.isTop desc,b.isEssence desc,b.updateDate desc,b.createDate desc");
		hql.append(" order by ").append(PlateUtil.getPlateOrderKey(query.getPlateId(),"b"));
		return getPagination(query.getPageIndex(), query.getPageSize(), hql.toString(), values.toArray(),PostVO.class);
	}

	@Override
	public Pagination getUnitPlate(PostQueryVO query) {
		List<Object> values = new ArrayList<Object>();
		StringBuffer hql = new StringBuffer("select b.title as title ,b.PLATE_NAME as plateName,b.ACCEPT_UNIT_NAME as acceptUnitName,b.MEMBER_NAME as memberName,b.CREATE_DATE as createDate,b.ACCEPT_TIME as acceptTime,r.HANDLE_TIME as handleTime,b.YELLOW_TIMES as yellowTimes,b.RED_TIMES as redTimes,b.COLSE_DESC as colseDesc,b.IS_COLSE as isColse,b.POST_ID as postId,b.IS_ACCEPT as isAccept from CMS_BBS_POST b left join CMS_BBS_REPLY r  on b.REPLY_ID = r.REPLY_ID  where    b.RECORD_STATUS = ?  and b.SITE_ID =?  ");
		values.add(AMockEntity.RecordStatus.Normal.toString());
		values.add(LoginPersonUtil.getSiteId());
		if(!query.getAdmin()){
			hql.append(" and b.ACCEPT_UNIT_ID = ?");
			values.add(query.getUnitId());
		}
		// 发送开始
		if (!AppUtil.isEmpty(query.getStartTime())) {
			Date start = query.getStartTime();
			hql.append(" and b.CREATE_DATE >= ?");
			values.add(start);
		}
		// 结束时间
		if (!AppUtil.isEmpty(query.getEndTime())) {
			Date end = query.getEndTime();
			Calendar date = Calendar.getInstance();
			date.setTime(end);
			date.set(Calendar.DATE, date.get(Calendar.DATE) + 1);// 结束日期增加一天
			end = date.getTime();
			hql.append(" and b.CREATE_DATE <= ?");
			values.add(end);
		}
		if(query.getAcceptUnitId() != null){
			hql.append(" and b.ACCEPT_UNIT_ID = ?");
			values.add(query.getAcceptUnitId());
		}


		if(query.getIsPublish() !=null){
			hql.append(" and b.IS_PUBLISH = ?");
			values.add(query.getIsPublish());
		}

		hql.append(" order by b.CREATE_DATE desc ");

		if(query.getIsPage()==1){
			return getPaginationBySql(query.getPageIndex(), query.getPageSize(), hql.toString(), values.toArray(), UnitPalteVO.class);

		}
		Pagination pagination =new Pagination();
		pagination.setData(getBeansBySql(hql.toString(), values.toArray(), UnitPalteVO.class));
		return pagination;
	}

	@Override
	public Pagination getMemberStatic(PostQueryVO query) {
		List<Object> values = new ArrayList<Object>();
		StringBuffer sql=new StringBuffer();
		sql.append("select* from ( select     min(m.NAME) as name,count(c.POST_ID) as postCount,NVL(sum(c.REPLY_COUNT),0) as replyCount from  CMS_MEMBER m left join   (select * from   CMS_BBS_POST p where p.RECORD_STATUS=?) c on m.ID =c.MEMBER_ID     where  m.RECORD_STATUS=? and m.SITE_ID=?  ");
		values.add(AMockEntity.RecordStatus.Normal.toString());
		values.add(AMockEntity.RecordStatus.Normal.toString());
		values.add(LoginPersonUtil.getSiteId());
		// 发送开始
		if (!AppUtil.isEmpty(query.getStartTime())) {
			Date start = query.getStartTime();
			sql.append(" and c.CREATE_DATE >= ?");
			values.add(start);
		}
		// 结束时间
		if (!AppUtil.isEmpty(query.getEndTime())) {
			Date end = query.getEndTime();
			Calendar date = Calendar.getInstance();
			date.setTime(end);
			date.set(Calendar.DATE, date.get(Calendar.DATE) + 1);// 结束日期增加一天
			end = date.getTime();
			sql.append(" and c.CREATE_DATE <= ?");
			values.add(end);
		}

		sql.append("group by m.ID) t order by t.postCount desc,t.replyCount desc");


		if(query.getIsPage()==1){
			return getPaginationBySql(query.getPageIndex(), query.getPageSize(), sql.toString(), values.toArray(), MemberStaticVO.class);

		}
		Pagination pagination =new Pagination();
		pagination.setData(getBeansBySql(sql.toString(), values.toArray(), MemberStaticVO.class));


		return pagination;
	}

	@Override
	public Pagination getUnitList(PostQueryVO query, String dn) {
		List<Object> values = new ArrayList<Object>();
		StringBuffer sql=new StringBuffer();
		sql.append("select * from ( select  min(r.NAME_) as name,  count (CASE WHEN c.IS_ACCEPT=1 THEN 1 ELSE null END) as replyCount, count(CASE WHEN c.IS_ACCEPT=0 THEN 1 ELSE null END ) as unReplyCount, count(CASE WHEN c.HANDLE_TIMES> c.OVERDUE_TIMES THEN 1 ELSE null END ) as outCount,count(CASE WHEN c.HANDLE_TIMES> YELLOW_TIMES and c.HANDLE_TIMES< c.RED_TIMES  THEN 1 ELSE null END ) as yellowCount,count(CASE WHEN c.HANDLE_TIMES> c.RED_TIMES THEN 1 ELSE null END ) as readCount, count(CASE WHEN c.POST_ID is not null THEN 1 ELSE null END) as count,  TRUNC (count (CASE WHEN c.IS_ACCEPT=1 THEN 1 ELSE null END)/count(CASE WHEN b.POST_ID is not null THEN 1 ELSE 0 END),2)*100  as rate   from   rbac_organ r left join CMS_BBS_POST c on r.ORGAN_ID =c.ACCEPT_UNIT_ID  left join CMS_BBS_REPLY b on c.REPLY_ID =b.REPLY_ID where    r.RECORD_STATUS='"+ AMockEntity.RecordStatus.Normal.toString()+"' and r.TYPE_ = '"+ OrganEO.Type.Organ.toString()+"' and r.DN_ like '%"+dn+"' ");


		// 发送开始
		if (!AppUtil.isEmpty(query.getStartTime())) {
			Date start = query.getStartTime();
			sql.append(" and c.CREATE_DATE >= ?");
			values.add(start);
		}
		// 结束时间
		if (!AppUtil.isEmpty(query.getEndTime())) {
			Date end = query.getEndTime();
			Calendar date = Calendar.getInstance();
			date.setTime(end);
			date.set(Calendar.DATE, date.get(Calendar.DATE) + 1);// 结束日期增加一天
			end = date.getTime();
			sql.append(" and c.CREATE_DATE <= ?");
			values.add(end);
		}
		if(query.getAcceptUnitId() != null){
			sql.append(" and r.ORGAN_ID = ?");
			values.add(query.getAcceptUnitId());
		}



		sql.append( "group by r.ORGAN_ID)  t");

		if(AppUtil.isEmpty(query.getOrder())){
			sql.append(" order by t.rate desc ,t.count desc");
		}
		else{
			if(query.getOrder().equals(0)){
				sql.append(" order by t.replyCount desc");


			}
			if(query.getOrder().equals(1)){
				sql.append(" order by t.unReplyCount desc");


			}
			if(query.getOrder().equals(2)){
				sql.append(" order by t.outCount desc");


			}
			if(query.getOrder().equals(3)){
				sql.append(" order by t.yellowCount desc");


			}
			if(query.getOrder().equals(4)){
				sql.append(" order by t.readCount desc");


			}
		}


		if(query.getIsPage()==1){
			return getPaginationBySql(query.getPageIndex(), query.getPageSize(), sql.toString(), values.toArray(), StaticUnitVO.class);

		}
		Pagination pagination =new Pagination();
		pagination.setData(getBeansBySql(sql.toString(), values.toArray(), StaticUnitVO.class));


		return pagination;


	}

	@Override
	public Pagination getStaticUnitPlate(PostQueryVO query, String dn) {
		List<Object> values = new ArrayList<Object>();
		StringBuffer sql=new StringBuffer();
		sql.append("select * from ( select  min(r.NAME_) as name,min(c.PLATE_NAME) as plateName, count (CASE WHEN c.IS_ACCEPT=1 THEN 1 ELSE null END) as replyCount, count(CASE WHEN c.IS_ACCEPT=0 THEN 1 ELSE null END ) as unReplyCount, count(CASE WHEN c.HANDLE_TIMES> c.OVERDUE_TIMES THEN 1 ELSE null END ) as outCount,count(CASE WHEN c.HANDLE_TIMES> YELLOW_TIMES and c.HANDLE_TIMES< c.RED_TIMES  THEN 1 ELSE null END ) as yellowCount,count(CASE WHEN c.HANDLE_TIMES> c.RED_TIMES THEN 1 ELSE null END ) as readCount, count(CASE WHEN c.POST_ID is not null THEN 1 ELSE null END) as count  from   rbac_organ r left join CMS_BBS_POST c on r.ORGAN_ID =c.ACCEPT_UNIT_ID  left join CMS_BBS_REPLY b on c.REPLY_ID =b.REPLY_ID where r.RECORD_STATUS='"+ AMockEntity.RecordStatus.Normal.toString()+"' and r.TYPE_ = '"+ OrganEO.Type.Organ.toString()+"' and r.DN_ like '%"+dn+"' ");

		if(!StringUtils.isEmpty(query.getParentIds())){
			sql.append(" and c.PARENT_IDS like ?");
			values.add(query.getParentIds().concat("%"));
		}
		// 发送开始
		if (!AppUtil.isEmpty(query.getStartTime())) {
			Date start = query.getStartTime();
			sql.append(" and c.CREATE_DATE >= ?");
			values.add(start);
		}
		// 结束时间
		if (!AppUtil.isEmpty(query.getEndTime())) {
			Date end = query.getEndTime();
			Calendar date = Calendar.getInstance();
			date.setTime(end);
			date.set(Calendar.DATE, date.get(Calendar.DATE) + 1);// 结束日期增加一天
			end = date.getTime();
			sql.append(" and c.CREATE_DATE <= ?");
			values.add(end);
		}
		if(query.getAcceptUnitId() != null){
			sql.append(" and r.ORGAN_ID = ?");
			values.add(query.getAcceptUnitId());
		}



		sql.append( "group by r.ORGAN_ID,c.PARENT_IDS)  t");

		if(AppUtil.isEmpty(query.getOrder())){
			sql.append(" order by t.count desc");
		}
		else{
			if(query.getOrder().equals(0)){
				sql.append(" order by t.replyCount desc");


			}
			if(query.getOrder().equals(1)){
				sql.append(" order by t.unReplyCount desc");


			}
			if(query.getOrder().equals(2)){
				sql.append(" order by t.outCount desc");


			}
			if(query.getOrder().equals(3)){
				sql.append(" order by t.yellowCount desc");


			}
			if(query.getOrder().equals(4)){
				sql.append(" order by t.readCount desc");


			}
		}


		if(query.getIsPage()==1){
			return getPaginationBySql(query.getPageIndex(), query.getPageSize(), sql.toString(), values.toArray(), UnitPlateStaticVO.class);

		}
		Pagination pagination =new Pagination();
		pagination.setData(getBeansBySql(sql.toString(), values.toArray(), UnitPlateStaticVO.class));


		return pagination;
	}

	@Override
	public Pagination getUnitReply(PostQueryVO query) {
		List<Object> values = new ArrayList<Object>();
		StringBuffer sql=new StringBuffer();
		sql.append("select  c.ACCEPT_UNIT_NAME as acceptUnitName, c.TITLE as title,c.PLATE_NAME as plateName,c.MEMBER_NAME as memberName,c.PUBLISH_DATE as publishDate,c.IP_ as ip, CASE  when c.IS_ACCEPT=1 THEN '已回复' ELSE '未回复' END as replyStatus, b.HANDLE_TIME as handleTime  from CMS_BBS_POST c left join  CMS_BBS_REPLY b on c.REPLY_ID =b.REPLY_ID where  c.ACCEPT_UNIT_ID is not null and   c.SITE_ID=? and  c.RECORD_STATUS=?");
		values.add(LoginPersonUtil.getSiteId());
		values.add(AMockEntity.RecordStatus.Normal.toString());

		if(query.getAcceptUnitId() != null){
			sql.append(" and c.ACCEPT_UNIT_ID = ?");
			values.add(query.getAcceptUnitId());
		}
		// 发送开始
		if (!AppUtil.isEmpty(query.getStartTime())) {
			Date start = query.getStartTime();
			sql.append(" and c.CREATE_DATE >= ?");
			values.add(start);
		}
		// 结束时间
		if (!AppUtil.isEmpty(query.getEndTime())) {
			Date end = query.getEndTime();
			Calendar date = Calendar.getInstance();
			date.setTime(end);
			date.set(Calendar.DATE, date.get(Calendar.DATE) + 1);// 结束日期增加一天
			end = date.getTime();
			sql.append(" and c.CREATE_DATE <= ?");
			values.add(end);
		}
		sql.append(" order by c.ACCEPT_UNIT_ID desc,b.HANDLE_TIME  desc");


		if(query.getIsPage()==1){
			return getPaginationBySql(query.getPageIndex(), query.getPageSize(), sql.toString(), values.toArray(), UnitReplyVO.class);

		}
		Pagination pagination =new Pagination();
		pagination.setData(getBeansBySql(sql.toString(), values.toArray(), UnitReplyVO.class));


		return pagination;
	}


	@Override
	public List<MemberCountVO> getPostsByMemberIds(List<Long> memberIds) {
		List<Object> values = new ArrayList<Object>();
		StringBuffer hql = new StringBuffer("select b.memberId as memberId,count(*) as postCount " +
				"from BbsPostEO b where b.recordStatus = 'Normal' and b.isPublish = 1 and b.siteId = ?");
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
	public List<ExportPostVO> getAllPost(PostQueryVO query) {
		StringBuffer sql = new StringBuffer("select p.post_id as postId ,p.old_id as oldId,p.ACCEPT_UNIT_ID as acceptUnitId,p.plate_Id as plateId from CMS_BBS_POST p");
		Pagination pagination = getPaginationBySql(query.getPageIndex(), query.getPageSize(),sql.toString(),new Object[]{}, ExportPostVO.class);
		List<ExportPostVO> list = (List<ExportPostVO>) pagination.getData();
		return list;
	}

	@Override
	public List<ExportPostVO> getAllPost() {
		StringBuffer sql = new StringBuffer("select p.post_id as postId ,p.ACCEPT_UNIT_ID as acceptUnitId,p.old_id as oldId,p.plate_Id as plateId from CMS_BBS_POST p");
		return (List<ExportPostVO>) getBeansBySql(sql.toString(), new Object[]{}, ExportPostVO.class, null);
	}

	@Override
	public List<BbsPostEO> getAllBbsPostEO(PostQueryVO query) {
		StringBuffer hql = new StringBuffer("from BbsPostEO");
		Pagination pagination = getPagination(query.getPageIndex(), query.getPageSize(),hql.toString(),new Object[]{});
		List<BbsPostEO> list = (List<BbsPostEO>) pagination.getData();
		return list;
	}

}
