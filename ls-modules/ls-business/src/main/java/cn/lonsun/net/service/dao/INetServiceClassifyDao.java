package cn.lonsun.net.service.dao;

import java.util.List;

import cn.lonsun.core.base.dao.IBaseDao;
import cn.lonsun.net.service.entity.CmsNetServiceClassifyEO;

/**
 * @author gu.fei
 * @version 2015-11-18 13:44
 */
public interface INetServiceClassifyDao extends IBaseDao<CmsNetServiceClassifyEO> {

    public List<CmsNetServiceClassifyEO> getEOsByPid(Long pid);

    public List<CmsNetServiceClassifyEO> getEOs();

    public void recurDel(Long id);

    public Object getSortNum(Long pid);

}
