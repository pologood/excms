/*
 * GuestBookCenterController.java         2016年7月4日 <br/>
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

package cn.lonsun.content.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.lonsun.core.base.controller.BaseController;

/**
 * 留言统一处理中心 <br/>
 *
 * @date 2016年7月4日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
@Controller
@RequestMapping(value = "/guestBook/center")
public class GuestBookCenterController extends BaseController {
    // 进入页面
    @RequestMapping(value = "index")
    public String guestBookList(HttpServletRequest request) {
        return "/content/guestBook/center/index";
    }
}