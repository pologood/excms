package cn.lonsun.govbbs.controller;

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.RequestUtil;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.govbbs.internal.entity.BbsLogEO;
import cn.lonsun.govbbs.internal.entity.BbsPlateEO;
import cn.lonsun.govbbs.internal.entity.BbsPostEO;
import cn.lonsun.govbbs.internal.entity.BbsSettingEO;
import cn.lonsun.govbbs.internal.service.IBbsFileService;
import cn.lonsun.govbbs.internal.service.IBbsPostService;
import cn.lonsun.govbbs.internal.service.IBbsReplyService;
import cn.lonsun.govbbs.internal.vo.PostQueryVO;
import cn.lonsun.govbbs.internal.vo.PostVO;
import cn.lonsun.govbbs.util.BbsFilesUtil;
import cn.lonsun.govbbs.util.BbsLogUtil;
import cn.lonsun.govbbs.util.BbsSettingUtil;
import cn.lonsun.rbac.internal.entity.PersonEO;
import cn.lonsun.util.DataDictionaryUtil;
import cn.lonsun.util.LoginPersonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping(value = "bbsPost", produces = { "application/json;charset=UTF-8" })
public class BbsPostController extends BaseController {

	@Autowired
	private IBbsPostService bbsPostService;

	@Autowired
	private IBbsReplyService bbsReplyService;

	@Autowired
	private TaskExecutor taskExecutor;

	@Autowired
	private IBbsFileService bbsFileService;


	@RequestMapping("list")
	public String list(Model m) {
		return "/bbs/post_list";
	}

	@RequestMapping("auditList")
	public String auditList(Model m) {
		return "/bbs/post_auditList";
	}

	@RequestMapping("delList")
	public String delList(Model m) {
		return "/bbs/post_delList";
	}

	@RequestMapping("edit")
	public String edit(Long postId,Model m) {
		if(postId != null){
			m.addAttribute("files",bbsFileService.getBbsFiles(postId));
		}
		return "/bbs/post_edit";
	}

	@RequestMapping("view")
	public String view(Model m,Long postId) {
		if(postId == null){
			throw new BaseRunTimeException(TipsMode.Message.toString(), "参数不能为空");
		}
		BbsPostEO post = bbsPostService.getEntity(BbsPostEO.class,postId);
		m.addAttribute("post",post);
		m.addAttribute("logs", BbsLogUtil.getLogs(postId));
		m.addAttribute("files",bbsFileService.getBbsFiles(postId));

		//保存浏览日志
		BbsLogUtil.saveSysLog(postId, BbsLogEO.Operation.View.toString(),null);
		return "/bbs/post_view";
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
//		if(!LoginPersonUtil.isRoot()){
//			if(!LoginPersonUtil.isSuperAdmin()){
//				query.setAdmin(false);
//				query.setUnitId(LoginPersonUtil.getUnitId());
//			}
//		}
		query.setSiteId(LoginPersonUtil.getSiteId());
		Pagination page = bbsPostService.getPage(query);
		if(page.getData() != null && page.getData().size() > 0){
			for(PostVO post:(List<PostVO>)page.getData()){
				BbsPlateEO plate = CacheHandler.getEntity(BbsPlateEO.class,post.getPlateId());
				post.setPlateName(plate == null?null:plate.getName());
			}
		}
		return getObject(page);
	}

	@RequestMapping("getPost")
	@ResponseBody
	public Object getPost(Long postId,Long plateId) {
		BbsPostEO bbsPost = null;
		// 当organId为null时，表示前端发起请求返回空对象
		if (postId != null) {
			bbsPost= bbsPostService.getEntity(BbsPostEO.class, postId);
		} else {
			bbsPost = new BbsPostEO();

			PersonEO person = LoginPersonUtil.getPerson();
			if(person !=null){
				bbsPost.setAddType(BbsPostEO.AddType.User.getAddType());
				bbsPost.setMemberId(person.getUserId());
				bbsPost.setMemberName(person.getName());
				bbsPost.setMemberPhone(person.getMobile());
				bbsPost.setMemberEmail(person.getMail());
				bbsPost.setMemberAddress(person.getOfficeAddress());
			}
			Date dayTime = new Date();
			bbsPost.setCreateDate(dayTime);
			BbsSettingEO bbsSetting = BbsSettingUtil.getSiteBbsSetting(LoginPersonUtil.getSiteId());
			if(bbsSetting != null){
				Integer replyDay = bbsSetting.getReplyDay();
				Integer yellowDay = bbsSetting.getYellowDay();
				Integer redDay = bbsSetting.getRedDay();
				//逾期时间
				Long times = dayTime.getTime()/1000;
				if(replyDay != null && replyDay != null){
					bbsPost.setOverdueTimes(times+replyDay*24*60*60);
				}
				//黄牌日期
				if(yellowDay != null && yellowDay != null){
					bbsPost.setYellowTimes(times+yellowDay*24*60*60);
				}
				//红牌日期
				if(redDay != null && redDay != null){
					bbsPost.setRedTimes(times+redDay*24*60*60);
				}
			}

		}
		return getObject(bbsPost);
	}

