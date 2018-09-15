package cn.lonsun.monitor.config.internal.service.impl;

import cn.lonsun.cache.client.CacheGroup;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.common.vo.PageQueryVO;
import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.exception.util.Jacksons;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.monitor.config.internal.dao.IMonitoredColumnConfigDao;
import cn.lonsun.monitor.config.internal.entity.MonitoredColumnConfigEO;
import cn.lonsun.monitor.config.internal.service.IMonitoredColumnConfigService;
import cn.lonsun.monitor.config.internal.service.IMonitoredVetoConfigService;
import cn.lonsun.monitor.internal.service.IMonitorSiteRegisterService;
import cn.lonsun.monitor.internal.vo.ColumnLevelVO;
import cn.lonsun.monitor.internal.vo.MonitorSiteRegisterVO;
import cn.lonsun.publicInfo.internal.entity.PublicCatalogEO;
import cn.lonsun.publicInfo.internal.entity.PublicContentEO;
import cn.lonsun.rbac.internal.entity.OrganEO;
import cn.lonsun.site.site.internal.entity.ColumnMgrEO;
import cn.lonsun.site.site.internal.entity.SiteConfigEO;
import cn.lonsun.site.site.internal.entity.SiteMgrEO;
import cn.lonsun.site.site.internal.service.ISiteConfigService;
import cn.lonsun.system.datadictionary.internal.entity.DataDictEO;
import cn.lonsun.system.datadictionary.internal.entity.DataDictItemEO;
import cn.lonsun.util.LoginPersonUtil;
import cn.lonsun.util.PublicCatalogUtil;
import cn.lonsun.webservice.config.IMonitoredService;
import cn.lonsun.webservice.to.WebServiceTO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

/**栏目类别配置服务
 * Created by lonsun on 2017-9-22.
 */
@Service("monitoredColumnConfigService")
public class MonitoredColumnConfigServiceImpl extends MockService<MonitoredColumnConfigEO> implements IMonitoredColumnConfigService {

    private Logger log = LoggerFactory.getLogger(getClass());

    @Resource
    private IMonitoredColumnConfigDao monitoredColumnConfigDao;

    @Resource
    private IMonitoredVetoConfigService monitoredVetoConfigService;

    @Resource
    private IMonitoredService monitoredService;

    @Resource
    private ISiteConfigService siteConfigService;

    @Resource
    private IMonitorSiteRegisterService monitorSiteRegisterService;

    /**
     * 分页查询配置
     * @param pageQueryVO
     * @return
     */
    @Override
    public Pagination getPage(PageQueryVO pageQueryVO) {
        return monitoredColumnConfigDao.getPage(pageQueryVO);
    }
    /**
     * 获取未删除项
     * @param ids
     * @return
     */
    @Override
    public List<MonitoredColumnConfigEO> getNoramlConfig(Long[] ids) {
        return monitoredColumnConfigDao.getNoramlConfig(ids);
    }
    /**
     * 获取栏目更新栏目
     * @return
     */
    @Override
    public List<MonitoredColumnConfigEO> getUpateColumn() {
        return monitoredColumnConfigDao.getUpateColumn();
    }

    /**
     * 获取栏目类别
     * @param infoUpdateTypeList
     * @return
     */
    @Override
    public List<MonitoredColumnConfigEO> getColumnByType(List<String> infoUpdateTypeList) {
        return monitoredColumnConfigDao.getColumnByType(infoUpdateTypeList);
    }
    /**
     * 获取当前站点栏目类别
     * @param siteId
     * @return
     */
    @Override
    public List<MonitoredColumnConfigEO> getSiteColumnType(Long siteId) {
        return monitoredColumnConfigDao.getSiteColumnType(siteId);
    }

    @Override
    public MonitoredColumnConfigEO queryConfigByCode(MonitoredColumnConfigEO columnConfigEO) {
        return monitoredColumnConfigDao.queryConfigByCode(columnConfigEO);
    }

    @Override
    public MonitoredColumnConfigEO queryConfigByTypeId(Long typeId) {
        return monitoredColumnConfigDao.queryConfigByTypeId(typeId);
    }

