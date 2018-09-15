package cn.lonsun.system.role.internal.service.impl;

import cn.lonsun.cache.client.CacheGroup;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.indicator.internal.entity.FunctionEO;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.publicInfo.internal.service.IPublicCatalogService;
import cn.lonsun.publicInfo.vo.OrganCatalogQueryVO;
import cn.lonsun.publicInfo.vo.OrganCatalogVO;
import cn.lonsun.rbac.internal.entity.RoleAssignmentEO;
import cn.lonsun.rbac.internal.service.IRoleAssignmentService;
import cn.lonsun.site.site.internal.entity.SiteConfigEO;
import cn.lonsun.system.datadictionary.internal.entity.DataDictItemEO;
import cn.lonsun.system.role.internal.cache.RightDictCache;
import cn.lonsun.system.role.internal.dao.IInfoOpenRightsDao;
import cn.lonsun.system.role.internal.entity.RbacInfoOpenRightsEO;
import cn.lonsun.system.role.internal.service.IInfoOpenRightsService;
import cn.lonsun.system.role.internal.service.IUserInfoOpenRightsService;
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
public class InfoOpenRightsService extends MockService<RbacInfoOpenRightsEO> implements IInfoOpenRightsService {

    private static final String PUBLIC_INFO_AUTH = "public_info_auth";

    @Autowired
    private IInfoOpenRightsDao iInfoOpenRightsDao;

    @Autowired
    private IPublicCatalogService publicCatalogService;

    @Autowired
    private IRoleAssignmentService roleAssignmentService;

    @Autowired
    private IUserSiteOptService userSiteOptService;

    @Resource
    private IUserInfoOpenRightsService userInfoOpenRightsService;

    private Map<String, String> itemMap;

