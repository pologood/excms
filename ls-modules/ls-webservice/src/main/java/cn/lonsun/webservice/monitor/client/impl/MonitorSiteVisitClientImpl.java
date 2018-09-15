package cn.lonsun.webservice.monitor.client.impl;

import cn.lonsun.webservice.core.WebServiceCaller;
import cn.lonsun.webservice.monitor.client.IMonitorSiteVisitClient;
import cn.lonsun.webservice.monitor.client.vo.SiteVisitResponseVO;
import org.springframework.stereotype.Service;

/**
 * @author gu.fei
 * @version 2017-10-18 9:45
 */
@Service
public class MonitorSiteVisitClientImpl implements IMonitorSiteVisitClient {

    private enum Codes{
        Monitor_getSiteVisitResult
    }

    @Override
    public SiteVisitResponseVO checkUrlConnect(Long reportId, Long siteId, String siteName, String code, String url, int timeout) {
        return  (SiteVisitResponseVO) WebServiceCaller.getSimpleObject(Codes.Monitor_getSiteVisitResult.toString(),
                new Object[]{reportId,siteId,siteName,code,url,timeout},SiteVisitResponseVO.class);
    }
}
