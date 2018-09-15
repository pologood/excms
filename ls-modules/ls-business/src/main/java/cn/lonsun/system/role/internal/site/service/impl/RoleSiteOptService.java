package cn.lonsun.system.role.internal.site.service.impl;

import cn.lonsun.cache.client.CacheGroup;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.common.vo.TreeNodeVO;
import cn.lonsun.core.base.service.impl.BaseService;
import cn.lonsun.indicator.internal.entity.FunctionEO;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.rbac.internal.entity.RoleAssignmentEO;
import cn.lonsun.rbac.internal.service.IOrganService;
import cn.lonsun.rbac.internal.service.IRoleAssignmentService;
import cn.lonsun.site.site.internal.entity.ColumnMgrEO;
import cn.lonsun.site.site.internal.service.IColumnConfigService;
import cn.lonsun.site.site.vo.ColumnVO;
import cn.lonsun.system.datadictionary.internal.entity.DataDictEO;
import cn.lonsun.system.datadictionary.internal.entity.DataDictItemEO;
import cn.lonsun.system.role.internal.service.IRoleAsgService;
import cn.lonsun.system.role.internal.service.ISiteRightsService;
import cn.lonsun.system.role.internal.site.cache.DicSiteColOptCache;
import cn.lonsun.system.role.internal.site.dao.IRoleSiteOptDao;
import cn.lonsun.system.role.internal.site.entity.CmsRoleSiteOptEO;
import cn.lonsun.system.role.internal.site.entity.CmsUserSiteOptEO;
import cn.lonsun.system.role.internal.site.entity.vo.ColumnOpt;
import cn.lonsun.system.role.internal.site.entity.vo.SiteOpt;
import cn.lonsun.system.role.internal.site.service.IRoleSiteOptService;
import cn.lonsun.system.role.internal.site.service.IUserSiteOptService;
import cn.lonsun.util.LoginPersonUtil;
import cn.lonsun.util.SeparatorUtil;
import net.sf.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;

/**
 * @author gu.fei
 * @version 2015-10-31 13:58
 */
@Service
public class RoleSiteOptService extends BaseService<CmsRoleSiteOptEO> implements IRoleSiteOptService {

    // 系统内置账号
    public static String super_admin_code = "super_admin";
    // 系统内置账号
    public static String site_admin_code = "site_admin";

    @Autowired
    private IRoleSiteOptDao roleSiteOptDao;

    @Autowired
    private IColumnConfigService columnConfigService;

    @Autowired
    private IRoleAssignmentService roleAssignmentService;

    @Autowired
    private IUserSiteOptService userSiteOptService;

    @Autowired
    private IRoleAsgService roleAsgService;

    @Autowired
    private IOrganService organService;

    @Autowired
    private ISiteRightsService siteRightsService;

    @Override
    public Object getSiteOpt(Long roleId) {

        boolean superAdmin = LoginPersonUtil.isRoot() || LoginPersonUtil.isSuperAdmin();
        boolean siteAdmin = LoginPersonUtil.isSiteAdmin();
        List<ColumnMgrEO> vos = null;
        boolean allOpt = false;
        if (superAdmin) {
            //超管|root
            allOpt = true;
            vos = columnConfigService.getTree(null);
        } else if (siteAdmin) {
            //站点管理员
            Long[] ids = getCurUserHasSiteColumnIds(true);
            if (ids.length <= 0) {
                allOpt = false;
                Long[] newIds = getCurUserHasSiteColumnIds(false);
                if (newIds.length <= 0) {
                    return new ArrayList<ColumnVO>();
                }
                vos = columnConfigService.getTreeByIds(newIds);
            } else {
                allOpt = true;
                vos = columnConfigService.getColumnBySiteIds(ids);
            }
        } else {
            //普通管理员
            allOpt = false;
            Long[] ids = getCurUserHasSiteColumnIds(false);
            if (ids.length <= 0) {
                return new ArrayList<ColumnVO>();
            }
            vos = columnConfigService.getTreeByIds(ids);
        }
        for (ColumnMgrEO vo : vos) {
            String type = vo.getType();
            boolean site = type.equals(IndicatorEO.Type.CMS_Site.toString());
            boolean section = type.equals(IndicatorEO.Type.CMS_Section.toString());
            if (site) {
                String key = getKey(vo.getIndicatorId(), vo.getSiteId());
                vo.setChecked(isChecked(key, roleId));
                vo.setFunctions(getDictOpt(roleId, key, allOpt, IndicatorEO.Type.CMS_Site.toString()));
            }
            if (section) {
                String key = getKey(vo.getSiteId(), vo.getIndicatorId());
                vo.setChecked(isChecked(key, roleId));
                vo.setFunctions(getDictOpt(roleId, key, allOpt, IndicatorEO.Type.CMS_Section.toString()));
            }
        }

        return vos;
    }

