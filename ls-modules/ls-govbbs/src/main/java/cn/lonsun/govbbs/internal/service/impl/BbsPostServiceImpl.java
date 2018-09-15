package cn.lonsun.govbbs.internal.service.impl;

import cn.lonsun.cache.client.CacheGroup;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.base.service.impl.BaseService;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.govbbs.internal.dao.IBbsPostDao;
import cn.lonsun.govbbs.internal.entity.*;
import cn.lonsun.govbbs.internal.service.IBbsFileService;
import cn.lonsun.govbbs.internal.service.IBbsPostService;
import cn.lonsun.govbbs.internal.service.IBbsReplyService;
import cn.lonsun.govbbs.internal.vo.ExportPostVO;
import cn.lonsun.govbbs.internal.vo.MemberCountVO;
import cn.lonsun.govbbs.internal.vo.PostQueryVO;
import cn.lonsun.govbbs.util.BbsLogUtil;
import cn.lonsun.govbbs.util.MemberRoleUtil;
import cn.lonsun.rbac.internal.entity.OrganEO;
import cn.lonsun.rbac.internal.service.IOrganService;
import cn.lonsun.site.site.internal.entity.SiteConfigEO;
import cn.lonsun.util.LoginPersonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("bbsPostService")
public class BbsPostServiceImpl extends BaseService<BbsPostEO> implements IBbsPostService {

	@Autowired
	private IBbsPostDao bbsPostDao;
	@Autowired
	private IOrganService organService;
	@Autowired
	private IBbsReplyService bbsReplyService;

	@Autowired
	private IBbsFileService bbsFileService;

	@Override
	public Pagination getPage(PostQueryVO query) {
		return bbsPostDao.getPage(query);
	}

	@Override
	public Boolean hasPost(Long plateId) {
		Boolean hasPost =false;
//		Map<String, Object> map =new HashMap<String, Object>();
//		map.put("plateId", plateId);
//		List<BbsPostEO> posts = getEntities(BbsPostEO.class, map);
		Long count = getCount(plateId);
		if(count !=null && count > 0){
			hasPost = true;
		}
		return hasPost;
	}

	@Override
	public Long getCount(Long indicatorId) {

		return bbsPostDao.getCount("from BbsPostEO where plateId = ?", new Object[]{indicatorId});
	}

	@Override
	public void delete(Long[] postIds,Integer isDel) {
		List<BbsPostEO> posts = getEntities(BbsPostEO.class,postIds);
		if(isDel == 0){
			if(posts != null && posts.size() >0){
				List<Long> memberIds = new ArrayList<Long>();
				for(BbsPostEO post:posts){
					post.setRecordStatus(BbsPostEO.RecordStatus.Removed.toString());
					if(post.getAddType() != null && post.getAddType() == 1 && post.getMemberId() != null){
						memberIds.add(post.getMemberId());
					}
				}
				updateEntities(posts);
				//刪除回復數
				List rmIds = updateReplys1(postIds, AMockEntity.RecordStatus.Removed.toString());
				//主题被删除时，计算会员积分
				MemberRoleUtil.updateMemberPoints(LoginPersonUtil.getSiteId(),memberIds,"delPost");
				//回復被删除时，计算会员积分
				MemberRoleUtil.updateMemberPoints(LoginPersonUtil.getSiteId(),rmIds,"delReply");
				//删除记录
				for(Long postId:postIds){
					BbsLogUtil.saveSysLog(postId, BbsLogEO.Operation.Audit.toString(),3);
				}
			}
		}else{
			bbsReplyService.delByPostIds(postIds);
			//删除记录
			BbsLogUtil.deleteByCaseIds(postIds);
			delete(BbsPostEO.class, postIds);
		}
		//删除附件
		bbsFileService.deleteBycaseId(postIds,isDel);
	}

	@Override
	public void restore(List<BbsPostEO> posts, Long[] postIds) {
		if(posts !=null && posts.size() > 0) {
			updateEntities(posts);
			//更新回复
			updateReplys(postIds, AMockEntity.RecordStatus.Normal.toString());
			//还原附件
			bbsFileService.restoreFiles(postIds);
		}
	}

	@Override
	public Pagination getUnitPlate(PostQueryVO query) {
		return bbsPostDao.getUnitPlate(query);
	}

	@Override
	public Pagination getMemberStatic(PostQueryVO query) {
		return bbsPostDao.getMemberStatic(query);
	}

	@Override
	public Pagination getUnitList(PostQueryVO query) {
		SiteConfigEO siteConfigEO = CacheHandler.getEntity(SiteConfigEO.class, CacheGroup.CMS_INDICATORID, LoginPersonUtil.getSiteId());
		if(siteConfigEO == null && StringUtils.isEmpty(siteConfigEO.getUnitIds())){
			return null;
		}
		OrganEO unit = organService.getEntity(OrganEO.class, Long.parseLong(siteConfigEO.getUnitIds()));
		if(unit == null){return new Pagination() ; }

		Pagination pagination =  bbsPostDao.getUnitList(query,unit.getDn());
		return pagination;
	}