    @Override
    public Object getInfoOpenRights(Long organId, Long roleId, Long siteId) {
        boolean siteAdmin = LoginPersonUtil.isSiteAdmin();
        List<OrganCatalogVO> vos = new ArrayList<OrganCatalogVO>();
        if (siteAdmin) {
            List<DataDictItemEO> itemEOs = RightDictCache.get(PUBLIC_INFO_AUTH);
            itemMap = new HashMap<String, String>();

            for (DataDictItemEO itemEO : itemEOs) {
                itemMap.put(itemEO.getCode(), itemEO.getName());
            }

            if (AppUtil.isEmpty(siteId)) {
//                Long[] siteIds = getCurUserHasIndicatorIds();
                Long[] siteIds = {LoginPersonUtil.getSiteId()};
                if (null != siteIds) {
                    for (Long _siteId : siteIds) {
                        OrganCatalogQueryVO qvo = new OrganCatalogQueryVO();
                        qvo.setOrganId(organId);
                        qvo.setAll(false);
                        qvo.setSiteId(_siteId);
                        qvo.setCatalog(true);
                        List<OrganCatalogVO> catalogVOs = publicCatalogService.getOrganCatalogTree(qvo);
                        vos.addAll(catalogVOs);

                        if (AppUtil.isEmpty(organId)) {
                            IndicatorEO indicatorEO = CacheHandler.getEntity(IndicatorEO.class, _siteId);
                            SiteConfigEO siteConfigEO = CacheHandler.getEntity(SiteConfigEO.class, CacheGroup.CMS_INDICATORID, _siteId);
                            OrganCatalogVO vo = new OrganCatalogVO();
                            vo.setId(Long.valueOf(siteConfigEO.getUnitIds()));
                            vo.setName(indicatorEO.getName());
                            vo.setType(IndicatorEO.Type.CMS_Site.toString());
                            if (null != catalogVOs && !catalogVOs.isEmpty()) {
                                vo.setIsParent(true);
                            }
                            vos.add(vo);
                        }
                    }
                }
            } else {
                OrganCatalogQueryVO qvo = new OrganCatalogQueryVO();
                qvo.setOrganId(organId);
                qvo.setAll(false);
                qvo.setSiteId(siteId);
                List<OrganCatalogVO> catalogVOs = publicCatalogService.getOrganCatalogTree(qvo);
                vos.addAll(catalogVOs);
            }

            Map<String, RbacInfoOpenRightsEO> curRoleRights = getCurRoleHasRights(roleId);
            for (OrganCatalogVO vo : vos) {
                if (vo.getType().equals(IndicatorEO.Type.CMS_Site.toString())) {
                    continue;
                }
                String key = turnNull2Str(roleId) + turnNull2Str(vo.getOrganId()) + turnNull2Str(vo.getCode());
                if (!AppUtil.isEmpty(curRoleRights.get(key))) {
                    vo.setChecked(true);
                } else {
                    vo.setChecked(false);
                }
                if (!vo.getIsParent()) {
                    List<FunctionEO> functionEOs = new ArrayList<FunctionEO>();
                    for (DataDictItemEO itemEO : itemEOs) {
                        FunctionEO functionEO = new FunctionEO();
                        functionEO.setAction(itemEO.getCode());
                        functionEO.setName(itemEO.getName());
                        functionEOs.add(functionEO);

                        String _key = turnNull2Str(roleId) + turnNull2Str(vo.getOrganId()) + turnNull2Str(vo.getCode()) + turnNull2Str(itemEO.getCode());
                        if (!AppUtil.isEmpty(curRoleRights.get(_key))) {
                            functionEO.setChecked(true);
                            vo.setChecked(true);
                        } else {
                            functionEO.setChecked(false);
                        }
                    }
                    vo.setFunctions(functionEOs);
                }
            }
        } else {
            List<DataDictItemEO> itemEOs = RightDictCache.get(PUBLIC_INFO_AUTH);
            itemMap = new HashMap<String, String>();

            for (DataDictItemEO itemEO : itemEOs) {
                itemMap.put(itemEO.getCode(), itemEO.getName());
            }

            if (AppUtil.isEmpty(siteId)) {
                Long[] siteIds = {LoginPersonUtil.getSiteId()};
                if (null != siteIds) {
                    for (Long _siteId : siteIds) {
                        OrganCatalogQueryVO qvo = new OrganCatalogQueryVO();
                        qvo.setOrganId(organId);
                        qvo.setAll(false);
                        qvo.setSiteId(_siteId);
                        qvo.setCatalog(true);
                        List<OrganCatalogVO> catalogVOs = publicCatalogService.getOrganCatalogTree(qvo);
                        vos.addAll(catalogVOs);

                        if (AppUtil.isEmpty(organId)) {
                            IndicatorEO indicatorEO = CacheHandler.getEntity(IndicatorEO.class, _siteId);
                            SiteConfigEO siteConfigEO = CacheHandler.getEntity(SiteConfigEO.class, CacheGroup.CMS_INDICATORID, _siteId);
                            OrganCatalogVO vo = new OrganCatalogVO();
                            vo.setId(Long.valueOf(siteConfigEO.getUnitIds()));
                            vo.setName(indicatorEO.getName());
                            vo.setType(IndicatorEO.Type.CMS_Site.toString());
                            if (null != catalogVOs && !catalogVOs.isEmpty()) {
                                vo.setIsParent(true);
                            }
                            vos.add(vo);
                        }
                    }
                }
            } else {
                OrganCatalogQueryVO qvo = new OrganCatalogQueryVO();
                qvo.setOrganId(organId);
                qvo.setAll(false);
                qvo.setSiteId(siteId);
                List<OrganCatalogVO> catalogVOs = publicCatalogService.getOrganCatalogTree(qvo);
                vos.addAll(catalogVOs);
            }

            Map<String, RbacInfoOpenRightsEO> curRoleRights = getCurRoleHasRights(roleId);
            Map<String, Set<FunctionEO>> curUserHasRights = getCurUserHasRights();
            for (OrganCatalogVO vo : vos) {
                if (vo.getType().equals(IndicatorEO.Type.CMS_Site.toString())) {
                    continue;
                }
                String key = turnNull2Str(roleId) + turnNull2Str(vo.getOrganId()) + turnNull2Str(vo.getCode());
                if (!AppUtil.isEmpty(curRoleRights.get(key))) {
                    vo.setChecked(true);
                } else {
                    vo.setChecked(false);
                }
                if (!vo.getIsParent()) {
                    String fkey = turnNull2Str(vo.getOrganId()) + turnNull2Str(vo.getCode());
                    List<FunctionEO> functionEOList = new ArrayList<FunctionEO>();
                    Set<FunctionEO> functionEOs = curUserHasRights.get(fkey);

                    Map<String,Boolean> fmap = new HashMap<String, Boolean>();
                    if(null != functionEOs) {

                        for(FunctionEO eo : functionEOs) {
                            fmap.put(eo.getAction(),true);
                        }

                        for (DataDictItemEO itemEO : itemEOs) {
                            FunctionEO functionEO = new FunctionEO();
                            functionEO.setAction(itemEO.getCode());
                            functionEO.setName(itemEO.getName());

                            if(null != fmap.get(itemEO.getCode()) && fmap.get(itemEO.getCode())) {
                                String _key = turnNull2Str(roleId) + turnNull2Str(vo.getOrganId()) + turnNull2Str(vo.getCode()) + turnNull2Str(itemEO.getCode());
                                if (!AppUtil.isEmpty(curRoleRights.get(_key))) {
                                    functionEO.setChecked(true);
                                    vo.setChecked(true);
                                } else {
                                    functionEO.setChecked(false);
                                }
                                functionEOList.add(functionEO);
                            }
                        }
                    }
                    vo.setFunctions(functionEOList);
                }
            }
        }
        return vos;
    }

