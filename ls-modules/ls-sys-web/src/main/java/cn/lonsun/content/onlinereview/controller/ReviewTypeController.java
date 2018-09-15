package cn.lonsun.content.onlinereview.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.lonsun.content.onlinereview.internal.entity.ReviewTypeEO;
import cn.lonsun.content.onlinereview.internal.service.IReviewTypeService;
import cn.lonsun.content.onlinereview.vo.ReviewQueryVO;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.TipsMode;

@Controller
@RequestMapping(value = "reviewType", produces = { "application/json;charset=UTF-8" })
public class ReviewTypeController extends BaseController{
	
	@Autowired
	private IReviewTypeService reviewTypeService;
	
	
	@RequestMapping("list")
	public String list() {
		return "/content/onlinereview/reviewType_list";
	}
	
	@RequestMapping("edit")
	public String edit() {
		return "/content/onlinereview/reviewType_edit";
	}
	
	@RequestMapping("getPage")
	@ResponseBody
	public Object getPage(ReviewQueryVO query){

		if(query.getColumnId() == null || query.getSiteId() == null){
			throw new BaseRunTimeException(TipsMode.Message.toString(), "栏目或站点id不能为空");
		}
		// 页码与查询最多查询数据量纠正
		if (query.getPageIndex()==null||query.getPageIndex() < 0) {
			query.setPageIndex(0L);
		}
		Integer size = query.getPageSize();
		if (size==null||size <= 0 || size > Pagination.MAX_SIZE) {
			query.setPageSize(15);
		}
		Pagination page = reviewTypeService.getPage(query);

		return getObject(page);
	}
	
	@RequestMapping("getReviewTypes")
	@ResponseBody
	public Object getReviewTypes(Long columnId,Long siteId){
		return getObject(reviewTypeService.getReviewTypes(columnId,siteId));
	}

	
	@RequestMapping("save")
	@ResponseBody
	public Object save(ReviewTypeEO reviewType){
		if(reviewType.getTypeId() != null){
			reviewTypeService.updateEntity(reviewType);
		}else{
			reviewTypeService.saveEntity(reviewType);
		}
		return getObject();
	}
	
	@RequestMapping("getReviewType")
	@ResponseBody
	public Object getReviewType(Long typeId){
		ReviewTypeEO reviewType = null;
		if(typeId == null){
			reviewType = new ReviewTypeEO();
		}else{
			reviewType = reviewTypeService.getEntity(ReviewTypeEO.class,typeId);
		}
		return getObject(reviewType);
	}


	@RequestMapping("delete")
	@ResponseBody
	public Object delete(@RequestParam("ids") Long[] ids) {
		try{
			if(ids != null && ids.length >0){
				reviewTypeService.delete(ReviewTypeEO.class,ids);
			}
		}catch(Exception e){
			throw new BaseRunTimeException();
		}
		return getObject();
	}

}
