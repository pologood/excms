package cn.lonsun.webservice.monitor.client;

import cn.lonsun.webservice.monitor.client.vo.ColumnDetailVO;
import cn.lonsun.webservice.to.WebServiceTO;

import java.util.List;

/**
 * @author liuk
 * @version 2017-11-08 15:45
 */
public interface IMonitorColumnDetailWebClient {

    /**
     * 推送栏目更新信息
     * @param columnArray
     * @return
     */
    WebServiceTO saveColumnDetails(List<ColumnDetailVO> columnArray);
}
