package cn.lonsun.content.leaderwin.controller;



import java.util.Date;
import java.util.List;
import java.util.Map;

import cn.lonsun.activemq.MessageSenderUtil;
import cn.lonsun.solr.SolrFactory;
import cn.lonsun.solr.vo.SolrIndexVO;
import cn.lonsun.util.HtmlUtil;
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
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.service.IBaseContentService;
import cn.lonsun.content.leaderwin.internal.entity.LeaderInfoEO;
import cn.lonsun.content.leaderwin.internal.service.ILeaderInfoService;
import cn.lonsun.content.leaderwin.internal.service.ILeaderTypeService;
import cn.lonsun.content.leaderwin.vo.LeaderInfoVO;
import cn.lonsun.content.vo.QueryVO;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.message.internal.entity.MessageEnum;
import cn.lonsun.message.internal.entity.MessageStaticEO;
import cn.lonsun.util.FileUploadUtil;

import static cn.lonsun.cache.client.CacheHandler.getEntity;


@Controller
@RequestMapping(value = "leaderInfo", produces = { "application/json;charset=UTF-8" })
public class LeaderInfoController extends BaseController{


	@Autowired
	private ILeaderInfoService leaderInfoService;

	@Autowired
	private ILeaderTypeService leaderTypeService;


	@Autowired
	private IBaseContentService baseContentService;


	@RequestMapping("index")
	public String index() {
		return "/content/leaderwin/leaderInfo_list";
	}

	@RequestMapping("edit")
	public String edit(Long infoId,Model m) {
		m.addAttribute("infoId", infoId);
		return "/content/leaderwin/leaderInfo_edit";
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
		query.setTypeCode(BaseContentEO.TypeCode.leaderInfo.toString());
		Pagination page = leaderInfoService.getPage(query);

		if(page.getData() !=null && page.getData().size()>0){
			Map<Long,String> mapType = leaderTypeService.getMap(query.getSiteId(),query.getColumnId());
			for(LeaderInfoVO leaderInfo:(List<LeaderInfoVO>) page.getData()){
				if(mapType != null){
					String typeName = mapType.get(leaderInfo.getLeaderTypeId());
					leaderInfo.setTypeName(typeName == null ? "":typeName);
				}
			}
		}
		return getObject(page);
	}


	@RequestMapping("save")
	@ResponseBody
	public Object save(LeaderInfoVO leaderInfoVO){
		BaseContentEO contentEO=leaderInfoService.save(leaderInfoVO);
		if(leaderInfoVO.getIssued()!=null&&leaderInfoVO.getIssued()==1){
			//发布
			MessageSender.sendMessage(new MessageStaticEO(contentEO.getSiteId(), contentEO.getColumnId(), new Long[]{contentEO.getId()}).setType(MessageEnum.PUBLISH.value()));
		}
		return getObject();
	}


	@RequestMapping("getLeaderInfo")
	@ResponseBody
	public Object getLeaderInfo(Long infoId,Long siteId,Long columnId){
		LeaderInfoVO leaderInfo = null;
		if(infoId == null){
			if(siteId == null || columnId == null){
				throw new BaseRunTimeException(TipsMode.Message.toString(), "栏目或站点id不能为空");
			}
			leaderInfo = new LeaderInfoVO();
			Long sortNum = baseContentService.getMaxSortNum(siteId,columnId,BaseContentEO.TypeCode.leaderInfo.toString());
			if (sortNum == null) {
				sortNum = 2L;
			} else {
				sortNum = sortNum + 2;
			}
			leaderInfo.setSortNum(sortNum);
		}else{
			leaderInfo = leaderInfoService.getLeaderInfoVO(infoId);
		}
		return getObject(leaderInfo);
	}

	@RequestMapping("delete")
	@ResponseBody
	public Object delete(@RequestParam("ids") Long[] ids,@RequestParam("contentIds") Long[] contentIds) {
		if (ids == null || ids.length < 1) {
			throw new BaseRunTimeException(TipsMode.Message.toString(), "请选择要删除的项！");
		}
		List<BaseContentEO> list = baseContentService.getEntities(BaseContentEO.class, contentIds);
		if (list != null && list.size() > 0) {
			Integer isPublish = 0;
			for (BaseContentEO contentEO : list) {
				if (contentEO != null && contentEO.getIsPublish() != null && contentEO.getIsPublish().intValue() == 1) {
					isPublish = 1;
					break;
				}
			}
			BaseContentEO baseContentEO=getEntity(BaseContentEO.class,contentIds[0]);
			leaderInfoService.delete(ids,contentIds);
			if(isPublish!=null&&isPublish.intValue()==1){
				MessageSenderUtil.publishContent(
						new MessageStaticEO(baseContentEO.getSiteId(), baseContentEO.getColumnId(),ids).setType(MessageEnum.UNPUBLISH.value()), 2);
			}
		}
		return getObject();
	}

}




