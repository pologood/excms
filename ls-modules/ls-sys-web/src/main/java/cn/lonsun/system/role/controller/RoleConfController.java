package cn.lonsun.system.role.controller;

import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.exception.BusinessException;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.rbac.internal.entity.OrganEO;
import cn.lonsun.rbac.internal.entity.RoleAssignmentEO;
import cn.lonsun.rbac.internal.entity.RoleEO;
import cn.lonsun.rbac.internal.service.IOrganService;
import cn.lonsun.rbac.internal.service.IRoleAssignmentService;
import cn.lonsun.rbac.internal.service.IRoleService;
import cn.lonsun.rbac.vo.RoleNodeVO;
import cn.lonsun.site.template.util.ResponseData;
import cn.lonsun.system.systemlog.internal.entity.CmsLogEO;
import cn.lonsun.util.LoginPersonUtil;
import cn.lonsun.util.SysLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author gu.fei
 * @version 2015-11-5 8:43
 */
@Controller
@RequestMapping("/role/conf")
public class RoleConfController extends BaseController {

    @Resource
    private IRoleService roleService;

    @Autowired
    private IRoleAssignmentService roleAssignmentService;

    @Autowired
    private IOrganService organService;

    @RequestMapping("/addOrEdit")
    public ModelAndView addOrEdit(String node, String type) {
        ModelAndView model = new ModelAndView("system/role/role_edit");
        model.addObject("node",node);
        model.addObject("type",type);
        return model;
    }

    @ResponseBody
    @RequestMapping("/saveAllRights")
    public Object saveAllRights(Long roleId,String menurs,String siters,String pinfors) {
        return null;
    }

    @ResponseBody
    @RequestMapping("/save")
    public Object save(RoleEO roleEO) {
        Long siteId = LoginPersonUtil.getSiteId();
        roleEO.setSiteId(siteId);
        Long id = this.roleService.saveEntity(roleEO);
        RoleEO role = roleService.getEntity(RoleEO.class,id);
        role.setCode(id + "_code");
        roleService.updateEntity(role);
        List<RoleAssignmentEO> assign = roleAssignmentService.getRoleAssignments(role.getRoleId());
        if(assign != null){
            for(RoleAssignmentEO eo : assign){
                eo.setRoleType(role.getType());
            }
            roleAssignmentService.saveEntities(assign);
        }
        SysLog.log("【系统管理】添加角色：" + roleEO.getName(), "RoleEO", CmsLogEO.Operation.Add.toString());
        return ResponseData.success(roleEO, "保存成功");
    }

    @ResponseBody
    @RequestMapping("/update")
    public Object update(RoleEO roleEO) {
        Long siteId = LoginPersonUtil.getSiteId();
        RoleEO role = roleService.getEntity(RoleEO.class,roleEO.getRoleId());
        role.setSiteId(siteId);
        role.setName(roleEO.getName());
        role.setCode(roleEO.getCode());
        role.setDescription(roleEO.getDescription());
        role.setScope(roleEO.getScope());
        role.setType(roleEO.getType());
        this.roleService.updateEntity(role);
        List<RoleAssignmentEO> assign = roleAssignmentService.getRoleAssignments(role.getRoleId());
        if(assign != null){
            for(RoleAssignmentEO eo : assign){
                eo.setRoleType(role.getType());
            }
            roleAssignmentService.saveEntities(assign);
        }
        SysLog.log("【系统管理】更新角色：" + roleEO.getName(), "RoleEO", CmsLogEO.Operation.Update.toString());
        return ResponseData.success(roleEO,"更新成功");
    }

    @ResponseBody
    @RequestMapping("/delete")
    public Object delete(Long roleId) {
        try {
            RoleEO role = roleService.getEntity(RoleEO.class,roleId);
            String roleName = role.getName();
            this.roleService.delete(roleId);
            SysLog.log("【系统管理】删除角色：" + roleName, "RoleEO", CmsLogEO.Operation.Delete.toString());
        } catch (BusinessException e) {
            e.printStackTrace();
            throw new BaseRunTimeException(TipsMode.Key.toString(), e.getKey());
        }
        return getObject();
    }

