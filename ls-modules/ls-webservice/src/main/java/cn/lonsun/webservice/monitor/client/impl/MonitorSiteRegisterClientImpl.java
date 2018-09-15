package cn.lonsun.webservice.monitor.client.impl;

import cn.lonsun.webservice.core.WebServiceCaller;
import cn.lonsun.webservice.monitor.client.IMonitorSiteRegisterClient;
import cn.lonsun.webservice.monitor.client.vo.SiteRegisterVO;
import cn.lonsun.webservice.to.WebServiceTO;
import net.sf.json.JSONArray;
import org.springframework.stereotype.Service;

import java.util.List;

import static cn.lonsun.webservice.core.WebServiceCaller.getWebServiceTO;

/**
 * 站点注册信息客户端接口实现类
 * @author gu.fei
 * @version 2017-11-23 9:09
 */
@Service
public class MonitorSiteRegisterClientImpl implements IMonitorSiteRegisterClient {

    private enum Codes{
        Monitor_saveSiteRegister,
        Monitor_disableSiteRegister
    }

    /**
     * 保存站点注册信息
     * @param registerArray
     */
    @Override
    public WebServiceTO saveSiteRegister(List<SiteRegisterVO> registerArray) {
        JSONArray array = JSONArray.fromObject(registerArray);
        return getWebServiceTO(MonitorSiteRegisterClientImpl.Codes.Monitor_saveSiteRegister.toString(),
                new Object[]{array.toString()});
    }

    @Override
    public WebServiceTO disableSiteRegister(String registerCode, Long siteId) {
        return WebServiceCaller.getWebServiceTO(MonitorSiteRegisterClientImpl.Codes.Monitor_disableSiteRegister.toString(),
                new Object[]{registerCode,siteId});
    }
}
