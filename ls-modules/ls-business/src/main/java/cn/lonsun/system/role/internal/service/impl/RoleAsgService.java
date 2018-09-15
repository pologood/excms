package cn.lonsun.system.role.internal.service.impl;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.common.vo.PageQueryVO;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.rbac.indicator.service.IIndicatorService;
import cn.lonsun.rbac.internal.entity.*;
import cn.lonsun.rbac.internal.service.*;
import cn.lonsun.system.role.internal.service.IRoleAsgService;
import cn.lonsun.system.role.internal.site.entity.CmsUserSiteOptEO;
import cn.lonsun.system.role.internal.site.service.IUserSiteOptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author gu.fei
 * @version 2015-9-18 16:26
 */
@Service("roleAsgService")
public class RoleAsgService extends MockService<RoleAssignmentEO> implements IRoleAsgService {

    @Autowired
    private IPersonService personService;

    @Autowired
    private IRoleAssignmentService roleAssignmentService;

    @Autowired
    private IUserSiteOptService userSiteOptService;

    @Autowired
    private IRoleService roleService;

    @Autowired
    private IOrganService organService;

    @Resource(name = "ex_8_IndicatorServiceImpl")
    private IIndicatorService indicatorService;

    @Autowired
    private IUserService userService;

    @Override
    public Object getEOs(PageQueryVO vo, Long roleId) {
        Pagination page = roleAssignmentService.getPageRoleAssignments(vo, roleId);

        List<RoleAssignmentEO> list = (List<RoleAssignmentEO>) page.getData();
        Long[] organIds = new Long[list.size()];
        Long[] userIds = new Long[list.size()];

        int count = 0;
        for(RoleAssignmentEO eo : list) {
            organIds[count] = eo.getOrganId();
            userIds[count] = eo.getUserId();
            count++;
        }

        List<PersonEO> personEOs = new ArrayList<PersonEO>();
        if(null != list && !list.isEmpty()) {
            personEOs = personService.getPersonsByUserIds(organIds,userIds);
        }


        for(PersonEO eo:personEOs) {
            Long organId = eo.getOrganId();
            Long userId = eo.getUserId();

            String fullName = "";
            if(eo.getUnitId() != null){
                OrganEO unit = organService.getEntity(OrganEO.class,eo.getUnitId());
                fullName = unit != null?unit.getName():"";
            }
            if(eo.getOrganId() != null){
                OrganEO organUnit = organService.getEntity(OrganEO.class,eo.getOrganId());
                fullName = (fullName != ""?fullName+">":"") + (organUnit != null?organUnit.getName():"");
           }

            eo.setFullOrganName(fullName);
            UserEO user = userService.getEntity(UserEO.class,eo.getUserId());
            if(null != user) {
                eo.setLastLoginDate(user.getLastLoginDate());
            }

            String siteRights = null;
            Long[] siteIds = getCurSiteIds(organId,userId);
            if(siteIds == null || siteIds.length <= 0) {continue;}
            List<IndicatorEO> indicatorEOs = indicatorService.getEntities(IndicatorEO.class, siteIds);
            for(IndicatorEO indicatorEO:indicatorEOs) {
                if(indicatorEO == null) {continue;}
                if(siteRights == null) {
                    siteRights = indicatorEO.getName();
                } else {
                    siteRights += "," + indicatorEO.getName();
                }
            }
            eo.setSiteRights(siteRights);
        }
        page.setData(personEOs);
        return page;
    }

    /**
     * 获取用户管理的站点id
     * @param organId
     * @param userId
     * @return
     */
    public Long[] getCurSiteIds(Long organId,Long userId) {

        List<CmsUserSiteOptEO> listSites = userSiteOptService.getOpts(organId,userId);

        if(listSites == null || listSites.size() <= 0) {return new Long[0];}

        Long[] ids = new Long[listSites.size()];
        int i = 0;
        for(CmsUserSiteOptEO eo : listSites) {
            ids[i++] = eo.getSiteId();
        }

        return ids;
    }

    @Override
    public boolean confirmRole(String roleCode, Long organId, Long userId) {
        boolean flag = false;
        List<RoleAssignmentEO> roles = roleAssignmentService.getAssignments(organId, userId);

        for(RoleAssignmentEO eo : roles) {
            if(eo.getRoleCode().equals(roleCode)) {flag = true;}
            else {continue;}
        }

        return flag;
    }

    @Override
    public void addUserRole(Long organId, Long userId, List<Long> roleIds) {
        roleAssignmentService.updateAssignments(organId,userId,roleIds);
    }

    @Override
    public void addAsgEO(RoleEO role, Long[] organIds, Long[] userIds) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("roleId", role.getRoleId());
        params.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
//        List<RoleAssignmentEO> dbAssignments = roleAssignmentService.getEntities(RoleAssignmentEO.class, params);
//        roleAssignmentService.delete(dbAssignments);
        roleAssignmentService.save(role, organIds, userIds);
    }

    @Override
    public void delAsgEO(Long roleId, String userIds, String organIds) {
        Long[] oIds = string2Array(organIds);
        Long[] uIds = string2Array(userIds);
        RoleEO eo = roleService.getEntity(RoleEO.class,roleId);
        roleAssignmentService.delete(eo, oIds, uIds);
        boolean flag = false;
        List<RoleAssignmentEO> roles = roleAssignmentService.getRoleAssignments(roleId);

        if(roles != null) {
            for(RoleAssignmentEO role : roles) {
                if( role.getRoleCode().equals(RoleEO.RoleCode.site_admin.toString())) {
                    flag = true;
                    break;
                }
            }
        }

        if(flag) {
            for(int i=0 ;i<oIds.length;i++) {
                userSiteOptService.deleteByUser(oIds[i],uIds[i]);
            }
        }
    }

    @Override
    public void removeUserRoles(Long organId, Long userId) {
        List<RoleAssignmentEO> roles = roleAssignmentService.getAssignments(organId, userId);
        for(RoleAssignmentEO eo : roles) {
            this.delAsgEO(eo.getRoleId(), String.valueOf(userId), String.valueOf(organId));
        }
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
}