    /**
     * 查询栏目级别详情
     * @param columnId
     * @return
     */
    @Override
    public Pagination getColumnLevel(String columnId,PageQueryVO pageQueryVO) {
        Pagination pagination =  new Pagination();

        if(AppUtil.isEmpty(columnId)){
            return  pagination;
        }
       String[] columnIds = columnId.split(",");
        pagination.setTotal(Long.valueOf(columnIds.length));
        List<ColumnLevelVO> columnLevelVOs =new ArrayList<ColumnLevelVO>();
        //栏目数不大于分页数,不分页
        if(columnIds.length>pageQueryVO.getPageSize()){
            String pageColumnIds[] =new String[pageQueryVO.getPageSize()];
            for(int i=0;i< pageQueryVO.getPageSize();i++){
                Integer index =Integer.valueOf(pageQueryVO.getPageIndex() * pageQueryVO.getPageSize() + i+"");
                if(columnIds.length<=index){
                    break;
                }
                pageColumnIds[i] = columnIds[Integer.valueOf(pageQueryVO.getPageIndex() * pageQueryVO.getPageSize() + i+"")];

            }
            columnIds=pageColumnIds;
        }

        for(String inderictId:  columnIds){
            if(!AppUtil.isEmpty(inderictId)){
                ColumnLevelVO columnLevelVO =new ColumnLevelVO();
                ColumnMgrEO columnMgrEO = CacheHandler.getEntity(ColumnMgrEO.class, Long.valueOf(inderictId));
                columnLevelVO.setColumnName(columnMgrEO.getName());
                columnLevelVO.setColumnLevelName(columnMgrEO.getName());
                dealColumnLevel(columnMgrEO, columnLevelVO);
                columnLevelVOs.add(columnLevelVO);
            }
        }
        pagination.setPageSize(pageQueryVO.getPageSize());
        pagination.setPageIndex(pageQueryVO.getPageIndex());
        pagination.setData(columnLevelVOs);


        return pagination;
    }

    /**
     * 查询公开栏目级别详情
     * @param columnId
     * @return
     */
    @Override
    public Pagination getPublicLevel(String columnId, PageQueryVO pageQueryVO) {
        Pagination pagination =  new Pagination();

        if(AppUtil.isEmpty(columnId)){
            return  pagination;
        }
        String[] columnIds = columnId.split(",");
        pagination.setTotal(Long.valueOf(columnIds.length));
        List<ColumnLevelVO> columnLevelVOs =new ArrayList<ColumnLevelVO>();


        //栏目数不大于分页数,不分页
        if(columnIds.length>pageQueryVO.getPageSize()){
            String pageColumnIds[] =new String[pageQueryVO.getPageSize()];
            for(int i=0;i< pageQueryVO.getPageSize();i++){
                Integer index =Integer.valueOf(pageQueryVO.getPageIndex() * pageQueryVO.getPageSize() + i+"");
                if(columnIds.length<=index){
                    break;
                }
                pageColumnIds[i] = columnIds[Integer.valueOf(pageQueryVO.getPageIndex() * pageQueryVO.getPageSize() + i+"")];

            }
            columnIds=pageColumnIds;
        }
        DataDictEO dictEO = CacheHandler.getEntity(DataDictEO.class, CacheGroup.CMS_CODE, PublicContentEO.PUBLIC_ITEM_CODE);
        List<DataDictItemEO> itemList = CacheHandler.getList(DataDictItemEO.class, CacheGroup.CMS_PARENTID, dictEO.getDictId());
        for(String inderictId:  columnIds){
            if(!AppUtil.isEmpty(inderictId)){
               String[] cate = inderictId.split("_");
                ColumnLevelVO columnLevelVO =new ColumnLevelVO();
                PublicCatalogEO publicCatalogEO = CacheHandler.getEntity(PublicCatalogEO.class, Long.valueOf(cate[1]));
                //公开字典项
                if(null==publicCatalogEO){
                    for(DataDictItemEO dictItemEO:itemList){
                        if(Long.valueOf(cate[1]).equals(dictItemEO.getItemId())){
                            OrganEO organEO = CacheHandler.getEntity(OrganEO.class, Long.valueOf(cate[0]));
                            columnLevelVO.setColumnName(dictItemEO.getName());
                            columnLevelVO.setColumnLevelName(organEO.getName()+">"+dictItemEO.getName());
                            break;
                        }

                    }
                }else{
                    columnLevelVO.setColumnName(publicCatalogEO.getName());
                    columnLevelVO.setColumnLevelName(publicCatalogEO.getName());
                    dealPublicLevel(Long.valueOf(cate[0]),publicCatalogEO, columnLevelVO);
                }

                columnLevelVOs.add(columnLevelVO);
            }
        }
        pagination.setPageSize(pageQueryVO.getPageSize());
        pagination.setPageIndex(pageQueryVO.getPageIndex());
        pagination.setData(columnLevelVOs);
        return pagination;
    }