    @Override
    public void delByRoleId(Long roleId) {
        roleSiteOptDao.delByRoleId(roleId);
    }

    @Override
    public Long[] getCurUserSiteIds() {
        Long organId = LoginPersonUtil.getOrganId();
        Long userId = LoginPersonUtil.getUserId();

        boolean siteAdmin = LoginPersonUtil.isSiteAdmin();

        if (siteAdmin) {
            List<CmsUserSiteOptEO> listSites = userSiteOptService.getOpts(organId, userId);
            if (listSites != null && listSites.size() > 0) {
                Long[] ids = new Long[listSites.size()];
                int i = 0;
                for (CmsUserSiteOptEO eo : listSites) {
                    ids[i++] = eo.getSiteId();
                }

                return ids;
            }
        }

        List<RoleAssignmentEO> roles = roleAssignmentService.getAssignments(organId, userId);
        Long[] roleIds = new Long[roles.size()];
        int count = 0;
        for (RoleAssignmentEO eo : roles) {
            roleIds[count++] = eo.getRoleId();
        }
        return roleSiteOptDao.getSiteIdsByRoleIds(roleIds);
    }

    @Override
    public List<SiteOpt> getCurUserSiteOpt() {
        Long organId = LoginPersonUtil.getOrganId();
        Long userId = LoginPersonUtil.getUserId();

        List<RoleAssignmentEO> roles = roleAssignmentService.getAssignments(organId, userId);
        Map<Long, String> map = new HashMap<Long, String>();

        for (RoleAssignmentEO eo : roles) {
            List<CmsRoleSiteOptEO> optEOs = getSiteOptByRoleId(eo.getRoleId());
            for (CmsRoleSiteOptEO eo1 : optEOs) {
                Long key = eo1.getSiteId();
                if (key == null) {
                    continue;
                }
                if (map.get(key) == null) {
                    map.put(key, eo1.getOpt());
                } else {
                    String opt = eo1.getOpt() + SeparatorUtil.OPT_TYPE_SAVE + map.get(key);
                    String[] strs = arrayUnique(opt.split(SeparatorUtil.OPT_TYPE));
                    String optNew = null;
                    for (String str : strs) {
                        if (optNew == null) {
                            optNew = str;
                        } else {
                            optNew += SeparatorUtil.OPT_TYPE_SAVE + str;
                        }
                    }
                    map.put(key, optNew);
                }
            }
        }

        List<SiteOpt> list = new ArrayList<SiteOpt>();
        for (Map.Entry<Long, String> entry : map.entrySet()) {
            SiteOpt siteOpt = new SiteOpt();
            siteOpt.setSiteId(entry.getKey());
            siteOpt.setOpt(entry.getValue());
            list.add(siteOpt);
        }

        return list;
    }

    private boolean isChecked(String key, Long roleId) {
        CmsRoleSiteOptEO optEO = getCurRoleHasOpt(roleId).get(key);
        return AppUtil.isEmpty(optEO) ? false : true;
    }

