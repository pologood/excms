package cn.lonsun.system.role.internal.service.impl;

import cn.lonsun.core.base.service.impl.BaseService;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.indicator.internal.entity.FunctionEO;
import cn.lonsun.site.site.internal.entity.ColumnMgrEO;
import cn.lonsun.site.site.internal.service.IColumnConfigService;
import cn.lonsun.system.datadictionary.internal.entity.DataDictItemEO;
import cn.lonsun.system.role.internal.cache.RightDictCache;
import cn.lonsun.system.role.internal.dao.IUserSiteRightsDao;
import cn.lonsun.system.role.internal.entity.RbacUserSiteRightsEO;
import cn.lonsun.system.role.internal.service.IUserSiteRightsService;
import cn.lonsun.util.LoginPersonUtil;
import net.sf.json.JSONArray;
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
@Service
public class UserSiteRightsService extends BaseService<RbacUserSiteRightsEO> implements IUserSiteRightsService {

    @Resource
    private IUserSiteRightsDao userSiteRightsDao;

    @Resource
    private IColumnConfigService columnConfigService;

    @Override
    public List<ColumnMgrEO> getSiteRights(Long userId) {
        Long[] siteIds = {LoginPersonUtil.getSiteId()};
        List<ColumnMgrEO> columnMgrs = columnConfigService.getColumnBySiteIds(siteIds);
        if (null != columnMgrs && !columnMgrs.isEmpty()) {
            Map<String, Boolean> map = getCheckedRightsMap(userId, LoginPersonUtil.getSiteId());
            for (ColumnMgrEO column : columnMgrs) {
                List<FunctionEO> functionEOs = new ArrayList<FunctionEO>();
                String key = String.valueOf(column.getIndicatorId());
                Boolean a = map.get(key);
                if (null != a && a) {
                    column.setChecked(true);
                }
                List<DataDictItemEO> items = this.getDictOpt(column.getColumnTypeCode());
                if (null != items) {
                    for (DataDictItemEO item : items) {
                        FunctionEO functionEO = new FunctionEO();
                        functionEO.setName(item.getName());
                        functionEO.setAction(item.getCode());
                        String nkey = key + "_" + item.getCode();
                        Boolean b = map.get(nkey);
                        if (null != b && b) {
                            functionEO.setChecked(true);
                        }
                        functionEOs.add(functionEO);
                    }
                    column.setFunctions(functionEOs);
                }
            }
        }
        return columnMgrs;
    }

    @Override
    public List<ColumnMgrEO> getCheckSiteRights(Long userId) {
        return this.getCheckSiteRights(userId, null);
    }

    @Override
    public List<ColumnMgrEO> getCheckSiteRights(Long userId, Long siteId) {
        List<Long> ids = userSiteRightsDao.getInicatorIdList(userId, siteId);
        List<ColumnMgrEO> mgrs = columnConfigService.getTreeByIds(ids.toArray(new Long[ids.size()]));
        if (null != mgrs && !mgrs.isEmpty()) {
            Map<String, Boolean> map = getCheckedRightsMap(userId, LoginPersonUtil.getSiteId());
            for (ColumnMgrEO mgr : mgrs) {
                List<FunctionEO> functionEOs = new ArrayList<FunctionEO>();
                List<DataDictItemEO> items = this.getDictOpt(mgr.getColumnTypeCode());
                if (null != items) {
                    for (DataDictItemEO item : items) {
                        FunctionEO functionEO = new FunctionEO();
                        functionEO.setName(item.getName());
                        functionEO.setAction(item.getCode());
                        String nkey = mgr.getIndicatorId() + "_" + item.getCode();
                        Boolean b = map.get(nkey);
                        if (null != b && b) {
                            functionEO.setChecked(true);
                        }
                        functionEOs.add(functionEO);
                    }
                    mgr.setFunctions(functionEOs);
                }
            }
        }
        return mgrs;
    }

    @Override
    public void saveSiteRights(Long userId, String rights) {
        JSONArray json = JSONArray.fromObject(rights);
        List<RbacUserSiteRightsEO> list = (List<RbacUserSiteRightsEO>) JSONArray.toCollection(json, RbacUserSiteRightsEO.class);
        userSiteRightsDao.deleteByUserId(userId);
        this.saveEntities(list);
    }

    /**
     * 获取菜单
     *
     * @param userId
     * @return
     */
    @Override
    public Map<String, Boolean> getCheckedRightsMap(Long userId, Long siteId) {
        Map<String, Boolean> rstMap = new HashMap<String, Boolean>();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("userId", userId);
        if (null != siteId) {
            map.put("siteId", siteId);
        }
        List<RbacUserSiteRightsEO> list = this.getEntities(RbacUserSiteRightsEO.class, map);
        if (null != list && !list.isEmpty()) {
            for (RbacUserSiteRightsEO right : list) {
                String key = String.valueOf(right.getIndicatorId());
                if (!StringUtils.isEmpty(right.getOptCode())) {
                    key += "_" + right.getOptCode();
                }
                rstMap.put(key, true);
            }
        }
        return rstMap;
    }

    /**
     * 根据字典编码获取字典项
     *
     * @param code
     * @return
     */
    private List<DataDictItemEO> getDictOpt(String code) {
        return RightDictCache.get(code);
    }
}
