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

package cn.lonsun.system.role.controller;

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.common.vo.PageQueryVO;
import cn.lonsun.common.vo.TreeNodeVO;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.rbac.internal.entity.PersonEO;
import cn.lonsun.rbac.internal.entity.RoleAssignmentEO;
import cn.lonsun.rbac.internal.entity.RoleEO;
import cn.lonsun.rbac.internal.service.*;
import cn.lonsun.site.site.internal.entity.SiteMgrEO;
import cn.lonsun.site.template.util.ResponseData;
import cn.lonsun.system.role.internal.service.IRoleAsgService;
import cn.lonsun.system.role.internal.util.RoleAuthUtil;
import cn.lonsun.system.systemlog.internal.entity.CmsLogEO;
import cn.lonsun.util.LoginPersonUtil;
import cn.lonsun.util.SysLog;
import com.sun.syndication.feed.atom.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TODO <br/>
 *
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 * @date 2015年8月26日 <br/>
 */
@Controller
@RequestMapping("/system/roleAsg")
public class RoleAsgController extends BaseController {

    private static final String FILE_BASE = "system/role/";

    @Autowired
    private IOrganService organService;

    @Autowired
    private IRoleAssignmentService roleAssignmentService;

    @Autowired
    private IRoleService roleService;

    @Autowired
    private IRoleAsgService roleAsgService;

    @Autowired
    private IPersonUserService personUserService;

    @RequestMapping("index")
    public ModelAndView index() {
        boolean isRoot = LoginPersonUtil.isRoot();
        ModelAndView model = new ModelAndView(FILE_BASE + "/role_manage");
        model.addObject("isRoot", isRoot);
        return model;
    }

    @RequestMapping("/userRight")
    public ModelAndView userRight(Long organId, Long userId) {
        ModelAndView model = new ModelAndView(FILE_BASE + "/user_rights");
        model.addObject("organId", organId);
        model.addObject("userId", userId);
        return model;
    }

    @RequestMapping("/userAdd")
    public ModelAndView userAdd(Long roleId) {
        ModelAndView model = new ModelAndView(FILE_BASE + "/user_select");
        boolean root = LoginPersonUtil.isRoot();
        boolean superAdmin = LoginPersonUtil.isSuperAdmin();
        boolean siteAdmin = LoginPersonUtil.isSiteAdmin();
        model.addObject("roleId", roleId);
        if(root) {
            model.addObject("roleCode","root");
        } else if(superAdmin) {
            model.addObject("roleCode","superAdmin");
        } else if(siteAdmin) {
            model.addObject("roleCode","siteAdmin");
        }
        return model;
    }

    @RequestMapping("/getTreeNodes")
    @ResponseBody
    public Object getTreeNodes(Long roleId,String name) {

        boolean superAdmin = LoginPersonUtil.isRoot() || LoginPersonUtil.isSuperAdmin();
        SiteMgrEO siteMgrEO = CacheHandler.getEntity(SiteMgrEO.class, LoginPersonUtil.getSiteId());
        RoleEO roleEO = roleService.getEntity(RoleEO.class, roleId);
        List<TreeNodeVO> list = null;

        if(AppUtil.isEmpty(name)) {
            if(superAdmin) {
                list = organService.getAllOrgans();
            } else {
                list = organService.getUnitsAndPersons(Long.valueOf(siteMgrEO.getUnitIds()));
            }
        } else {
            if(superAdmin) {
                list = organService.getOrgansAndPersons(null,name);
            } else {
                list = new ArrayList<TreeNodeVO>();
                if(null != siteMgrEO.getUnitIds()) {
                    Long siteUnitId = Long.valueOf(siteMgrEO.getUnitIds());
                    List<TreeNodeVO> nodeVOs = organService.getOrgansAndPersons(null, name);
                    for(TreeNodeVO vo : nodeVOs) {
                        if(vo.getType().equals(TreeNodeVO.Type.Person.toString())) {
                            List<TreeNodeVO> organEOs = organService.getParentOrgansById(vo.getUnitId());
                            if(vo.getUnitId().intValue() == siteUnitId.intValue()) {
                                list.add(vo);
                            } else if(null != organEOs) {
                                for(TreeNodeVO node : organEOs) {
                                    if(null != node.getUnitId() && node.getUnitId().intValue() == siteUnitId.intValue()) {
                                        list.add(vo);
                                    }
                                }
                            }
                        } else {
                            list.add(vo);
                        }
                    }
                }
            }
        }

        if (!AppUtil.isEmpty(roleId)) {
            List<RoleAssignmentEO> listRa = roleAssignmentService.getRoleAssignments(roleId);
            Map<String, Object> map = new HashMap<String, Object>(listRa.size());
            for (RoleAssignmentEO eo : listRa) {
                String key = eo.getOrganId() + ":" + eo.getUserId();
                map.put(key, eo);
            }
            if(list != null) {
                for (TreeNodeVO vo : list) {
                    if(vo.getType().equals(TreeNodeVO.Type.Person.toString())) {
                        List<RoleEO> roles = roleService.getUserRoles(vo.getUserId(), vo.getOrganId());
                        if(roles != null) {
                            for(RoleEO eo : roles) {
                                if(AppUtil.isEmpty(vo.getRoleCode())) {
                                    vo.setRoleCode(eo.getCode());
                                    vo.setRoleName("【" + eo.getName() + "】");
                                } else {
                                    if(!vo.getRoleCode().contains(eo.getCode())) {
                                        vo.setRoleCode(vo.getRoleCode() + "," + eo.getCode());
                                        vo.setRoleName(vo.getRoleName() + "【" + eo.getName() + "】");
                                    }
                                }
                                if (RoleEO.RoleCode.super_admin.toString().equals(roleEO.getCode())) {
                                    vo.setEnabled(false);
                                } else {
                                    //用户在当前站点是站点管理员，则不可选
                                    if(RoleAuthUtil.confirmUserSiteAdmin(vo.getOrganId(),vo.getUserId(),null)) {
                                        vo.setEnabled(false);
                                    }
                                    /*if("site_admin".equals(eo.getCode()) || "super_admin".equals(eo.getCode())) {
                                        vo.setEnabled(true);
                                    }*/
                                }
                            }
                        }
                    }
                    vo.setIsParent(false);
                    String key = vo.getOrganId() + ":" + vo.getUserId();
                    if (!AppUtil.isEmpty(map.get(key))) {
                        vo.setChecked(true);
                    }
                }
            }
        }

        return list;
    }

