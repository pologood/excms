package cn.lonsun.webservice.config.impl;

import cn.lonsun.webservice.config.IMonitoredService;
import cn.lonsun.webservice.core.WebServiceCaller;
import cn.lonsun.webservice.to.WebServiceTO;
import org.springframework.stereotype.Service;

/**
 * Created by lonsun on 2017-9-28.
 */
@Service("monitoredService")
public class MonitoredServiceImpl implements IMonitoredService {

    /**
     * 服务编码
     *
     * @author xujh
     * @version 1.0 2015年4月24日
     *
     */
    private enum Codes {
        Monitored_getMonitorConfigCode,
        Monitored_getDefaultTypeCode,
        Monitored_getAllMonitorConfig,
        Monitored_saveSiteVetoConfig
    }


    /**
     * webService调取接口
     * @param typeCode
     * @param columnTypeCode
     * @param baseCode
     * @return
     */

    @Override
    public WebServiceTO getMonitorConfig(String typeCode, String columnTypeCode, String baseCode, Long siteId, String registerCode) {
        return WebServiceCaller.getWebServiceTO(Codes.Monitored_getMonitorConfigCode.toString(),
                new Object[]{typeCode,columnTypeCode,baseCode, siteId,registerCode});

    }
    /**
     * 获取默认栏目类别
     * @return
     */
    @Override
    public WebServiceTO getDefaultColumnType(Long siteId, String registerCode) {
        return WebServiceCaller.getWebServiceTO(Codes.Monitored_getDefaultTypeCode.toString(),new Object[]{siteId,registerCode});
    }
    /**
     * 获取所有监测规则
     * @return
     */
    @Override
    public WebServiceTO getAllMonitorConfig(Long siteId, String registerCode) {
        return WebServiceCaller.getWebServiceTO(Codes.Monitored_getAllMonitorConfig.toString(),new Object[]{siteId,registerCode});
    }
    /**
     * 将规则配置保存到云端
     * @return
     */
    @Override
    public WebServiceTO saveSiteVetoConfig(String json, String siteId, String registerCode) {
        return WebServiceCaller.getWebServiceTO(Codes.Monitored_saveSiteVetoConfig.toString(),new Object[]{json,siteId,registerCode});
    }
}
