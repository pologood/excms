package cn.lonsun.content.internal.service.impl;

import cn.lonsun.content.internal.dao.IBaseContentSpecialDao;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.service.IBaseContentSpecialService;
import cn.lonsun.core.base.service.impl.MockService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by Administrator on 2017/9/26.
 */
@Service
public class BaseContentSpecialServiceImpl extends MockService<BaseContentEO> implements IBaseContentSpecialService  {

    @Resource
    private IBaseContentSpecialDao baseContentSpecialDao;
    @Override
    public Long getCountByColumnId(Long columnId) {
        return baseContentSpecialDao.getCountByColumnId(columnId);
    }
}
