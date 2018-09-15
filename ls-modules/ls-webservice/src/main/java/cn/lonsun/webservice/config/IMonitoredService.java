package cn.lonsun.webservice.config;

import cn.lonsun.webservice.to.WebServiceTO;

/**
 * Created by lonsun on 2017-9-28.
 */
public interface IMonitoredService {
    /**
     * webService调取接口
     * @param typeCode
     * @param columnTypeCode
     * @param baseCode
     * @return
     */
     public WebServiceTO  getMonitorConfig(String typeCode,String columnTypeCode,String baseCode, Long siteId, String registerCode);

    /**
     * 获取默认栏目类别
     * @return
     */
    public  WebServiceTO getDefaultColumnType(Long siteId, String registerCode);

    public  WebServiceTO getAllMonitorConfig(Long siteId, String registerCode);

    public  WebServiceTO saveSiteVetoConfig(String json, String siteId, String registerCode);
}


