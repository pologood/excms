package cn.lonsun.system.role.internal.util;

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.publicInfo.internal.entity.PublicCatalogEO;
import cn.lonsun.rbac.internal.entity.RoleAssignmentEO;
import cn.lonsun.rbac.internal.entity.RoleEO;
import cn.lonsun.rbac.internal.service.IRoleAssignmentService;
import cn.lonsun.rbac.internal.service.IRoleService;
import cn.lonsun.system.role.internal.entity.RbacInfoOpenRightsEO;
import cn.lonsun.system.role.internal.entity.RbacSiteRightsEO;
import cn.lonsun.system.role.internal.service.IInfoOpenRightsService;
import cn.lonsun.system.role.internal.service.ISiteRightsService;
import cn.lonsun.system.role.internal.site.entity.CmsUserSiteOptEO;
import cn.lonsun.system.role.internal.site.service.IUserSiteOptService;
import cn.lonsun.util.LoginPersonUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author gu.fei
 * @version 2016-11-10 17:36
 */
public class RoleAuthUtil {

    //栏目管理员角色标识
    private static final String ROLR_TYPE = "Column";

    //信息公开管理员角色标识
    private static final String PUBLIC_ROLR_TYPE = "PublicInfo";

    private static final IRoleAssignmentService roleAssignmentService = SpringContextHolder.getBean("roleAssignmentService");

    private static final ISiteRightsService siteRightsService = SpringContextHolder.getBean("siteRightsService");

    private static final IInfoOpenRightsService infoOpenRightsService = SpringContextHolder.getBean("infoOpenRightsService");

    private static final IUserSiteOptService userSiteOptService = SpringContextHolder.getBean("userSiteOptService");

    private static final IRoleService roleService = SpringContextHolder.getBean("roleService");

    /**
     * 判断当前用户是否是栏目管理员
     * @return
     */
    public static boolean isCurUserColumnAdmin() {
        return isCurUserColumnAdmin(null,null);
    }

    /**
     * 判断当前用户是否是信息公开管理员
     * @return
     */
    public static boolean isCurUserPublicInfoAdmin() {
        return isCurUserPublicInfoAdmin(null,null);
    }

    /**
     * 判断指定用户是否是栏目管理员
     * @return
     */
    public static boolean isCurUserColumnAdmin(Long organId,Long userId) {

        if(null == organId) {
            //当前用户单位ID
            organId = LoginPersonUtil.getOrganId();
        }

        if(null == userId) {
            //当前用户ID
            userId = LoginPersonUtil.getUserId();
        }

        //获取用户所拥有的权限
        List<RoleAssignmentEO> roles = roleAssignmentService.getAssignments(organId, userId);

        boolean rst = false;
        //判断是否有栏目管理员权限
        if(null != roles) {
            for(RoleAssignmentEO eo : roles) {
                if(eo.getRoleType().equals(ROLR_TYPE)) {
                    rst = true;
                    break;
                }
            }
        }

        return rst;
    }

    /**
     * 判断指定用户是否是信息公开管理员
     * @return
     */
    public static boolean isCurUserPublicInfoAdmin(Long organId,Long userId) {

        if(null == organId) {
            //当前用户单位ID
            organId = LoginPersonUtil.getOrganId();
        }

        if(null == userId) {
            //当前用户ID
            userId = LoginPersonUtil.getUserId();
        }

        //获取用户所拥有的权限
        List<RoleAssignmentEO> roles = roleAssignmentService.getAssignments(organId, userId);

        boolean rst = false;
        //判断是否有栏目管理员权限
        if(null != roles) {
            for(RoleAssignmentEO eo : roles) {
                if(eo.getRoleType().equals(PUBLIC_ROLR_TYPE)) {
                    rst = true;
                    break;
                }
            }
        }

        return rst;
    }

