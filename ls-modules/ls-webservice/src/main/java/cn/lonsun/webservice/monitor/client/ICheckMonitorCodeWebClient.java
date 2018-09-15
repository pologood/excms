package cn.lonsun.webservice.monitor.client;

import cn.lonsun.webservice.processEngine.vo.ProcessVO;
import cn.lonsun.webservice.to.WebServiceTO;

import java.util.List;

/**
 * 检测站群标识码是否存在
 * @author liuk
 * @version 2017年9月22日
 */
public interface ICheckMonitorCodeWebClient {

    /**
     * 检测站群标识码是否存在
     * @param code
     * @return
     */
    Object checkMonitorCode(String code);
}
