package cn.lonsun.webservice.monitor.client.impl;

import cn.lonsun.webservice.core.WebServiceCaller;
import cn.lonsun.webservice.monitor.client.IMonitorSeriousErrorClient;
import cn.lonsun.webservice.monitor.client.vo.SeriousErrorVO;
import cn.lonsun.webservice.to.WebServiceTO;
import net.sf.json.JSONArray;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author gu.fei
 * @version 2017-11-08 15:55
 */
@Service
public class MonitorSeriousErrorClientImpl implements IMonitorSeriousErrorClient {

    private enum Codes{
        Monitor_saveSeriousError
    }

    @Override
    public WebServiceTO saveSeriousError(List<SeriousErrorVO> seriousErrorArray) {
        JSONArray array = JSONArray.fromObject(seriousErrorArray);
        return WebServiceCaller.getWebServiceTO(MonitorSeriousErrorClientImpl.Codes.Monitor_saveSeriousError.toString(),
                new Object[]{array.toString()});
    }
}
