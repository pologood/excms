package cn.lonsun.system.role.controller;

import cn.lonsun.rbac.internal.entity.RoleEO;
import cn.lonsun.rbac.internal.service.IRoleService;
import cn.lonsun.system.systemlog.internal.entity.CmsLogEO;
import cn.lonsun.util.SysLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.system.role.internal.service.IMenuAsgService;

/**
 * @author gu.fei
 * @version 2015-10-30 10:14
 */
@Controller
@RequestMapping("/role/asg/menu")
public class MenuAsgController extends BaseController{

    @Autowired
    private IMenuAsgService menuAsgService;

    @Autowired
    private IRoleService roleService;

    @ResponseBody
    @RequestMapping("saveRoleAndRights")
    public Object save(Long roleId, String menuRights,String optRights) {
        menuAsgService.save(roleId, menuRights,optRights);
        try{
            RoleEO eo = roleService.getEntity(RoleEO.class, roleId);
            SysLog.log("【系统管理】修改角色，角色： " + eo.getName()  + "，操作：修改菜单权限",
                    "RoleAssignmentEO", CmsLogEO.Operation.Update.toString());
        }catch (Exception e){
            e.printStackTrace();
        }

        return this.getObject();
    }

}
