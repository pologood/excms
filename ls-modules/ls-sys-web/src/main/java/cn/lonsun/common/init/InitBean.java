/*
 * InitBean.java         2016年8月24日 <br/>
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

package cn.lonsun.common.init;

import java.util.Date;

import javax.annotation.PostConstruct;

import org.apache.commons.beanutils.ConvertUtils;
import org.springframework.stereotype.Component;

import cn.lonsun.util.DateConverter;

/**
 * 初始化一些数据 <br/>
 *
 * @date 2016年8月24日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
@Component
public class InitBean {

    @PostConstruct
    public void init() {
        // 注册日期转换类
        DateConverter dateConverter = new DateConverter();
        dateConverter.setUseLocaleFormat(true);
        ConvertUtils.register(dateConverter, Date.class);
    }
}