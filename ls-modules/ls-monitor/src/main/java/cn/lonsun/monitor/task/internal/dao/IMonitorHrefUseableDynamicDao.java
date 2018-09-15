package cn.lonsun.monitor.task.internal.dao;

import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.monitor.task.internal.entity.MonitorHrefUseableDynamicEO;
import cn.lonsun.monitor.task.internal.entity.vo.HrefUseableQueryVO;

import java.util.Date;

/**
 * @author gu.fei
 * @version 2017-09-28 9:22
 */
public interface IMonitorHrefUseableDynamicDao extends IMockDao<MonitorHrefUseableDynamicEO> {

    /**
     * 分页查询
     * @param vo
     * @return
     */
    Pagination getPage(HrefUseableQueryVO vo);

    /**
     * 获取指定日期站点首页链接不可访问数量
     * @param taskId
     * @param date
     * @return
     */
    Long getHrefUseableDynamicCout(Long taskId, Date date);
}
