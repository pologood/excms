package cn.lonsun.webservice.monitor.client;

import cn.lonsun.webservice.monitor.client.vo.InteractDetailVO;
import cn.lonsun.webservice.to.WebServiceTO;

import java.util.List;

/**
 * @author liuk
 * @version 2017-11-08 15:45
 */
public interface IMonitorInteractDetailWebClient {

    /**
     * 推送互动回应更新信息
     * @param interactArray
     * @return
     */
    WebServiceTO saveInteractDetails(List<InteractDetailVO> interactArray);
}