	@Override
	public Pagination getStaticUnitPlate(PostQueryVO query) {
		SiteConfigEO siteConfigEO = CacheHandler.getEntity(SiteConfigEO.class, CacheGroup.CMS_INDICATORID, LoginPersonUtil.getSiteId());
		if(siteConfigEO == null && StringUtils.isEmpty(siteConfigEO.getUnitIds())){
			return null;
		}
		OrganEO unit = organService.getEntity(OrganEO.class, Long.parseLong(siteConfigEO.getUnitIds()));
		if(unit == null){return new Pagination() ; }

		Pagination pagination =  bbsPostDao.getStaticUnitPlate(query,unit.getDn());
		return pagination;

	}

	@Override
	public Pagination getUnitReply(PostQueryVO query) {
		return bbsPostDao.getUnitReply(query);
	}

	/**
	 * 更新回复状态
	 * @param postIds
	 * @param recordStatus
	 */
	private List<Long> updateReplys1(Long[] postIds, String recordStatus) {
		List<Long> memberIds = new ArrayList<Long>();
		Map<String,Object> params = new HashMap<String, Object>();
		params.put("recordStatus","Removed".equals(recordStatus)? "Normal":"Removed");
		params.put("postId", postIds);
		List<BbsReplyEO> replys = bbsReplyService.getEntities(BbsReplyEO.class,params);
		if(replys != null && replys.size() > 0){
			for(BbsReplyEO reply:replys){
				reply.setRecordStatus(recordStatus);
				if(reply.getAddType() != null && reply.getAddType() == 1) {
					memberIds.add(reply.getMemberId());
				}
			}
			bbsReplyService.updateEntities(replys);
		}
		//更新所有回复-至已删除
		return memberIds;
	}


	/**
	 * 更新回复状态
	 * @param postIds
	 * @param recordStatus
	 */
	private void updateReplys(Long[] postIds, String recordStatus) {
		Map<String,Object> params = new HashMap<String, Object>();
		params.put("recordStatus","Removed".equals(recordStatus)? "Normal":"Removed");
		params.put("postId", postIds);
		List<BbsReplyEO> replys = bbsReplyService.getEntities(BbsReplyEO.class,params);
		if(replys != null && replys.size() > 0){
			for(BbsReplyEO reply:replys){
				reply.setRecordStatus(recordStatus);
			}
			bbsReplyService.updateEntities(replys);
		}
		//更新所有回复-至已删除
//				bbsReplyService.updateRecordStatus(postIds);
	}


	@Override
	public void updateStatus(Integer type, Integer status, Long[] postIds) {
		if(postIds !=null && postIds.length>0){
			List<BbsPostEO> posts =  new ArrayList<BbsPostEO>();
			List<Long> ufIds =  new ArrayList<Long>();
			List<Long> memberIds =  new ArrayList<Long>();
			List<Long> postLogIds =  new ArrayList<Long>();
			for(int i = postIds.length-1;i>=0;i--){
				BbsPostEO post = getEntity(BbsPostEO.class, postIds[i]);
				switch (type) {
					case 0:
						if(!post.getIsPublish().equals(status)){
							post.setIsPublish(status);
							post.setPublishDate(new Date());
							post.setAuditUserId(LoginPersonUtil.getUserId());
							post.setAuditUserName(LoginPersonUtil.getUserName());
							post.setAuditTime(new Date());
							posts.add(post);
							ufIds.add(postIds[i]);
							//保存审核日志
							postLogIds.add(post.getPostId());
							if(post.getAddType() != null && post.getAddType() == 1 && post.getMemberId() != null){
								//屏蔽，计算会员积分
								memberIds.add(post.getMemberId());
							}
						}
						break;
					case 1:
						if(!post.getIsHeadTop().equals(status)) {
							post.setIsHeadTop(status);
							posts.add(post);
						}
						break;
					case 2:
						if(!post.getIsTop().equals(status)) {
							post.setIsTop(status);
							posts.add(post);
						}
						break;
					case 3:
						if(!post.getIsEssence().equals(status)) {
							post.setIsEssence(status);
							posts.add(post);
						}
						break;
					case 4:
						if(!post.getIsLock().equals(status)) {
							post.setIsLock(status);
							posts.add(post);
						}
						break;
					case 5:
						if(!post.getIsColse().equals(status)) {
							post.setIsColse(status);
							posts.add(post);
						}
						break;
					default:
						break;
				}
			}
			if(posts != null && posts.size() > 0){
				updateEntities(posts);
				//如果审核 //更新所有附件状态
				if(ufIds != null && ufIds.size() > 0){
					bbsFileService.setFilesAuditStatus(ufIds.toArray(new Long[]{}),status == 1?1:0);
				}
				if(type == 0){
					if(status == 2){
						//主题被删除时，计算会员积分
						MemberRoleUtil.updateMemberPoints(LoginPersonUtil.getSiteId(),memberIds,"opposePost");
					}else{
						//審核通過
						MemberRoleUtil.updateMemberPRCount(LoginPersonUtil.getSiteId(),memberIds,"post");
					}
				}
				//删除记录
				if(postLogIds != null && postLogIds.size()>0){
					for(Long postId:postLogIds){
						BbsLogUtil.saveSysLog(postId, BbsLogEO.Operation.Audit.toString(),status);
					}
				}
			}
		}
	}

