/*
 * SiteColumnService.java         2017年01月05日 <br/>
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

package cn.lonsun.webservice;

import cn.lonsun.webservice.to.WebServiceTO;

/**
 * TODO <br/>
 * 
 * @date 2017年01月05日 <br/>
 * @author liuk <br/>
 * @version v1.0 <br/>
 */
public interface SiteColumnService {

    /**
     * 获取站点栏目列表
     * 
     * @author liuk
     * @param siteId
     * @return
     */
    WebServiceTO getSiteColumnList(Long siteId);
}