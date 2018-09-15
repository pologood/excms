package cn.lonsun.system.role.internal.service.impl;

import cn.lonsun.cache.client.CacheGroup;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.indicator.internal.entity.FunctionEO;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.publicInfo.internal.service.IPublicCatalogService;
import cn.lonsun.publicInfo.vo.OrganCatalogQueryVO;
import cn.lonsun.publicInfo.vo.OrganCatalogVO;
import cn.lonsun.site.site.internal.entity.SiteConfigEO;
import cn.lonsun.system.datadictionary.internal.entity.DataDictItemEO;
import cn.lonsun.system.role.internal.cache.RightDictCache;
import cn.lonsun.system.role.internal.dao.IUserInfoOpenRightsDao;
import cn.lonsun.system.role.internal.entity.RbacUserInfoOpenRightsEO;
import cn.lonsun.system.role.internal.service.IUserInfoOpenRightsService;
import cn.lonsun.util.LoginPersonUtil;
import net.sf.json.JSONArray;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author gu.fei
 * @version 2015-9-18 16:26
 */
@Service
public class UserInfoOpenRightsService extends MockService<RbacUserInfoOpenRightsEO> implements IUserInfoOpenRightsService {

    private static final String PUBLIC_INFO_AUTH = "public_info_auth";

    @Resource
    private IPublicCatalogService publicCatalogService;

    @Resource
    private IUserInfoOpenRightsDao userInfoOpenRightsDao;

    @Override
    public List<OrganCatalogVO> getInfoOpenRights(Long userId,Long organId) {
        Long siteId = LoginPersonUtil.getSiteId();
        List<OrganCatalogVO> vos = new ArrayList<OrganCatalogVO>();
        OrganCatalogQueryVO qvo = new OrganCatalogQueryVO();
        qvo.setOrganId(organId);
        qvo.setAll(false);
        qvo.setSiteId(siteId);
        qvo.setCatalog(true);
        List<OrganCatalogVO> catalogVOs = publicCatalogService.getOrganCatalogTree(qvo);
        vos.addAll(catalogVOs);
        if (null == organId) {
            IndicatorEO indicatorEO = CacheHandler.getEntity(IndicatorEO.class, siteId);
            SiteConfigEO siteConfigEO = CacheHandler.getEntity(SiteConfigEO.class, CacheGroup.CMS_INDICATORID, siteId);
            OrganCatalogVO vo = new OrganCatalogVO();
            vo.setId(Long.valueOf(siteConfigEO.getUnitIds()));
            vo.setName(indicatorEO.getName());
            vo.setType(IndicatorEO.Type.CMS_Site.toString());
            if (null != catalogVOs && !catalogVOs.isEmpty()) {
                vo.setIsParent(true);
            }
            vos.add(vo);
        }
        Map<String,Boolean> map = getCheckedRightsMap(userId);
        this.setFunctions(vos,map);
        return vos;
    }

    @Override
    public void saveInfoOpenRights(Long userId,String organIds,String rights) {
        JSONArray json = JSONArray.fromObject(rights);
        List<RbacUserInfoOpenRightsEO> list = (List<RbacUserInfoOpenRightsEO>) JSONArray.toCollection(json, RbacUserInfoOpenRightsEO.class);
        userInfoOpenRightsDao.delete(userId, organIds);
        this.saveEntities(list);
    }

    @Override
    public Map<String, Set<FunctionEO>> setInfoOpenFunction(Map<String, Set<FunctionEO>> map,Long siteId) {
        Map<String,String> itemMap = new HashMap<String, String>();
        List<DataDictItemEO> itemEOs = RightDictCache.get(PUBLIC_INFO_AUTH);
        for (DataDictItemEO itemEO : itemEOs) {
            itemMap.put(itemEO.getCode(), itemEO.getName());
        }
        if(null == map) {
            map = new HashMap<String, Set<FunctionEO>>();
        }
        Map<String,Object> params = new HashMap<String, Object>();
        params.put("userId",LoginPersonUtil.getUserId());
        if(null != siteId) {
            params.put("siteId",siteId);
        }
        List<RbacUserInfoOpenRightsEO> list = this.getEntities(RbacUserInfoOpenRightsEO.class,params);
        if(null != list && !list.isEmpty()) {
            for(RbacUserInfoOpenRightsEO eo : list) {
                String key = turnNull2Str(eo.getOrganId()) + turnNull2Str(eo.getCode());
                if(!"".equals(key)) {
                    Set<FunctionEO> set = map.get(key);
                    if(null == set) {
                        set = new HashSet<FunctionEO>();
                    }
                    if(null != eo.getOptCode()) {
                        FunctionEO f = new FunctionEO();
                        f.setAction(eo.getOptCode());
                        f.setName(itemMap.get(eo.getOptCode()));
                        f.setChecked(true);
                        set.add(f);
                    }
                    map.put(key,set);
                }
            }
        }
        return map;
    }

    /**
     * 设置信息公开操作权限
     * @param catalogs
     * @param map
     */
    private void setFunctions(List<OrganCatalogVO> catalogs,Map<String,Boolean> map) {
        if(null != catalogs) {
            List<DataDictItemEO> items = RightDictCache.get(PUBLIC_INFO_AUTH);
            for(OrganCatalogVO vo : catalogs) {
                String key = String.valueOf(vo.getOrganId()) + "_" + vo.getCode();
                Boolean a = map.get(key);
                if(null != a && a) {
                    vo.setChecked(true);
                }
                List<FunctionEO> fcs = new ArrayList<FunctionEO>();
                for(DataDictItemEO item : items) {
                    FunctionEO fc = new FunctionEO();
                    fc.setName(item.getName());
                    fc.setAction(item.getCode());
                    String nkey = key + "_" + item.getCode();
                    Boolean b = map.get(nkey);
                    if(null != b && b) {
                        fc.setChecked(true);
                    }
                    fcs.add(fc);
                }
                vo.setFunctions(fcs);
            }
        }
    }

    /**
     * 获取菜单
     * @param userId
     * @return
     */
    private Map<String,Boolean> getCheckedRightsMap(Long userId) {
        Map<String,Boolean> rstMap = new HashMap<String, Boolean>();
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("userId",userId);
        map.put("siteId",LoginPersonUtil.getSiteId());
        List<RbacUserInfoOpenRightsEO> list = this.getEntities(RbacUserInfoOpenRightsEO.class,map);
        if(null != list && !list.isEmpty()) {
            for(RbacUserInfoOpenRightsEO right : list) {
                String key = String.valueOf(right.getOrganId()) + "_" + right.getCode();
                if(!StringUtils.isEmpty(right.getOptCode())) {
                    key += "_" + right.getOptCode();
                }
                rstMap.put(key,true);
            }
        }
        return rstMap;
    }

    private String turnNull2Str(Object obj) {
        return obj == null ? "" : String.valueOf(obj);
    }
}
