package cn.lonsun.monitor.config.internal.dao;

import cn.lonsun.common.vo.PageQueryVO;
import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.monitor.config.internal.entity.MonitoredVetoConfigEO;

import java.util.List;

/**
 * Created by lonsun on 2017-9-25.
 */
public interface IMonitoredVetoConfigDao extends IMockDao<MonitoredVetoConfigEO> {
    /**
     * 根据类别查询
     * @param typeCode
     * @return
     */
    List<MonitoredVetoConfigEO> getDataByCode(String typeCode,Long siteId);


    /**
     * 获取单项否决-栏目不更新
     * @param pageQueryVO
     * @param typeCode
     * @return
     */
    Pagination getColumnUpdatePage(PageQueryVO pageQueryVO, String typeCode,Long siteId);

    /**
     * 根据栏目类别和否决类别编码查询栏目不更新配置
     * @param columnTypeCode
     * @param typeCode
     * @return
     */
    List<MonitoredVetoConfigEO> getColumnUpdateDataByCode(String columnTypeCode, String typeCode,Long siteId);

    /**
     * @param typeCode(类别编码)
     * @param columnTypeCode(栏目类别编码)
     * @param baseCode( vote:单项否决,scop:综合评分)
     * @param siteId
     * @return
     */
    List<MonitoredVetoConfigEO>  getMonitorConfig(String typeCode, String columnTypeCode, String baseCode,Long siteId);
}
