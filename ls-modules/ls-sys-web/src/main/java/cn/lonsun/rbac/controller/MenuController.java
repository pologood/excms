/*
 * MenuController.java         2015年8月25日 <br/>
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

package cn.lonsun.rbac.controller;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.lonsun.common.vo.PageQueryVO;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.rbac.indicator.service.IIndicatorService;

/**
 * TODO <br/>
 *
 * @date 2015年8月25日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
@Controller
@RequestMapping("/system/menu")
public class MenuController extends BaseController {

    @Resource(name = "ex_8_IndicatorServiceImpl")
    private IIndicatorService indicatorService;

    /**
     * 获取菜单
     *
     * @author
     * @return
     */
    @ResponseBody
    @RequestMapping("getMenu")
    public Object getMenu(@RequestParam(value = "all", defaultValue = "2") String all, Long roleId) throws Exception {
        // 默认为2是表示查询要过滤的权限
        return this.indicatorService.getMenu(!"2".equals(all), roleId);
    }

    /**
     * 获取按钮
     *
     * @author
     * @return
     */
    @ResponseBody
    @RequestMapping("getButton")
    public Object getButton(PageQueryVO vo, Long indicatorId) throws Exception {
        // 默认为2是表示查询要过滤的权限
        return this.indicatorService.getButton(vo, indicatorId);
    }

    /**
     * 
     * 转向菜单管理主页
     *
     * @author fangtinghua
     * @return
     */
    @RequestMapping("index")
    public String index() {
        return "system/menu/index";
    }

    /**
     * 
     * 转向菜单编辑主页
     *
     * @author fangtinghua
     * @return
     */
    @RequestMapping("edit")
    public String edit(Long type, ModelMap map) {
        map.put("type", type);
        return "system/menu/edit";
    }
}