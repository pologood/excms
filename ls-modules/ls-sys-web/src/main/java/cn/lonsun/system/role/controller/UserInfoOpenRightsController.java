package cn.lonsun.system.role.controller;

import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.system.role.internal.service.IUserInfoOpenRightsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 用户信息公开权限控制类
 * @author gu.fei
 * @version 2015-12-23 9:51
 */
@Controller
@RequestMapping("/user/info/open")
public class UserInfoOpenRightsController extends BaseController {

    @Autowired
    private IUserInfoOpenRightsService userInfoOpenRightsService;

    /**
     * 获取
     * @param userId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "getInfoOpenRights")
    public Object getInfoOpenRights(Long userId,Long organId) {
        return getObject(userInfoOpenRightsService.getInfoOpenRights(userId,organId));
    }

    /**
     * 保存权限
     * @param userId
     * @param rights
     * @return
     */
    @ResponseBody
    @RequestMapping("saveInfoOpenRights")
    public Object saveInfoOpenRights(Long userId,String organIds,String rights) {
        userInfoOpenRightsService.saveInfoOpenRights(userId,organIds,rights);
        return this.ajaxOk();
    }
}
