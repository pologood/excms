/*
 * MessageController.java         2016年1月4日 <br/>
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

package cn.lonsun.message.controller;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.message.internal.entity.MessageReceiveEO;
import cn.lonsun.message.internal.service.IMessageReceiveService;
import cn.lonsun.message.vo.MessageQueryVO;
import cn.lonsun.util.LoginPersonUtil;

/**
 * TODO <br/>
 *
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 * @date 2016年1月4日 <br/>
 */
@Controller
@RequestMapping("/message")
public class MessageController extends BaseController {

    @Resource
    private IMessageReceiveService messageReceiveService;

    /**
     * 未读消息列表
     *
     * @return
     * @author fangtinghua
     */
    @RequestMapping("index")
    public String index(Model model) {
        return "message/index";
    }

    /**
     * 获取当前用户未读消息列表
     *
     * @return
     * @author fangtinghua
     */
    @ResponseBody
    @RequestMapping("getPage")
    public Object getPage(MessageQueryVO vo) {
        Long userId = LoginPersonUtil.getUserId();
        vo.setUserId(userId);
        // vo.setMessageStatus(MessageReceiveEO.NO_READ);
        return messageReceiveService.getPagination(vo);
    }

    /**
     * 更新消息状态 已读
     *
     * @return
     * @author fangtinghua
     */
    @ResponseBody
    @RequestMapping("readMessage")
    public Object readMessage(Long[] ids) {
        messageReceiveService.updateMessageStatus(ids, MessageReceiveEO.READ);
        return getObject();
    }

    /**
     * 更新全部消息状态 已读
     *
     * @return
     * @author fangtinghua
     */
    @ResponseBody
    @RequestMapping("readAllMessage")
    public Object readAllMessage() {
        Long userId = LoginPersonUtil.getUserId();
        messageReceiveService.updateMessageStatus(userId, MessageReceiveEO.READ);
        return getObject();
    }

    /**
     * 更新消息状态 已办
     *
     * @return
     * @author fangtinghua
     */
    @ResponseBody
    @RequestMapping("handleMessage")
    public Object handleMessage(Long[] ids) {
        messageReceiveService.updateMessageStatus(ids, MessageReceiveEO.HAS_HANDLE);
        return getObject();
    }

}