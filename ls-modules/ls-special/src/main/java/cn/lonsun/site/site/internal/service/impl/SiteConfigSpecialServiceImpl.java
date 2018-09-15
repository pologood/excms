package cn.lonsun.site.site.internal.service.impl;

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.indicator.internal.dao.IIndicatorDao;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.rbac.indicator.service.IIndicatorSpecialService;
import cn.lonsun.site.site.internal.entity.SiteConfigEO;
import cn.lonsun.site.site.internal.entity.SiteMgrEO;
import cn.lonsun.site.site.internal.service.ISiteConfigSpecialService;
import cn.lonsun.system.systemlog.internal.entity.CmsLogEO;
import cn.lonsun.util.SysLog;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by Administrator on 2017/9/26.
 */
@Service
public class SiteConfigSpecialServiceImpl extends MockService<SiteConfigEO> implements ISiteConfigSpecialService {

    @Resource
    private IIndicatorDao indicatorDao;

    @Resource
    private IIndicatorSpecialService indicatorSpecialService;

    @Override
    public SiteConfigEO saveEO(SiteMgrEO siteVO) {
        Long parentId = siteVO.getParentId();
        // 添加站点子节点,修改父节点的属性,添加父节点
        if (parentId != null) {
            IndicatorEO pindicatorEO = CacheHandler.getEntity(IndicatorEO.class, parentId);
            if (pindicatorEO != null && pindicatorEO.getIsParent() == 0) {
                pindicatorEO.setIsParent(1);
                indicatorSpecialService.save(pindicatorEO);
            }
        } else {
            siteVO.setParentId(1L);
        }
        // 添加站点子节点
        IndicatorEO indicatorEO = new IndicatorEO();
        AppUtil.copyProperties(indicatorEO, siteVO);
        indicatorEO.setType(IndicatorEO.Type.CMS_Site.toString());
        indicatorSpecialService.save(indicatorEO);
        SiteConfigEO eo = saveSiteConfig(siteVO, indicatorEO,siteVO.getIndicatorId());
        return eo;
    }

    @Override
    public SiteConfigEO updateEO(SiteMgrEO siteVO) {
        // 添加站点子节点
        IndicatorEO indicatorEO = new IndicatorEO();
        AppUtil.copyProperties(indicatorEO, siteVO);
        indicatorDao.merge(indicatorEO);
        SiteConfigEO eo = saveSiteConfig(siteVO, indicatorEO,siteVO.getIndicatorId());
        return eo;
    }

    //保存站点配置信息
    public SiteConfigEO saveSiteConfig(SiteMgrEO siteVO, IndicatorEO indicatorEO,Long indicatorId) {
        SiteConfigEO configEO = new SiteConfigEO();

        IndicatorEO oldIndicator = indicatorSpecialService.getEntity(IndicatorEO.class,indicatorId);
        String oldSiteTitle = "";
        String oldSiteIDCode = "";
        String oldKeyWords = "";
        String oldDescription = "";
        StringBuffer logStr = new StringBuffer();
        // 修改
        if (siteVO.getSiteConfigId() != null) {
            configEO = getEntity(SiteConfigEO.class, siteVO.getSiteConfigId());
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
            saveEntity(configEO);
            SysLog.log("添加站点配置信息 >> ID：" + configEO.getSiteConfigId(),
                    "SiteConfigEO", CmsLogEO.Operation.Add.toString());
        } else {
            updateEntity(configEO);
            sysLogInfo(logStr,"站点名称",oldSiteTitle,configEO.getSiteTitle());
            sysLogInfo(logStr,"站点标识符",oldSiteIDCode,configEO.getSiteIDCode());
            sysLogInfo(logStr,"关键词",oldKeyWords,configEO.getKeyWords());
            sysLogInfo(logStr,"描述",oldDescription,configEO.getDescription());

            SysLog.log("【站群管理】更新站点信息，站点："+ oldSiteTitle + logStr.toString(),
                    "SiteConfigEO", CmsLogEO.Operation.Update.toString());
        }
        return configEO;
    }

    public StringBuffer sysLogInfo(StringBuffer logStr,String logInfo,String old,String now){
        if(!old.equals(now)){
            logStr.append(",修改"+logInfo+"为:"+now);
        }
        return logStr;
    }
}
