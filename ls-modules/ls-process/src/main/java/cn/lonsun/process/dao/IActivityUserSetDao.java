package cn.lonsun.process.dao;

import cn.lonsun.core.base.dao.IBaseDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.process.entity.ActivityUserSetEO;
import cn.lonsun.process.vo.ActivityUserSetQueryVO;

/**
 * Created by liuk on 2017-03-22.
 */
public interface IActivityUserSetDao extends IBaseDao<ActivityUserSetEO> {


    /**
     * 获取分页列表
     * @param queryVO
     * @return
     */
    Pagination getPagination(ActivityUserSetQueryVO queryVO);

}
