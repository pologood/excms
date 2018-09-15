package cn.lonsun.monitor.task.internal.dao;

import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.monitor.task.internal.entity.MonitorSeriousErrorResultEO;
import cn.lonsun.monitor.task.internal.entity.vo.SeriousErrorQueryVO;

import java.util.List;

/**
 * @author gu.fei
 * @version 2017-09-28 9:22
 */
public interface IMonitorSeriousErrorResultDao extends IMockDao<MonitorSeriousErrorResultEO> {


    /**
     * 分页查询严重错误
     * @param vo
     * @return
     */
    Pagination getSeriousErrorPage(SeriousErrorQueryVO vo);

    /**
     * 不分页分页查询严重错误
     * @param vo
     * @return
     */
    List<MonitorSeriousErrorResultEO> getSeriousErrorList(SeriousErrorQueryVO vo);

    /**
     * 查询数量
     * @param taskId
     * @return
     */
    Long getCount(Long taskId);

    /**
     * 查询数量
     * @param taskId
     * @param checkType
     * @return
     */
    Long getCount(Long taskId,String checkType);

    /**
     * 获取严重错误信息
     * @return
     */
    List<MonitorSeriousErrorResultEO> getMonitorSiteGroupErrors();
}