    @Override
    public Object getInfoOpenRights(Long organId, Long roleId) {

        boolean superAdmin = LoginPersonUtil.isSuperAdmin();
        boolean isRoot = LoginPersonUtil.isRoot();

        if (isRoot) {
            return new ArrayList<OrganCatalogVO>();
        }

        List<DataDictItemEO> itemEOs = RightDictCache.get(PUBLIC_INFO_AUTH);
        itemMap = new HashMap<String, String>();
        for (DataDictItemEO itemEO : itemEOs) {
            itemMap.put(itemEO.getCode(), itemEO.getName());
        }

        Long siteId = LoginPersonUtil.getSiteId();
        OrganCatalogQueryVO qvo = new OrganCatalogQueryVO();
        qvo.setOrganId(organId);
        qvo.setAll(false);
        qvo.setSiteId(siteId);

        Map<String, RbacInfoOpenRightsEO> curRoleRights = getCurRoleHasRights(roleId);
        List<OrganCatalogVO> vos = publicCatalogService.getOrganCatalogTree(qvo);
        if (superAdmin) {
            for (OrganCatalogVO vo : vos) {
                String key = turnNull2Str(roleId) + turnNull2Str(vo.getOrganId()) + turnNull2Str(vo.getCode());
                if (!AppUtil.isEmpty(curRoleRights.get(key))) {
                    vo.setChecked(true);
                } else {
                    vo.setChecked(false);
                }
                if (!vo.getIsParent()) {
                    List<FunctionEO> functionEOs = new ArrayList<FunctionEO>();
                    for (DataDictItemEO itemEO : itemEOs) {
                        FunctionEO functionEO = new FunctionEO();
                        functionEO.setAction(itemEO.getCode());
                        functionEO.setName(itemEO.getName());
                        functionEOs.add(functionEO);

                        String _key = turnNull2Str(roleId) + turnNull2Str(vo.getOrganId()) + turnNull2Str(vo.getCode()) + turnNull2Str(itemEO.getCode());
                        if (curRoleRights.get(_key) != null) {
                            functionEO.setChecked(true);
                            vo.setChecked(true);
                        } else {
                            functionEO.setChecked(false);
                        }
                    }
                    vo.setFunctions(functionEOs);
                }
            }
        } else {
            Map<String, Set<FunctionEO>> _map = getCurUserHasRights();

            List<OrganCatalogVO> list = new ArrayList<OrganCatalogVO>();
            for (OrganCatalogVO vo : vos) {
                String key = turnNull2Str(vo.getOrganId()) + turnNull2Str(vo.getCode());
                if (!AppUtil.isEmpty(_map.get(key))) {
                    list.add(vo);
                }
            }

            for (OrganCatalogVO vo : list) {
                String key = turnNull2Str(vo.getOrganId()) + turnNull2Str(vo.getCode());
                String rolekey = turnNull2Str(roleId) + turnNull2Str(vo.getOrganId()) + turnNull2Str(vo.getCode());

                if (!AppUtil.isEmpty(curRoleRights.get(rolekey))) {
                    vo.setChecked(true);
                } else {
                    vo.setChecked(false);
                }

                List<FunctionEO> functionEOs = new ArrayList<FunctionEO>();
                if (null != _map.get(key)) {
                    functionEOs.addAll(_map.get(key));
                }

                for (FunctionEO _eo : functionEOs) {
                    String _key = turnNull2Str(roleId) + turnNull2Str(vo.getOrganId()) + turnNull2Str(vo.getCode()) + turnNull2Str(_eo.getAction());
                    _eo.setName(itemMap.get(_eo.getAction()));
                    if (curRoleRights.get(_key) != null) {
                        _eo.setChecked(true);
                        vo.setChecked(true);
                    } else {
                        _eo.setChecked(false);
                    }
                }

                vo.setFunctions(functionEOs);
            }

            vos = list;
        }
        return AppUtil.isEmpty(vos) ? new ArrayList<OrganCatalogVO>() : vos;
    }

