package cn.lonsun.monitor.task.internal.service;

import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.monitor.task.internal.entity.MonitorSiteStatisEO;
import cn.lonsun.monitor.task.internal.entity.vo.SiteStatisQueryVO;

/**
 * @author gu.fei
 * @version 2017-09-28 9:24
 */
public interface IMonitorSiteStatisService extends IMockService<MonitorSiteStatisEO> {

    /**
     * 分页查询站点统计信息
     * @param vo
     * @return
     */
    Pagination getSiteStatisPage(SiteStatisQueryVO vo);

    /**
     * 单项否决-站点无法访问
     * @param siteId 站点ID
     * @param taskId 任务ID
     * @param status 0：不合格 1：合格
     */
    void updateSiteDeny(Long siteId,Long taskId,Integer status);

    /**
     * 单项否决-站点更新
     * @param siteId 站点ID
     * @param taskId 任务ID
     * @param status 0：不合格 1：合格
     */
    void updateSiteUpdate(Long siteId,Long taskId,Integer status);

    /**
     * 单项否决-栏目更新
     * @param siteId 站点ID
     * @param taskId 任务ID
     * @param status 0：不合格 1：合格
     */
    void updateColumnUpdate(Long siteId,Long taskId,Integer status);

    /**
     * 单项否决-严重错误
     * @param siteId 站点ID
     * @param taskId 任务ID
     * @param status 0：不合格 1：合格
     */
    void updateError(Long siteId,Long taskId,Integer status);

    /**
     * 单项否决-互动回应
     * @param siteId 站点ID
     * @param taskId 任务ID
     * @param status 0：不合格 1：合格
     */
    void updateReply(Long siteId,Long taskId,Integer status);

    /**
     * 综合评分-网站可用性
     * @param siteId 站点ID
     * @param taskId 任务ID
     * @param score 0：不合格 1：合格
     */
    void updateSiteUse(Long siteId,Long taskId,String score);

    /**
     * 综合评分-首页信息更新
     * @param siteId 站点ID
     * @param taskId 任务ID
     * @param score 0：不合格 1：合格
     */
    void updateIndexInfoUpdate(Long siteId,Long taskId,String score);

    /**
     * 综合评分-信息更新
     * @param siteId 站点ID
     * @param taskId 任务ID
     * @param score 0：不合格 1：合格
     */
    void updateInfoUpdate(Long siteId,Long taskId,String score);

    /**
     * 综合评分-互动回应
     * @param siteId 站点ID
     * @param taskId 任务ID
     * @param score 0：不合格 1：合格
     */
    void updateReplyScope(Long siteId,Long taskId,String score);

    /**
     * 统计总结数据
     * @param siteId
     * @param taskId
     */
    void summaryStatisData(Long siteId, Long taskId);

    /**
     * 获取站点监控统计
     * @param siteId
     * @return
     */
    MonitorSiteStatisEO getSiteStatis(Long siteId);

    /**
     * 获取站点监控统计
     * @param siteId
     * @param taskId
     * @return
     */
    MonitorSiteStatisEO getSiteStatis(Long siteId,Long taskId);
}
