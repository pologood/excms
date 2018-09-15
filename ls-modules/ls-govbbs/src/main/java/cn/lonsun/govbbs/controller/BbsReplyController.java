package cn.lonsun.govbbs.controller;


import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.RequestUtil;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.govbbs.internal.entity.BbsPlateEO;
import cn.lonsun.govbbs.internal.entity.BbsPostEO;
import cn.lonsun.govbbs.internal.entity.BbsReplyEO;
import cn.lonsun.govbbs.internal.service.IBbsFileService;
import cn.lonsun.govbbs.internal.service.IBbsPostService;
import cn.lonsun.govbbs.internal.service.IBbsReplyService;
import cn.lonsun.govbbs.internal.vo.PostQueryVO;
import cn.lonsun.govbbs.internal.vo.ReplyVO;
import cn.lonsun.govbbs.util.MemberRoleUtil;
import cn.lonsun.rbac.internal.entity.PersonEO;
import cn.lonsun.util.LoginPersonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Controller
@RequestMapping(value = "bbsReply", produces = { "application/json;charset=UTF-8" })
public class BbsReplyController extends BaseController {

	@Autowired
	private IBbsReplyService bbsReplyService;

	@Autowired
	private IBbsPostService bbsPostService;


	@Autowired
	private IBbsFileService bbsFileService;

	@Autowired
	private TaskExecutor taskExecutor;


	@RequestMapping("list")
	public String list() {
		return "/bbs/reply_list";
	}

	@RequestMapping("auditList")
	public String auditList(Model m) {
		return "/bbs/reply_auditList";
	}

	@RequestMapping("audit")
	public String audit() {
		return "/bbs/post_audit";
	}

	@RequestMapping("edit")
	public String edit(Model m,Long replyId,Long postId) {
		if(postId == null || replyId == null){
			throw new BaseRunTimeException(TipsMode.Message.toString(), "参数不能为空");
		}
		BbsReplyEO reply = bbsReplyService.getEntity(BbsReplyEO.class,replyId);
		BbsPostEO post = bbsPostService.getEntity(BbsPostEO.class,postId);
		m.addAttribute("post",post);
		m.addAttribute("reply",reply);
		m.addAttribute("files",bbsFileService.getBbsFiles(replyId));
		return "/bbs/reply_edit";
	}

	@RequestMapping("view")
	public String view(Model m,Long replyId,Long postId) {
		if(postId == null || replyId == null){
			throw new BaseRunTimeException(TipsMode.Message.toString(), "参数不能为空");
		}
		BbsReplyEO reply = bbsReplyService.getEntity(BbsReplyEO.class,replyId);
		BbsPostEO post = bbsPostService.getEntity(BbsPostEO.class,postId);
		m.addAttribute("post",post);
		m.addAttribute("reply",reply);
		m.addAttribute("files",bbsFileService.getBbsFiles(replyId));
		return "/bbs/reply_view";
	}
	@RequestMapping("getPage")
	@ResponseBody
	public Object getPage(PostQueryVO query){
		if (query.getPageIndex()==null||query.getPageIndex() < 0) {
			query.setPageIndex(0L);
		}
		Integer size = query.getPageSize();
		if (size==null||size <= 0 || size > Pagination.MAX_SIZE) {
			query.setPageSize(15);
		}
		if(!LoginPersonUtil.isRoot()){
			if(!LoginPersonUtil.isSuperAdmin()){
				query.setAdmin(false);
				query.setUnitId(LoginPersonUtil.getUnitId());
			}
		}
		query.setSiteId(LoginPersonUtil.getSiteId());
		Pagination page = bbsReplyService.getPage(query);
		if(page.getData() != null && page.getData().size() > 0){
			for(ReplyVO reply:(List<ReplyVO>)page.getData()){
				BbsPlateEO plate = CacheHandler.getEntity(BbsPlateEO.class,reply.getPlateId());
				reply.setPlateName(plate == null?null:plate.getName());
			}
		}
		return getObject(page);
	}

	@RequestMapping("getReply")
	@ResponseBody
	public Object getPost(Long replyId) {
		BbsReplyEO bbsReply = null;
		// 当organId为null时，表示前端发起请求返回空对象
		if (replyId != null) {
			bbsReply= bbsReplyService.getEntity(BbsReplyEO.class, replyId);
		} else {
			bbsReply = new BbsReplyEO();
			PersonEO person = LoginPersonUtil.getPerson();
			if(person !=null){
				bbsReply.setMemberId(person.getUserId());
				bbsReply.setMemberAddress(person.getOfficeAddress());
				bbsReply.setMemberName(person.getName());
				bbsReply.setMemberPhone(person.getMobile());
				bbsReply.setMemberEmail(person.getMail());
				bbsReply.setMemberAddress(person.getOfficeAddress());
			}
		}

		return getObject(bbsReply);
	}

