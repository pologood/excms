package cn.lonsun.content.statistics.internal.service.impl;

import cn.lonsun.content.statistics.internal.dao.IClickCountStatDao;
import cn.lonsun.content.statistics.internal.entity.ClickCountStatEO;
import cn.lonsun.content.statistics.internal.service.IClickCountStatService;
import cn.lonsun.core.base.service.impl.BaseService;
import cn.lonsun.statistics.WordListVO;
import cn.lonsun.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 点击数统计服务层实现
 * Created by zhushouyong on 2016-8-12.
 */
@Service
public class ClickCountStatServiceImpl extends BaseService<ClickCountStatEO> implements IClickCountStatService {

    @Autowired
    private IClickCountStatDao clickCountStatDao;

    /**
     * 保存点击数统计
     *
     * @param columnId
     * @param contentId
     */
    @Override
    public void saveClickCountStat(Long siteId, Long columnId, Long contentId) {
        if (null != contentId) {
            try {
                Date statDay = DateUtil.getToday();
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("siteId", siteId);
                params.put("contentId", contentId);
                params.put("statDay", statDay);
                ClickCountStatEO clickCountStat = clickCountStatDao.getEntity(ClickCountStatEO.class, params);
                if (null != clickCountStat) {
                    clickCountStat.setColumnId(columnId);// 更新栏目id，防止是移动的栏目，导致点击率错误
                    clickCountStat.setCount(clickCountStat.getCount() + 1);
                    clickCountStatDao.update(clickCountStat);
                } else {
                    clickCountStat = new ClickCountStatEO();
                    clickCountStat.setSiteId(siteId);
                    clickCountStat.setColumnId(columnId);
                    clickCountStat.setContentId(contentId);
                    clickCountStat.setCount(1L);
                    clickCountStat.setStatDay(statDay);
                    clickCountStatDao.save(clickCountStat);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取指定日期当天点击内容ID
     *
     * @param topCount 目标条数
     * @param date     指定日期
     * @param sort     排序（0:降序 1:升序）
     * @return
     */
    @Override
    public Long[] getContentIdsByDay(Long siteId, Integer topCount, Date date, Integer sort) {
        if (null == topCount || topCount <= 0) topCount = 10;
        if (null == date) {
            date = DateUtil.getToday();
        } else {
            date = DateUtil.getDayDate(date);
        }
        if (null == sort) sort = 0;
        Long[] contentIds = null;
        List<Long> list = clickCountStatDao.getContentIdsByDate(siteId, topCount, date, sort);
        if (null != list && list.size() > 0) {
            contentIds = new Long[list.size()];
            list.toArray(contentIds);
        }
        return contentIds;
    }

    /**
     * 获取指定日期前一周点击内容ID
     *
     * @param topCount 目标条数
     * @param date     指定日期
     * @param sort     排序（0:降序 1:升序）
     * @return
     */
    @Override
    public Long[] getContentIdsByWeek(Long siteId, Integer topCount, Date date, Integer sort) {
        if (null != date) {
            date = DateUtil.getDayDate(date, -7);
        }
        return getContentIdsByDay(siteId, topCount, date, sort);
    }

    /**
     * 获取指定日期前一个月点击内容ID
     *
     * @param topCount 目标条数
     * @param date     指定日期
     * @param sort     排序（0:降序 1:升序）
     * @return
     */
    @Override
    public Long[] getContentIdsByMonth(Long siteId, Integer topCount, Date date, Integer sort) {
        if (null != date) {
            date = DateUtil.getDayDate(date, -30);
        }
        return getContentIdsByDay(siteId, topCount, date, sort);
    }

    @Override
    public List<Object> getColumnIdsByDate(Long siteId, Integer topCount, Date date, Integer sort) {
        if (null == topCount || topCount <= 0) topCount = 10;
        if (null == sort) sort = 0;
//        Long[] columnIds = null;
//        List<Long> list =  clickCountStatDao.getColumnIdsByDate(siteId,topCount,date,sort);
//        if(null != list && list.size() > 0){
//            columnIds = new Long[list.size()];
//            list.toArray(columnIds);
//        }
        return clickCountStatDao.getColumnIdsByDate(siteId, topCount, date, sort);
    }

    @Override
    public Long[] getContentIdsByDate(Long siteId, Integer topCount, Date date, Integer sort) {
        if (null == topCount || topCount <= 0) topCount = 10;
        if (null == sort) sort = 0;
        Long[] contentIds = null;
        List<Long> list = clickCountStatDao.getContentIdsByDate(siteId, topCount, date, sort);
        if (null != list && list.size() > 0) {
            contentIds = new Long[list.size()];
            list.toArray(contentIds);
        }
        return contentIds;
    }

}
