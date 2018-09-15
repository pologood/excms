package cn.lonsun.system.role.internal.service.impl;

import cn.lonsun.base.anno.DbInject;
import cn.lonsun.cache.client.CacheGroup;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.base.service.impl.BaseService;
import cn.lonsun.indicator.internal.entity.FunctionEO;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.rbac.internal.entity.RoleAssignmentEO;
import cn.lonsun.rbac.internal.service.IRoleAssignmentService;
import cn.lonsun.site.site.internal.dao.IColumnConfigDao;
import cn.lonsun.site.site.internal.entity.ColumnMgrEO;
import cn.lonsun.site.site.internal.entity.SiteMgrEO;
import cn.lonsun.site.site.internal.service.IColumnConfigService;
import cn.lonsun.system.datadictionary.internal.entity.DataDictItemEO;
import cn.lonsun.system.role.internal.cache.RightDictCache;
import cn.lonsun.system.role.internal.cache.SiteRightsCache;
import cn.lonsun.system.role.internal.dao.ISiteRightsDao;
import cn.lonsun.system.role.internal.dao.IUserSiteRightsDao;
import cn.lonsun.system.role.internal.entity.RbacSiteRightsEO;
import cn.lonsun.system.role.internal.entity.vo.IdsVO;
import cn.lonsun.system.role.internal.entity.vo.SiteRightsVO;
import cn.lonsun.system.role.internal.service.ISiteRightsService;
import cn.lonsun.system.role.internal.service.IUserSiteRightsService;
import cn.lonsun.system.role.internal.site.entity.CmsUserSiteOptEO;
import cn.lonsun.system.role.internal.site.service.IUserSiteOptService;
import cn.lonsun.util.LoginPersonUtil;
import net.sf.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author gu.fei
 * @version 2015-9-18 16:26
 */
@Service
public class SiteRightsService extends BaseService<RbacSiteRightsEO> implements ISiteRightsService {

    @Autowired
    private ISiteRightsDao siteRightsDao;

    @Autowired
    private IColumnConfigService columnConfigService;

    @DbInject("columnConfig")
    private IColumnConfigDao columnConfigDao;

    @Autowired
    private IRoleAssignmentService roleAssignmentService;

    @Autowired
    private IUserSiteOptService userSiteOptService;

    @Resource
    private IUserSiteRightsService userSiteRightsService;

    @Resource
    private IUserSiteRightsDao userSiteRightsDao;

    @Resource(name = "ex_8_IndicatorServiceImpl")
    private cn.lonsun.rbac.indicator.service.IIndicatorService indicatorService;

    @Override
    public void delByRoleId(Long roleId) {
        siteRightsDao.delByRoleId(roleId);
    }

    @Override
    public Object getRoleRights(Long roleId) {
        boolean siteAdmin = LoginPersonUtil.isSiteAdmin();
        if(!siteAdmin) {
            return getCurHasSiteAndColumnRights(roleId);
        }
        Map<Long,SiteRightsVO> map = getCurRoleHasRights(roleId);
        Long[] siteIds = {LoginPersonUtil.getSiteId()};
        if(!AppUtil.isEmpty(siteIds) && siteIds.length > 0) {
            List<ColumnMgrEO> vos = columnConfigService.getColumnBySiteIds(siteIds);
            setFullRights(vos,map,roleId);
            return vos;
        }
        return new ArrayList<ColumnMgrEO>();
    }

    @Override
    public Object getSiteRights(Long roleId) {
        return getCurHasSiteAndColumnRights(roleId);
    }

    @Override
    public void saveRights(String rights, Long roleId) {
        JSONArray json = JSONArray.fromObject(rights);
        List<RbacSiteRightsEO> list = (List<RbacSiteRightsEO>) JSONArray.toCollection(json,RbacSiteRightsEO.class);

        this.delByRoleId(roleId);
        this.saveEntities(list);

        SiteRightsCache.refresh();
        CacheHandler.reload(RbacSiteRightsEO.class.getName());
    }