    @Override
    public List<OrganCatalogVO> getInfoOpenRights(List<OrganCatalogVO> list) {
        boolean superAdmin = LoginPersonUtil.isSuperAdmin();
        boolean siteAdmin = LoginPersonUtil.isSiteAdmin();

        if (superAdmin || siteAdmin) {
            for (OrganCatalogVO vo : list) {
                vo.setSuperAdmin(true);
            }
            return list;
        }

        Map<String, Set<FunctionEO>> _map = getCurUserHasRights();
        _map = userInfoOpenRightsService.setInfoOpenFunction(_map,LoginPersonUtil.getSiteId());

        List<OrganCatalogVO> vos = new ArrayList<OrganCatalogVO>();
        for (OrganCatalogVO vo : list) {
            String key = turnNull2Str(vo.getOrganId()) + turnNull2Str(vo.getCode());
            if (!AppUtil.isEmpty(_map.get(key))) {
                List<FunctionEO> functionEOs = new ArrayList<FunctionEO>();
                if (null != _map.get(key)) {
                    functionEOs.addAll(_map.get(key));
                }
                vo.setFunctions(functionEOs);
                vos.add(vo);
            }
        }

        return vos;
    }

    /**
     * 获取当前用户拥有的权限
     *
     * @return
     */
    @Override
    public Map<String, Set<FunctionEO>> getCurUserHasRights() {

        if (null == itemMap || itemMap.isEmpty()) {
            List<DataDictItemEO> itemEOs = RightDictCache.get(PUBLIC_INFO_AUTH);
            itemMap = new HashMap<String, String>();
            for (DataDictItemEO itemEO : itemEOs) {
                itemMap.put(itemEO.getCode(), itemEO.getName());
            }
        }

        Long organId = LoginPersonUtil.getOrganId();
        Long userId = LoginPersonUtil.getUserId();
        Map<String, Set<FunctionEO>> map = new HashMap<String, Set<FunctionEO>>();
        List<RoleAssignmentEO> roles = roleAssignmentService.getAssignments(organId, userId);
        for (RoleAssignmentEO eo : roles) {
            Long[] roleIds = new Long[]{eo.getRoleId()};
            List<RbacInfoOpenRightsEO> rightsEOs = this.getEOsByRoleIds(roleIds);
            for (RbacInfoOpenRightsEO rightsEO : rightsEOs) {
                String key = turnNull2Str(rightsEO.getOrganId()) + turnNull2Str(rightsEO.getCode());
                Set<FunctionEO> functionEOs = map.get(key);
                if (functionEOs != null) {
                    if (!AppUtil.isEmpty(rightsEO.getOptCode())) {
                        FunctionEO functionEO = new FunctionEO();
                        functionEO.setAction(rightsEO.getOptCode());
                        functionEO.setName(itemMap.get(rightsEO.getOptCode()));
                        functionEOs.add(functionEO);
                    }
                } else {
                    functionEOs = new HashSet<FunctionEO>();
                    if (!AppUtil.isEmpty(rightsEO.getOptCode())) {
                        FunctionEO functionEO = new FunctionEO();
                        functionEO.setAction(rightsEO.getOptCode());
                        functionEO.setName(itemMap.get(rightsEO.getOptCode()));
                        functionEOs.add(functionEO);
                    }
                    map.put(key, functionEOs);
                }
            }
        }

        return map;
    }

