package cn.lonsun.process.service.impl;

import cn.lonsun.core.base.service.impl.BaseService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.process.dao.IActivityUserSetDao;
import cn.lonsun.process.entity.ActivityUserSetEO;
import cn.lonsun.process.service.IActivityUserSetService;
import cn.lonsun.process.vo.ActivityUserSetQueryVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by liuk on 2017-3-23.
 */
@Service
public class ActivityUserSetServiceImpl extends BaseService<ActivityUserSetEO> implements IActivityUserSetService {

    @Autowired
    private IActivityUserSetDao activityUserSetDao;


    @Override
    public Pagination getPagination(ActivityUserSetQueryVO queryVO) {
        return activityUserSetDao.getPagination(queryVO);
    }
}