    @ResponseBody
    @RequestMapping("/addAsgEO")
    public Object addAsgEO(Long roleId, String userIds, String organIds) {
        RoleEO eo = roleService.getEntity(RoleEO.class, roleId);
        if (null == eo) {
            return ResponseData.success("添加失败!");
        }
        roleAsgService.addAsgEO(eo,string2Array(organIds), string2Array(userIds));
        if(!AppUtil.isEmpty(userIds)){
            String[] userId = userIds.split(",");
            for(int i=0;i<userId.length;i++){
                PersonEO personEO = personUserService.getPerson(Long.valueOf(userId[i]));
                if(personEO!=null){
                    SysLog.log("【系统管理】角色： " + eo.getName()  + ",绑定用户：" + personEO.getName(),
                            "RoleAssignmentEO", CmsLogEO.Operation.Add.toString());
                }
            }
        }
        return ResponseData.success("添加成功!");
    }

    @ResponseBody
    @RequestMapping("/removeUserRoles")
    public Object removeUserRoles(Long organId,Long userId) {
        roleAsgService.removeUserRoles(organId,userId);
        PersonEO personEO = personUserService.getPerson(Long.valueOf(userId));
        if(personEO!=null){
            List<RoleAssignmentEO> eos = roleAssignmentService.getAssignments(organId,userId);
            for(RoleAssignmentEO eo:eos){
                SysLog.log("【系统管理】角色： " + eo.getRoleName()  + ",删除用户：" + personEO.getName(),
                        "RoleAssignmentEO", CmsLogEO.Operation.Delete.toString());
            }
        }
        return ResponseData.success("角色移除成功!");
    }

    /*
    * 拆分字符串到数组
    * */
    private Long[] string2Array(String str) {
        if(AppUtil.isEmpty(str)) {
            return new Long[0];
        } else {
            String[] strA = str.split(",");
            Long[] rst = new Long[strA.length];
            for (int i = 0; i < strA.length; i++) {
                rst[i] = Long.parseLong(strA[i]);
            }

            return rst;
        }
    }

    @ResponseBody
    @RequestMapping("/delAsgEO")
    public Object delAsgEO(Long roleId, String userIds, String organIds) {
        RoleEO eo = roleService.getEntity(RoleEO.class, roleId);
        if (null == eo) {
            return ResponseData.success("删除失败!");
        }
        roleAsgService.delAsgEO(roleId,userIds,organIds);
        if(!AppUtil.isEmpty(userIds)){
            String[] userId = userIds.split(",");
            for(int i=0;i<userId.length;i++){
                PersonEO personEO = personUserService.getPerson(Long.valueOf(userId[i]));
                if(personEO!=null){
                    SysLog.log("【系统管理】角色： " + eo.getName()  + ",删除用户：" + personEO.getName(),
                            "RoleAssignmentEO", CmsLogEO.Operation.Delete.toString());
                }
            }
        }
        return ResponseData.success("删除成功!");
    }

    @ResponseBody
    @RequestMapping("/getAsgEOs")
    public Object getAsgEOs(PageQueryVO vo, Long roleId) {

        if(roleId == null) {
            return new ArrayList<PersonEO>();
        }

        return roleAsgService.getEOs(vo,roleId);
    }
}