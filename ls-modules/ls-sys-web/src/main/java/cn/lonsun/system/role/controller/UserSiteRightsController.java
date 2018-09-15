package cn.lonsun.system.role.controller;

import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.system.role.internal.service.IUserSiteRightsService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * 用户栏目权限控制类
 * @author gu.fei
 * @version 2015-12-1 14:53
 */
@Controller
@RequestMapping("/user/site/rights")
public class UserSiteRightsController extends BaseController {

    @Resource
    private IUserSiteRightsService userSiteRightsService;

    /**
     * 获取
     * @param userId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "getSiteRights")
    public Object getSiteRights(Long userId) {
        return getObject(userSiteRightsService.getSiteRights(userId));
    }

    /**
     * 保存权限
     * @param userId
     * @param rights
     * @return
     */
    @ResponseBody
    @RequestMapping("saveSiteRights")
    public Object saveSiteRights(Long userId,String rights) {
        userSiteRightsService.saveSiteRights(userId,rights);
        return this.ajaxOk();
    }
}
