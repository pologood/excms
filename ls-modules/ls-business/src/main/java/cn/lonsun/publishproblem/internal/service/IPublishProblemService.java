package cn.lonsun.publishproblem.internal.service;

import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.publishproblem.vo.PulishProblemQueryVO;

/**
 * Created by huangxx on 2017/9/1.
 */
public interface IPublishProblemService extends IMockService<BaseContentEO> {

    /**
     * 获取发布中状态内容分页
     * @param vo
     * @return
     */
    Pagination getPage (PulishProblemQueryVO vo);

    /**
     * 发布
     * @param publish
     * @param ids
     * @return
     */
    String publish(Long[] ids, Integer publish);
}
