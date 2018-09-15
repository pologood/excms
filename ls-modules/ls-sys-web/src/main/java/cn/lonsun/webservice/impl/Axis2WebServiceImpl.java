/*
 * Axis2WebServiceImpl.java         2016年9月18日 <br/>
 *
 * Copyright (c) 1994-1999 AnHui LonSun, Inc. <br/>
 * All rights reserved.	<br/>
 *
 * This software is the confidential and proprietary information of AnHui	<br/>
 * LonSun, Inc. ("Confidential Information").  You shall not	<br/>
 * disclose such Confidential Information and shall use it only in	<br/>
 * accordance with the terms of the license agreement you entered into	<br/>
 * with Sun. <br/>
 */

package cn.lonsun.webservice.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;

import cn.lonsun.webservice.Axis2WebService;
import cn.lonsun.webservice.internal.entity.WebServiceEO;
import cn.lonsun.webservice.internal.service.IWebserviceService;
import cn.lonsun.webservice.to.WebServiceTO;
import cn.lonsun.webservice.utils.WebServiceTOUtil;

/**
 * webservice初始化 <br/>
 * 
 * @date 2016年9月18日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
@Service("axis2WebService")
public class Axis2WebServiceImpl implements Axis2WebService {

    @Resource(name = "webserviceService")
    private IWebserviceService webserviceService;

    @Override
    public WebServiceTO getServices(String[] systemCodes) {
        WebServiceTO to = new WebServiceTO();
        try {
            List<WebServiceEO> webServiceList = webserviceService.getServicesBySystemCode(systemCodes);
            to.setJsonData(JSON.toJSONString(webServiceList));
        } catch (Exception e) {
            WebServiceTOUtil.setErrorInfo(to, e);
        }
        return to;
    }
}