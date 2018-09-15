package cn.lonsun.pushinfo.service.impl;

import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.pushinfo.dao.IPushInfoDao;
import cn.lonsun.pushinfo.entity.PushInfoEO;
import cn.lonsun.pushinfo.service.IPushInfoService;
import cn.lonsun.site.template.internal.entity.ParamDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author gu.fei
 * @version 2016-1-21 14:36
 */
@Service("pushInfoService")
public class PushInfoService extends MockService<PushInfoEO> implements IPushInfoService {

    @Autowired
    private IPushInfoDao pushInfoDao;

    @Override
    public Pagination getPageEOs(ParamDto dto) {
        return pushInfoDao.getPageEOs(dto);
    }

    @Override
    public PushInfoEO getByPath(String path) {
        return pushInfoDao.getByPath(path);
    }

    @Override
    public void deleteEOs(Long[] ids) {
        pushInfoDao.deleteEOs(ids);
    }
}
