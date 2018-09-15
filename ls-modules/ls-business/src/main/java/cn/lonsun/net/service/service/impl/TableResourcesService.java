package cn.lonsun.net.service.service.impl;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.service.impl.BaseService;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.net.service.dao.ITableResourcesDao;
import cn.lonsun.net.service.entity.CmsResourcesClassifyEO;
import cn.lonsun.net.service.entity.CmsTableResourcesEO;
import cn.lonsun.net.service.service.IResourcesClassifyService;
import cn.lonsun.net.service.service.ITableResourcesService;
import cn.lonsun.site.template.internal.entity.ParamDto;
import cn.lonsun.util.FileUploadUtil;
import cn.lonsun.util.LoginPersonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author gu.fei
 * @version 2015-11-18 13:47
 */
@Service
public class TableResourcesService extends BaseService<CmsTableResourcesEO> implements ITableResourcesService {

    @Autowired
    private ITableResourcesDao tableResourcesDao;

    @Autowired
    private IResourcesClassifyService resourcesClassifyService;

    @Override
    public List<CmsTableResourcesEO> getEOs() {
        return tableResourcesDao.getEOs();
    }

    @Override
    public Pagination getPageEOs(ParamDto dto) {
        return tableResourcesDao.getPageEOs(dto);
    }

    @Override
    public void saveEO(CmsTableResourcesEO eo, String cIds) {
        Long siteId = null != eo.getSiteId()?eo.getSiteId():LoginPersonUtil.getSiteId();
        eo.setSiteId(siteId);
        Long pid = this.saveEntity(eo);
        if(!AppUtil.isEmpty(cIds)) {
            List<CmsResourcesClassifyEO> listc = new ArrayList<CmsResourcesClassifyEO>();
            Long[] ids = StringUtils.getArrayWithLong(cIds,",");
            for(Long id : ids) {
                CmsResourcesClassifyEO cEO = new CmsResourcesClassifyEO();
                cEO.setpId(pid);
                cEO.setcId(id);
                cEO.setType(CmsResourcesClassifyEO.Type.TABLE.toString());
                listc.add(cEO);
            }
            resourcesClassifyService.saveEntities(listc);
        }

        FileUploadUtil.saveFileCenterEO(eo.getUploadUrl());
    }

    @Override
    public void updateEO(CmsTableResourcesEO eo, String cIds) {
        Long siteId = null != eo.getSiteId()?eo.getSiteId():LoginPersonUtil.getSiteId();
        eo.setSiteId(siteId);
        this.updateEntity(eo);

        resourcesClassifyService.deleteByPid(eo.getId());
        if(!AppUtil.isEmpty(cIds)) {
            List<CmsResourcesClassifyEO> listc = new ArrayList<CmsResourcesClassifyEO>();
            Long[] ids = StringUtils.getArrayWithLong(cIds,",");
            for(Long id : ids) {
                CmsResourcesClassifyEO cEO = new CmsResourcesClassifyEO();
                cEO.setpId(eo.getId());
                cEO.setcId(id);
                cEO.setType(CmsResourcesClassifyEO.Type.TABLE.toString());
                listc.add(cEO);
            }
            resourcesClassifyService.saveEntities(listc);
        }
    }

    @Override
    public void deleteEO(Long id) {
        this.delete(CmsTableResourcesEO.class,id);
        resourcesClassifyService.deleteByPid(id);
    }
}
