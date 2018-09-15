package cn.lonsun.net.service.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.core.base.service.impl.BaseService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.net.service.dao.ISceneServiceDao;
import cn.lonsun.net.service.entity.CmsSceneServiceEO;
import cn.lonsun.net.service.service.ISceneServiceService;
import cn.lonsun.site.template.internal.entity.ParamDto;

/**
 * @author gu.fei
 * @version 2015-11-18 13:47
 */
@Service
public class SceneServiceService extends BaseService<CmsSceneServiceEO> implements ISceneServiceService {

    @Autowired
    private ISceneServiceDao sceneServiceDao;

    @Override
    public List<CmsSceneServiceEO> getEOs() {
        return sceneServiceDao.getEOs();
    }

    @Override
    public Pagination getPageEOs(ParamDto dto) {
        Pagination page = sceneServiceDao.getPageEOs(dto);
        List<CmsSceneServiceEO> list = (List<CmsSceneServiceEO>) page.getData();

        for(CmsSceneServiceEO eo:list) {
            if(null != eo.getColumnId()) {
                IndicatorEO indicatorEO = CacheHandler.getEntity(IndicatorEO.class,eo.getColumnId());
                eo.setColumnName(indicatorEO.getName());
            }
        }

        return page;
    }

    @Override
    public void saveEO(CmsSceneServiceEO eo) {
        this.saveEntity(eo);
    }

    @Override
    public void updateEO(CmsSceneServiceEO eo) {
        this.updateEntity(eo);
    }

    @Override
    public void deleteEO(Long id) {
        this.delete(CmsSceneServiceEO.class, id);
    }

    @Override
    public Object publish(Long[] ids,Long publish) {
        return sceneServiceDao.publish(ids,publish);
    }
}
