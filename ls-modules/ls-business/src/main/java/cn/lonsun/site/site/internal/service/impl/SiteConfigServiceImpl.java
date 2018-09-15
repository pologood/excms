package cn.lonsun.site.site.internal.service.impl;

import cn.lonsun.cache.client.CacheGroup;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.indicator.internal.dao.IIndicatorDao;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.site.site.internal.dao.ISiteConfigDao;
import cn.lonsun.site.site.internal.entity.SiteConfigEO;
import cn.lonsun.site.site.internal.entity.SiteMgrEO;
import cn.lonsun.site.site.internal.service.ISiteConfigService;
import cn.lonsun.site.site.vo.SiteVO;
import cn.lonsun.system.systemlog.internal.entity.CmsLogEO;
import cn.lonsun.util.SysLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 站点配置业务实现类<br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2015-8-24 <br/>
 */
@Service("siteConfigService")
public class SiteConfigServiceImpl extends MockService<SiteConfigEO> implements ISiteConfigService {

    @Autowired
    private ISiteConfigDao siteConfigDao;

    @Autowired
    private IIndicatorDao indicatorDao;

    @Resource(name = "ex_8_IndicatorServiceImpl")
    private cn.lonsun.rbac.indicator.service.IIndicatorService indicatorService;

    /**
     * 检查站点名称是否存在
     *
     * @param siteName
     * @param parentId
     * @param indicatorId
     * @return
     */
    @Override
    public boolean checkSiteNameExist(String siteName, Long parentId, Long indicatorId) {
        Map<String, Object> map = new HashMap<String, Object>();
        String name = siteName.trim();
        map.put("name", name);
        map.put("parentId", parentId);
        map.put("type", IndicatorEO.Type.CMS_Site.toString());
        map.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
        List<IndicatorEO> list = indicatorService.getEntities(IndicatorEO.class, map);
        if (list == null || list.size() == 0) {
            return true;
        }
        IndicatorEO eo = list.get(0);
        if (eo.getIndicatorId().equals(indicatorId)) {
            return true;
        }
        return false;
    }

    /*
     * @Override public SiteConfigEO getSiteConfigByIndicatorId(Long
     * indicatorId) { return
     * siteConfigDao.getSiteConfigByIndicatorId(indicatorId); }
     */