    /**
     * 增加用户权限-2017-8-23 gf
     * @param siteVOs
     * @return
     */
    @Override
    public List<SiteMgrEO> getCurUserSiteOpt(List<SiteMgrEO> siteVOs) {
        Long organId = LoginPersonUtil.getOrganId();
        Long userId = LoginPersonUtil.getUserId();
        List<RoleAssignmentEO> roles = roleAssignmentService.getAssignments(organId, userId);

        Long[] ids = new Long[roles.size()];
        int count = 0;
        for(RoleAssignmentEO eo : roles) {
            ids[count++] = eo.getRoleId();
        }
        List<SiteRightsVO> rightsVOs = siteRightsDao.getEOsByRoleIds(ids, IndicatorEO.Type.CMS_Site.toString());
        Map<Long,Boolean> map = new HashMap<Long, Boolean>();
        for(SiteRightsVO right : rightsVOs) {
            map.put(right.getIndicatorId(),true);
        }

        /********增加用户权限-gf(2017-8-23)************/
        Map<String, Boolean> userRightsMap = userSiteRightsService.getCheckedRightsMap(LoginPersonUtil.getUserId(),null);
        /********增加用户权限-gf(2017-8-23)-end********/

        List<SiteMgrEO> rst = new ArrayList<SiteMgrEO>();
        for(SiteMgrEO vo : siteVOs) {
            boolean flag = map.get(vo.getIndicatorId());
            /********增加用户权限-gf(2017-8-23)************/
            boolean flagb = userRightsMap.get(String.valueOf(vo.getIndicatorId()));
            /********增加用户权限-gf(2017-8-23)-end********/
            if(flag || flagb) {
                rst.add(vo);
            }
        }

        return rst;
    }

    @Override
    public Long[] getCurUserSiteIds() {
        return getCurUserSiteIds(false);
    }

    /**
     *  增加用户权限-2017-8-23 gf
     * @param subshow
     * @return
     */
    @Override
    public Long[] getCurUserSiteIds(boolean subshow) {

        Long organId = LoginPersonUtil.getOrganId();
        Long userId = LoginPersonUtil.getUserId();


        List<Long> idsList = new ArrayList<Long>();
        List<CmsUserSiteOptEO> listSites = userSiteOptService.getOpts(organId,userId);
        if(listSites != null && listSites.size() > 0) {
            for(CmsUserSiteOptEO eo : listSites) {
                idsList.add(eo.getSiteId());
                Map<String,Object> map=new HashMap<String, Object>();
                map.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
                map.put("parentId",eo.getSiteId());
                map.put("type", IndicatorEO.Type.SUB_Site.toString());
                List<IndicatorEO> list=indicatorService.getEntities(IndicatorEO.class,map);
                if(list!=null&&list.size()>0){
                    for(IndicatorEO indicatorEO:list){
                        idsList.add(indicatorEO.getIndicatorId());
                    }
                }
            }
        }

        List<RoleAssignmentEO> roles = roleAssignmentService.getAssignments(organId, userId);

        Long[] ids = new Long[roles.size()];
        int count = 0;
        for(RoleAssignmentEO eo : roles) {
            ids[count++] = eo.getRoleId();
        }

        List<IdsVO> siteIds = siteRightsDao.getSiteIdsByRoleIds(ids, IndicatorEO.Type.CMS_Site.toString());
        List<IdsVO> usiteIds = userSiteRightsDao.getSites(LoginPersonUtil.getUserId(),IndicatorEO.Type.CMS_Site.toString());
        if(null != usiteIds) {
            siteIds.addAll(usiteIds);
        }
        if(subshow) {
            List<IdsVO> _siteIds = siteRightsDao.getSiteIdsByRoleIds(ids, IndicatorEO.Type.SUB_Site.toString());
            List<IdsVO> _usiteIds = userSiteRightsDao.getSites(LoginPersonUtil.getUserId(),IndicatorEO.Type.SUB_Site.toString());
            if(!AppUtil.isEmpty(_siteIds)) {
                siteIds.addAll(_siteIds);
            }
            if(null != _usiteIds) {
                siteIds.addAll(_usiteIds);
            }
        }
        for(int i=0;i<siteIds.size();i++){
            if(!idsList.contains(Long.valueOf(siteIds.get(i).getIndicatorId()))) {
                idsList.add(Long.valueOf(siteIds.get(i).getIndicatorId()));
            }
        }


        Long[] siteIdsArr = new Long[idsList.size()];
        for(int i=0;i<idsList.size();i++){
            siteIdsArr[i] = Long.valueOf(idsList.get(i));
        }


        return siteIdsArr;
    }

