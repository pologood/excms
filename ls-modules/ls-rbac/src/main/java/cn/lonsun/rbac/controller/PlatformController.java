package cn.lonsun.rbac.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.lonsun.common.vo.PageQueryVO;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.rbac.internal.entity.PlatformEO;
import cn.lonsun.rbac.internal.service.IPlatformService;

/**
 * 平台管理控制器
 * 
 * @author xujh
 * @version 1.0 2015年4月24日
 * 
 */
@Controller
@RequestMapping("platform")
public class PlatformController extends BaseController {
	
	@Autowired
	private IPlatformService platformService;
	
	/**
	 * 编辑页面
	 *
	 * @param id
	 * @return
	 */
	@RequestMapping("listPage")
	public String listPage(){
		return "/app/mgr/developer/platform_list";
	}
	
	/**
	 * 编辑页面
	 *
	 * @param request
	 * @param id
	 * @return
	 */
	@RequestMapping("editPage")
	public String editPage(HttpServletRequest request,Long id){
		if(id!=null&&id>0){
			request.setAttribute("id", id);
		}
		return "/app/mgr/developer/platform_edit";
	}
	
	/**
	 * 获取空的PlatformEO
	 *
	 * @return
	 */
	@RequestMapping("getEmptyPlatform")
	@ResponseBody
	public Object getEmptyPlatform(){
		return getObject(new PlatformEO());
	}
	
	/**
	 * 根据主键获取对象
	 *
	 * @param id
	 * @return
	 */
	@RequestMapping("getPlatform")
	@ResponseBody
	public Object getPlatform(Long id){
		return getObject(platformService.getEntity(PlatformEO.class, id));
	}

	/**
	 * 保存
	 * 
	 * @param platform
	 * @return
	 */
	@RequestMapping("save")
	@ResponseBody
	public Object save(PlatformEO platform) {
		// 参数验证，除了描述-description，其余都不能为空，且code不能重复
		String code = platform.getCode();
		//code由数字和英文字母组成，如果存在特殊字符，可能对前台产生影响
		
		String name = platform.getName();
		String webserviceUrl = platform.getWebserviceUrl();
		String namespace = platform.getNameSpace();
		String method = platform.getMethod();
		if (StringUtils.isEmpty(code) || StringUtils.isEmpty(name)
				|| StringUtils.isEmpty(webserviceUrl)
				|| StringUtils.isEmpty(namespace)
				|| StringUtils.isEmpty(method)) {
			throw new BaseRunTimeException(TipsMode.Message.toString(), "请输入完整的信息");
		}
		platformService.save(platform);
		return getObject();
	}
	
	/**
	 * 更新
	 * 
	 * @param platform
	 * @return
	 */
	@RequestMapping("update")
	@ResponseBody
	public Object update(PlatformEO platform) {
		// 参数验证，除了描述-description，其余都不能为空，且code不能重复
		String code = platform.getCode();
		String name = platform.getName();
		String webserviceUrl = platform.getWebserviceUrl();
		String namespace = platform.getNameSpace();
		String method = platform.getMethod();
		if (StringUtils.isEmpty(code) || StringUtils.isEmpty(name)
				|| StringUtils.isEmpty(webserviceUrl)
				|| StringUtils.isEmpty(namespace)
				|| StringUtils.isEmpty(method)) {
			throw new BaseRunTimeException(TipsMode.Message.toString(), "请输入完整的信息");
		}
		platformService.update(platform);
		return getObject();
	}
	
	/**
	 * 删除
	 * 
	 * @param ids
	 * @return
	 */
	@RequestMapping("delete")
	@ResponseBody
	public Object delete(@RequestParam("ids[]")Long[] ids){
		platformService.delete(PlatformEO.class, ids);
		return getObject();
	}
	
	/**
	 * 获取分页列表
	 * 
	 * @param query
	 * @return
	 */
	@RequestMapping("getPagination")
	@ResponseBody
	public Object getPagination(PageQueryVO query){
		return getObject(platformService.getPagination(query));
	}

}