	@Override
	public void updateViewCount(Long caseId) {
		if(caseId != null){
			BbsPostEO post = getEntity(BbsPostEO.class,caseId);
			if(post != null){
				post.setViewCount(post.getViewCount() == null?1:post.getViewCount()+1);
				updateEntity(post);
			}
		}
	}

	@Override
	public void updatePostReply(Long postId) {
		BbsPostEO post = getEntity(BbsPostEO.class,postId);
		if(post != null){
			List<BbsReplyEO> replys = bbsReplyService.getBbsReplyEOs(postId);
			Integer replyCount = 0;
			BbsReplyEO lastReply = null;
			if(replys != null && replys.size() > 0){
				replyCount = replys.size();
				lastReply = replys.get(0);
			}
			post.setReplyCount(replyCount);
			post.setLastMemberId(lastReply == null?null:lastReply.getMemberId());
			post.setLastMemberName(lastReply == null?null:lastReply.getMemberName());
			post.setLastTime(lastReply == null?null:lastReply.getCreateDate());
			updateEntity(post);
		}
	}

	@Override
	public void updateSupport(Long caseId) {
		BbsPostEO post = getEntity(BbsPostEO.class,caseId);
		if(post != null){
			Integer support = post.getSupport() == null?0:post.getSupport();
			if(support != 1){
				post.setSupport(1);
				updateEntity(post);
			}
		}
	}

	@Override
	public Map<Long,Long> getPostsByMemberIds(List<Long> memberIds) {
		List<MemberCountVO> vos = bbsPostDao.getPostsByMemberIds(memberIds);
		Map<Long,Long> map = null;
		if(vos != null && vos.size() > 0) {
			map = new HashMap<Long, Long>();
			for (MemberCountVO vo : vos) {
				map.put(vo.getMemberId(),vo.getPostCount());
			}
		}
		return map;
	}

	@Override
	public List<ExportPostVO> getAllPost(PostQueryVO query) {
		return bbsPostDao.getAllPost(query);
	}

	@Override
	public List<ExportPostVO> getAllPost() {
		return bbsPostDao.getAllPost();
	}

	@Override
	public List<BbsPostEO> getAllBbsPostEO(PostQueryVO query) {
		return bbsPostDao.getAllBbsPostEO(query);
	}

	@Override
	public void move(Long[] postIds,BbsPlateEO plate) {
		List<BbsPostEO> posts = getEntities(BbsPostEO.class, postIds);
		if(posts != null && posts.size() > 0 ){
			Long plateId = plate.getPlateId();
			String pname = plate.getName();
			String parentIds = plate.getParentIds();
			List<BbsPostEO> postRs = new ArrayList<BbsPostEO>();
			List<Long> ids = new ArrayList<Long>();
			for(BbsPostEO post:posts){
				if(!post.getPlateId().equals(plateId)){
					post.setPlateId(plateId);
					post.setPlateName(pname);
					post.setParentIds(parentIds);
					ids.add(post.getPostId());
					postRs.add(post);
				}
			}
			if(postRs != null && postRs.size() > 0){
				updateEntities(postRs);
				//回复
				Map<String,Object> params = new HashMap<String, Object>();
				params.put("postId", ids);
				List<BbsReplyEO> replys = bbsReplyService.getEntities(BbsReplyEO.class,params);
				if(replys != null && replys.size() > 0){
					for(BbsReplyEO reply:replys){
						reply.setPlateId(plateId);
						reply.setParentIds(parentIds);
					}
					bbsReplyService.updateEntities(replys);
				}
				//附件
				params.clear();;
				params.put("postId", ids);
				List<BbsFileEO> files = bbsFileService.getEntities(BbsFileEO.class, params);
				if (files != null && files.size() > 0) {
					for (BbsFileEO file : files) {
						file.setPlateId(plateId);
					}
					bbsFileService.updateEntities(files);
				}
			}
		}
	}
}
