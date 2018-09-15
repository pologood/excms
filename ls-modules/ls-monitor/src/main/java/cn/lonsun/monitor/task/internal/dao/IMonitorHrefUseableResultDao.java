package cn.lonsun.monitor.task.internal.dao;

import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.monitor.task.internal.entity.MonitorHrefUseableResultEO;
import cn.lonsun.monitor.task.internal.entity.vo.HrefUseableQueryVO;
import cn.lonsun.monitor.task.internal.entity.vo.HrefUseableStatisVO;

import java.util.List;

/**
 * @author gu.fei
 * @version 2017-09-28 9:22
 */
public interface IMonitorHrefUseableResultDao extends IMockDao<MonitorHrefUseableResultEO> {


    /**
     * 分页查询错误链接
     * @param vo
     * @return
     */
    Pagination getHrefUseablePage(HrefUseableQueryVO vo);

    /**
     * 不分页查询错误链接
     * @param vo
     * @return
     */
    List<MonitorHrefUseableResultEO> getHrefUseableList(HrefUseableQueryVO vo);

    /**
     * 错链数量统计
     * @param taskId
     * @return
     */
    HrefUseableStatisVO getHrefUseaStatis(Long taskId);
}
