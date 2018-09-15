package cn.lonsun.net.service.dao.impl;

import cn.lonsun.core.base.dao.impl.BaseDao;
import cn.lonsun.net.service.dao.INetServiceClassifyDao;
import cn.lonsun.net.service.entity.CmsNetServiceClassifyEO;
import cn.lonsun.util.LoginPersonUtil;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author gu.fei
 * @version 2015-11-18 13:46
 */
@Repository
public class NetServiceClassifyDao extends BaseDao<CmsNetServiceClassifyEO> implements INetServiceClassifyDao {

    @Override
    public List<CmsNetServiceClassifyEO> getEOsByPid(Long pid) {
        return this.getEntitiesByHql("from CmsNetServiceClassifyEO where pid = ? order by sort",new Object[] {pid});
    }

    @Override
    public List<CmsNetServiceClassifyEO> getEOs() {
        Long siteId = LoginPersonUtil.getSiteId();
        return this.getEntitiesByHql("from CmsNetServiceClassifyEO where siteId = ? order by sort",new Object[] {siteId});
    }

    @Override
    public void recurDel(Long id) {
    }

    @Override
    public Object getSortNum(Long pid) {
        StringBuffer sb = new StringBuffer("select max(sort) from CmsNetServiceClassifyEO where pid = ");
        sb.append(pid);
        List<Object> obj = (List<Object>)this.getObjects(sb.toString(),new Object[]{});
        return null == obj || obj.size() <= 0 || null == obj.get(0) ?0:obj.get(0);
    }
}