    @Override
    public List<Long> getCurUserHasColumnIds(Long siteId) {
        Long organId = LoginPersonUtil.getOrganId();
        Long userId = LoginPersonUtil.getUserId();

        List<Long> columnIds = new ArrayList<Long>();
        List<ColumnMgrEO> mgrEOs = columnConfigService.getTree(siteId);
        for(ColumnMgrEO eo:mgrEOs) {
            if(eo.getType().equals(IndicatorEO.Type.CMS_Site.toString())) {
                continue;
            }
            columnIds.add(eo.getIndicatorId());
        }

        boolean siteAdmin = LoginPersonUtil.isSiteAdmin();

        if(siteAdmin) {
            List<CmsUserSiteOptEO> listSites = userSiteOptService.getOpts(organId,userId);
            if(listSites != null && listSites.size() > 0) {
                for(CmsUserSiteOptEO eo : listSites) {
                    if(eo.getSiteId().intValue() == siteId.intValue()) {
                        return columnIds;
                    }
                }
            }
        }

        List<RoleAssignmentEO> roles = roleAssignmentService.getAssignments(organId, userId);

        Long[] ids = new Long[roles.size()];
        int count = 0;
        for(RoleAssignmentEO eo : roles) {
            ids[count++] = eo.getRoleId();
        }

        List<IdsVO> idsVOs = siteRightsDao.getSiteIdsByRoleIds(ids, IndicatorEO.Type.CMS_Section.toString());
        List<IdsVO> uidsVOs = userSiteRightsDao.getSites(LoginPersonUtil.getUserId(), IndicatorEO.Type.CMS_Section.toString());
        if(null != uidsVOs) {
            idsVOs.addAll(uidsVOs);
        }
        List<Long> idsarr = new ArrayList<Long>();
        for(IdsVO vo : idsVOs) {
            if(!idsarr.contains(vo.getIndicatorId())) {
                idsarr.add(vo.getIndicatorId());
            }
        }

        idsarr.retainAll(columnIds);
        return idsarr;
    }

    /**
     *  增加用户权限-2017-8-23 gf
     * @param columnVOs
     * @return
     */
    @Override
    public List<ColumnMgrEO> getCurUserColumnOpt(List<ColumnMgrEO> columnVOs) {

        boolean superAdmin = LoginPersonUtil.isRoot() || LoginPersonUtil.isSuperAdmin();
        boolean siteAdmin = LoginPersonUtil.isSiteAdmin();

        if(superAdmin || siteAdmin) {
            return columnVOs;
        }

        Long organId = LoginPersonUtil.getOrganId();
        Long userId = LoginPersonUtil.getUserId();
        List<RoleAssignmentEO> roles = roleAssignmentService.getAssignments(organId, userId);

        Long[] ids = new Long[roles.size()];
        int count = 0;
        for(RoleAssignmentEO eo : roles) {
            ids[count++] = eo.getRoleId();
        }

        List<SiteRightsVO> rightsVOs = siteRightsDao.getEOsByRoleIds(ids, IndicatorEO.Type.CMS_Section.toString());
        List<SiteRightsVO> userRvos = userSiteRightsDao.getSiteRights(LoginPersonUtil.getUserId(),IndicatorEO.Type.CMS_Section.toString(),LoginPersonUtil.getSiteId());
        if(null != userRvos) {
            rightsVOs.addAll(userRvos);
        }
        List<SiteRightsVO> comrightsVOs = siteRightsDao.getEOsByRoleIds(ids, IndicatorEO.Type.COM_Section.toString());
        List<SiteRightsVO> ucvos = userSiteRightsDao.getSiteRights(LoginPersonUtil.getUserId(),IndicatorEO.Type.COM_Section.toString(),LoginPersonUtil.getSiteId());
        if(null != ucvos) {
            rightsVOs.addAll(ucvos);
        }
        rightsVOs.addAll(comrightsVOs == null?new ArrayList<SiteRightsVO>():comrightsVOs);
        Map<Long,List<String>> map = new HashMap<Long, List<String>>();
        for(SiteRightsVO right : rightsVOs) {
            if(null == map.get(right.getIndicatorId())) {
                List<String> list = new ArrayList<String>();
                list.add(right.getOptCode());
                map.put(right.getIndicatorId(),list);
            } else {
                List<String> list = map.get(right.getIndicatorId());
                list.add(right.getOptCode());
                map.put(right.getIndicatorId(),list);
            }
        }

        List<ColumnMgrEO> temp = new ArrayList<ColumnMgrEO>();
        for(ColumnMgrEO vo : columnVOs) {
            vo.setOpt("");
            List<String> list = map.get(vo.getIndicatorId());
            if(null != list) {
                List<FunctionEO> functionEOs = new ArrayList<FunctionEO>();
                for(String str : list) {
                    FunctionEO eo = new FunctionEO();
                    eo.setAction(str);
                    eo.setChecked(true);
                    functionEOs.add(eo);
                }

                vo.setFunctions(functionEOs);
                temp.add(vo);
            }
        }

        return temp;
    }

