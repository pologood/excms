package cn.lonsun.datasourcemanage.internal.service.impl;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.datasourcemanage.internal.dao.IDataimportSiteDataSourceDao;
import cn.lonsun.datasourcemanage.internal.entity.DataimportSiteDataSourceEO;
import cn.lonsun.datasourcemanage.internal.service.IDataimportSiteDataSourceService;
import cn.lonsun.datasourcemanage.internal.vo.DataSourceQueryVo;
import cn.lonsun.datasourcemanage.internal.vo.SiteDataSourceVo;
import cn.lonsun.datasourcemanage.internal.vo.SiteInfoVo;
import cn.lonsun.internal.metadata.DataModule;
import cn.lonsun.site.site.internal.entity.SiteMgrEO;
import cn.lonsun.site.site.internal.service.ISiteConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lonsun on 2018-2-5.
 *
 */
@Service
public class DataimportSiteDataSourceServiceImpl extends MockService<DataimportSiteDataSourceEO> implements IDataimportSiteDataSourceService {
    @Autowired
    private IDataimportSiteDataSourceDao dataimportSiteDataSourceDao;
    @Autowired
    private ISiteConfigService siteConfigService;

    /**
     * 获取站点分页信息
     * @param queryVo
     * @return
     */
    @Override
    public Pagination getSitePage(DataSourceQueryVo queryVo) {
       Pagination pagination = siteConfigService.getSiteInfos(queryVo.getPageIndex(), queryVo.getPageSize());
       List<SiteMgrEO> siteMgrEOs =(List<SiteMgrEO>) pagination.getData();
       List<SiteInfoVo> siteInfoVos =new ArrayList<SiteInfoVo>();
        if(null!=siteMgrEOs&&siteMgrEOs.size()>0){
           for(SiteMgrEO siteMgrEO:  siteMgrEOs){
               SiteInfoVo siteInfoVo =new SiteInfoVo();
               AppUtil.copyProperties(siteInfoVo,siteMgrEO);
               siteInfoVos.add(siteInfoVo);
           }
       }
        pagination.setData(siteInfoVos);
        return pagination;
    }

    /**
     * 站点栏目类别
     * @param queryVo
     * @return
     */
    @Override
    public Pagination getClomunTypePage(DataSourceQueryVo queryVo) {
       Pagination pagination = dataimportSiteDataSourceDao.getClomunTypePage(queryVo);
      List<SiteDataSourceVo> siteDataSourceVos =(List<SiteDataSourceVo>)  pagination.getData();
        if(null!=siteDataSourceVos&&siteDataSourceVos.size()>0){
            for( SiteDataSourceVo sourceVo:siteDataSourceVos){
                sourceVo.setTypeCodeName(DataModule.valueOf(sourceVo.getTypeCode()).getText());

            }

        }
        return pagination;
    }
    /**
     * 绑定数据源
     * @param siteId
     * @param id
     * @param dataSourceId
     */
    @Override
    public void bindDataSource(Long siteId, Long id, Long dataSourceId,String typeCode) {
        DataimportSiteDataSourceEO dataimportSiteDataSourceEO =new DataimportSiteDataSourceEO();
        if(AppUtil.isEmpty(id)){
            List<DataimportSiteDataSourceEO> dataimportSiteDataSourceEOs =  getSiteDataSource(siteId,typeCode,null);
            if(dataimportSiteDataSourceEOs.size()>0){
                throw  new BaseRunTimeException(TipsMode.Message.toString(),"该站点的栏目类别配置已存在，无需重复添加!");
            }
            dataimportSiteDataSourceEO.setDataSourceId(dataSourceId);
            dataimportSiteDataSourceEO.setSiteId(siteId);
            dataimportSiteDataSourceEO.setTypeCode(typeCode);
            saveEntity(dataimportSiteDataSourceEO);
        } else{
            dataimportSiteDataSourceEO =  getEntity(DataimportSiteDataSourceEO.class,id);
            dataimportSiteDataSourceEO.setDataSourceId(dataSourceId);
            updateEntity(dataimportSiteDataSourceEO);
        }



    }

    /**
     *
     * @param siteId
     * @param type
     * @param dataSourceId
     * @return
     */
    @Override
    public List<DataimportSiteDataSourceEO> getSiteDataSource(Long siteId, String type, Long dataSourceId) {
        return dataimportSiteDataSourceDao.getSiteDataSource(siteId,type,dataSourceId);
    }
}
