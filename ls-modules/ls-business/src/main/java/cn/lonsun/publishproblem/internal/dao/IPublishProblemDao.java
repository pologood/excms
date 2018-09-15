package cn.lonsun.publishproblem.internal.dao;

import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.publishproblem.vo.PulishProblemQueryVO;

/**
 * Created by huangxx on 2017/9/1.
 */
public interface IPublishProblemDao extends IMockDao<BaseContentEO> {

    /**
     * 获取发布中状态内容分页
     * @param vo
     * @return
     */
    Pagination getPage (PulishProblemQueryVO vo);
}