	@RequestMapping("saveAudit")
	@ResponseBody
	public Object saveAudit(BbsReplyEO bbsReply, HttpServletRequest request) {
		if(bbsReply.getPostId() == null){
			throw new BaseRunTimeException(TipsMode.Message.toString(), "主题postId不能为空");
		}
		BbsPostEO post = bbsPostService.getEntity(BbsPostEO.class, bbsReply.getPostId());
		if(post != null){
			if(bbsReply.getIsPublish() == 1 && bbsReply.getPublishDate()== null){
				bbsReply.setPublishDate(new Date());
			}
			if(bbsReply.getReplyId() !=null){
				bbsReplyService.updateEntity(bbsReply);
			}else{
				bbsReply.setSiteId(LoginPersonUtil.getSiteId());
				bbsReply.setPostTile(post.getTitle());
				bbsReply.setParentIds(post.getParentIds());
				bbsReply.setIsHandle(1);
				bbsReply.setHandleUserId(LoginPersonUtil.getUserId());
				bbsReply.setHandleUnitId(LoginPersonUtil.getUnitId());
				Date dayTime = new Date();
				bbsReply.setHandleUnitName(LoginPersonUtil.getUnitName());
				bbsReply.setHandleTime(new Date());
				bbsReply.setPlateId(post.getPlateId());
				bbsReply.setIp(RequestUtil.getIpAddr(request));
				if(bbsReply.getIsPublish()== 1){
					bbsReply.setAuditUserId(LoginPersonUtil.getUserId());
					bbsReply.setPublishDate(new Date());
					bbsReply.setAuditUserId(LoginPersonUtil.getUserId());
					bbsReply.setAuditUserName(LoginPersonUtil.getUserName());
					bbsReply.setAuditTime(new Date());
				}
				bbsReplyService.saveEntity(bbsReply);
				post.setIsAccept(BbsPostEO.Status.Yes.getStatus());
				post.setHandleTimes(dayTime.getTime()/1000);
				post.setReplyId(bbsReply.getReplyId());
				post.setLastMemberId(LoginPersonUtil.getUserId());
				post.setLastMemberName(LoginPersonUtil.getUserName());
				post.setLastTime(new Date());
				Integer rc = (post.getReplyCount()==null?0:post.getReplyCount());
				post.setReplyCount(rc+1);
				bbsPostService.updateEntity(post);
			}
		}
		return getObject();
	}

	@RequestMapping("updateReply")
	@ResponseBody
	public Object updateReply(BbsReplyEO bbsReply) {
		if(bbsReply.getReplyId() == null){
			throw new BaseRunTimeException(TipsMode.Message.toString(), "回复信息不能为空");
		}
		BbsReplyEO reply = bbsReplyService.getEntity(BbsReplyEO.class,bbsReply.getReplyId());
		if(reply != null){
			Boolean isUpdatePost = false;
			if(!reply.getIsPublish().equals(bbsReply.getIsPublish())){
				reply.setAuditUserId(LoginPersonUtil.getUserId());
				reply.setPublishDate(new Date());
				reply.setAuditUserId(LoginPersonUtil.getUserId());
				reply.setAuditUserName(LoginPersonUtil.getUserName());
				reply.setAuditTime(new Date());
				isUpdatePost = true;
			}
			reply.setSiteId(LoginPersonUtil.getSiteId());
			reply.setIsColse(bbsReply.getIsColse());
			reply.setColseDesc(bbsReply.getColseDesc());
			reply.setMemberName(bbsReply.getMemberName());
			reply.setIsPublish(bbsReply.getIsPublish());
			reply.setContent(bbsReply.getContent());
			//更新帖子回复数和最后回复时间
			bbsReplyService.updateEntity(reply);
			//更新帖子回复数和最后回复时间
			if(isUpdatePost){
				updatePostReply(reply.getPostId());
			}
		}
		return getObject();
	}


	@RequestMapping("delete")
	@ResponseBody
	public Object delete(Long[] replyIds) {
		bbsReplyService.delete(replyIds);
		return getObject();
	}

	/**
	 *
	 * @param type 类型  0 审核  1 封贴
	 * @param status
	 * @param postIds
	 * @return
	 */
	@RequestMapping("setStatus")
	@ResponseBody
	public Object setStatus(Integer type,Integer status,Long[] replyIds) {
		if(replyIds !=null && replyIds.length>0){
			List<BbsReplyEO> replys =  new ArrayList<BbsReplyEO>();
			List<Long> ufIds =  new ArrayList<Long>();
			Map<Long,Long> postIds = new HashMap<Long, Long>();
			List<Long> memberIds =  new ArrayList<Long>();
			for(int i = replyIds.length-1;i>=0;i--){
				BbsReplyEO reply = bbsReplyService.getEntity(BbsReplyEO.class, replyIds[i]);
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
							if(reply.getAddType() != null && reply.getAddType() == 1){
								memberIds.add(reply.getMemberId());
							}
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
			//更新
			if(replys != null && replys.size() > 0){
				bbsReplyService.updateEntities(replys);
				//如果审核 //更新所有附件状态
				if(ufIds != null && ufIds.size() > 0){
					bbsFileService.setFilesAuditStatus(ufIds.toArray(new Long[]{}),status == 1?1:0);
				}
				if(type == 0){
					if(status == 2){
						//主题被删除时，计算会员积分
						MemberRoleUtil.updateMemberPoints(LoginPersonUtil.getSiteId(),memberIds,"opposeReply");
					}else{
						//審核通過
						MemberRoleUtil.updateMemberPRCount(LoginPersonUtil.getSiteId(),memberIds,"reply");
					}
				}
			}
			//更新帖子最后回复信息
			if(postIds != null){
				for (Map.Entry<Long, Long> entry : postIds.entrySet()) {
					if(entry.getValue() != null){
						updatePostReply(entry.getValue());
					}
				}
			}
		}
		return getObject();
	}

	//跟新最后回复人、回复时间
	private void updatePostReply(final Long postId) {
		taskExecutor.execute(new Runnable() {
			@Override
			public void run() {
				try{
					bbsPostService.updatePostReply(postId);
				}catch (Exception e){}
			}
		});
	}

}
