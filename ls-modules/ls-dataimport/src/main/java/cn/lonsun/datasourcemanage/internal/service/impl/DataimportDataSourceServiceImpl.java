package cn.lonsun.datasourcemanage.internal.service.impl;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.datasourcemanage.internal.dao.IDataimportDataSourceDao;
import cn.lonsun.datasourcemanage.internal.dao.IDataimportSiteDataSourceDao;
import cn.lonsun.datasourcemanage.internal.entity.DataimportDataSourceEO;
import cn.lonsun.datasourcemanage.internal.entity.DataimportSiteDataSourceEO;
import cn.lonsun.datasourcemanage.internal.service.IDataimportDataSourceService;
import cn.lonsun.datasourcemanage.internal.vo.DataSourceQueryVo;
import cn.lonsun.internal.metadata.DataModule;
import cn.lonsun.manufacturer.internal.entity.ManufacturerEO;
import cn.lonsun.manufacturer.internal.service.IManufacturerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lonsun on 2018-2-5.
 *
 */
@Service
public class DataimportDataSourceServiceImpl extends MockService<DataimportDataSourceEO> implements IDataimportDataSourceService {

     @Autowired
     private IDataimportDataSourceDao dataimportDataSourceDao;
     @Autowired
     private IDataimportSiteDataSourceDao siteDataSourceDao;
     @Autowired
     private IManufacturerService manufacturerService;

     /**
      * 数据源列表
      * @param dataSourceQueryVo
      * @return
      */
     @Override
     public Pagination getPage(DataSourceQueryVo dataSourceQueryVo) {
        Pagination pagination = dataimportDataSourceDao.getPage(dataSourceQueryVo);
          return pagination;
     }

     /**
      * 保存
      * @param dataimportDataSourceEO
      */
     @Override
     public void saveOrUpdateDataSource(DataimportDataSourceEO dataimportDataSourceEO) {
          ManufacturerEO manufacturerEO = manufacturerService.getEntity(ManufacturerEO.class, dataimportDataSourceEO.getManufacturerid());
          if(null!=manufacturerEO){
               dataimportDataSourceEO.setManufacturercode(manufacturerEO.getUniqueCode());
          }
          if(AppUtil.isEmpty(dataimportDataSourceEO.getDataSourceId())){
               saveEntity(dataimportDataSourceEO);
          } else {
               updateEntity(dataimportDataSourceEO);
          }

     }

     @Override
     public DataimportDataSourceEO getDataSourceBySite(Long siteId, DataModule typeCode) {
          Map<String, Object> map = new HashMap<String, Object>();
          map.put("recordStatus", DataimportSiteDataSourceEO.RecordStatus.Normal.toString());
          map.put("siteId", siteId);
          map.put("typeCode", typeCode.toString());
          DataimportSiteDataSourceEO siteConfig = siteDataSourceDao.getEntity(DataimportSiteDataSourceEO.class, map);
          return dataimportDataSourceDao.getEntity(DataimportDataSourceEO.class, siteConfig.getDataSourceId());
     }

     @Override
     public void delete(Long[] ids) {
          dataimportDataSourceDao.delete(DataimportDataSourceEO.class,ids);
     }

     /**
      * 数据源列表
      * @return
      */
     @Override
     public List<DataimportDataSourceEO> getList() {
          return dataimportDataSourceDao.getList();
     }
}
