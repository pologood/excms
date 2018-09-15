package cn.lonsun.content.commentMgr.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.commentMgr.internal.entity.CommentEO;
import cn.lonsun.content.commentMgr.internal.service.ICommentService;
import cn.lonsun.content.commentMgr.vo.CommentPageVO;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.service.IBaseContentService;
import cn.lonsun.content.vo.CommentsInfoVO;
import cn.lonsun.content.vo.ContentPageVO;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.util.LoginPersonUtil;

@Controller
@RequestMapping(value="commentMgr", produces = { "application/json;charset=UTF-8" })
public class CommentController extends BaseController {
	
	@Autowired
	private IBaseContentService baseContentService;
	
	@Autowired
	private ICommentService commentService;
	@RequestMapping("index")
	public String index(){
		return "/content/comment/comment_list";
	}
	
	@RequestMapping("listPage")
	public String listPage(Long indicatorId,Long siteId,ModelMap map){
		map.put("columnId", indicatorId);
		map.put("siteId", siteId);
		return "/content/comment/comment_list";
	} 
	
	@RequestMapping("commentsPage")
	public String commentsPage(Long contentId,String title,ModelMap map){
		map.put("contentId", contentId);
		map.put("title", title);
		return "/content/comment/comments_page";
	}
	
	@RequestMapping("getCommentsByContentId")
	@ResponseBody
	public Object getCommentsByContentId(CommentPageVO pageVO){
		return getObject(commentService.getPage(pageVO));
	}
	
	@RequestMapping("formPage")
	public String formPage(){
		return "/content/comment/comment_page";
	}
	
	@RequestMapping("saveForm")
	@ResponseBody
	public Object saveForm(CommentEO commentEo){
		commentService.saveEntity(commentEo);
		return ajaxOk("提交成功");
	}
	
	
	@RequestMapping("getPage")
	@ResponseBody
	public Object getPage(CommentPageVO commentVO){
		if(AppUtil.isEmpty(commentVO.getSiteId())){
			commentVO.setSiteId(LoginPersonUtil.getSiteId());
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String dateStr = sdf.format(new Date());
		Date date = null;
		try {
			date = new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		} 
		Pagination page = commentService.getPage(commentVO);
		@SuppressWarnings("unchecked")
		List<CommentEO> list = (List<CommentEO>) page.getData();
		for(CommentEO l:list){
			if(l.getCreateDate().getTime()<=date.getTime()){
				l.setType(1);
			}else{
				l.setType(0);
			}
		}
		page.setData(list);
		return getObject(page);
	}
	
	@RequestMapping("changStatus")
	@ResponseBody
	public Object changStatus(Long id,Integer mark){
		commentService.changStatus(id, mark);
		return getObject();
	}
	
	@RequestMapping("batchChangePublish")
	@ResponseBody
	public Object batchChangePublish(@RequestParam(value="ids[]",required=false)Long[] ids,Integer status){
		commentService.updatePublish(ids, status);
		return getObject();
	}
	
	@RequestMapping("deletes")
	@ResponseBody
	public Object deletes(@RequestParam(value="ids[]",required=false)Long[] ids){
		commentService.delete(CommentEO.class, ids);
		return getObject();
	}
	
	@RequestMapping("getOpenCommentContent")
	@ResponseBody
	public Object getOpenCommentContent(ContentPageVO pageVO){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String dateStr = sdf.format(new Date());
		Date date = null;
		try {
			date = new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		} 
		Pagination page=baseContentService.getOpenCommentContent(pageVO);
		List<BaseContentEO> list=(List<BaseContentEO>) page.getData();
		List<CommentsInfoVO> _list=new ArrayList<CommentsInfoVO>();
		for(BaseContentEO li:list){
			IndicatorEO incat=CacheHandler.getEntity(IndicatorEO.class,li.getColumnId());
			li.setColumnName(incat.getName());
			CommentsInfoVO _vo=new CommentsInfoVO();
			try {
				BeanUtils.copyProperties(_vo, li);
			} catch (Exception e) {
				e.printStackTrace();
			}
			List<CommentEO> commentsArr = commentService.getContentComments(li.getId());
			//评论数为0 continue
			if(commentsArr.size()<=0) continue;
			_vo.setCommentsNum(commentsArr.size());
			Integer unPublishNum=0;
			Integer todayNum=0;
			if(commentsArr.size()>0){
				for(CommentEO arr:commentsArr){
					if(arr.getIsPublish()==0){
						unPublishNum++;
					}
					if(arr.getCreateDate().getTime()>date.getTime()){
						todayNum++;
					}
				}
			}
			_vo.setUnPublishNum(unPublishNum);
			_vo.setTodayCommentsNum(todayNum);
			
			_list.add(_vo);
		}
		page.setData(_list);
		return getObject(page);
	}
	
}