    @Override
    public List<FunctionEO> getOptByColumnId(Long columnId) {
        Long organId = LoginPersonUtil.getOrganId();
        Long userId = LoginPersonUtil.getUserId();
        List<RoleAssignmentEO> roles = roleAssignmentService.getAssignments(organId, userId);

        Long[] ids = new Long[roles.size()];
        int count = 0;
        for(RoleAssignmentEO eo : roles) {
            ids[count++] = eo.getRoleId();
        }

        List<SiteRightsVO> rightsVOs = siteRightsDao.getEOsByRoleIds(ids, IndicatorEO.Type.CMS_Section.toString());
        List<SiteRightsVO> urightsVOs = userSiteRightsDao.getSiteRights(LoginPersonUtil.getUserId(),IndicatorEO.Type.CMS_Section.toString(),null);
        if(null != urightsVOs) {
            rightsVOs.addAll(urightsVOs);
        }
        Map<Long,List<String>> map = new HashMap<Long, List<String>>();
        for(SiteRightsVO right : rightsVOs) {
            if(null == map.get(right.getIndicatorId())) {
                List<String> list = new ArrayList<String>();
                list.add(right.getOptCode());
                map.put(right.getIndicatorId(),list);
            } else {
                List<String> list = map.get(right.getIndicatorId());
                list.add(right.getOptCode());
                map.put(right.getIndicatorId(),list);
            }
        }

        List<String> list = map.get(columnId);
        List<FunctionEO> functionEOs = new ArrayList<FunctionEO>();
        if(null != list) {
            for(String str : list) {
                FunctionEO eo = new FunctionEO();
                eo.setAction(str);
                eo.setChecked(true);
                functionEOs.add(eo);
            }
        }

        return functionEOs;
    }

    /**
     * 获取当前角色权限
     * @param roleId
     * @return
     */
    private List<ColumnMgrEO> getCurHasSiteAndColumnRights(Long roleId) {
        boolean superAdmin = LoginPersonUtil.isRoot() || LoginPersonUtil.isSuperAdmin();
        boolean siteAdmin = LoginPersonUtil.isSiteAdmin();
        List<ColumnMgrEO> vos = null;
        Map<Long,SiteRightsVO> map = getCurRoleHasRights(roleId);

        Long organId = LoginPersonUtil.getOrganId();
        Long userId = LoginPersonUtil.getUserId();
        List<RoleAssignmentEO> roles = roleAssignmentService.getAssignments(organId, userId);

        if(superAdmin) {
            vos = columnConfigService.getTree(null);
            setFullRights(vos, map, roleId);
        } else if(siteAdmin) {
            boolean allRights = true;
            Long[] ids = getCurUserHasIndicatorIds(true);
            if(null == ids || ids.length <= 0) {
                ids = getCurUserHasIndicatorIds(false);
                allRights = false;
                vos = columnConfigService.getTreeByIds(ids);
            } else {
                vos = columnConfigService.getColumnBySiteIds(ids);
            }

            if(ids.length <= 0) {
                return new ArrayList<ColumnMgrEO>();
            }

            if(allRights) {
                setFullRights(vos,map,roleId);
            } else {
                setPonitRights(vos,roles,map,roleId);
            }
        } else {
            Long[] ids = getCurUserHasIndicatorIds(false);
            if(ids.length <= 0) {
                return new ArrayList<ColumnMgrEO>();
            }
            vos = columnConfigService.getTreeByIds(ids);
            setPonitRights(vos,roles,map,roleId);
        }

        return vos;
    }

    /**
     * 超管、站点管理员（未分配站点权限）权限设置
     * @param vos
     * @param map
     * @param roleId
     * @return
     */
    private List<ColumnMgrEO> setFullRights(List<ColumnMgrEO> vos, Map<Long,SiteRightsVO> map, Long roleId) {
        for(ColumnMgrEO vo : vos) {
            if(null!= map && null != map.get(vo.getIndicatorId())) {
                vo.setChecked(true);
            }

            List<DataDictItemEO> items = getDictOpt(vo.getColumnTypeCode());
            if(null == items) {
                continue;
            }
            List<FunctionEO> functionEOs = new ArrayList<FunctionEO>();
            for(DataDictItemEO item : items) {
                RbacSiteRightsEO rightsEO = SiteRightsCache.get(roleId, vo.getIndicatorId(), item.getCode());
                FunctionEO functionEO = new FunctionEO();
                functionEO.setName(item.getName());
                functionEO.setAction(item.getCode());
                if(null != rightsEO) {
                    functionEO.setChecked(true);
                }
                functionEOs.add(functionEO);
            }

            vo.setFunctions(functionEOs);
        }

        return vos;
    }

