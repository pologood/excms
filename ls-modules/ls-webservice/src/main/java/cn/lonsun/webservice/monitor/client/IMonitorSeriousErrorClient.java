package cn.lonsun.webservice.monitor.client;

import cn.lonsun.webservice.monitor.client.vo.SeriousErrorVO;
import cn.lonsun.webservice.to.WebServiceTO;

import java.util.List;

/**
 * @author gu.fei
 * @version 2017-10-18 9:41
 */
public interface IMonitorSeriousErrorClient {

    /**
     * 保存严重错误信息
     * @param seriousErrorArray
     */
    WebServiceTO saveSeriousError(List<SeriousErrorVO> seriousErrorArray);
}
