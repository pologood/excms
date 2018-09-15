package cn.lonsun.rbac.controller;

import java.util.ArrayList;
import java.util.List;

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
import cn.lonsun.rbac.internal.entity.RoleRelationEO;
import cn.lonsun.rbac.internal.service.IRoleRelationService;

/**
 * 角色关系控制器
 *
 * @author xujh
 * @version 1.0
 * 2015年5月27日
 *
 */
@Controller
@RequestMapping("roleRelation")
public class RoleRelationController extends BaseController {
	
	@Autowired
	private IRoleRelationService roleRelationService;
	
	/**
	 * 角色关联关系页面
	 * 
	 * @return
	 */
	@RequestMapping("roleRelationPage")
	public String roleRelationPage(HttpServletRequest request,Long roleId){
		request.setAttribute("roleId", roleId);
		return "/app/mgr/role/role_relation_list";
	}
	
	/**
	 * 保存角色关系
	 *
	 * @param roleId
	 * @param targetRoleIds
	 * @param type
	 * @return
	 */
	@RequestMapping("saveRelations")
	@ResponseBody
	public Object saveRelations(Long roleId,String targetRoleIds,String type){
		if(roleId!=null&&!StringUtils.isEmpty(targetRoleIds)&&!StringUtils.isEmpty(type)){
			String[] array = targetRoleIds.split(",");
			List<Long> ids = new ArrayList<Long>(array.length);
			for(String element:array){
				ids.add(Long.valueOf(element));
			}
			roleRelationService.saveRelations(roleId, ids, type);
		}
		return getObject();
	}

	/**
	 * 删除关联关系
	 *
	 * @param roleRelationIds
	 * @return
	 */
	@RequestMapping("delete")
	@ResponseBody
	public Object delete(@RequestParam(value="ids[]")Long[] ids){
		if(ids==null||ids.length<=0){
			throw new BaseRunTimeException(TipsMode.Message.toString(), "请至少选择一条记录");
		}
		roleRelationService.delete(RoleRelationEO.class, ids);
		return getObject();
	}

	/**
	 * 获取分页列表信息
	 * 
	 * @param roleId
	 * @param query
	 * @return
	 */
	@RequestMapping("getPage")
	@ResponseBody
	public Object getPage(Long roleId,PageQueryVO query){
		return getObject(roleRelationService.getPage(roleId, query));
	}
}
