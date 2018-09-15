package cn.lonsun.content.ideacollect.controller;


import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.lonsun.activemq.MessageSenderUtil;
import cn.lonsun.content.leaderwin.vo.LeaderInfoVO;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.solr.SolrFactory;
import cn.lonsun.solr.vo.SolrIndexVO;
import cn.lonsun.system.systemlog.internal.entity.CmsLogEO;
import cn.lonsun.util.HtmlUtil;
import cn.lonsun.util.SysLog;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.lonsun.activemq.MessageSender;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.content.ideacollect.internal.entity.CollectInfoEO;
import cn.lonsun.content.ideacollect.internal.service.ICollectInfoService;
import cn.lonsun.content.ideacollect.vo.CollectInfoVO;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.service.IBaseContentService;
import cn.lonsun.content.vo.QueryVO;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.message.internal.entity.MessageEnum;
import cn.lonsun.message.internal.entity.MessageStaticEO;
import cn.lonsun.util.FileUploadUtil;
import cn.lonsun.util.TimeOutUtil;

import static cn.lonsun.cache.client.CacheHandler.getEntity;

@Controller
@RequestMapping(value = "collectInfo", produces = { "application/json;charset=UTF-8" })
public class CollectInfoController extends BaseController{

	@Autowired
	private ICollectInfoService collectInfoService;

	@Autowired
	private IBaseContentService baseContentService;


	@Autowired
	private TaskExecutor taskExecutor;

	@RequestMapping("index")
	public String index() {
		return "/content/ideacollect/collectInfo_list";
	}

	@RequestMapping("edit")
	public String edit(Long infoId,Model m) {
		m.addAttribute("infoId", infoId);
		return "/content/ideacollect/collectInfo_edit";
	}


	@SuppressWarnings("unchecked")
	@RequestMapping("getPage")
	@ResponseBody
	public Object getPage(QueryVO query){
		if(query.getColumnId() == null || query.getSiteId() == null){
			throw new BaseRunTimeException(TipsMode.Message.toString(), "栏目或站点id不能为空");
		}
		if (query.getPageIndex()==null||query.getPageIndex() < 0) {
			query.setPageIndex(0L);
		}
		Integer size = query.getPageSize();
		if (size==null||size <= 0 || size > Pagination.MAX_SIZE) {
			query.setPageSize(15);
		}
		query.setTypeCode(BaseContentEO.TypeCode.collectInfo.toString());
		Pagination page = collectInfoService.getPage(query);
		if(page.getData() !=null && page.getData().size()>0){
			for(CollectInfoVO collectInfo:(List<CollectInfoVO>) page.getData()){
				if(collectInfo.getStartTime() != null && collectInfo.getEndTime() !=null){
					collectInfo.setIsTimeOut(TimeOutUtil.getTimeOut(collectInfo.getStartTime(), collectInfo.getEndTime()));
				}
			}
		}
		return getObject(page);
	}


	@RequestMapping("save")
	@ResponseBody
	public Object save(CollectInfoVO collectInfoVO){
		final BaseContentEO baseContentEO=collectInfoService.save(collectInfoVO);
		if(collectInfoVO.getIsIssued()!=null&&collectInfoVO.getIsIssued()==1){
			MessageSender.sendMessage(new MessageStaticEO(baseContentEO.getSiteId(),baseContentEO.getColumnId(),new Long[]{baseContentEO.getId()}).setType(MessageEnum.PUBLISH.value()));
		}
		return getObject();
	}

	@RequestMapping("getCollectInfo")
	@ResponseBody
	public Object getCollectInfo(Long infoId,Long siteId,Long columnId){
		if(siteId == null || columnId == null){
			throw new BaseRunTimeException(TipsMode.Message.toString(), "栏目或站点id不能为空");
		}
		CollectInfoVO collectInfo = null;
		if(infoId == null){
			collectInfo = new CollectInfoVO();
			Long sortNum = baseContentService.getMaxSortNum(siteId,columnId,BaseContentEO.TypeCode.collectInfo.toString());
			if (sortNum == null) {
				sortNum = 2L;
			} else {
				sortNum = sortNum + 2;
			}
			collectInfo.setSortNum(sortNum);
		}else{
			collectInfo = collectInfoService.getCollectInfoVO(infoId);
		}
		return getObject(collectInfo);
	}


	@RequestMapping("delete")
	@ResponseBody
	public Object delete(@RequestParam("ids") Long[] ids,@RequestParam("contentIds") Long[] contentIds) {
		if(ids==null||ids.length<=0){
			throw new BaseRunTimeException(TipsMode.Message.toString(), "请选择要删除的留言！");
		}
		// 批量删除主表（假删）
		List<BaseContentEO> list = baseContentService.getEntities(BaseContentEO.class, contentIds);
		if (list != null && list.size() > 0) {
			Integer isPublish=0;
			for(BaseContentEO contentEO:list){
				if(contentEO!=null&&contentEO.getIsPublish()!=null&&contentEO.getIsPublish().intValue()==1){
					isPublish=1;
					break;
				}
				//添加操作日志
				if(list.size()>1){
					SysLog.log("民意征集：批量删除内容（" + contentEO.getTitle()+"）", "CollectInfoEO",
							CmsLogEO.Operation.Update.toString());
				}else{
					SysLog.log("民意征集：删除内容（" + contentEO.getTitle()+"）", "CollectInfoEO",
							CmsLogEO.Operation.Update.toString());
				}
			}
			BaseContentEO baseContentEO=getEntity(BaseContentEO.class,contentIds[0]);
			collectInfoService.delete(ids,contentIds);
			if(isPublish!=null&&isPublish.intValue()==1){
				MessageSenderUtil.publishContent(
						new MessageStaticEO(baseContentEO.getSiteId(), baseContentEO.getColumnId(), contentIds).setType(MessageEnum.UNPUBLISH.value()), 2);
			}
		}
		return getObject();
	}

	@RequestMapping("updateSort")
	@ResponseBody
	public Object updateSort(Long columnId,Long siteId){
		Long sortNum = 2L;
		List<BaseContentEO> cis = baseContentService.getBaseContents(siteId,columnId);
		for (BaseContentEO content:cis) {
			if(content != null){
				content.setNum(sortNum);
				baseContentService.updateEntity(content);
				sortNum = sortNum+2;
			}
		}
		return getObject();
	}

}
