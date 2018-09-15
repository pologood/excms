package cn.lonsun.system.role.internal.site.service.impl;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.base.service.impl.BaseService;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.rbac.internal.entity.RoleAssignmentEO;
import cn.lonsun.rbac.internal.service.IRoleAssignmentService;
import cn.lonsun.site.site.internal.service.ISiteConfigService;
import cn.lonsun.system.role.internal.site.dao.IUserSiteOptDao;
import cn.lonsun.system.role.internal.site.entity.CmsUserSiteOptEO;
import cn.lonsun.system.role.internal.site.service.IUserSiteOptService;
import cn.lonsun.system.role.internal.util.RoleAuthUtil;
import cn.lonsun.util.LoginPersonUtil;
import net.sf.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author gu.fei
 * @version 2015-10-31 13:58
 */
@Service
public class UserSiteOptService extends BaseService<CmsUserSiteOptEO> implements IUserSiteOptService {

    @Autowired
    private IUserSiteOptDao userSiteOptDao;

    @Autowired
    private ISiteConfigService siteConfigService;

    @Autowired
    private IRoleAssignmentService roleAssignmentService;

    @Override
    public List<CmsUserSiteOptEO> getOpts(Long organId, Long userId) {
        return userSiteOptDao.getOpts(organId, userId);
    }

    @Override
    public CmsUserSiteOptEO getOpts(Long organId, Long userId, Long siteId) {
        return userSiteOptDao.getOpts(organId, userId, siteId);
    }

    @Override
    public List<IndicatorEO> getUserOpts(Long organId, Long userId) {

        Map<Long, Boolean> map = new HashMap<Long, Boolean>();
        Long[] ids = getCurUserHasOptIds(organId, userId);

        for (long id : ids) {
            map.put(id, true);
        }
        List<IndicatorEO> list = siteConfigService.getAllSites();

        for (IndicatorEO eo : list) {
            if (map.get(eo.getIndicatorId()) != null && map.get(eo.getIndicatorId())) {
                eo.setChecked(true);
            }
            eo.setIsParent(0);
        }

        return AppUtil.isEmpty(list) ? new ArrayList<IndicatorEO>() : list;
    }

    @Override
    public void deleteByUser(Long organId, Long userId) {
        userSiteOptDao.deleteByUser(organId, userId);
    }

    @Override
    public void deleteById(Long siteId, Long columnId) {
        userSiteOptDao.deleteById(siteId, columnId);
    }

    @Override
    public void saveUserOpt(String strJson, Long organId, Long userId) {
        JSONArray json = JSONArray.fromObject(strJson);
        List<CmsUserSiteOptEO> list = (List<CmsUserSiteOptEO>) JSONArray.toCollection(json, CmsUserSiteOptEO.class);

        this.deleteByUser(organId, userId);
        Map<String, Object> params = new HashMap<String, Object>();
        List<Long> roleIds = RoleAuthUtil.getCurHasConmmonRoleList(LoginPersonUtil.getSiteId(),organId,userId);
        //添加站点管理员前先把用户在此站点下的普通角色权限去除掉
        if(null != roleIds && !roleIds.isEmpty()) {
            params.put("roleId", RoleAuthUtil.getCurHasConmmonRoleList(LoginPersonUtil.getSiteId(),organId,userId));
            params.put("organId", organId);
            params.put("userId", userId);
            params.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
            RoleAssignmentEO dbAssignments = roleAssignmentService.getEntity(RoleAssignmentEO.class, params);
            roleAssignmentService.delete(dbAssignments);
        }
        this.saveEntities(list);
    }

    @Override
    public List<CmsUserSiteOptEO> getRightsBySiteId(Long siteId) {
        return userSiteOptDao.getRightsBySiteId(siteId);
    }

    /*
            * 获取当前用户站点栏目ID
            * */
    private Long[] getCurUserHasOptIds(Long organId, Long userId) {

        List<CmsUserSiteOptEO> opts = userSiteOptDao.getOpts(organId, userId);
        Set<Long> set = new HashSet<Long>();

        for (CmsUserSiteOptEO eo : opts) {
            set.add(eo.getSiteId());
        }

        List<Long> list = new ArrayList<Long>();

        for (Long id : set) {
            if (id == null) {
                continue;
            }
            list.add(id);
        }

        Long[] ids = new Long[list.size()];
        int count = 0;
        for (Long id : list) {
            if (id == null) {
                continue;
            }
            ids[count++] = id;
        }
        return ids;
    }
}
