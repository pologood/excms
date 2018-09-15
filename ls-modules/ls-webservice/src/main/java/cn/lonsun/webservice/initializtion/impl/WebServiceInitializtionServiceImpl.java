package cn.lonsun.webservice.initializtion.impl;

import org.springframework.stereotype.Service;

import cn.lonsun.webservice.core.WebServiceCaller;
import cn.lonsun.webservice.initializtion.IWebServiceInitializtionService;

/**
 * WebService初始化接口实现
 *
 * @author xujh
 * @version 1.0 2015年4月9日
 *
 */
@Service("indirectWebServiceInitializtionService")
public class WebServiceInitializtionServiceImpl implements IWebServiceInitializtionService {

    /**
     * WebService编码
     * 
     * @author xujh
     * @date 2014年11月5日 下午4:19:39
     * @version V1.0
     */
    private enum Codes {
        WebService_init
    }

    @Override
    public Boolean init() {
        String code = Codes.WebService_init.toString();
        return (Boolean) WebServiceCaller.getSimpleObject(code, new Object[] {}, Boolean.class);
    }
}