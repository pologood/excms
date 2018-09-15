package cn.lonsun.content.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.service.IBaseContentService;
import cn.lonsun.content.internal.service.IContentPicService;
import cn.lonsun.content.internal.service.IGuestBookService;
import cn.lonsun.content.internal.service.IVideoNewsService;
import cn.lonsun.content.vo.GuestBookVO;
import cn.lonsun.content.vo.UnAuditContentsVO;
import cn.lonsun.content.vo.VideoNewsVO;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.indicator.internal.entity.FunctionEO;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.mongodb.entity.ContentMongoEO;
import cn.lonsun.mongodb.service.ContentMongoServiceImpl;
import cn.lonsun.site.site.internal.entity.ColumnMgrEO;
import cn.lonsun.site.site.internal.service.IColumnConfigService;
import cn.lonsun.system.role.internal.service.ISiteRightsService;
import cn.lonsun.util.LoginPersonUtil;
/**
 * 
* @ClassName: RecycleBinController
* @Description: 回收站
* @author hujun
* @date 2015年12月12日 下午2:32:32
*
 */

@Controller
@RequestMapping(value="recycleBin")
public class RecycleBinController extends BaseController{
	@Autowired
    private IVideoNewsService videoService;

	@Autowired
	private ISiteRightsService siteRightsService;
	@Autowired
	private IColumnConfigService columnConfigService;
	@Autowired
	private IBaseContentService baseontentService;
	@Autowired
	private ContentMongoServiceImpl contentMongoService;
	@Autowired
	private IGuestBookService guestBookService;
	@Autowired
	private IContentPicService contentPicService;
	
	@RequestMapping("index")
	public String index(){
		return "/content/recyclebin/recycle_content_index";
	}
	
	/*@RequestMapping("getPage")
	@ResponseBody
	public Object getPage(GuestBookPageVO pageVO){
		return getObject(guestBookService.getRecycleBinPage(pageVO));
		
	}*/
	
	
	//回收站列表查询
	@RequestMapping("getRecycleContentPage")
	@ResponseBody
	public Object getUnAuditContents(UnAuditContentsVO uaVO,HttpServletRequest request){
		if(LoginPersonUtil.isSiteAdmin()||LoginPersonUtil.isSuperAdmin()){
			uaVO.setSiteId((Long)request.getSession().getAttribute("siteId"));
			uaVO.setColumnIds(null);
		}else{
			List<Long> ids=new ArrayList<Long>();
			uaVO.setSiteId((Long)request.getSession().getAttribute("siteId"));
			List<ColumnMgrEO> columns = columnConfigService.getTree((Long)request.getSession().getAttribute("siteId"));
			List<ColumnMgrEO>  myColumns= siteRightsService.getCurUserColumnOpt(columns);
			for(ColumnMgrEO myColumn:myColumns){
				List<FunctionEO> functions = myColumn.getFunctions();
				for(FunctionEO function :functions){
					if("publish".equals(function.getAction())){
						ids.add(myColumn.getIndicatorId());
					}
				}
			}
			uaVO.setColumnIds(ids.toArray(new Long[ids.size()]));
		}
		//获取回收站内容分页
		 Pagination p=baseontentService.getRecycleContentPage(uaVO);
		 @SuppressWarnings("unchecked")
		List<BaseContentEO> list = (List<BaseContentEO>) p.getData();
		 for(BaseContentEO eo:list){
			 if(eo.getColumnId()!=null&&CacheHandler.getEntity(IndicatorEO.class, eo.getColumnId())!=null){
				 eo.setColumnName(CacheHandler.getEntity(IndicatorEO.class, eo.getColumnId()).getName());
			 }
		 }
		 
		return getObject(p);
		}
	
	//回收站详情
	@RequestMapping("details")
	@ResponseBody
	public ModelAndView details(Long id,String typeCode){
		if("guestBook".equals(typeCode)){
			GuestBookVO vo = (GuestBookVO) guestBookService.queryRemoved(id);
			ModelAndView mv = new ModelAndView("/content/recyclebin/guestbook_edit");
			mv.addObject("typeCode", typeCode);
			mv.addObject("vo", vo);
			return mv;
		}else if("articleNews".equals(typeCode)){
			ModelAndView mv = new ModelAndView("/content/recyclebin/article_news_details"); 
			BaseContentEO eo = baseontentService.getRemoved(id);
			Criteria criteria = Criteria.where("_id").is(id);
			Query query=new Query(criteria);
			ContentMongoEO _eo = contentMongoService.queryOne(query);
			mv.addObject("eo", eo);
			String content="";
			if(!AppUtil.isEmpty(_eo)){
				content=_eo.getContent();
			}
			mv.addObject("content", content);
			return mv;
		}else if("pictureNews".equals(typeCode)){
			ModelAndView mv = new ModelAndView("/content/recyclebin/picture_news_details");
			BaseContentEO eo = baseontentService.getRemoved(id);
			Criteria criteria = Criteria.where("_id").is(id);
			Query query=new Query(criteria);
			ContentMongoEO _eo = contentMongoService.queryOne(query);
			mv.addObject("eo", eo);
			String content="";
			if(!AppUtil.isEmpty(_eo)){
				content=_eo.getContent();
			}
			mv.addObject("content", content);
			mv.addObject("picList", contentPicService.getPicsList(id));
			return mv;
		}else if("videoNews".equals(typeCode)){
			ModelAndView mv = new ModelAndView("/content/recyclebin/video_news_details");
			BaseContentEO eo = baseontentService.getRemoved(id);
			VideoNewsVO vo = videoService.getRemovedVideo(id);
			mv.addObject("vo", vo);
			mv.addObject("eo", eo);
			return mv;
		}
		return null;
	}
	
	//恢复留言
	@RequestMapping("recovery")
	@ResponseBody
	public Object recovery(Long id){
		guestBookService.recovery(id);
		return getObject();
	}
	
	//物理删除留言(彻底删除)
	@RequestMapping("completelyDelete")
	@ResponseBody
	public Object completelyDelete(Long id){
		guestBookService.completelyDelete(id);
		return getObject();
	}
	
	//批量恢复
	@RequestMapping("batchRecovery")
	@ResponseBody
	public Object batchRecovery(@RequestParam(value="ids[]",required=false)Long[] ids){
		guestBookService.batchRecovery(ids);
		return getObject();
	}
	
	//批量物理删除
	@RequestMapping("batchCompletelyDelete")
	@ResponseBody
	public Object batchCompletelyDelete(@RequestParam(value="ids[]",required=false)Long[] ids){
		guestBookService.batchCompletelyDelete(ids);
		return getObject();
	}
	
	
		

}
