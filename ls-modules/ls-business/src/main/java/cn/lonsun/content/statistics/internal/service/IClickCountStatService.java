package cn.lonsun.content.statistics.internal.service;

import cn.lonsun.content.statistics.internal.entity.ClickCountStatEO;
import cn.lonsun.core.base.service.IBaseService;
import cn.lonsun.statistics.WordListVO;

import java.util.Date;
import java.util.List;

/**
 *
 * 点击数统计服务层接口
 * Created by zhushouyong on 2016-8-12.
 */
public interface IClickCountStatService extends IBaseService<ClickCountStatEO>  {

    /**
     * 保存点击数统计
     * @param siteId
     * @param columnId
     * @param contentId
     */
    void saveClickCountStat(Long siteId,Long columnId,Long contentId);

    /**
     * 获取指定日期当天后点击排行内容ID
     * @param topCount
     * @param siteId
     *        目标条数
     * @param date
     *        指定日期
     * @param sort
     *        排序（0:降序 1:升序）
     * @return
     */
    Long[] getContentIdsByDay(Long siteId,Integer topCount,Date date,Integer sort);


    /**
     * 获取指定日期前一周后点击排行内容ID
     * @param siteId
     * @param topCount
     *        目标条数
     * @param date
     *        指定日期
     * @param sort
     *        排序（0:降序 1:升序）
     * @return
     */
    Long[] getContentIdsByWeek(Long siteId,Integer topCount,Date date,Integer sort);

    /**
     * 获取指定日期前一个月后点击排行内容ID
     * @param siteId
     * @param topCount
     *        目标条数
     * @param date
     *        指定日期
     * @param sort
     *        排序（0:降序 1:升序）
     * @return
     */
    Long[] getContentIdsByMonth(Long siteId,Integer topCount,Date date,Integer sort);

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

    /**
     * 根据日期获取点击排行内容ID
     * @param topCount
     * @param siteId
     *        目标条数
     * @param date
     *        指定日期
     * @param sort
     *        排序（0:降序 1:升序）
     * @return
     */
    Long[] getContentIdsByDate(Long siteId,Integer topCount,Date date,Integer sort);
}
