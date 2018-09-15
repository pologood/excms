package cn.lonsun.monitor.config.internal.service.impl;


import cn.lonsun.common.util.AppUtil;
import cn.lonsun.common.vo.JSONConvertUtil;
import cn.lonsun.common.vo.PageQueryVO;
import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.exception.util.Jacksons;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.monitor.config.internal.dao.IMonitoredVetoConfigDao;
import cn.lonsun.monitor.config.internal.entity.MonitoredVetoConfigEO;
import cn.lonsun.monitor.config.internal.service.IMonitoredVetoConfigService;
import cn.lonsun.monitor.internal.service.IMonitorSiteRegisterService;
import cn.lonsun.monitor.internal.vo.MonitorSiteRegisterVO;
import cn.lonsun.site.site.internal.service.ISiteConfigService;
import cn.lonsun.webservice.config.IMonitoredService;
import cn.lonsun.webservice.to.WebServiceTO;
import com.alibaba.fastjson.JSONArray;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.*;

/**单项否决
 * Created by lonsun on 2017-9-25.
 */
@Service("monitoredVetoConfigService")
public class MonitoredVetoConfigServiceImpl extends MockService<MonitoredVetoConfigEO> implements IMonitoredVetoConfigService {

     private Logger log = LoggerFactory.getLogger(getClass());

     @Resource
     private IMonitoredVetoConfigDao monitoredVetoConfigDao;

     @Resource
     private IMonitoredService monitoredService;

     @Resource
     private ISiteConfigService siteConfigService;

     @Resource
     private IMonitorSiteRegisterService monitorSiteRegisterService;


     /**
      * 根据类别查询
      * @param typeCode
      * @return
      */
     @Override
     public Map<String, Object> getDataByCode(String typeCode, String id, Long siteId) {
          MonitoredVetoConfigEO eo = null;
          if(!cn.lonsun.core.base.util.StringUtils.isEmpty(id)){
               eo = monitoredVetoConfigDao.getEntity(MonitoredVetoConfigEO.class, Long.valueOf(id));
               Map<String, Object> map = JSONConvertUtil.toObejct(eo.getContent(), HashMap.class);
               map.put("id", id);
               map.put("columnTypeName", eo.getColumnTypeName());
               map.put("columnTypeCode", eo.getColumnTypeCode());
               return map;
          }
          List<MonitoredVetoConfigEO> monitoredVetoConfigEOs = monitoredVetoConfigDao.getDataByCode(typeCode, siteId);
          if (null != monitoredVetoConfigEOs && monitoredVetoConfigEOs.size() > 0) {
               MonitoredVetoConfigEO monitoredVetoConfigEO = monitoredVetoConfigEOs.get(0);
               String content = monitoredVetoConfigEO.getContent();
               if (!AppUtil.isEmpty(content)) {
                    Map<String, Object> map = JSONConvertUtil.toObejct(content, HashMap.class);
                    map.put("id", id);
                    map.put("columnTypeName", monitoredVetoConfigEO.getColumnTypeName());
                    map.put("columnTypeCode", monitoredVetoConfigEO.getColumnTypeCode());
                    return map;
               }
          } else {
               return Collections.emptyMap();
          }
          return Collections.emptyMap();
     }
     /**
      * 根据类别查询
      * @param typeCode
      * @return
      */
     @Override
     public Map<String, Object> getDataByCode(String typeCode, Long siteId) {
          return getDataByCode(typeCode, null, siteId);
     }

