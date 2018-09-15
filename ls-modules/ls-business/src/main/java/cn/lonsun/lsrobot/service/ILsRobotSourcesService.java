package cn.lonsun.lsrobot.service;

import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.lsrobot.entity.LsRobotSourcesEO;
import cn.lonsun.lsrobot.vo.RobotPageVO;

import java.util.List;

/**
 * @author gu.fei
 * @version 2016-07-07 14:19
 */
public interface ILsRobotSourcesService extends IMockService<LsRobotSourcesEO> {

    /**
     * 分页获取龙讯智能机器人数据源
     * @param vo
     * @return
     */
    public Pagination getPageEntities(RobotPageVO vo);

    /**
     * 获取启用的资源
     * @return
     */
    public List<LsRobotSourcesEO> getEntities(RobotPageVO vo);

    /**
     * 获取完全匹配内容
     * @param vo
     * @return
     */
    public LsRobotSourcesEO getEntity(RobotPageVO vo);

    /**
     * 获取当前最大的排序数
     * @return
     */
    public Long getMaxSortNum();
}
