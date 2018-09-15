package cn.lonsun.webservice.monitor.client;

import cn.lonsun.webservice.monitor.client.vo.ReportResponseVO;

/**
 * @author gu.fei
 * @version 2017-10-18 9:41
 */
public interface IMonitorReportRecordClient {

    /**
     * 保存云端检测报告
     * @param registerCode
     * @param siteId
     * @param siteName
     * @return
     */
    ReportResponseVO pushReportRecord(String registerCode, Long siteId, String siteName);
}
