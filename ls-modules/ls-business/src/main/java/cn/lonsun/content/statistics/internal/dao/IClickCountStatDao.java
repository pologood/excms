package cn.lonsun.content.statistics.internal.dao;

import cn.lonsun.content.statistics.internal.entity.ClickCountStatEO;
import cn.lonsun.core.base.dao.IBaseDao;
import cn.lonsun.statistics.WordListVO;

import java.util.Date;
import java.util.List;

/**
 * 点击数统计数据持久层接口
 * Created by zhushouyong on 2016-8-12.
 */
public interface IClickCountStatDao extends IBaseDao<ClickCountStatEO> {


    /**
     * 根据日期获取内容IDs
     *
     * @param siteId
     * @param topCount
     * @param date
     * @param sort
     * @return
     */
    List<Long> getContentIdsByDate(Long siteId, Integer topCount, Date date, Integer sort);

    /**
     * 根据日期获取栏目IDs
     *
     * @param siteId
     * @param topCount
     * @param date
     * @param sort
     * @return
     */
    List<Object> getColumnIdsByDate(Long siteId, Integer topCount, Date date, Integer sort);
}