    @Override
    public List<ColumnOpt> getColumnOptBySite(Long siteId) {

        List<ColumnOpt> list = new ArrayList<ColumnOpt>();

        Long organId = LoginPersonUtil.getOrganId();
        Long userId = LoginPersonUtil.getUserId();
        boolean siteAdmin = LoginPersonUtil.isSiteAdmin();
        boolean superAdmin = LoginPersonUtil.isSuperAdmin();

        if (siteAdmin) {
            CmsUserSiteOptEO eo = userSiteOptService.getOpts(organId, userId, siteId);
            if (eo != null) {
                List<ColumnMgrEO> columnVOs = columnConfigService.getTree(siteId);

                for (ColumnMgrEO vo : columnVOs) {
                    ColumnOpt columnOpt = new ColumnOpt();
                    columnOpt.setColumnId(vo.getIndicatorId());
                    columnOpt.setOpt(getDict());
                    list.add(columnOpt);
                }
                return list;
            }
        }

        if (superAdmin) {
            List<ColumnMgrEO> columnVOs = columnConfigService.getTree(siteId);
            for (ColumnMgrEO vo : columnVOs) {
                ColumnOpt columnOpt = new ColumnOpt();
                columnOpt.setColumnId(vo.getIndicatorId());
                columnOpt.setOpt("super");
                list.add(columnOpt);
            }
            return list;
        }

        List<RoleAssignmentEO> roles = roleAssignmentService.getAssignments(organId, userId);
        Map<Long, String> map = new HashMap<Long, String>();

        for (RoleAssignmentEO eo : roles) {
            List<CmsRoleSiteOptEO> optEOs = roleSiteOptDao.getRoleOpt(eo.getRoleId(), siteId);
            for (CmsRoleSiteOptEO eo1 : optEOs) {
                Long key = eo1.getColumnId();
                if (key == null) continue;
                if (map.get(key) == null) {
                    map.put(key, eo1.getOpt());
                } else {
                    String opt = eo1.getOpt() + SeparatorUtil.OPT_TYPE_SAVE + map.get(key);
                    String[] strs = arrayUnique(opt.split(SeparatorUtil.OPT_TYPE));
                    String optNew = null;
                    for (String str : strs) {
                        if (optNew == null) {
                            optNew = str;
                        } else {
                            optNew += SeparatorUtil.OPT_TYPE_SAVE + str;
                        }
                    }
                    map.put(key, optNew);
                }
            }
        }

        for (Map.Entry<Long, String> entry : map.entrySet()) {
            ColumnOpt columnOpt = new ColumnOpt();
            columnOpt.setColumnId(entry.getKey());
            columnOpt.setOpt(entry.getValue());
            list.add(columnOpt);
        }

        return list;
    }

    @Override
    public void saveOpt(String strJson, Long roleId) {
        JSONArray json = JSONArray.fromObject(strJson);
        List<CmsRoleSiteOptEO> list = (List<CmsRoleSiteOptEO>) JSONArray.toCollection(json, CmsRoleSiteOptEO.class);

        this.delByRoleId(roleId);
        this.saveEntities(list);

        CacheHandler.reload(CmsRoleSiteOptEO.class.getName());
    }

    @Override
    public List<TreeNodeVO> getUserAuthForColumn(Long unitId, Long columnId) {
        List<TreeNodeVO> list = organService.getUnitOrganAndUser(unitId);
        List<TreeNodeVO> users = new ArrayList<TreeNodeVO>();
        for (TreeNodeVO vo : list) {
            if (!vo.getType().equals(TreeNodeVO.Type.Person.toString())) {
                continue;
            }
            if (isAuthForColumn(vo.getOrganId(), vo.getUserId(), columnId)) {
                users.add(vo);
            }
        }
        return users;
    }

    /*
            * 获取栏目操作权限拼装字符串
            * */
    private String getDict() {
        String dictCode = DicSiteColOptCache.DICT_COLUMN_CODE;
        DataDictEO dictEO = CacheHandler.getEntity(DataDictEO.class, CacheGroup.CMS_CODE, dictCode);
        List<DataDictItemEO> dicts = CacheHandler.getList(DataDictItemEO.class, CacheGroup.CMS_PARENTID, dictEO.getDictId());
        String rst = null;

        for (DataDictItemEO eo : dicts) {
            if (rst == null) {
                rst = eo.getValue();
            } else {
                rst += SeparatorUtil.OPT_TYPE_SAVE + eo.getValue();
            }
        }

        return rst;
    }