    /**
     * 删除站点
     *
     * @param indicatorId
     */
    @Override
    public void deleteEO(Long indicatorId) {
        // 本站点
        IndicatorEO indicatorEO = CacheHandler.getEntity(IndicatorEO.class, indicatorId);
        // 父站点
        IndicatorEO pEO = CacheHandler.getEntity(IndicatorEO.class, indicatorEO.getParentId());
        // 配置类
        Map<String, Object> map1 = new HashMap<String, Object>();
        map1.put("indicatorId", indicatorId);
        /*
         * List<SiteConfigEO> list1=getEntities(SiteConfigEO.class,
         * map1);CacheHandler. if(list1==null||list1.size()==0){ return; }
         */
        SiteConfigEO siteConfigEO = CacheHandler.getEntity(SiteConfigEO.class, CacheGroup.CMS_INDICATORID, indicatorId);
        if (siteConfigEO != null) {
            siteConfigDao.delete(SiteConfigEO.class, siteConfigEO.getSiteConfigId());
        }
        indicatorService.delete(indicatorId);
        SysLog.log("删除站点 >> ID：" + indicatorId + ",名称：" + indicatorEO.getName(),
            "IndicatorEO", CmsLogEO.Operation.Delete.toString());
        // 如果父节点没有了子节点，将isParent设为0
        if (pEO != null) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("parentId", pEO.getParentId());
            List<IndicatorEO> list = CacheHandler.getList(IndicatorEO.class, CacheGroup.CMS_PARENTID, pEO.getIndicatorId());
            if (list == null || list.size() == 0) {
                pEO.setIsParent(0);
                indicatorService.save(pEO);
            }
        }
    }

    /**
     * 保存站点
     *
     * @param siteVO
     * @return
     */
    @Override
    public SiteConfigEO saveEO(SiteMgrEO siteVO) {
        IndicatorEO indicatorEO = indicatorService.getEntity(IndicatorEO.class,siteVO.getIndicatorId());
        IndicatorEO oldIndicator = new IndicatorEO();
        if(null == indicatorEO){
            indicatorEO = new IndicatorEO();
        }
        AppUtil.copyProperties(oldIndicator, indicatorEO);
        Long parentId = siteVO.getParentId();
        // 添加站点子节点,修改父节点的属性,添加父节点
        if (parentId != null) {
            IndicatorEO pindicatorEO = CacheHandler.getEntity(IndicatorEO.class, parentId);
            if (pindicatorEO != null && pindicatorEO.getIsParent() == 0) {
                pindicatorEO.setIsParent(1);
                indicatorService.save(pindicatorEO);
            }
        } else {
            siteVO.setParentId(1L);
        }
        // 添加站点子节点
//        IndicatorEO indicatorEO = new IndicatorEO();

        AppUtil.copyProperties(indicatorEO, siteVO);
        indicatorEO.setType(IndicatorEO.Type.CMS_Site.toString());
        indicatorService.save(indicatorEO);
        SiteConfigEO eo = saveSiteConfig(siteVO, indicatorEO,oldIndicator);
        return eo;
    }

    /**
     * 更新站点
     *
     * @param siteVO
     * @return
     */
    @Override
    public SiteConfigEO updateEO(SiteMgrEO siteVO) {
        IndicatorEO oldIndicator = indicatorService.getEntity(IndicatorEO.class,siteVO.getIndicatorId());

        // 添加站点子节点
        IndicatorEO indicatorEO = new IndicatorEO();
        AppUtil.copyProperties(indicatorEO, siteVO);
        indicatorDao.merge(indicatorEO);
        SiteConfigEO eo = saveSiteConfig(siteVO, indicatorEO,oldIndicator);
        return eo;
    }

    //保存站点配置信息
    public SiteConfigEO saveSiteConfig(SiteMgrEO siteVO, IndicatorEO indicatorEO,IndicatorEO oldIndicator) {
        SiteConfigEO configEO = new SiteConfigEO();
        String oldSiteTitle = "";
        String oldSiteIDCode = "";
        String oldKeyWords = "";
        String oldDescription = "";
        StringBuffer logStr = new StringBuffer();
        // 修改
        if (siteVO.getSiteConfigId() != null) {
            configEO = siteConfigDao.getEntity(SiteConfigEO.class, siteVO.getSiteConfigId());
            oldSiteIDCode = configEO.getSiteIDCode();
            oldKeyWords = configEO.getKeyWords();
            oldDescription = configEO.getDescription();
            //configEO = CacheHandler.getEntity(SiteConfigEO.class, siteVO.getSiteConfigId());
        }
        if(null != oldIndicator){
            oldSiteTitle = oldIndicator.getName();
        }
        configEO.setSiteTitle(siteVO.getSiteTitle());
        configEO.setIndicatorId(indicatorEO.getIndicatorId());
        configEO.setKeyWords(siteVO.getKeyWords());
        configEO.setDescription(siteVO.getDescription());
        configEO.setSiteIDCode(siteVO.getSiteIDCode());

        if (IndicatorEO.Type.CMS_Site.toString().equals(siteVO.getType())) {//标准站
            configEO.setIsVideoTrans(siteVO.getIsVideoTrans());
            configEO.setVideoTransUrl(siteVO.getVideoTransUrl());
            configEO.setUnitNames(siteVO.getUnitNames());
            configEO.setUnitIds(siteVO.getUnitIds());
            configEO.setIndexTempId(siteVO.getIndexTempId());
            configEO.setCommentTempId(siteVO.getCommentTempId());
            configEO.setErrorTempId(siteVO.getErrorTempId());
            configEO.setPublicTempId(siteVO.getPublicTempId());
            configEO.setSearchTempId(siteVO.getSearchTempId());
            configEO.setMemberId(siteVO.getMemberId());
            configEO.setStationId(siteVO.getStationId());
            configEO.setStationPwd(siteVO.getStationPwd());
            configEO.setIsWap(siteVO.getIsWap());
            configEO.setWapTempId(siteVO.getWapTempId());
            configEO.setWapPublicTempId(siteVO.getWapPublicTempId());
            configEO.setPhoneTempId(siteVO.getPhoneTempId());
            configEO.setSpecialId(siteVO.getSpecialId());
            configEO.setSiteType(siteVO.getSiteType());
        } else {//虚拟子站
            configEO.setUnitNames(siteVO.getUnitNames());
            configEO.setUnitIds(siteVO.getUnitIds());
            configEO.setComColumnId(siteVO.getComColumnId());
            configEO.setSiteTempId(siteVO.getSiteTempId());
        }
        // AppUtil.copyProperties(configEO,siteVO);
        if (siteVO.getSiteConfigId() == null) {
            siteConfigDao.save(configEO);
            SysLog.log("添加站点配置信息 >> ID：" + configEO.getSiteConfigId(),
                "SiteConfigEO", CmsLogEO.Operation.Add.toString());
        } else {
            siteConfigDao.update(configEO);
            sysLogInfo(logStr,"站点名称",oldSiteTitle,indicatorEO.getName());
            sysLogInfo(logStr,"站点标识符",oldSiteIDCode,configEO.getSiteIDCode());
            sysLogInfo(logStr,"关键词",oldKeyWords,configEO.getKeyWords());
            sysLogInfo(logStr,"描述",oldDescription,configEO.getDescription());

            SysLog.log("【站群管理】更新站点信息，站点："+ oldSiteTitle + logStr.toString(),
                    "SiteConfigEO", CmsLogEO.Operation.Update.toString());
        }
        return configEO;
    }

    public StringBuffer sysLogInfo(StringBuffer logStr,String logInfo,String old,String now){
        if(!AppUtil.isEmpty(old) && !AppUtil.isEmpty(now) && !old.equals(now)){
            logStr.append(",修改"+logInfo+"为:"+now);
        }
        return logStr;
    }

    /**
     * 获取排序号
     *
     * @param parentId
     * @param isSub
     * @return
     */
    public Integer getNewSortNum(Long parentId, boolean isSub) {
        return siteConfigDao.getNewSortNum(parentId, isSub);
    }

    /**
     * 获取站点树（异步）
     *
     * @param indicatorId
     * @return
     */
    @Override
    public List<SiteVO> getSiteTree(Long indicatorId) {
        return siteConfigDao.getSiteTree(indicatorId);
    }

    /**
     * 获取所有站点树
     *
     * @return
     */
    @Override
    public List<IndicatorEO> getAllSites() {
        List<IndicatorEO> list = CacheHandler.getList(IndicatorEO.class, CacheGroup.CMS_TYPE.toString(), IndicatorEO.Type.CMS_Site.toString());
        List<IndicatorEO> list1 = CacheHandler.getList(IndicatorEO.class, CacheGroup.CMS_TYPE.toString(), IndicatorEO.Type.SUB_Site.toString());
        if (list != null) {
            if (list1 != null) {
                list.addAll(list1);
            }
        }
        return list;
    }

    /**
     * 保存虚拟子站
     *
     * @param siteVO
     * @return
     */
    @Override
    public SiteConfigEO saveSubEO(SiteMgrEO siteVO) {
        IndicatorEO oldIndicator = indicatorService.getEntity(IndicatorEO.class,siteVO.getIndicatorId());
        Long parentId = siteVO.getParentId();
        // 添加站点子节点,修改父节点的属性
        if (parentId != null) {
            IndicatorEO pindicatorEO = CacheHandler.getEntity(IndicatorEO.class, parentId);
            if (pindicatorEO != null && pindicatorEO.getIsParent() == 0) {
                pindicatorEO.setIsParent(1);
                indicatorService.save(pindicatorEO);
            }
        } else {
            siteVO.setParentId(1L);
        }
        // 添加站点子节点
        IndicatorEO indicatorEO = new IndicatorEO();
        AppUtil.copyProperties(indicatorEO, siteVO);
        indicatorEO.setType(IndicatorEO.Type.SUB_Site.toString());
        indicatorService.save(indicatorEO);
        SiteConfigEO eo = saveSiteConfig(siteVO, indicatorEO,oldIndicator);
        return eo;
    }

    /**
     * 根据公共栏目获取绑定的站点
     *
     * @param comColumnId
     * @return
     */
    @Override
    public List<SiteMgrEO> getByComColumnId(Long comColumnId) {
        return siteConfigDao.getByComColumnId(comColumnId);
    }

    @Override
    public SiteMgrEO getById(Long indicatorId) {
        return siteConfigDao.getById(indicatorId);
    }

    @Override
    public Pagination getSiteInfos(Long pageIndex, Integer pageSize){
        return siteConfigDao.getSiteInfos(pageIndex,pageSize);
    }


}