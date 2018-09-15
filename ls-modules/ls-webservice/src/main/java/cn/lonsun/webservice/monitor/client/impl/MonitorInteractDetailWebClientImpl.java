package cn.lonsun.webservice.monitor.client.impl;

import cn.lonsun.webservice.core.WebServiceCaller;
import cn.lonsun.webservice.monitor.client.IMonitorInteractDetailWebClient;
import cn.lonsun.webservice.monitor.client.vo.InteractDetailVO;
import cn.lonsun.webservice.to.WebServiceTO;
import net.sf.json.JSONArray;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author liuk
 * @version 2017-11-27
 */
@Service
public class MonitorInteractDetailWebClientImpl implements IMonitorInteractDetailWebClient {

    private enum Codes{
        Monitor_saveInteractDetails
    }

    /**
     * 推送互动回应更新信息
     * @param interactArray
     * @return
     */
    @Override
    public WebServiceTO saveInteractDetails(List<InteractDetailVO> interactArray) {
        JSONArray array = JSONArray.fromObject(interactArray);
        return WebServiceCaller.getWebServiceTO(MonitorInteractDetailWebClientImpl.Codes.Monitor_saveInteractDetails.toString(),
                new Object[]{array.toString()});
    }
}
