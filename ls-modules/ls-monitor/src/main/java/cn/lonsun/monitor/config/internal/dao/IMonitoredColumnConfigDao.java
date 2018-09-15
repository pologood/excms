package cn.lonsun.monitor.config.internal.dao;

import cn.lonsun.common.vo.PageQueryVO;
import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.monitor.config.internal.entity.MonitoredColumnConfigEO;

import java.util.List;

/**栏目类别配置服务
 * Created by lonsun on 2017-9-22.
 */
public interface IMonitoredColumnConfigDao extends IMockDao<MonitoredColumnConfigEO> {
    /**
      * 分页查询列表
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

    MonitoredColumnConfigEO queryConfigByCode(MonitoredColumnConfigEO columnConfigEO);

    MonitoredColumnConfigEO queryConfigByTypeId(Long typeId);
}
