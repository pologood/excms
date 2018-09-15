package cn.lonsun.net.service.dao;

import java.util.List;
import java.util.Map;

import cn.lonsun.core.base.dao.IBaseDao;
import cn.lonsun.net.service.entity.CmsResourcesClassifyEO;

/**
 * @author gu.fei
 * @version 2015-11-18 13:44
 */
public interface IResourcesClassifyDao extends IBaseDao<CmsResourcesClassifyEO> {

    public List<CmsResourcesClassifyEO> getEOsBySid(Long pId);

    public Map<Long,Object> getMap(Long pId);

    public void deleteByPid(Long pId);

    public List<CmsResourcesClassifyEO> getEOs(Long cId, String type);
}
