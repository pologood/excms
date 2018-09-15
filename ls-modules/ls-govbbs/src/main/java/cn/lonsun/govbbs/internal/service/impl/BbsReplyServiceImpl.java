package cn.lonsun.govbbs.internal.service.impl;

import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.base.service.impl.BaseService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.govbbs.internal.dao.IBbsReplyDao;
import cn.lonsun.govbbs.internal.entity.BbsPostEO;
import cn.lonsun.govbbs.internal.entity.BbsReplyEO;
import cn.lonsun.govbbs.internal.service.IBbsFileService;
import cn.lonsun.govbbs.internal.service.IBbsPostService;
import cn.lonsun.govbbs.internal.service.IBbsReplyService;
import cn.lonsun.govbbs.internal.vo.BbsMemberVO;
import cn.lonsun.govbbs.internal.vo.ExportReplyVO;
import cn.lonsun.govbbs.internal.vo.MemberCountVO;
import cn.lonsun.govbbs.internal.vo.PostQueryVO;
import cn.lonsun.govbbs.util.MemberRoleUtil;
import cn.lonsun.util.LoginPersonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("bbsReplyService")
public class BbsReplyServiceImpl extends BaseService<BbsReplyEO> implements IBbsReplyService {

	@Autowired
	private IBbsReplyDao bbsReplyDao;

	@Autowired
	private IBbsPostService bbsPostService;

	@Autowired
	private IBbsFileService bbsFileService;



	@Override
	public Pagination getPage(PostQueryVO query) {
		return bbsReplyDao.getPage(query);
	}

	@Override
	public Pagination getVoPage(PostQueryVO query) {
		return bbsReplyDao.getVoPage(query);
	}

	@Override
	public void delete(Long[] replyIds) {
		if(replyIds != null && replyIds.length > 0){
			List<Long> memberIds =  new ArrayList<Long>();
			List<BbsReplyEO> replyEOs = getEntities(BbsReplyEO.class, replyIds);
			for(BbsReplyEO reply:replyEOs){
				if(reply.getAddType() != null && reply.getAddType() == 1 && reply.getMemberId() != null) {
					memberIds.add(reply.getMemberId());
				}
				delete(reply);
				//更新
				BbsPostEO post = bbsPostService.getEntity(BbsPostEO.class, reply.getPostId());
				if(post != null){
					if(reply.getIsHandle() !=null && reply.getIsHandle() ==1){
						post.setReplyId(null);
						post.setIsAccept(BbsPostEO.Status.No.getStatus());
					}
					List<BbsReplyEO> replys = getBbsReplyEOs(post.getPostId());
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
					bbsPostService.updateEntity(post);
				}
			}
			//主题被删除时，计算会员积分
			MemberRoleUtil.updateMemberPoints(LoginPersonUtil.getSiteId(),memberIds,"delReply");
			//删除附件
			bbsFileService.deleteBycaseId(replyIds,1);
		}

	}



	@Override
	public void delByPostIds(Long[] postIds) {
		bbsReplyDao.delByPostIds(postIds);
	}


	@Override
	public void updateStatus(Integer type, Integer status, Long[] replyIds) {
		if(replyIds !=null && replyIds.length>0){
			List<BbsReplyEO> replys =  new ArrayList<BbsReplyEO>();
			List<Long> ufIds =  new ArrayList<Long>();
			Map<Long,Long> postIds = new HashMap<Long, Long>();
			for(int i = replyIds.length-1;i>=0;i--){
				BbsReplyEO reply = getEntity(BbsReplyEO.class, replyIds[i]);
				switch (type) {
					case 0:
						if(!reply.getIsPublish().equals(status)){
							reply.setIsPublish(status);
							reply.setPublishDate(new Date());
							reply.setAuditUserId(LoginPersonUtil.getUserId());
							reply.setAuditUserName(LoginPersonUtil.getUserName());
							reply.setAuditTime(new Date());
							replys.add(reply);
							ufIds.add(replyIds[i]);
							postIds.put(reply.getPostId(),reply.getPostId());
						}
						break;
					case 1:
						if(!reply.getIsColse().equals(status)) {
							reply.setIsColse(status);
							replys.add(reply);
						}
						break;
					default:
						break;
				}
			}
			if(replys != null && replys.size() > 0){
				//如果审核 //更新所有附件状态
				if(ufIds != null && ufIds.size() > 0){
					bbsFileService.setFilesAuditStatus(ufIds.toArray(new Long[]{}),status == 1?1:0);
				}
				updateEntities(replys);
			}
		}
	}

	@Override
	public void updateReplys(BbsPostEO bbsPost) {
		Map<String,Object> params = new HashMap<String, Object>();
		params.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
		params.put("postId", bbsPost.getPostId());
		List<BbsReplyEO> replys = getEntities(BbsReplyEO.class,params);
		if(replys != null && replys.size() > 0 ){
			for(BbsReplyEO reply:replys){
				reply.setPostTile(bbsPost.getTitle());
				reply.setPlateId(bbsPost.getPlateId());
				reply.setParentIds(bbsPost.getParentIds());
			}
			updateEntities(replys);
		}
	}

	@Override
	public void updateRecordStatus(Long[] postIds) {
		bbsReplyDao.updateRecordStatus(postIds);
	}

	@Override
	public void saveWebEntity(BbsReplyEO reply, BbsPostEO post, BbsMemberVO member) {
		saveEntity(reply);
		Boolean isUpdatePost = false;
		if(reply.getIsHandle() != null && reply.getIsHandle() == 1) {
			//设置已办理
			post.setIsAccept(BbsPostEO.Status.Yes.getStatus());
			post.setHandleTimes(new Date().getTime() / 1000);
			post.setReplyId(reply.getReplyId());
			isUpdatePost = true;
		}
		if(reply.getIsPublish() == 1){
			post.setLastMemberId(member.getId());
			post.setLastMemberName(member.getName());
			post.setLastTime(new Date());
			Integer rc = (post.getReplyCount()==null?0:post.getReplyCount());
			post.setReplyCount(rc+1);
			isUpdatePost = true;
		}
		if(isUpdatePost){
			bbsPostService.updateEntity(post);
		}
	}

	@Override
	public List<BbsReplyEO> getBbsReplyEOs(Long postId) {
		return bbsReplyDao.getBbsReplyEOs(postId);
	}

	@Override
	public List<ExportReplyVO> getBbsReplys() {
		return bbsReplyDao.getBbsReplys();
	}

	@Override
	public Map<Long, Long> getReplysByMemberIds(List<Long> memberIds) {
		List<MemberCountVO> vos = bbsReplyDao.getReplysByMemberIds(memberIds);
		Map<Long,Long> map = null;
		if(vos != null && vos.size() > 0) {
			map = new HashMap<Long, Long>();
			for (MemberCountVO vo : vos) {
				map.put(vo.getMemberId(),vo.getReplyCount());
			}
		}
		return map;
	}

	@Override
	public Long getReplyCount(String type) {
		return bbsReplyDao.getReplyCount(type);
	}


}
