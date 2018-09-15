package cn.lonsun.monitor.config.internal.service;


import cn.lonsun.common.vo.PageQueryVO;
import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.monitor.config.internal.entity.MonitoredVetoConfigEO;

import java.util.List;
import java.util.Map;

/**单项否决
 * Created by lonsun on 2017-9-25.
 */

public interface IMonitoredVetoConfigService extends IMockService<MonitoredVetoConfigEO> {
    /**
     * 根据类别查询
     * @param typeCode
     * @return
     */
    public Map<String, Object> getDataByCode(String typeCode, String id, Long siteId);
    /**
     * 根据类别查询
     * @param typeCode
     * @return
     */
    public Map<String, Object> getDataByCode(String typeCode, Long siteId);

    /**
     * 保存
     * @param content
     * @param typeCode
     * @param typeCode
     */
    void saveData(String content, String typeCode, Long siteId);


    /**
     * @param typeCode(类别编码)
     * @param columnTypeCode(栏目类别编码)
     * @param baseCode(vote:单项否决,scop:综合评分)
     * @param siteId
     * @return
     */
    List<MonitoredVetoConfigEO> getMonitorConfig(String typeCode, String columnTypeCode, String baseCode,Long siteId);


    /**
     * 查询配置
     * @param typeCode
     * @param columnTypeCode
     * @param baseCode
     * @param siteId
     * @return
     */
    Map<String, Object> getConfigByTypes(String typeCode, String columnTypeCode, String baseCode,Long siteId);

    /**
     * 将站点的配置推送到云端
     * @param siteId
     */
    public void sentConfigToCloud(Long siteId);

    /**
     * 从服务端拉取配置
     * @param siteId
     */
    public void getConfigFromCloud(String... siteId);

    Pagination getDataPageByCode(PageQueryVO page, String typeCode, Long siteId);
}
