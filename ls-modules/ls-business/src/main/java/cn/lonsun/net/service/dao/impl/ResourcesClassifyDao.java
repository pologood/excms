package cn.lonsun.net.service.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import cn.lonsun.core.base.dao.impl.BaseDao;
import cn.lonsun.net.service.dao.IResourcesClassifyDao;
import cn.lonsun.net.service.entity.CmsResourcesClassifyEO;

/**
 * @author gu.fei
 * @version 2015-11-18 13:46
 */
@Repository
public class ResourcesClassifyDao extends BaseDao<CmsResourcesClassifyEO> implements IResourcesClassifyDao {

    @Override
    public List<CmsResourcesClassifyEO> getEOsBySid(Long pId) {
        return this.getEntitiesByHql("from CmsResourcesClassifyEO where pId = ?",new Object[] {pId});
    }

    @Override
    public Map<Long, Object> getMap(Long sId) {
        Map<Long, Object> map = new HashMap<Long, Object>();
        List<CmsResourcesClassifyEO> list = getEOsBySid(sId);
        for(CmsResourcesClassifyEO eo : list) {
            map.put(eo.getcId(),eo);
        }
        return map;
    }

    @Override
    public void deleteByPid(Long pId) {
        this.executeUpdateByHql("delete from CmsResourcesClassifyEO where pId = ?", new Object[]{pId});
    }

    @Override
    public List<CmsResourcesClassifyEO> getEOs(Long cId, String type) {
        return this.getEntitiesByHql("from CmsResourcesClassifyEO where cId = ? and type = ?",new Object[] {cId,type});
    }
}
