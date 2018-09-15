package cn.lonsun.net.service.service;

import java.util.List;
import java.util.Map;

import cn.lonsun.core.base.service.IBaseService;
import cn.lonsun.net.service.entity.CmsResourcesClassifyEO;

/**
 * @author gu.fei
 * @version 2015-11-18 13:45
 */
public interface IResourcesClassifyService extends IBaseService<CmsResourcesClassifyEO> {

    /**
     * @param pId
     * @return
     */
    public List<CmsResourcesClassifyEO> getEOsBySid(Long pId);

    /**
     * @param pId
     * @return
     */
    public Map<Long,Object> getMap(Long pId);

    /**
     * @param pId
     */
    public void deleteByPid(Long pId);

    /**
     * @param cId
     * @param type
     * @return
     */
    public List<CmsResourcesClassifyEO> getEOs(Long cId,String type);

}
