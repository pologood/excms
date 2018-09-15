package cn.lonsun.system.role.controller;

import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.rbac.internal.entity.RoleEO;
import cn.lonsun.rbac.internal.service.IRoleService;
import cn.lonsun.system.systemlog.internal.entity.CmsLogEO;
import cn.lonsun.util.SysLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.lonsun.site.template.util.ResponseData;
import cn.lonsun.system.role.internal.service.IInfoOpenRightsService;

/**
 * @author gu.fei
 * @version 2015-12-23 9:51
 */
@Controller
@RequestMapping("/info/open")
public class InfoOpenRightsController extends BaseController {

    @Autowired
    private IInfoOpenRightsService infoOpenRightsService;
    @Autowired
    private IRoleService roleService;

    @ResponseBody
    @RequestMapping("/getInfoOpenRights")
    public Object getInfoOpenRights(Long organId,Long roleId,Long siteId) {
        return infoOpenRightsService.getInfoOpenRights(organId,roleId,siteId);
    }

    @ResponseBody
    @RequestMapping("/saveRights")
    public Object saveRights(String rights, Long roleId,String organIds) {
        infoOpenRightsService.saveRights(rights,roleId,organIds);
        try{
            RoleEO eo = roleService.getEntity(RoleEO.class, roleId);
            SysLog.log("【系统管理】修改角色，角色： " + eo.getName()  + "，操作：修改信息公开权限",
                    "RoleAssignmentEO", CmsLogEO.Operation.Update.toString());
        }catch (Exception e){
            e.printStackTrace();
        }
        return ResponseData.success("保存成功!");
    }
}
