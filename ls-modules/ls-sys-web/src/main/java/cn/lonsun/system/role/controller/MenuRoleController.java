package cn.lonsun.system.role.controller;

import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.rbac.indicator.entity.MenuEO;
import cn.lonsun.system.role.internal.service.IMenuRoleService;
import cn.lonsun.util.LoginPersonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;

/**
 * @author gu.fei
 * @version 2015-10-30 10:56
 */
@Controller
@RequestMapping("/role/menu")
public class MenuRoleController extends BaseController {

    @Autowired
    private IMenuRoleService menuRoleService;

    /**
     * 获取菜单
     *
     * @author
     * @return
     */
    @ResponseBody
    @RequestMapping("getMenu")
    public Object getMenu() throws Exception {
        boolean flag = LoginPersonUtil.isRoot();
        return menuRoleService.getMenu(flag, null);
    }

    /**
     * 获取选中权限菜单
     *
     * @author
     * @return
     */
    @ResponseBody
    @RequestMapping("getCheckMenu")
    public Object getCheckMenu(Long roleId) throws Exception {
        if(roleId == null) {
            return new ArrayList<MenuEO>();
        }
        boolean flag = LoginPersonUtil.isRoot();
        return menuRoleService.getMenu(flag, roleId);
    }

}
