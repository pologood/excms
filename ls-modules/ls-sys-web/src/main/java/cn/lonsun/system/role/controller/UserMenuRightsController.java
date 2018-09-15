package cn.lonsun.system.role.controller;

import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.system.role.internal.service.IUserMenuRightsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 用户菜单权限控制类
 * @author gu.fei
 * @version 2015-12-1 14:53
 */
@Controller
@RequestMapping("/user/menu/rights")
public class UserMenuRightsController extends BaseController {

    @Autowired
    private IUserMenuRightsService userMenuRightsService;

    /**
     * 获取菜单
     * @param userId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "getMenu")
    public Object getMenu(Long userId) {
        return getObject(userMenuRightsService.getMenu(userId));
    }

    /**
     * 保存菜单权限
     * @param userId
     * @param optRights
     * @return
     */
    @ResponseBody
    @RequestMapping("saveMenuRights")
    public Object saveMenuRights(Long userId,String optRights) {
        userMenuRightsService.saveMenuRights(userId,optRights);
        return this.ajaxOk();
    }
}