    /**
     * 判断当前栏目当前用户当前站点是否有栏目管理员权限
     * @param columnId
     * @return
     */
    public static boolean isCurUserColumnAdmin(Long columnId) {
        return isCurUserColumnAdmin(columnId,null,null,null);
    }

    /**
     * 判断信息信息公开项在当前用户下是否是栏目管理员权限
     * @param organId
     * @param code
     * @return
     */
    public static boolean isCurUserOrganPublicInfoAdmin(Long organId,String code) {
        return isCurUserOrganPublicInfoAdmin(organId,code,null,null,null);
    }

    /**
     * 判断信息信息公开项在当前用户下是否是栏目管理员权限
     * @param organId
     * @param catId
     * @return
     */
    public static boolean isCurUserOrganPublicInfoAdmin(Long organId,Long catId) {
        PublicCatalogEO catalog = CacheHandler.getEntity(PublicCatalogEO.class,catId);
        if(null != catalog) {
            return isCurUserOrganPublicInfoAdmin(organId,catalog.getCode(),null,null,null);
        }
        return false;
    }

    /**
     * 判断当前栏目是否有栏目管理员权限
     * @param columnId
     * @param organId
     * @param userId
     * @param siteId
     * @return
     */
    public static boolean isCurUserColumnAdmin(Long columnId,Long organId,Long userId,Long siteId) {
        if(null == columnId) {
            return false;
        }

        String key = "isColumnMgr_"+ columnId.longValue();
        Boolean isColumnMgr = LoginPersonUtil.getAttribute(Boolean.class, key);
        if(isColumnMgr != null){
            return isColumnMgr;
        }
        isColumnMgr = false;

        if(null == organId) {
            //当前用户单位ID
            organId = LoginPersonUtil.getOrganId();
        }

        if(null == userId) {
            //当前用户ID
            userId = LoginPersonUtil.getUserId();
        }

        if(null == siteId) {
            //当前站点ID
//            siteId = LoginPersonUtil.getSiteId();
        }

        //获取用户所拥有的权限
        List<RoleAssignmentEO> roles = roleAssignmentService.getAssignments(null, userId);

        //判断栏目是否有栏目管理员权限
        if(null != roles) {
            List<RbacSiteRightsEO> rgteos = new ArrayList<RbacSiteRightsEO>();
            for(RoleAssignmentEO eo : roles) {
                if(eo.getRoleType().equals(ROLR_TYPE)) {
                    Map<String,Object> map = new HashMap<String, Object>();
                    map.put("roleId",eo.getRoleId());
                    map.put("indicatorId",columnId);
//                    map.put("siteId",siteId);
                    List<RbacSiteRightsEO> rgteo = siteRightsService.getEntities(RbacSiteRightsEO.class,map);
                    if(null != rgteo) {
                        rgteos.addAll(rgteo);
                    }
                }
            }

            if(rgteos.size() > 0) {
                isColumnMgr = true;
            }
        }
        LoginPersonUtil.setAttribute(key, isColumnMgr);
        return isColumnMgr;
    }