    /**
     * 获取当前角色拥有的操作权限
     *
     * @param roleId
     * @return
     */
    private Map<String, RbacInfoOpenRightsEO> getCurRoleHasRights(Long roleId) {
        Long[] roleIds = new Long[]{roleId};
        List<RbacInfoOpenRightsEO> list = this.getEOsByRoleIds(roleIds);
        Map<String, RbacInfoOpenRightsEO> map = new HashMap<String, RbacInfoOpenRightsEO>();
        if (null == list) {
            return map;
        }
        for (RbacInfoOpenRightsEO eo : list) {
            String key = turnNull2Str(eo.getRoleId()) + turnNull2Str(eo.getOrganId()) + turnNull2Str(eo.getCode()) + turnNull2Str(eo.getOptCode());
            map.put(key, eo);
        }

        return map;
    }

    @Override
    public void saveRights(String rights, Long roleId, String organIds) {
        JSONArray json = JSONArray.fromObject(rights);
        List<RbacInfoOpenRightsEO> list = (List<RbacInfoOpenRightsEO>) JSONArray.toCollection(json, RbacInfoOpenRightsEO.class);

        this.delByRoleIdAndOrganId(roleId, organIds);
        this.saveEntities(list);
        CacheHandler.reload(RbacInfoOpenRightsEO.class.getName());
    }

    @Override
    public List<RbacInfoOpenRightsEO> getEOsByRoleIds(Long[] roleIds) {
        return iInfoOpenRightsDao.getEOsByRoleIds(roleIds);
    }

    @Override
    public void delByRoleIdAndOrganId(Long roleId, String organIds) {
        iInfoOpenRightsDao.delByRoleIdAndOrganId(roleId, organIds);
    }

    /**
     * 获取当前用户拥有的站点栏目项
     *
     * @return
     */
    public Long[] getCurUserHasIndicatorIds() {
        Long organId = LoginPersonUtil.getOrganId();
        Long userId = LoginPersonUtil.getUserId();

        List<CmsUserSiteOptEO> listSites = userSiteOptService.getOpts(organId, userId);
        if (listSites != null && listSites.size() > 0) {
            Long[] siteids = new Long[listSites.size()];
            int i = 0;
            for (CmsUserSiteOptEO eo : listSites) {
                siteids[i++] = eo.getSiteId();
            }

            return siteids;
        }
        return new Long[0];
    }

    private String turnNull2Str(Object obj) {
        return obj == null ? "" : String.valueOf(obj);
    }
}
