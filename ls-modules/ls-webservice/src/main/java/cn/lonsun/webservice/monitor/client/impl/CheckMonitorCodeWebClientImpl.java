package cn.lonsun.webservice.monitor.client.impl;

import cn.lonsun.webservice.monitor.client.ICheckMonitorCodeWebClient;
import cn.lonsun.webservice.processEngine.util.WSClient;
import cn.lonsun.webservice.to.WebServiceTO;
import org.springframework.stereotype.Service;

/**
 * 检测站群标识码是否存在
 * @author liuk
 * @version 2017年9月22日
 */
@Service("checkMonitorCodeWebClient")
public class CheckMonitorCodeWebClientImpl implements ICheckMonitorCodeWebClient {
    private enum Codes{
        Monitor_checkMonitorCode

        /**
         *   /services/checkMonitorCodeWebService
         *
         *   http://impl.webservice.axis2.api.lonsun.cn
         */
    }

    /**
     * 检测站群标识码是否存在
     * @param code
     * @return
     */
    @Override
    public Object checkMonitorCode(String code) {
        return  WSClient.getObject(CheckMonitorCodeWebClientImpl.Codes.Monitor_checkMonitorCode.toString(),
                new Object[]{code},Object.class);
    }
}
