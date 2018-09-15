package cn.lonsun.webservice.monitor.client.impl;

import cn.lonsun.webservice.core.WebServiceCaller;
import cn.lonsun.webservice.monitor.client.IMonitorReportRecordClient;
import cn.lonsun.webservice.monitor.client.vo.ReportResponseVO;
import org.springframework.stereotype.Service;

/**
 * @author gu.fei
 * @version 2017-10-18 9:45
 */
@Service
public class MonitorReportRecordClientImpl implements IMonitorReportRecordClient {

    private enum Codes{
        Monitor_saveReportRecord
    }

    @Override
    public ReportResponseVO pushReportRecord(String registerCode, Long siteId, String siteName) {
        return (ReportResponseVO) WebServiceCaller.getSimpleObject(Codes.Monitor_saveReportRecord.toString(),
                new Object[]{registerCode,siteId,siteName},ReportResponseVO.class);
    }
}