    /**
     * 普通管理员设置指定权限
     * @param vos
     * @param roles
     * @param map
     * @param roleId
     * @return
     */
    private List<ColumnMgrEO> setPonitRights(List<ColumnMgrEO> vos, List<RoleAssignmentEO> roles, Map<Long,SiteRightsVO> map, Long roleId) {
        for(ColumnMgrEO vo : vos) {
            if(null!= map && null != map.get(vo.getIndicatorId())) {
                vo.setChecked(true);
            }
//            boolean site = vo.getType().equals(IndicatorEO.Type.CMS_Site.toString());
            List<DataDictItemEO> items = getDictOpt(vo.getColumnTypeCode());
            if(null == items) {
                continue;
            }
            List<FunctionEO> functionEOs = new ArrayList<FunctionEO>();
            for(DataDictItemEO item : items) {
                for(RoleAssignmentEO role : roles) {
                    if(null != SiteRightsCache.get(role.getRoleId(), vo.getIndicatorId(), item.getCode())) {
                        RbacSiteRightsEO rightsEO = SiteRightsCache.get(roleId, vo.getIndicatorId(), item.getCode());
                        FunctionEO functionEO = new FunctionEO();
                        functionEO.setName(item.getName());
                        functionEO.setAction(item.getCode());
                        if(null != rightsEO) {
                            functionEO.setChecked(true);
                        }
                        functionEOs.add(functionEO);
                    }
                }
            }

            vo.setFunctions(functionEOs);
        }

        return vos;
    }

    /**
     * 获取当前用户拥有的站点栏目项
     * @return
     */
    private Long[] getCurUserHasIndicatorIds(boolean siteAdmin) {
        Long organId = LoginPersonUtil.getOrganId();
        Long userId = LoginPersonUtil.getUserId();

        if(siteAdmin) {
            List<CmsUserSiteOptEO> listSites = userSiteOptService.getOpts(organId,userId);
            if(listSites != null && listSites.size() > 0) {
                Long[] siteids = new Long[listSites.size()];
                int i = 0;
                for(CmsUserSiteOptEO eo : listSites) {
                    siteids[i++] = eo.getSiteId();
                }

                return siteids;
            }
            return new Long[0];
        }

        List<RoleAssignmentEO> roles = roleAssignmentService.getAssignments(organId, userId);
        Set<Long> set = new HashSet<Long>();

        for(RoleAssignmentEO eo : roles) {
            List<RbacSiteRightsEO> rightsEOs = CacheHandler.getList(RbacSiteRightsEO.class, CacheGroup.CMS_ROLE_ID,eo.getRoleId());
            if(null == rightsEOs) {
                continue;
            }
            for(RbacSiteRightsEO rightsEO : rightsEOs) {
                if(rightsEO != null && rightsEO.getSiteId() != null && rightsEO.getSiteId().intValue() == LoginPersonUtil.getSiteId().intValue()) {
                    set.add(rightsEO.getIndicatorId());
                } else if(rightsEO.getIndicatorId().intValue() == LoginPersonUtil.getSiteId().intValue()) {
                    set.add(rightsEO.getIndicatorId());
                }
            }
        }

        List<Long> list = new ArrayList<Long>();

        for(Long id : set) {
            if(id == null) {
                continue;
            }
            list.add(id);
        }

        Long[] ids = new Long[list.size()];
        int count = 0;
        for(Long id : list) {
            if(id == null) {
                continue;
            }
            ids[count++] = id;
        }
        return ids;
    }

    /**
     * 获取当前角色拥有的权限
     * @param roleId
     * @return
     */
    private Map<Long,SiteRightsVO> getCurRoleHasRights(Long roleId) {
        List<SiteRightsVO> list = siteRightsDao.getEOsByRoleIds(new Long[]{roleId});
        Map<Long,SiteRightsVO> map = new HashMap<Long, SiteRightsVO>();

        for(SiteRightsVO eo : list) {
            map.put(eo.getIndicatorId(),eo);
        }

        return map;
    }

    /**
     * 根据字典编码获取字典项
     * @param code
     * @return
     */
    private List<DataDictItemEO> getDictOpt(String code) {
        List<DataDictItemEO> items = RightDictCache.get(code);
        return items;
    }
}
