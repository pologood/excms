package cn.lonsun.internal.service.impl;

import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.internal.dao.IPublicCatalogRelationDao;
import cn.lonsun.internal.entity.PublicCatalogRelationEO;
import cn.lonsun.internal.service.IPublicCatalogRelationService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 导入失败的内容记录
 * @author zhongjun
 */
@Service("publicCatalogRelationService")
public class PublicCatalogRelationServiceImpl extends MockService<PublicCatalogRelationEO> implements IPublicCatalogRelationService {

    @Autowired
    private IPublicCatalogRelationDao publicCatalogRelationDao;

    @Override
    public Map<Long, String> getMap(Long siteId) {
        List<PublicCatalogRelationEO> all = getByOldId(null);
        if(all == null || all.isEmpty()){
            return Collections.emptyMap();
        }
        Map<Long, String> map = new HashMap<Long, String>();
        for(PublicCatalogRelationEO catalog : all){
            map.put(catalog.getNewId(), catalog.getOldId());
        }
        return map;
    }

    @Override
    public List<PublicCatalogRelationEO> getByOldId(String oldId) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
        if(StringUtils.isNotEmpty(oldId)){
            map.put("oldId", oldId);
        }
        return publicCatalogRelationDao.getEntities(PublicCatalogRelationEO.class, map);
    }

    @Override
    public void saveRelation(String oldId, Long newId) {
        publicCatalogRelationDao.save(new PublicCatalogRelationEO(newId, oldId));
    }

    @Override
    public void deleteAll() {
        publicCatalogRelationDao.executeUpdateByHql("delete from PublicCatalogRelationEO", new Object[]{});
    }

    @Override
    public void deleteMany(String... oldId) {
        publicCatalogRelationDao.deleteByOldId(oldId);
    }
}
