package cn.lonsun.webservice.monitor.client;

import cn.lonsun.webservice.monitor.client.vo.HrefUseableDynamicVO;
import cn.lonsun.webservice.monitor.client.vo.HrefUseableVO;
import cn.lonsun.webservice.to.WebServiceTO;

import java.util.List;

/**
 * @author gu.fei
 * @version 2017-10-18 9:41
 */
public interface IMonitorHrefUseableClient {

    /**
     * 保存错链检测信息
     * @param hrefUseable
     */
    WebServiceTO saveHrefUseable(HrefUseableVO hrefUseable);

    /**
     * 保存错链检测信息
     * @param hrefUseableArray
     */
    WebServiceTO saveHrefUseable(List<HrefUseableVO> hrefUseableArray);

    /**
     * 保存错链检测信息
     * @param hrefUseable
     */
    WebServiceTO saveHrefUseableDynamic(HrefUseableDynamicVO hrefUseable);

    /**
     * 保存错链检测信息
     * @param hrefUseableArray
     */
    WebServiceTO saveHrefUseableDynamic(List<HrefUseableDynamicVO> hrefUseableArray);
}
