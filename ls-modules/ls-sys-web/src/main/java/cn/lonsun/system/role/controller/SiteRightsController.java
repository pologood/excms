package cn.lonsun.system.role.controller;

import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.rbac.internal.entity.RoleEO;
import cn.lonsun.rbac.internal.service.IRoleService;
import cn.lonsun.site.site.vo.ColumnVO;
import cn.lonsun.site.template.util.ResponseData;
import cn.lonsun.system.role.internal.service.ISiteRightsService;
import cn.lonsun.system.systemlog.internal.entity.CmsLogEO;
import cn.lonsun.util.SysLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;

/**
 * @author gu.fei
 * @version 2015-12-1 14:53
 */
@Controller
@RequestMapping("/site/rights")
public class SiteRightsController extends BaseController {

    @Autowired
    private ISiteRightsService siteRightsService;

    @Autowired
    private IRoleService roleService;

    @ResponseBody
    @RequestMapping("/getSiteOpt")
    public Object getSiteOpt(Long roleId) {
        if(roleId == null) {
            return new ArrayList<ColumnVO>();
        }
        return siteRightsService.getRoleRights(roleId);
    }

    @ResponseBody
    @RequestMapping("/saveRights")
    public Object saveRights(String rights,Long roleId) {
        siteRightsService.saveRights(rights, roleId);
        try{
            RoleEO eo = roleService.getEntity(RoleEO.class, roleId);
            SysLog.log("【系统管理】修改角色，角色： " + eo.getName()  + "，操作：修改栏目权限",
                    "RoleAssignmentEO", CmsLogEO.Operation.Update.toString());
        }catch (Exception e){
            e.printStackTrace();
        }
        return ResponseData.success("保存权限成功!");
    }

}