    @ResponseBody
    @RequestMapping("getTreeByScope")
    public Object getTreeByScope(String scope,String roleIds) {
        boolean root = LoginPersonUtil.isRoot();
        boolean superAdmin = LoginPersonUtil.isSuperAdmin();
        boolean siteAdmin = LoginPersonUtil.isSiteAdmin();

        List<RoleNodeVO> roles = new ArrayList<RoleNodeVO>();
        if (root) {
            RoleEO role = roleService.getRoleByCode("super_admin");
            RoleNodeVO vo = new RoleNodeVO();
            vo.setId(role.getRoleId());
            vo.setName(role.getName());
            vo.setIsParent(false);
            vo.setHasChildren(false);
            vo.setPid((long)-1);
            vo.setType(role.getType());

            vo.setRoleId(role.getRoleId());
            vo.setCode(role.getCode());
            vo.setDescription(role.getDescription());
            roles.add(vo);
            //return roles;
        } else if(superAdmin) {
            RoleEO role = roleService.getRoleByCode("site_admin");
            RoleNodeVO vo = new RoleNodeVO();
            vo.setId(role.getRoleId());
            vo.setName(role.getName());
            vo.setIsParent(false);
            vo.setHasChildren(false);
            vo.setPid((long)-1);
            vo.setType(role.getType());

            vo.setRoleId(role.getRoleId());
            vo.setCode(role.getCode());
            vo.setDescription(role.getDescription());
            roles.add(vo);
        } else if(siteAdmin) {
            Long siteId = LoginPersonUtil.getSiteId();
            List<RoleEO> roleEOs = roleService.getRolesBySiteId(siteId,LoginPersonUtil.getOrganId());
            if(null != roleEOs) {
                Set<Long> set = new HashSet<Long>();

                for (RoleEO role : roleEOs) {
                    RoleNodeVO vo = new RoleNodeVO();
                    vo.setId(role.getRoleId());
                    vo.setName(role.getName());
                    vo.setIsParent(false);
                    vo.setHasChildren(false);
                    OrganEO organEO = organService.getDirectlyUpLevelUnit(role.getCreateOrganId());
                    if(null == organEO) {
                        vo.setPid(-1L);
                    } else {
                        vo.setPid(role.getCreateOrganId());
                        set.add(role.getCreateOrganId());
                    }
                    vo.setType(role.getType());

                    vo.setRoleId(role.getRoleId());
                    vo.setCode(role.getCode());
                    vo.setDescription(role.getDescription());

                    roles.add(vo);
                }
                for(Long l:set) {
                    OrganEO organEO = organService.getDirectlyUpLevelUnit(l);
                    RoleNodeVO vo = new RoleNodeVO();
                    vo.setId(l);
                    vo.setName(organEO.getName());
                    vo.setIsParent(true);
                    vo.setHasChildren(true);
                    vo.setPid((long)-1);
                    vo.setType("Organ");
                    roles.add(vo);
                }
            }
        } else {
            Long siteId = LoginPersonUtil.getSiteId();
            List<RoleEO> roleEOs = roleService.getCurUserRoles(LoginPersonUtil.getOrganId(), LoginPersonUtil.getUserId());

            if(scope.equals("2")) {
                List<RoleAssignmentEO> roless = roleAssignmentService.getAssignments(LoginPersonUtil.getOrganId(), LoginPersonUtil.getUserId());
                if(null != roleEOs) {
                    for (RoleAssignmentEO role : roless) {
                        RoleEO eo = roleService.getEntity(RoleEO.class,role.getRoleId());
                        if(null == roleEOs) {
                            roleEOs = new ArrayList<RoleEO>();
                        }

                        if(null != eo) {
                            roleEOs.add(eo);
                        }
                    }
                }
            }

            if(null != roleEOs) {
                for (RoleEO role : roleEOs) {
                    if(role.getSiteId() != null && role.getSiteId().intValue() == siteId.intValue()) {
                        RoleNodeVO vo = new RoleNodeVO();
                        vo.setId(role.getRoleId());
                        vo.setName(role.getName());
                        vo.setIsParent(false);
                        vo.setHasChildren(false);
                        vo.setPid((long)-1);
                        vo.setType(role.getType());

                        vo.setRoleId(role.getRoleId());
                        vo.setCode(role.getCode());
                        vo.setDescription(role.getDescription());
                        roles.add(vo);
                    }
                }
            }
        }

        return ResponseData.success(roles, superAdmin || root ? "true" : "false");
    }

	private List<RoleEO> getRoleEOs(String roleIds, List<RoleEO> roleEOs) {
		// 角色Id获取-用于更新角色赋予关系
		List<Long> rlIds = null;
		if (!StringUtils.isEmpty(roleIds)) {
			rlIds = StringUtils.getListWithLong(roleIds, ",");
		}
		
		if(rlIds!=null && rlIds.size()>0){
			if(roleEOs!=null&&roleEOs.size()>0){
    			Iterator<RoleEO>  iterator = roleEOs.iterator();
    			while(iterator.hasNext()){
    				RoleEO ra = iterator.next();
    				if(rlIds.contains(ra.getRoleId())){
    					//去除已存在的,其余的需要新增到数据库中
    					rlIds.remove(ra.getRoleId());
    				}
    			}
    		}else{
    			roleEOs = new ArrayList<RoleEO>();
    		}
		}
		if(rlIds!=null && rlIds.size()>0){
			for(Long roleId :rlIds){
				RoleEO role = roleService.getEntity(RoleEO.class, roleId);
				if(role != null){
					roleEOs.add(role);
				}
			}
		}
		return roleEOs;
	}
    
    
}
