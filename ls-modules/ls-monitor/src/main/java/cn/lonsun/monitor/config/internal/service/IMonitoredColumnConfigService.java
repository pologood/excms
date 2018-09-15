package cn.lonsun.monitor.config.internal.service;

import cn.lonsun.common.vo.PageQueryVO;
import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.monitor.config.internal.entity.MonitoredColumnConfigEO;
import cn.lonsun.site.site.internal.entity.ColumnMgrEO;

import java.util.List;

/**监测栏目类别
 * Created by lonsun on 2017-9-22.
 */
public interface IMonitoredColumnConfigService extends IMockService<MonitoredColumnConfigEO> {
    /**
     * 分页查询配置
     * @param pageQueryVO
     * @return
     */
    Pagination getPage(PageQueryVO pageQueryVO);

    /**
     * 获取未删除项
     * @param ids
     * @return
     */
    List<MonitoredColumnConfigEO> getNoramlConfig(Long[] ids);

    /**
     * 获取栏目更新栏目
     * @return
     */
    List<MonitoredColumnConfigEO> getUpateColumn();

    /**
     * 获取栏目类别
     * @param infoUpdateTypeList
     * @return
     */
    List<MonitoredColumnConfigEO> getColumnByType(List<String> infoUpdateTypeList);

    /**
     * 获取当前站点栏目类别
     * @param siteId
     * @return
     */
    List<MonitoredColumnConfigEO> getSiteColumnType(Long siteId);

    /**
     * 根据编码查询
     * @param columnConfigEO
     * @return
     */
    MonitoredColumnConfigEO queryConfigByCode(MonitoredColumnConfigEO columnConfigEO);

    MonitoredColumnConfigEO queryConfigByTypeId(Long typeId);

    /**
     * 查询栏目级别详情
     * @param columnId
     * @return
     */
    Pagination getColumnLevel(String columnId,PageQueryVO pageQueryVO);

    /**
     * 根据typeCode查询栏目
     * @param code
     * @return
     */
    List<ColumnMgrEO>  getColumnByCode(String code,Long siteId);

    /**
     * 根据typeCode查询绑定的信息公开目录
     * @param code
     * @return
     */
    String getPublicCatsByCode(String code,Long siteId);
    /**
     * 查询公开栏目级别详情
     * @param columnId
     * @return
     */
    Pagination getPublicLevel(String columnId, PageQueryVO pageQueryVO);

    /**
     * 栏目同步完成后同步监测配置
     * @param siteId
     * @return
     * @author zhongjun
     */
    public boolean syncConfigFromCloud(String... siteId);
}
