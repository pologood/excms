package cn.lonsun.net.service.service;

import java.util.List;

import cn.lonsun.core.base.service.IBaseService;
import cn.lonsun.net.service.entity.CmsNetServiceClassifyEO;

/**
 * @author gu.fei
 * @version 2015-11-18 13:45
 */
public interface INetServiceClassifyService extends IBaseService<CmsNetServiceClassifyEO> {

    public List<CmsNetServiceClassifyEO> getEOsByPid(Long pid);

    public List<CmsNetServiceClassifyEO> getEOs();

    /*
    * 删除节点及其所有子节点
    * */
    public void recurDel(Long id);

    public void save(CmsNetServiceClassifyEO eo,String flag);

    public void update(CmsNetServiceClassifyEO eo);

    public void delete(Long id,Long pid);

    public Object getSortNum(Long pid);

}
