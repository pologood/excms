package cn.lonsun.process.service;

import cn.lonsun.core.base.service.IBaseService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.process.entity.ActivityUserSetEO;
import cn.lonsun.process.vo.ActivityUserSetQueryVO;

/**
 * Created by liuk on 2017-3-23.
 */
public interface IActivityUserSetService extends IBaseService<ActivityUserSetEO> {


    /**
     * 获取分页列表
     * @param queryVO
     * @return
     */
    Pagination getPagination(ActivityUserSetQueryVO queryVO);
}
