/*
 * RoleController.java         2015年8月26日 <br/>
 *
 * Copyright (c) 1994-1999 AnHui LonSun, Inc. <br/>
 * All rights reserved.	<br/>
 *
 * This software is the confidential and proprietary information of AnHui	<br/>
 * LonSun, Inc. ("Confidential Information").  You shall not	<br/>
 * disclose such Confidential Information and shall use it only in	<br/>
 * accordance with the terms of the license agreement you entered into	<br/>
 * with Sun. <br/>
 */

package cn.lonsun.rbac.controller;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.exception.BusinessException;
import cn.lonsun.core.util.SessionUtil;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.rbac.indicator.service.IEXRoleService;
import cn.lonsun.rbac.internal.entity.RoleEO;
import cn.lonsun.rbac.internal.service.IRoleService;
import cn.lonsun.rbac.vo.RoleNodeVO;
import cn.lonsun.util.LoginPersonUtil;

/**
 * TODO <br/>
 *
 * @date 2015年8月26日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
@Controller("ex_8_RoleController")
@RequestMapping("/system/role")
public class RoleController extends BaseController {

    @Resource(name = "ex_8_RoleServiceImpl")
    private IEXRoleService exRoleService;

    @Resource
    private IRoleService roleService;

    /**
     * 
     * 转向角色管理主页
     *
     * @author fangtinghua
     * @return
     */
    @RequestMapping("index")
    public String index() {
        return "system/role/role_manage";
    }

    @RequestMapping("edit")
    public String edit() {
        return "system/role/edit";
    }

    @RequestMapping("/addOrEdit")
    public ModelAndView addOrEdit(String node,String type) {
        String data = "";
        try{
            data = new String(node.getBytes("ISO-8859-1"),"UTF-8");
        }catch (Exception e){
            e.printStackTrace();
        }
        ModelAndView model = new ModelAndView("system/role/role_edit");
        model.addObject("node",data);
        model.addObject("type",type);
        return model;
    }

    @ResponseBody
    @RequestMapping("saveOrUpdate")
    public Object saveOrUpdate(RoleEO roleEO) {
        this.roleService.saveOrUpdateEntity(roleEO);
        return getObject(roleEO);
    }

    @ResponseBody
    @RequestMapping("saveRoleAndRights")
    public Object save(HttpSession session, Long roleId, String rights) {
        exRoleService.save(roleId, rights);
        return this.getObject();
    }

    @ResponseBody
    @RequestMapping("updateRoleAndRights")
    public Object update(RoleEO role, String rights) {
        RoleNodeVO roleNode = new RoleNodeVO();
        try {
            roleService.update(role, rights);
            if (null != role.getOrganId() && role.getOrganId() > 0) {
                roleNode = roleService.getForOrganByRoleId(role.getRoleId());
            } else {
                roleNode = roleService.getByRoleId(role.getRoleId());
            }
        } catch (BusinessException e) {
            e.printStackTrace();
            throw new BaseRunTimeException(TipsMode.Key.toString(), e.getKey());
        }
        return this.getObject(roleNode);
    }

    @ResponseBody
    @RequestMapping("delete")
    public Object delete(Long roleId) {
        try {
            this.roleService.delete(roleId);
        } catch (BusinessException e) {
            e.printStackTrace();
            throw new BaseRunTimeException(TipsMode.Key.toString(), e.getKey());
        }
        return getObject();
    }

    @ResponseBody
    @RequestMapping("getTreeByScope")
    public Object getTreeByScope(HttpSession session, String scope) {
        boolean superAdmin = LoginPersonUtil.isRoot();

        List<RoleNodeVO> roles = null;
        if (superAdmin) {
            roles = this.roleService.getRoleTreeNodesByScope(scope);
        } else {
            Long organId = SessionUtil.getLongProperty(session, "organId");
            Long userId = SessionUtil.getLongProperty(session, "userId");
            roles = this.roleService.getRoleTreeNodesByScope(organId, userId, scope);
        }
        return roles;
    }

    @ResponseBody
    @RequestMapping("getRole")
    public Object getRole(Long roleId) {
        RoleEO role = null;
        if (roleId != null) {
            role = roleService.getEntity(RoleEO.class, roleId);
        } else {
            role = new RoleEO();
        }
        return role;
    }

    @RequestMapping("selectMenu")
    public String selectMenu() {
        return "/system/role/selectMenu";
    }
}