package cn.lonsun.monitor.internal.dao;

import cn.lonsun.core.base.dao.IBaseDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.monitor.internal.entity.MonitorInteractDetailEO;
import cn.lonsun.monitor.internal.vo.MonitorDetailQueryVO;

import java.util.List;

/**
 * 日常监测互动更新详细dao层<br/>
 *
 */

public interface IMonitorInteractDetailDao extends IBaseDao<MonitorInteractDetailEO> {

    /**
     * 获取日常监测互动更新详细分页
     */
    Pagination getPage(MonitorDetailQueryVO queryVO);

    /**
     * 获取超过三个月未回复留言
     */
    Pagination getUnReplyPage(Long pageIndex,Integer pageSize,String contentIds);

    /**
     * 获取日常监测互动更新详细 不分页
     */
    List<MonitorInteractDetailEO> getList(MonitorDetailQueryVO queryVO);

    /**
     * 获取规定时间内政务咨询类(留言)更新数和未回复数
     * @param columnIds
     * @param updateCycle
     * @param unreplyCycle
     * @return
     */
    List<MonitorInteractDetailEO> getZWZXInfo(String columnIds, Integer updateCycle,Integer unreplyCycle);

    /**
     * 获取规定时间内已经更新数据的栏目
     * @param columnIds
     * @param updateCycle
     * @return
     */
    List<MonitorInteractDetailEO> getUpdatedColumns(String columnIds,Integer updateCycle);

    /**
     * 获取日常监测栏目更新详细数目
     */
    Long getCounts(MonitorDetailQueryVO queryVO);

    List<MonitorInteractDetailEO> getList(Long monitorId,String[] columnType,Long updateCount);

    Long getCount(Long monitorId,String[] columnType,Long updateCount);

}