     @Override
     public void saveData(String content, String typeCode, Long siteId) {
          MonitoredVetoConfigEO.BaseCode baseCode = MonitoredVetoConfigEO.BaseCode.getCodeBySubType( MonitoredVetoConfigEO.CodeType.valueOf(typeCode));
          List<MonitoredVetoConfigEO> monitoredVetoConfigEOs = monitoredVetoConfigDao.getDataByCode(typeCode, siteId);
          if (null == monitoredVetoConfigEOs || monitoredVetoConfigEOs.size() <= 0) {
               MonitoredVetoConfigEO monitoredVetoConfigEO = new MonitoredVetoConfigEO();
               monitoredVetoConfigEO.setCodeType(typeCode);
               monitoredVetoConfigEO.setContent(content);
               monitoredVetoConfigEO.setBaseCode(baseCode.toString());
               saveEntity(monitoredVetoConfigEO);
          } else {
               if(monitoredVetoConfigEOs.size()  == 1){
                    MonitoredVetoConfigEO monitoredVetoConfigEO = monitoredVetoConfigEOs.get(0);
                    monitoredVetoConfigEO.setContent(content);
                    updateEntity(monitoredVetoConfigEO);
                    return;
               }
               HashMap<String, Object> contentobj = JSONConvertUtil.toObejct(content, HashMap.class);
               for(MonitoredVetoConfigEO monitoredVetoConfigEO : monitoredVetoConfigEOs){
                    if(monitoredVetoConfigEO.getColumnTypeCode().equals(contentobj.get("columnTypeCode").toString())){
                         monitoredVetoConfigEO.setContent(content);
                         updateEntity(monitoredVetoConfigEO);
                         break;
                    }
               }
          }
     }

     /**
      * @param typeCode(类别编码)
      * @param columnTypeCode(栏目类别编码)
      * @param baseCode(vote:单项否决,scop:综合评分)
      * @param siteId
      * @return
      */
     @Override
     public List<MonitoredVetoConfigEO> getMonitorConfig(String typeCode, String columnTypeCode, String baseCode, Long siteId) {
         List<MonitoredVetoConfigEO> monitoredVetoConfigEOs   =  monitoredVetoConfigDao.getMonitorConfig(typeCode, columnTypeCode, baseCode,siteId);
          return monitoredVetoConfigEOs;
     }

     @Override
     public Map<String, Object> getConfigByTypes(String typeCode, String columnTypeCode, String baseCode,Long siteId){
          List<MonitoredVetoConfigEO> monitoredVetoConfigEOs   =  monitoredVetoConfigDao.getMonitorConfig(typeCode, columnTypeCode, baseCode,siteId);
          if(null!=monitoredVetoConfigEOs&&monitoredVetoConfigEOs.size()>0){
               MonitoredVetoConfigEO monitoredVetoConfigEO =   monitoredVetoConfigEOs.get(0);
               String content=monitoredVetoConfigEO.getContent();
               if(!AppUtil.isEmpty(content)){
                    Map<String, Object> configVO = JSONConvertUtil.toObejct(content, HashMap.class);
                    return configVO;
               }
          }
          return Collections.emptyMap();
     }

     /**
      * 将站点的配置推送到云端
      * @param siteId
      */
     @Override
     public void sentConfigToCloud(Long siteId) {
//          SiteMgrEO siteConfigEO = siteConfigService.getById(Long.valueOf(siteId));
          MonitorSiteRegisterVO siteRegisterVO = monitorSiteRegisterService.getSiteRegisterInfo(siteId);
          if(siteRegisterVO == null){
               throw new BaseRunTimeException("未找到站点！");
          }
          if(siteRegisterVO.getIsRegistered() == null || siteRegisterVO.getIsRegistered() == 0 || StringUtils.isEmpty(siteRegisterVO.getRegisteredCode())){
               throw new BaseRunTimeException("站点尚未注册云监控服务，请先注册！");
          }
          Map<String, Object> map = new HashMap<String, Object>();
          map.put("recordStatus", MonitoredVetoConfigEO.RecordStatus.Normal.toString());
          map.put("siteId", siteId);
          List list = super.getEntities(MonitoredVetoConfigEO.class, map);
          monitoredService.saveSiteVetoConfig(Jacksons.json().fromObjectToJson(list).toString(),String.valueOf(siteId),
                  siteRegisterVO.getRegisteredCode());
     }