    /*
    * 获取数据字典操作值
    * */
    private List<FunctionEO> getDictOpt(Long roleId, String key, boolean allOpt, String type) {

        List<FunctionEO> funcs = new ArrayList<FunctionEO>();
        if (allOpt) {
            String dictCode = DicSiteColOptCache.DICT_COLUMN_CODE;

            if (type.equals(IndicatorEO.Type.CMS_Site.toString())) {
                dictCode = DicSiteColOptCache.DICT_SITE_CODE;
            }

            DataDictEO dictEO = CacheHandler.getEntity(DataDictEO.class, CacheGroup.CMS_CODE, dictCode);

            List<DataDictItemEO> dicts = CacheHandler.getList(DataDictItemEO.class, CacheGroup.CMS_PARENTID, dictEO.getDictId());

            CmsRoleSiteOptEO optEO = getCurRoleHasOpt(roleId).get(key);
            for (DataDictItemEO eo : dicts) {
                FunctionEO fEO = new FunctionEO();
                fEO.setAction(eo.getValue());
                fEO.setName(eo.getName());
                String opt = AppUtil.isEmpty(optEO) ? "" : optEO.getOpt();
                boolean flag = opt != null && opt.length() > 0 && opt.contains(eo.getCode());
                if (flag) {
                    fEO.setChecked(true);
                } else {
                    fEO.setChecked(false);
                }
                funcs.add(fEO);
            }
        } else {
            Map<String, CmsRoleSiteOptEO> userhas = getCurUserHasOpt();
            Map<String, CmsRoleSiteOptEO> rolehas = getCurRoleHasOpt(roleId);
            CmsRoleSiteOptEO optUserHasEO = userhas.get(key);
            CmsRoleSiteOptEO optRoleHasEO = rolehas.get(key);

            String userOpt = AppUtil.isEmpty(optUserHasEO) ? "" : optUserHasEO.getOpt();
            String roleOpt = AppUtil.isEmpty(optRoleHasEO) ? "" : optRoleHasEO.getOpt();


            if (userOpt != null && userOpt.length() > 0) {
                String[] opt = userOpt.split(SeparatorUtil.OPT_TYPE);
                for (String str : opt) {
                    String dicKey = null;
                    if (type.equals(IndicatorEO.Type.CMS_Site.toString())) {
                        dicKey = str + "_" + DicSiteColOptCache.DICT_SITE_CODE;
                    } else {
                        dicKey = str + "_" + DicSiteColOptCache.DICT_COLUMN_CODE;
                    }
                    DataDictItemEO dictEO = DicSiteColOptCache.get(dicKey);
                    FunctionEO fEO = new FunctionEO();
                    fEO.setAction(str);
                    fEO.setName(dictEO.getName());
                    boolean flag = roleOpt != null && roleOpt.length() > 0 && roleOpt.contains(str);
                    if (flag) {
                        fEO.setChecked(true);
                    } else {
                        fEO.setChecked(false);
                    }
                    funcs.add(fEO);
                }
            }

        }
        return funcs;
    }