    /**
     * 根据typeCode查询栏目
     * @param code
     * @return
     */
    @Override
    public List<ColumnMgrEO> getColumnByCode(String code,Long siteId) {
        MonitoredColumnConfigEO columnConfigEO =new MonitoredColumnConfigEO();
        columnConfigEO.setTypeCode(code);
        columnConfigEO.setSiteId(siteId);
        columnConfigEO=monitoredColumnConfigDao.queryConfigByCode(columnConfigEO);
        String synColumnIds = columnConfigEO.getSynColumnIds();
        List<ColumnMgrEO> columnMgrEOs =new ArrayList<ColumnMgrEO>();
        if(AppUtil.isEmpty(synColumnIds)){
          return   columnMgrEOs;
        }
         else {
           String[] columnIds =synColumnIds.split(",");
           for( String columnId: columnIds){
               if(!AppUtil.isEmpty(columnId)){
                   ColumnMgrEO columnMgrEO = CacheHandler.getEntity(ColumnMgrEO.class, Long.valueOf(columnId));
                   if(columnMgrEO!=null&&columnMgrEO.getIsStartUrl()!=1){
                       columnMgrEOs.add(columnMgrEO);
                   }
               }
            }
        }
        return columnMgrEOs;
    }

    /**
     * 根据typeCode查询绑定的信息公开目录
     * @param code
     * @return
     */
    @Override
    public String getPublicCatsByCode(String code,Long siteId) {
        MonitoredColumnConfigEO columnConfigEO =new MonitoredColumnConfigEO();
        columnConfigEO.setTypeCode(code);
        columnConfigEO.setSiteId(siteId);
        columnConfigEO=monitoredColumnConfigDao.queryConfigByCode(columnConfigEO);
        String synOrganCatIds = columnConfigEO.getSynOrganCatIds();

        if(!AppUtil.isEmpty(synOrganCatIds)){
            String[] organCatIds =synOrganCatIds.split(",");
            for( String organCatId: organCatIds){
                if(!AppUtil.isEmpty(organCatId)){
                    String[] ids =  organCatId.split("_");
                    Long organId = Long.parseLong(ids[0]);
                    Long catId = Long.parseLong(ids[1]);
                    PublicCatalogEO publicCatalogEO =  CacheHandler.getEntity(PublicCatalogEO.class, catId);
                    PublicCatalogUtil.filterCatalog(publicCatalogEO, organId);
                    if(publicCatalogEO!=null&&!publicCatalogEO.getIsShow()){//去除隐藏的目录
                        if(synOrganCatIds.contains(organCatId+",")){
                            synOrganCatIds = synOrganCatIds.replace(organCatId+",","");
                        }else{
                            synOrganCatIds = synOrganCatIds.replace(organCatId,"");
                        }
                    }
                }
            }
        }

        return synOrganCatIds;
    }


    private void dealColumnLevel(ColumnMgrEO columnMgrEO,ColumnLevelVO columnLevelVO){
        StringBuffer stringBuffer =new StringBuffer();

        ColumnMgrEO pcolumnMgrEO = CacheHandler.getEntity(ColumnMgrEO.class, columnMgrEO.getParentId());
        if(null!=pcolumnMgrEO&&!AppUtil.isEmpty(pcolumnMgrEO.getIndicatorId())){
            columnLevelVO.setColumnLevelName(stringBuffer.append(pcolumnMgrEO.getName()).append(">").append(columnLevelVO.getColumnLevelName()).toString());
            dealColumnLevel(pcolumnMgrEO,columnLevelVO);
        }
        else {
            SiteMgrEO siteMgrEO =  CacheHandler.getEntity(SiteMgrEO.class, columnMgrEO.getParentId());
            columnLevelVO.setColumnLevelName(stringBuffer.append(siteMgrEO.getName()).append(">").append(columnLevelVO.getColumnLevelName()).toString());
        }



    }

