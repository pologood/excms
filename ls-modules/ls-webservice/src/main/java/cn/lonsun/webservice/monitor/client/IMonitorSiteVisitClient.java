package cn.lonsun.webservice.monitor.client;

import cn.lonsun.webservice.monitor.client.vo.SiteVisitResponseVO;

/**
 * @author gu.fei
 * @version 2017-10-18 9:41
 */
public interface IMonitorSiteVisitClient {

    /**
     * 验证站点访问情况
     * @param reportId 任务批次ID
     * @param siteId
     * @param siteName
     * @param code 站点注册编码
     * @param url 监测地址
     * @param timeout 超时时间
     * @return
     */
    SiteVisitResponseVO checkUrlConnect(Long reportId,Long siteId,String siteName,String code,String url, int timeout);
}