    /*
    * boolean : flag
    * true ： 返回用户拥有的站点权限
    * false ： 返回当前用户所在角色的权限
    * */
    private Long[] getCurUserHasSiteColumnIds(boolean flag) {

        Long organId = LoginPersonUtil.getOrganId();
        Long userId = LoginPersonUtil.getUserId();

        if (flag) {
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

        List<RoleAssignmentEO> roles = roleAssignmentService.getAssignments(organId, userId);
        Set<Long> set = new HashSet<Long>();

        for (RoleAssignmentEO eo : roles) {
            List<CmsRoleSiteOptEO> optEOs = roleSiteOptDao.getRoleOpt(eo.getRoleId());
            for (CmsRoleSiteOptEO eo1 : optEOs) {
                set.add(eo1.getSiteId());
                set.add(eo1.getColumnId());
            }
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

    /**
     * 判断用户是否有栏目的操作权限
     *
     * @param organId
     * @param userId
     * @param columnId
     * @return
     */
    private boolean isAuthForColumn(Long organId, Long userId, Long columnId) {
        // 判断超管
        boolean isSuperAdmin = roleAsgService.confirmRole(super_admin_code, organId, userId);
        boolean isSiteAdmin = roleAsgService.confirmRole(site_admin_code, organId, userId);
        if (isSuperAdmin || isSiteAdmin) {
            return true;
        }

        List<RoleAssignmentEO> roles = roleAssignmentService.getAssignments(organId, userId);
        for (RoleAssignmentEO eo : roles) {
            List<ColumnMgrEO> optEOs = (List<ColumnMgrEO> )siteRightsService.getSiteRights(eo.getRoleId());
            for (ColumnMgrEO opt : optEOs) {
                if (columnId != null && opt.getIndicatorId() != null && opt.getIndicatorId().equals(columnId)) {
                    return true;
                }
            }
        }

        return false;
    }

    /*
    * 获取当前用户拥有的权限
    * */
    private Map<String, CmsRoleSiteOptEO> getCurUserHasOpt() {

        Long organId = LoginPersonUtil.getOrganId();
        Long userId = LoginPersonUtil.getUserId();

        List<RoleAssignmentEO> roles = roleAssignmentService.getAssignments(organId, userId);
        Map<String, CmsRoleSiteOptEO> map = new HashMap<String, CmsRoleSiteOptEO>();

        for (RoleAssignmentEO eo : roles) {
            List<CmsRoleSiteOptEO> optEOs = getSiteOptByRoleId(eo.getRoleId());
            for (CmsRoleSiteOptEO eo1 : optEOs) {
                String key = getKey(eo1.getSiteId(), eo1.getColumnId());
                if (map.get(key) == null) {
                    map.put(key, eo1);
                } else {
                    CmsRoleSiteOptEO eo2 = map.get(key);
                    String opt = eo1.getOpt() + SeparatorUtil.OPT_TYPE_SAVE + eo2.getOpt();
                    String[] strs = arrayUnique(opt.split(SeparatorUtil.OPT_TYPE));
                    String optNew = null;
                    for (String str : strs) {
                        if (optNew == null) {
                            optNew = str;
                        } else {
                            optNew += SeparatorUtil.OPT_TYPE_SAVE + str;
                        }
                    }
                    eo2.setOpt(optNew);
                    map.put(key, eo2);
                }
            }
        }

        return map;
    }

    private String getKey(Long siteId, Long columnId) {

        String siteKey = siteId == null ? "" : String.valueOf(siteId);
        String columnKey = columnId == null ? "" : String.valueOf(columnId);
        String key = null;

        if (siteId != null && columnId != null) {
            key = siteKey + "_" + columnKey;
        } else {
            key = siteKey + columnKey;
        }

        return key;
    }

    //去除数组中重复的记录
    private String[] arrayUnique(String[] a) {
        // array_unique
        List<String> list = new LinkedList<String>();
        for (int i = 0; i < a.length; i++) {
            if (a[i] == null || a[i].length() <= 0 || a[i].equals("null")) {
                continue;
            }
            if (!list.contains(a[i])) {
                list.add(a[i]);
            }
        }
        return (String[]) list.toArray(new String[list.size()]);
    }

    /*
    * 获取当前角色拥有的权限
    * */
    private Map<String, CmsRoleSiteOptEO> getCurRoleHasOpt(Long roleId) {

        List<CmsRoleSiteOptEO> optEOs = getSiteOptByRoleId(roleId);
        Map<String, CmsRoleSiteOptEO> map = new HashMap<String, CmsRoleSiteOptEO>();

        for (CmsRoleSiteOptEO eo : optEOs) {
            String key = getKey(eo.getSiteId(), eo.getColumnId());
            map.put(key, eo);
        }

        return map;
    }

    private List<CmsRoleSiteOptEO> getSiteOptByRoleId(Long roleId) {
        List<CmsRoleSiteOptEO> optEOs = CacheHandler.getList(CmsRoleSiteOptEO.class, CacheGroup.CMS_PARENTID, roleId);
        return optEOs == null ? new ArrayList<CmsRoleSiteOptEO>() : optEOs;
    }

    private static <T> List<T> deepcopy(List<T> src) {
        try {
            ByteArrayOutputStream byteout = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(byteout);
            out.writeObject(src);
            ByteArrayInputStream bytein = new ByteArrayInputStream(byteout
                .toByteArray());
            ObjectInputStream in = new ObjectInputStream(bytein);
            List<T> dest = (List<T>) in.readObject();
            return dest;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<T>();
        }
    }
}