     /**
      * 从服务端拉取配置
      * @param siteId
      */
     @Transactional
     @Override
     public void getConfigFromCloud(String... siteId){
          if(siteId == null){
               return;
          }
//          SiteMgrEO siteConfigEO = siteConfigService.getById(Long.valueOf(siteId[0]));
          MonitorSiteRegisterVO siteRegisterVO = monitorSiteRegisterService.getSiteRegisterInfo(Long.valueOf(siteId[0]));
          //获取站点注册码
//          SiteConfigEO siteConfigEO = CacheHandler.getEntity(SiteConfigEO.class, CacheGroup.CMS_INDICATORID, Long.valueOf(siteId[0]));
          if(siteRegisterVO == null){
               throw new BaseRunTimeException("未找到站点！");
          }
          if(siteRegisterVO.getIsRegistered() == null || siteRegisterVO.getIsRegistered() == 0 || StringUtils.isEmpty(siteRegisterVO.getRegisteredCode())){
               throw new BaseRunTimeException("站点尚未注册云监控服务，请先注册！");
          }
          WebServiceTO result = monitoredService.getAllMonitorConfig(Long.valueOf(siteId[0]), siteRegisterVO.getRegisteredCode());
          List<MonitoredVetoConfigEO> list = JSONArray.parseArray(result.getJsonData(), MonitoredVetoConfigEO.class) ;
          //每个站点保存一份
          for(String id : siteId){
               saveSiteConfig(list, Long.valueOf(id));

          }
     }

     /**
      * 将数据保存到站点，如果已存在，则更新，否则新增，删除list中不存在的类型
      * @param list
      * @param siteId
      */
     @Transactional
     public void saveSiteConfig(List<MonitoredVetoConfigEO> list, Long siteId) {
          Map<String, Object> map = new HashMap<String, Object>();
          map.put("recordStatus", MonitoredVetoConfigEO.RecordStatus.Normal.toString());
          map.put("siteId", siteId);
          List<MonitoredVetoConfigEO> exiting = getEntities(MonitoredVetoConfigEO.class, map);
          List<MonitoredVetoConfigEO> slist = new ArrayList<MonitoredVetoConfigEO>();
          for(MonitoredVetoConfigEO item : list){
               boolean flag = false;
               for(MonitoredVetoConfigEO exit : exiting){
                    //如果栏目类型为空
                    if(StringUtils.isEmpty(item.getColumnTypeCode())){
                         throw new BaseRunTimeException("云端配置异常，同步结束！");
                    }
                    //如果该配置已存在，则做更新操作
                    if(item.getBaseCode().equals(exit.getBaseCode())
                            && item.getCodeType().equals(exit.getCodeType())
                            && item.getColumnTypeCode().equals(exit.getColumnTypeCode())){
                         exit.setColumnTypeName(item.getColumnTypeName());
                         exit.setContent(item.getContent());
                         monitoredVetoConfigDao.update(exit);
                         flag = true;
                         exiting.remove(exit);
                         break;
                    }
               }
               //如果不存在则做新增操作
               if(!flag){
                    MonitoredVetoConfigEO news = new MonitoredVetoConfigEO();
                    BeanUtils.copyProperties(item, news);
                    news.setSiteId(siteId);
                    news.setDeniedId(null);
                    slist.add(news);
               }
          }
          super.saveEntities(slist);
          //删除多余的配置
          monitoredVetoConfigDao.delete(exiting);
          //同步完成后再将站点的数据推送到银平台
          sentConfigToCloud(siteId);
     }

     @Override
     public Pagination getDataPageByCode(PageQueryVO page, String typeCode, Long siteId) {
          Pagination pagination = monitoredVetoConfigDao.getColumnUpdatePage(page, typeCode, siteId);
          List<MonitoredVetoConfigEO> monitoredVetoConfigEOs = (List<MonitoredVetoConfigEO>) pagination.getData();
          List<HashMap<String, Object>> columnUpdateVOs = new ArrayList<HashMap<String, Object>>();
          for (MonitoredVetoConfigEO monitoredVetoConfigEO : monitoredVetoConfigEOs) {
               HashMap<String, Object> baseConfigVO = JSONConvertUtil.toObejct(monitoredVetoConfigEO.getContent(), HashMap.class);
               baseConfigVO.put("id", monitoredVetoConfigEO.getDeniedId());
               baseConfigVO.put("columnTypeName", monitoredVetoConfigEO.getColumnTypeName());
               baseConfigVO.put("columnTypeCode", monitoredVetoConfigEO.getColumnTypeCode());
               columnUpdateVOs.add(baseConfigVO);
          }
          pagination.setData(columnUpdateVOs);
          return pagination;
     }


}
