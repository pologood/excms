package cn.lonsun.internal.service;

import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.internal.entity.PublicUnitRelationEO;

import java.util.List;

/**
 * 失败记录
 * @author zhongjun
 */
public interface IPublicUnitRelationService extends IMockService<PublicUnitRelationEO> {

    /**
     * 根据老id获取数据
     * @param oldId
     * @return
     */
    public List<PublicUnitRelationEO> getByOldId(Long siteId, String oldId);

    /**
     * 保存新老id对应关系
     * @param oldId
     * @param newId
     * @param oldName
     * @param newName
     */
    public void saveRelation(String oldId, Long newId, String oldName, String newName);

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
