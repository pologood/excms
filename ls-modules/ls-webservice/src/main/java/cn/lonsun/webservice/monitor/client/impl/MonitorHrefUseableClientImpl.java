package cn.lonsun.webservice.monitor.client.impl;

import cn.lonsun.webservice.core.WebServiceCaller;
import cn.lonsun.webservice.monitor.client.IMonitorHrefUseableClient;
import cn.lonsun.webservice.monitor.client.vo.HrefUseableDynamicVO;
import cn.lonsun.webservice.monitor.client.vo.HrefUseableVO;
import cn.lonsun.webservice.to.WebServiceTO;
import net.sf.json.JSONArray;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * @author gu.fei
 * @version 2017-10-18 9:45
 */
@Repository
public class MonitorHrefUseableClientImpl implements IMonitorHrefUseableClient {

    private enum Codes{
        Monitor_saveHrefUseable,
        Monitor_saveHrefUseableDynamic
    }

    @Override
    public WebServiceTO saveHrefUseable(HrefUseableVO hrefUseable) {
        List<HrefUseableVO> hrefUseableArray = new ArrayList<HrefUseableVO>();
        hrefUseableArray.add(hrefUseable);
        return saveHrefUseable(hrefUseableArray);
    }

    @Override
    public WebServiceTO saveHrefUseable(List<HrefUseableVO> hrefUseableArray) {
        JSONArray array = JSONArray.fromObject(hrefUseableArray);
        return WebServiceCaller.getWebServiceTO(MonitorHrefUseableClientImpl.Codes.Monitor_saveHrefUseable.toString(),
                new Object[]{array.toString()});
    }

    @Override
    public WebServiceTO saveHrefUseableDynamic(HrefUseableDynamicVO hrefUseable) {
        List<HrefUseableDynamicVO> hrefUseableArray = new ArrayList<HrefUseableDynamicVO>();
        hrefUseableArray.add(hrefUseable);
        return saveHrefUseableDynamic(hrefUseableArray);
    }

    @Override
    public WebServiceTO saveHrefUseableDynamic(List<HrefUseableDynamicVO> hrefUseableArray) {
        JSONArray array = JSONArray.fromObject(hrefUseableArray);
        return WebServiceCaller.getWebServiceTO(MonitorHrefUseableClientImpl.Codes.Monitor_saveHrefUseableDynamic.toString(),
                new Object[]{array.toString()});
    }
}
