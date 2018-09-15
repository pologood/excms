package cn.lonsun.content.ideacollect.controller;


import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.lonsun.content.ideacollect.internal.entity.CollectIdeaEO;
import cn.lonsun.content.ideacollect.internal.service.ICollectIdeaService;
import cn.lonsun.content.ideacollect.vo.IdeaQueryVO;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.RequestUtil;
import cn.lonsun.core.util.TipsMode;


@Controller
@RequestMapping(value = "collectIdea", produces = { "application/json;charset=UTF-8" })
public class CollectIdeaController extends BaseController{

	@Autowired
	private ICollectIdeaService collectIdeaService;

	@RequestMapping("list")
	public String list(Long collectInfoId,Model m) {
		m.addAttribute("collectInfoId", collectInfoId);
		return "/content/ideacollect/collectIdea_list";
	}

	@RequestMapping("view")
	public String view(Long ideaId,Model m) {

		CollectIdeaEO collectIdea = null;
		if(ideaId == null){
			collectIdea = new CollectIdeaEO();
		}else{
			collectIdea = collectIdeaService.getEntity(CollectIdeaEO.class,ideaId);
		}
		m.addAttribute("ideaId", ideaId);
		m.addAttribute("collectIdea", collectIdea);
		return "/content/ideacollect/collectIdea_view";
	}

	@RequestMapping("edit")
	public String edit(Long ideaId,Model m) {
		m.addAttribute("ideaId", ideaId);
		return "/content/ideacollect/collectIdea_edit";
	}

	@RequestMapping("getPage")
	@ResponseBody
	public Object getPage(IdeaQueryVO query){

		if(query.getCollectInfoId() == null){
			throw new BaseRunTimeException(TipsMode.Message.toString(), "collectInfoId不能为空");
		}

		if (query.getPageIndex()==null||query.getPageIndex() < 0) {
			query.setPageIndex(0L);
		}
		Integer size = query.getPageSize();
		if (size==null||size <= 0 || size > Pagination.MAX_SIZE) {
			query.setPageSize(15);
		}
		Pagination page = collectIdeaService.getPage(query);

		return getObject(page);
	}


	@RequestMapping("save")
	@ResponseBody
	public Object save(CollectIdeaEO collectIdea,HttpServletRequest request){

		if(collectIdea.getCollectIdeaId() != null){
			collectIdeaService.updateEntity(collectIdea);
		}else{
			collectIdea.setIp(RequestUtil.getIpAddr(request));
			collectIdeaService.save(collectIdea);
		}
		return getObject();
	}


	@RequestMapping("updateIssued")
	@ResponseBody
	public Object updateIssued(@RequestParam("ids") Long[] ids,
							   Integer status) {
		if(ids !=null && ids.length >0){
			for(Long id:ids){
				CollectIdeaEO collectIdea = collectIdeaService.getEntity(CollectIdeaEO.class, id);
				if(collectIdea!= null){
					if(collectIdea.getIsIssued() != status){
						if(status == 1){
							collectIdea.setIssuedTime(new Date());
						}else{
							collectIdea.setIssuedTime(null);
						}
						collectIdea.setIsIssued(status);
						collectIdeaService.updateEntity(collectIdea);
					}
				}
			}
		}
		return getObject();
	}


	@RequestMapping("getCollectIdea")
	@ResponseBody
	public Object getCollectIdea(Long collectIdeaId){
		CollectIdeaEO collectIdea = null;
		if(collectIdeaId == null){
			collectIdea = new CollectIdeaEO();
		}else{
			collectIdea = collectIdeaService.getEntity(CollectIdeaEO.class,collectIdeaId);
		}
		return getObject(collectIdea);
	}


	@RequestMapping("delete")
	@ResponseBody
	public Object delete(@RequestParam("ids") Long[] ids,Long collectInfoId) {
		try{
			if(collectInfoId == null){
				throw new BaseRunTimeException(TipsMode.Message.toString(), "collectInfoId不能为空");
			}

			if(ids != null && ids.length >0){
				collectIdeaService.delete(ids,collectInfoId);
			}
		}catch(Exception e){
			throw new BaseRunTimeException();
		}
		return getObject();
	}

}
