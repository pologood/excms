package cn.lonsun.monitor.internal.dao;

import cn.lonsun.core.base.dao.IBaseDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.monitor.internal.entity.MonitorColumnDetailEO;
import cn.lonsun.monitor.internal.vo.MonitorDetailQueryVO;

import java.util.Date;
import java.util.List;

/**
 * 日常监测栏目更新详细dao层<br/>
 *
 */

public interface IMonitorColumnDetailDao extends IBaseDao<MonitorColumnDetailEO> {

    /**
     * 获取日常监测栏目更新详细分页
     */
    Pagination getPage(MonitorDetailQueryVO queryVO);

    List<MonitorColumnDetailEO> getList(MonitorDetailQueryVO queryVO);

    List<MonitorColumnDetailEO> getList(Long monitorId,String[] columnType,String infoType,Long updateCount);

    /**
     * 根据条件查询首页栏目列表
     * @param monitorId
     * @param columnType
     * @param updateCount
     * @return
     */
//    List<MonitorColumnDetailEO> getIndexList(Long monitorId,String[] columnType,Long updateCount);

    Long getCount(Long monitorId,String[] columnType,String infoType,Long updateCount);

    /**
     * 根据条件查询首页栏目更新总数
     * @param monitorId
     * @param columnType
     * @param updateCount
     * @return
     */
//    Long getIndexCount(Long monitorId,String[] columnType,Long updateCount);

    /**
     * 获取规定时间内已经更新数据的首页栏目
     * @param columnIds
     * @param st
     * @param ed
     * @return
     */
    List<MonitorColumnDetailEO> getIndexUpdatedColumns(String columnIds,Date st,Date ed);

    /**
     * 获取规定时间内已经更新数据的栏目
     * @param columnIds
     * @param updateCycle
     * @return
     */
    List<MonitorColumnDetailEO> getUpdatedColumns(String columnIds,Integer updateCycle);

    /**
     * 获取规定时间内已经更新数据的首页信息公开栏目
     * @param organCatId
     * @param st
     * @param ed
     * @return
     */
    List<MonitorColumnDetailEO> getIndexUpdatedPublics(String[] organCatId,Date st,Date ed);

    /**
     * 获取规定时间内已经更新数据的信息公开栏目
     * @param organCatId
     * @param updateCycle
     * @return
     */
    List<MonitorColumnDetailEO> getUpdatedPublics(String[] organCatId,Integer updateCycle);

    /**
     * 获取日常监测栏目更新详细数目
     */
    Long getCounts(MonitorDetailQueryVO queryVO);

    /**
     * 获取非空吧栏目列表
     */
    List<Long> getUnEmptyColumnList(Long siteId);

    /**
     * 获取非空吧栏目列表
     */
    List<MonitorColumnDetailEO> getUnEmptyColumnInfoList(Long siteId);


}

