package cn.lonsun.internal.service;

import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.internal.entity.PublicCatalogRelationEO;
import cn.lonsun.internal.entity.PublicUnitRelationEO;

import java.util.List;
import java.util.Map;

/**
 * 信息公开目录对应关系表
 * @author zhongjun
 */
public interface IPublicCatalogRelationService extends IMockService<PublicCatalogRelationEO> {

    public Map<Long, String> getMap(Long siteId);

    /**
     * 根据老id获取数据
     * @param oldId
     * @return
     */
    public List<PublicCatalogRelationEO> getByOldId(String oldId);

    /**
     * 保存新老id对应关系
     * @param oldId
     * @param newId
     */
    public void saveRelation(String oldId, Long newId);

    /**
     * 删除所有对应关系
     */
    public void deleteAll();

    /**
     * 删除老数据的关联
     * @param oldId
     */
    public void deleteMany(String... oldId);


}
