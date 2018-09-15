package cn.lonsun.content.onlinereview.controller;



import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.lonsun.content.onlinereview.internal.entity.ReviewObjectEO;
import cn.lonsun.content.onlinereview.internal.service.IReviewObjectService;
import cn.lonsun.content.onlinereview.vo.ReviewQueryVO;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.TipsMode;

@Controller
@RequestMapping(value = "reviewObject", produces = { "application/json;charset=UTF-8" })
public class ReviewObjectController extends BaseController{

	@Autowired
	private IReviewObjectService reviewObjectService;

	@RequestMapping("list")
	public String list() {
		return "/content/onlinereview/reviewObject_list";
	}

	@RequestMapping("edit")
	public String edit() {
		return "/content/onlinereview/reviewObject_edit";
	}

	@RequestMapping("addOrgans")
	public String addOrgans() {
		return "/content/onlinereview/addOrgans";
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
		Pagination page = reviewObjectService.getPage(query);

		return getObject(page);
	}
	
	@RequestMapping("getReviewObjects")
	@ResponseBody
	public Object getReviewObjects(Long columnId,Long siteId){
		return getObject(reviewObjectService.getReviewObjects(columnId,siteId));
	}

	@RequestMapping("save")
	@ResponseBody
	public Object save(ReviewObjectEO reviewObject){
		if(reviewObject.getObjectId() != null){
			reviewObjectService.updateEntity(reviewObject);
		}else{
			if(!StringUtils.isEmpty(reviewObject.getName())){
				reviewObjectService.saveEntity(reviewObject);
			}
			List<ReviewObjectEO> objects = null;
			if(!StringUtils.isEmpty(reviewObject.getOrganNames())){
				String[] organNames = reviewObject.getOrganNames().split(",");
				if(organNames !=null && organNames.length >0){
					objects = new ArrayList<ReviewObjectEO>();
					for(String name:organNames){
						ReviewObjectEO ro = new ReviewObjectEO();
						ro.setColumnId(reviewObject.getColumnId());
						ro.setSiteId(reviewObject.getSiteId());
						ro.setName(name);
						ro.setContent(reviewObject.getContent());
						objects.add(ro);
					}
				}
			}
			if(objects !=null && objects.size()>0){
				reviewObjectService.saveEntities(objects);
			}
		}
		return getObject();
	}

	@RequestMapping("getReviewObject")
	@ResponseBody
	public Object getReviewObject(Long objectId){
		ReviewObjectEO reviewObject = null;
		if(objectId == null){
			reviewObject = new ReviewObjectEO();
		}else{
			reviewObject = reviewObjectService.getEntity(ReviewObjectEO.class,objectId);
		}
		return getObject(reviewObject);
	}

	@RequestMapping("updateShow")
	@ResponseBody
	public Object updateShow(@RequestParam("ids") Long[] ids,
			Integer status) {
		if(ids !=null && ids.length >0){
			for(Long id:ids){
				ReviewObjectEO reviewObject = reviewObjectService.getEntity(ReviewObjectEO.class, id);
				if(reviewObject!= null){
					if(reviewObject.getIsShow() != status){
						if(status == 1){
							reviewObject.setShowTime(new Date());
						}else{
							reviewObject.setShowTime(null);
						}
						reviewObject.setIsShow(status);
						reviewObjectService.updateEntity(reviewObject);
					}
				}
			}
		}
		return getObject();
	}


	@RequestMapping("delete")
	@ResponseBody
	public Object delete(@RequestParam("ids") Long[] ids) {
		try{
			if(ids != null && ids.length >0){
				reviewObjectService.delete(ReviewObjectEO.class,ids);
			}
		}catch(Exception e){
			throw new BaseRunTimeException();
		}
		return getObject();
	}

}