    private void dealPublicLevel(Long organId,PublicCatalogEO publicCatalogEO,ColumnLevelVO columnLevelVO){
        StringBuffer stringBuffer =new StringBuffer();

        PublicCatalogEO pcatalogEO = CacheHandler.getEntity(PublicCatalogEO.class, publicCatalogEO.getParentId());
        if(null!=pcatalogEO&&!AppUtil.isEmpty(pcatalogEO.getId())){
             if(pcatalogEO.getParentId().equals(0L)){
                 OrganEO organEO = CacheHandler.getEntity(OrganEO.class, organId);
                 columnLevelVO.setColumnLevelName(stringBuffer.append(organEO.getName()).append(">").append(columnLevelVO.getColumnLevelName()).toString());
             }else {
                 columnLevelVO.setColumnLevelName(stringBuffer.append(pcatalogEO.getName()).append(">").append(columnLevelVO.getColumnLevelName()).toString());
                 dealPublicLevel(organId,pcatalogEO,columnLevelVO);
             }


        }


    }

    /**
     * 栏目同步完成后同步监测配置
     * @param siteId
     * @return
     */
    @Transactional
    @Override
    public boolean syncConfigFromCloud(String... siteId){
        if(siteId == null || siteId.length == 0){
            log.error("同步栏目类型，站点id为空");
            return true;
        }
//        SiteMgrEO siteConfigEO = siteConfigService.getById(Long.valueOf(siteId[0]));
        MonitorSiteRegisterVO siteRegisterVO = monitorSiteRegisterService.getSiteRegisterInfo(Long.valueOf(siteId[0]));
        //缓存存在不刷新的情况，此处换成查数据库
//        SiteConfigEO siteConfigEO = CacheHandler.getEntity(SiteConfigEO.class, CacheGroup.CMS_INDICATORID, Long.valueOf(siteId[0]));
        if(siteRegisterVO == null){
            throw new BaseRunTimeException("未找到站点！");
        }
        if(siteRegisterVO.getIsRegistered() == null || siteRegisterVO.getIsRegistered() == 0 || StringUtils.isEmpty(siteRegisterVO.getRegisteredCode())){
            throw new BaseRunTimeException("站点尚未注册云监控服务，请先注册！");
        }
        WebServiceTO webServiceTO = monitoredService.getDefaultColumnType(Long.valueOf(siteId[0]), siteRegisterVO.getRegisteredCode());
        if(webServiceTO.getStatus()==0){
            return false;
        }
        try {
            List<MonitoredColumnConfigEO>  colundcolumnConfigEOs = (List<MonitoredColumnConfigEO>) Jacksons.json().fromJsonToList(webServiceTO.getJsonData(), MonitoredColumnConfigEO.class);
            for(String id : siteId){
                saveSiteConfig(colundcolumnConfigEOs, Long.valueOf(id));
            }
            monitoredVetoConfigService.getConfigFromCloud(siteId);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseRunTimeException("同步数据异常");
        }
        return true;
    }

    /**
     * 保存一个站点的配置
     * @param colundcolumnConfigEOs
     * @param siteId
     */
    @Transactional
    public void saveSiteConfig(List<MonitoredColumnConfigEO> colundcolumnConfigEOs, Long siteId) {
        List<MonitoredColumnConfigEO> existing = getSiteColumnType(siteId);
        List<MonitoredColumnConfigEO> insert = new ArrayList<MonitoredColumnConfigEO>();
        for(MonitoredColumnConfigEO columnConfigEO: colundcolumnConfigEOs){
            boolean flag = false;
            for(MonitoredColumnConfigEO exit : existing){
                //如果该类型已存在则做更新
                if(exit.getTypeCode().equals(columnConfigEO.getTypeCode())){
                    exit.setStatus(columnConfigEO.getStatus());
                    exit.setTypeCode(columnConfigEO.getTypeCode());
                    exit.setTypeName(columnConfigEO.getTypeName());
                    updateEntity(exit);
                    flag = true;
                    //删除已处理的元素，最后剩下的元素就是服务端不存在的配置
                    existing.remove(exit);
                    break;
                }
            }
            //如果不存在，则新增
            if(!flag){
                //复制新的对象，防止更新原有数据造成异常
                MonitoredColumnConfigEO eo = new MonitoredColumnConfigEO();
                BeanUtils.copyProperties(columnConfigEO, eo);
                eo.setSiteId(siteId);
                eo.setTypeId(null);
                insert.add(eo);
            }
        }
        monitoredColumnConfigDao.delete(existing);
        saveEntities(insert);
    }
}
