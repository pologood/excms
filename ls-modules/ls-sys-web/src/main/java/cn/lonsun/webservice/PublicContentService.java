/*
 * PublicContentService.java         2016年10月21日 <br/>
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

import cn.lonsun.publicInfo.vo.PublicContentVO;
import cn.lonsun.webservice.to.WebServiceTO;

/**
 * TODO <br/>
 * 
 * @date 2016年10月21日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
public interface PublicContentService {

    /**
     * 添加信息公开内容
     * 
     * @author fangtinghua
     * @param contentVO
     * @return
     */
    WebServiceTO savePublicContent(PublicContentVO contentVO);
}