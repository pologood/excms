package cn.lonsun.webservice.monitor.client;

import cn.lonsun.webservice.monitor.client.vo.SiteRegisterVO;
import cn.lonsun.webservice.to.WebServiceTO;

import java.util.List;

/**
 * 站点注册信息客户端服务接口
 * @author gu.fei
 * @version 2017-11-23 9:08
 */
public interface IMonitorSiteRegisterClient {

    /**
     * 保存站点注册信息
     * @param registerArray
     */
    WebServiceTO saveSiteRegister(List<SiteRegisterVO> registerArray);

    /**
     * 设置站点注册不可以用
     * @param registerCode
     * @param siteId
     * @return
     */
    WebServiceTO disableSiteRegister(String registerCode,Long siteId);
}
