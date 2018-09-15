package cn.lonsun.net.service.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.lonsun.cache.client.CacheGroup;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.core.base.service.impl.BaseService;
import cn.lonsun.net.service.dao.INetServiceClassifyDao;
import cn.lonsun.net.service.entity.CmsNetServiceClassifyEO;
import cn.lonsun.net.service.service.INetServiceClassifyService;

/**
 * @author gu.fei
 * @version 2015-11-18 13:47
 */
@Service
public class NetServiceClassifyService extends BaseService<CmsNetServiceClassifyEO> implements INetServiceClassifyService {

    @Autowired
    private INetServiceClassifyDao netServiceClassifyDao;

    @Override
    public List<CmsNetServiceClassifyEO> getEOsByPid(Long pid) {
        return netServiceClassifyDao.getEOsByPid(pid);
    }

    @Override
    public List<CmsNetServiceClassifyEO> getEOs() {
        return netServiceClassifyDao.getEOs();
    }

    @Override
    public void recurDel(Long id) {

    }

    @Override
    public void save(CmsNetServiceClassifyEO eo, String flag) {
        if(eo.getPid() == null) {eo.setPid((long) -1);}
        if("false".equals(flag)) {
            updateParentFlag(eo.getPid(),"true");
        }
        this.saveEntity(eo);
        CacheHandler.reload(CmsNetServiceClassifyEO.class.getName());
    }

    @Override
    public void update(CmsNetServiceClassifyEO eo) {
        CmsNetServiceClassifyEO cnsc = this.getEntity(CmsNetServiceClassifyEO.class, eo.getId());
        cnsc.setName(eo.getName());
        cnsc.setLink(eo.getLink());
        cnsc.setSort(eo.getSort());
        this.updateEntity(cnsc);
        CacheHandler.reload(CmsNetServiceClassifyEO.class.getName());
    }

    @Override
    public void delete(Long id, Long pid) {
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("pid", pid);
        this.delete(CmsNetServiceClassifyEO.class, id);
        List<CmsNetServiceClassifyEO> list = CacheHandler.getList(CmsNetServiceClassifyEO.class, CacheGroup.CMS_PARENTID,pid);

        //由于delete 事件还没触发，所以需要-1判断是否还有子节点
        if(null == list || list.size() - 1 <= 0) {
            updateParentFlag(pid,"false");
        }
    }

    /*
        * 更改是否是父节点标识
        * */
    private void updateParentFlag(Long id,String flag) {
        CmsNetServiceClassifyEO cnsc = CacheHandler.getEntity(CmsNetServiceClassifyEO.class, id);
        cnsc.setIsParent(flag);
        this.updateEntity(cnsc);
    }

    @Override
    public Object getSortNum(Long pid) {
        return netServiceClassifyDao.getSortNum(pid);
    }
}
