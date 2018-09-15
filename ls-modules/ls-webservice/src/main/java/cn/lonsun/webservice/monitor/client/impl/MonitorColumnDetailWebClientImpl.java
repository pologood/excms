package cn.lonsun.webservice.monitor.client.impl;

import cn.lonsun.webservice.core.WebServiceCaller;
import cn.lonsun.webservice.monitor.client.IMonitorColumnDetailWebClient;
import cn.lonsun.webservice.monitor.client.vo.ColumnDetailVO;
import cn.lonsun.webservice.to.WebServiceTO;
import net.sf.json.JSONArray;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author liuk
 * @version 2017-11-27
 */
@Service
public class MonitorColumnDetailWebClientImpl implements IMonitorColumnDetailWebClient {

    private enum Codes{
        Monitor_saveColumnDetails
    }

    /**
     * 推送栏目更新信息
     * @param columnArray
     * @return
     */
    @Override
    public WebServiceTO saveColumnDetails(List<ColumnDetailVO> columnArray) {
        JSONArray array = JSONArray.fromObject(columnArray);
        return WebServiceCaller.getWebServiceTO(MonitorColumnDetailWebClientImpl.Codes.Monitor_saveColumnDetails.toString(),
                new Object[]{array.toString()});
    }
}
