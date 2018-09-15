package cn.lonsun.system.role.controller;

import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.site.site.vo.ColumnVO;
import cn.lonsun.site.template.util.ResponseData;
import cn.lonsun.system.role.internal.site.service.IRoleSiteOptService;
import cn.lonsun.system.role.internal.site.service.IUserSiteOptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;

/**
 * @author gu.fei
 * @version 2015-11-2 10:00
 */
@Controller
@RequestMapping("/role/site/opt")
public class CmsRoleSiteOptController extends BaseController {

    @Autowired
    private IRoleSiteOptService roleSiteOptService;

    @Autowired
    private IUserSiteOptService userSiteOptService;

    @ResponseBody
    @RequestMapping("/getSiteOpt")
    public Object getSiteOpt(Long roleId) {
        if(roleId == null) {
            return new ArrayList<ColumnVO>();
        }
        return roleSiteOptService.getSiteOpt(roleId);
    }

    @ResponseBody
    @RequestMapping("/getUserSiteOpt")
    public Object getUserSiteOpt(Long organId,Long userId) {
        return userSiteOptService.getUserOpts(organId, userId);
    }

    @ResponseBody
    @RequestMapping("/saveOpt")
    public Object saveOpt(String strJson,Long roleId) {
        roleSiteOptService.saveOpt(strJson,roleId);
        return ResponseData.success("保存权限成功!");
    }

    @ResponseBody
    @RequestMapping("/saveUserOpt")
    public Object saveUserOpt(String strJson,Long organId,Long userId) {
        userSiteOptService.saveUserOpt(strJson,organId,userId);
        return ResponseData.success("保存权限成功!");
    }

}