    /**
     * 判断当前栏目是否有信息公开管理员权限
     * @param organId
     * @param code
     * @param userorganId
     * @param userId
     * @param siteId
     * @return
     */
    public static boolean isCurUserOrganPublicInfoAdmin(Long organId,String code,Long userorganId,Long userId,Long siteId) {
        if(null == organId) {
            return false;
        }

        String key = "isPublicMgr_"+ organId.longValue();
        Boolean isColumnMgr = LoginPersonUtil.getAttribute(Boolean.class, key);
        if(isColumnMgr != null){
            return isColumnMgr;
        }
        isColumnMgr = false;


        if(null == userorganId) {
            //当前用户单位ID
            userorganId = LoginPersonUtil.getOrganId();
        }

        if(null == userId) {
            //当前用户ID
            userId = LoginPersonUtil.getUserId();
        }

        if(null == siteId) {
            //当前站点ID
//            siteId = LoginPersonUtil.getSiteId();
        }

        //获取用户所拥有的权限
        List<RoleAssignmentEO> roles = roleAssignmentService.getAssignments(userorganId, userId);

        //判断栏目是否有栏目管理员权限
        if(null != roles) {
            List<RbacInfoOpenRightsEO> rgteos = new ArrayList<RbacInfoOpenRightsEO>();
            for(RoleAssignmentEO eo : roles) {
                if(eo.getRoleType().equals(ROLR_TYPE)) {
                    Map<String,Object> map = new HashMap<String, Object>();
                    map.put("organId",organId);
                    map.put("code",code);
                    map.put("roleId",eo.getRoleId());
//                    map.put("siteId",siteId);
                    List<RbacInfoOpenRightsEO> rgteo = infoOpenRightsService.getEntities(RbacInfoOpenRightsEO.class,map);
                    if(null != rgteo) {
                        rgteos.addAll(rgteo);
                    }
                }
            }

            if(rgteos.size() > 0) {
                isColumnMgr = true;
            }
        }
        LoginPersonUtil.setAttribute(key, isColumnMgr);
        return isColumnMgr;
    }

    /**
     * 判断当前用户在当前站点下是否是站点管理员
     * @return
     */
    public static boolean confirmUserSiteAdmin() {
        return confirmUserSiteAdmin(LoginPersonUtil.getSiteId());
    }

    /**
     * 判断当前用户在当前站点下是否是站点管理员
     * @param siteId
     * @return
     */
    public static boolean confirmUserSiteAdmin(Long siteId) {
        Map<String,Object> param = new HashMap<String, Object>();
        param.put("siteId",siteId);
        param.put("organId", LoginPersonUtil.getOrganId());
        param.put("userId", LoginPersonUtil.getUserId());
        List<CmsUserSiteOptEO> opts = userSiteOptService.getEntities(CmsUserSiteOptEO.class,param);
        return null != opts && !opts.isEmpty();
    }

    /**
     * 判断当前用户在当前站点下是否是站点管理员
     * @param organId
     * @param userId
     * @param siteId
     * @return
     */
    public static boolean confirmUserSiteAdmin(Long organId,Long userId,Long siteId) {
        if(null == siteId) {
            siteId = LoginPersonUtil.getSiteId();
        }
        Map<String,Object> param = new HashMap<String, Object>();
        param.put("siteId",siteId);
        param.put("organId",organId);
        param.put("userId",userId);
        List<CmsUserSiteOptEO> opts = userSiteOptService.getEntities(CmsUserSiteOptEO.class,param);
        return null != opts && !opts.isEmpty();
    }

    /**
     * 获取用户在指定站点下有普通角色权限
     * @param siteId
     * @return
     */
    public static List<Long> getCurHasConmmonRoleList(Long siteId,Long organId,Long userId) {
        if(null == siteId) {
            siteId = LoginPersonUtil.getSiteId();
        }

        if(null == organId) {
            //当前用户单位ID
            organId = LoginPersonUtil.getOrganId();
        }

        if(null == userId) {
            //当前用户ID
            userId = LoginPersonUtil.getUserId();
        }

        //获取用户所拥有的权限
        List<RoleAssignmentEO> roles = roleAssignmentService.getAssignments(organId, userId);
        List<Long> idsList = new ArrayList<Long>();
        //判断栏目是否有栏目管理员权限
        if(null != roles && !roles.isEmpty()) {
            for(RoleAssignmentEO eo : roles) {
                RoleEO roleEO = roleService.getEntity(RoleEO.class,eo.getRoleId());
                if(roleEO.getCode().contains(RoleEO.RoleCode.super_admin.toString()) ||
                        roleEO.getCode().contains(RoleEO.RoleCode.site_admin.toString())) {
                    continue;
                } else {
                    if(null != roleEO.getSiteId() && roleEO.getSiteId().intValue() == siteId.intValue()) {
                        idsList.add(roleEO.getRoleId());
                    }
                }
            }
        }

        return idsList;
    }
}
