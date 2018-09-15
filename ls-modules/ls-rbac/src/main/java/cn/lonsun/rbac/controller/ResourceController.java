package cn.lonsun.rbac.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.rbac.internal.entity.ResourceEO;
import cn.lonsun.rbac.internal.service.IResourceService;

/**
 * 资源管理控制器
* @Description: 
* @author xujh 
* @date 2014年9月23日 下午10:02:50
* @version V1.0
 */
@Controller
@RequestMapping(value="resource", produces = { "application/json;charset=UTF-8" })
public class ResourceController extends BaseController {
	
	@Autowired
	private IResourceService resourceService;
	
	@RequestMapping("getResource")
	@ResponseBody
	public Object getResource(Long resourceId,Long indicatorId){
		ResourceEO resource = null;
		if(resourceId==null||resourceId<=0){
			resource = new ResourceEO();
			resource.setIndicatorId(indicatorId);
		}else{
			resource = resourceService.getEntity(ResourceEO.class, resourceId);
		}
		return getObject(resource);
	}
	
	public Object getResources(Long indicatorId){
		if(indicatorId==null||indicatorId<=0){
			throw new NullPointerException();
		}
		
		return getObject();
	}
	
	/**
	 * 保存资源
	 * @param resource
	 * @return
	 */
	public Object save(ResourceEO resource){
		return getObject();
	}

}