	@RequestMapping("saveOrUpdate")
	@ResponseBody
	public Object saveOrUpdate(BbsPostEO bbsPost, Long[] fileIds, HttpServletRequest request) {
		if(bbsPost.getPlateId() == null){
			throw new BaseRunTimeException(TipsMode.Message.toString(), "版块plateId不能为空");
		}
		Boolean isChangeStatus = false; // 状态是否变更
		Boolean isChangePlate = false;//栏目是否更新
		bbsPost.setSiteId(LoginPersonUtil.getSiteId());
		if(bbsPost.getPostId() !=null){
			if(!StringUtils.isEmpty(bbsPost.getChangeFiled())){
				if(bbsPost.getChangeFiled().indexOf("plateId") > -1){
					isChangePlate = true;
				}
				//如果改变办理单位
				if(bbsPost.getChangeFiled().indexOf("isAccept") > -1){
					bbsPost.setIsAccept(BbsPostEO.IsAccept.ToReply.getIsAccept());
					bbsPost.setAcceptTime(new Date());
				}
				//状态改变时,记录审核人
				if(bbsPost.getChangeFiled().indexOf("isPublish") > -1){
					isChangeStatus = true;
					bbsPost.setAuditUserId(LoginPersonUtil.getUserId());
					bbsPost.setAuditUserName(LoginPersonUtil.getUserName());
					bbsPost.setAuditTime(new Date());
				}
			}
			//是否有附件
			bbsPostService.updateEntity(bbsPost);

		}else{
			//已审核
			if(!bbsPost.getIsPublish().equals(BbsPostEO.IsPublish.TO_AUDIT.getIsPublish())){
				isChangeStatus = true;
				bbsPost.setAuditUserId(LoginPersonUtil.getUserId());
				bbsPost.setAuditUserName(LoginPersonUtil.getUserName());
				bbsPost.setAuditTime(new Date());
			}
			//如果办理单位不为空 设置待办理
			if(bbsPost.getAcceptUnitId() != null){
				bbsPost.setIsAccept(BbsPostEO.IsAccept.ToReply.getIsAccept());
				bbsPost.setAcceptTime(new Date());
			}
			//系统用户添加
			bbsPost.setAddType(BbsPostEO.AddType.User.getAddType());
			bbsPost.setIp(RequestUtil.getIpAddr(request));
			bbsPostService.saveEntity(bbsPost);
		}
		//保存附件
		if(fileIds != null && fileIds.length > 0){
			bbsFileService.setFilesStatus(fileIds,bbsPost.getIsPublish() == 1?1:0,bbsPost.getPostId(),bbsPost.getPostId(),bbsPost.getPlateId());
		}
		//状态改变时，保存审核日志
		if(isChangeStatus){
			BbsLogUtil.saveSysLog(bbsPost.getPostId(), BbsLogEO.Operation.Audit.toString(),bbsPost.getIsPublish());
		}
		//如果主题栏目更新了 更新所有回复 所有附件pid
		if(isChangePlate){
			updateAllReply(bbsPost);
		}
		//更新是否有附件
		BbsFilesUtil.updatePostFilesSuffix(bbsPost.getPostId());
		return getObject();
	}

	/**
	 * 更新所有回复
	 */
	private void updateAllReply(final BbsPostEO bbsPost) {
		taskExecutor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					bbsReplyService.updateReplys(bbsPost);
					//更新所有附件pid
					bbsFileService.updateFilesPlateId(bbsPost);
				}catch (Exception e){
					e.printStackTrace();
				}
			}
		});
	}

	@RequestMapping("delete")
	@ResponseBody
	public Object delete(Long[] postIds,Integer isDel) {
		bbsPostService.delete(postIds,isDel);
		return getObject();
	}

	/**
	 * 还原
	 * @param postIds
	 * @return
	 */
	@RequestMapping("restore")
	@ResponseBody
	public Object restore(Long[] postIds) {
		if(postIds !=null && postIds.length>0) {
			List<BbsPostEO> posts = bbsPostService.getEntities(BbsPostEO.class, postIds);
			for(BbsPostEO post:posts){
				post.setRecordStatus(BbsPostEO.RecordStatus.Normal.toString());
				//恢复记录
				BbsLogUtil.saveSysLog(post.getPostId(), BbsLogEO.Operation.Audit.toString(),4);
			}
			bbsPostService.restore(posts,postIds);
		}
		return getObject();
	}

	/**
	 * 还原
	 * @param postIds
	 * @return
	 */
	@RequestMapping("move")
	@ResponseBody
	public Object move(Long[] postIds,Long plateId) {
		if(postIds !=null && postIds.length>0) {
			BbsPlateEO plate = CacheHandler.getEntity(BbsPlateEO.class,plateId);
			if(plate == null){
				throw new BaseRunTimeException(TipsMode.Message.toString(), "版本信息不存在");
			}
			bbsPostService.move(postIds,plate);
		}

		return getObject();
	}

	/**
	 *
	 * @param type 类型  0 发布  1 总固顶 2 固顶  3推荐 4锁定 5封贴
	 * @param status
	 * @param postIds
	 * @return
	 */
	@RequestMapping("setStatus")
	@ResponseBody
	public Object setStatus(Integer type,Integer status,Long[] postIds) {
		bbsPostService.updateStatus(type,status,postIds);
		return getObject();
	}

	@RequestMapping("getBbsType")
	@ResponseBody
	public Object getBbsType() {

		return getObject(DataDictionaryUtil.getDDList("bbs_type"));
	}
}
