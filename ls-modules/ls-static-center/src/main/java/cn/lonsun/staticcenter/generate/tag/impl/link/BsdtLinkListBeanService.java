/*
 * LinkListBeanService.java         2015年11月25日 <br/>
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

package cn.lonsun.staticcenter.generate.tag.impl.link;

import cn.lonsun.staticcenter.exproject.lixin.AesCBCDecrypt;
import cn.lonsun.staticcenter.exproject.lixin.IPortalServicePortType;
import cn.lonsun.staticcenter.generate.BsdtLinkListVO;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * 亳州网上办事大厅接口调用链接管理 <br/>
 *
 * @author liuk <br/>
 * @version v1.0 <br/>
 * @date 2016年9月2日 <br/>
 */
@Component
public class BsdtLinkListBeanService extends AbstractBeanService {

    @Resource
    private LinkListJsBeanService beanService;

    @Override
    public Object getObject(JSONObject paramObj) {
        try {
            // 获取栏目配置信息
            Context context = ContextHolder.getContext();// 获取栏目id
            Integer mainTab = paramObj.getInteger("mainTab");
            Integer subTab = paramObj.getInteger("subTab");
            Integer length = paramObj.getInteger("length");

            //Object[] objects = WebServiceUtil.callNormalService("http://60.174.83.212/app-net-ws/services/IPortalService?wsdl","http://services.ws.iflytek.com","getGyxSearchTemplate",new Object[]{},String.class);
            //调用webservice获取数据START
            URL url= null;
            url = new URL("http://60.174.83.212/app-net-ws/services/IPortalService?wsdl");
            QName qName = new QName("http://services.ws.iflytek.com", "IPortalService");
            Service service  = Service.create(url,qName);
            IPortalServicePortType portalServicePortType = service.getPort(IPortalServicePortType.class);
            String result =   portalServicePortType.getLxxSearchTemplate();
//            System.out.println(result);
            result = AesCBCDecrypt.decrypt(result, AesCBCDecrypt.key);
            result = "{\"data\" : "+result+"}";
//            System.out.println(result);
            //调用webservice获取数据END
            List<BsdtLinkListVO> bsdtLinkVOList = new ArrayList<BsdtLinkListVO>();

            if(mainTab==null||mainTab==0){//个人办事
                bsdtLinkVOList = AesCBCDecrypt.getList(result,"personal",length);
            }else if(mainTab==1){//企业办事
                bsdtLinkVOList = AesCBCDecrypt.getList(result,"enterprise",length);
            }else if(mainTab==2){//部门服务
                bsdtLinkVOList = AesCBCDecrypt.getList(result,"bmfw",length);
            }

            return bsdtLinkVOList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}