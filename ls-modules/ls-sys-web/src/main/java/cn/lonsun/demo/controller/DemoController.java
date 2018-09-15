/*
 * DemoController.java         2018年8月1日 <br/>
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
package cn.lonsun.demo.controller;

import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.demo.entity.DemoEO;
import cn.lonsun.demo.service.IDemoService;
import cn.lonsun.demo.vo.DemoQueryVO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * 演示 <br/>
 *
 * @author wangshibao <br/>
 * @version v1.0 <br/>
 * @date 2018年8月1日 <br/>
 */
@Controller
@RequestMapping("/demo")
public class DemoController extends BaseController {

    @Resource
    private IDemoService demoService;


    /**
     * 跳转新页面
     * @return
     */
    @RequestMapping("/index")
    public String getDemoPage(){
        return "/demo/index";
    }

    /**
     * 跳转新页面
     * @return
     */
    @RequestMapping("/model")
    public String getModelPage(Model model){
        model.addAttribute("name","model");
        return "/demo/index";
    }

    /**
     * 跳转新页面
     * @return
     */
    @RequestMapping("/modelMap")
    public String getModelMapPage(ModelMap model){
        model.addAttribute("name","modelMap");
        return "/demo/index";
    }

    /**
     * 跳转新页面
     * @return
     */
    @RequestMapping("/modelandview")
    public ModelAndView getModelViewPage(ModelAndView model){
        model.addObject("name","modelandview");
        model.setViewName("/demo/index");
        return model;
    }

    /**
     * 跳转新页面
     * @return
     */
    @RequestMapping("/ajax")
    @ResponseBody
    public Object getAjaxPage(String code){
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("name","this is a ajax result");
        logger.info("参数为 ====" + code);
        return map;
    }

    /**
     * 分页
     * @return
     */
    @RequestMapping("/pageList")
    @ResponseBody
    public Object getPageDemoInfos(DemoQueryVO demoQueryVO){
        demoQueryVO.setPageIndex(0L);
        demoQueryVO.setPageSize(10);
        return demoService.getDemoPage(demoQueryVO);
    }

    /**
     * 列表
     * @return
     */
    @RequestMapping("/list")
    @ResponseBody
    public Object getDemoInfos(String code,String name){
        return demoService.getDemoListByCodeAndName(code,name);
    }

    @RequestMapping("/save")
    @ResponseBody
    public Object addDemoInfo(DemoEO eo){
        int random = new Random().nextInt(100);
        eo.setCode("" + random);
        eo.setName("test" + random);
        return demoService.saveDemoInfo(eo);
    }

    @RequestMapping("/update")
    @ResponseBody
    public Object updateDemoInfo(DemoEO eo){
        eo.setId(7069792L);
        int rand = new Random().nextInt(100);
        eo.setCode("" + rand);
        eo.setName("test" + rand);
        demoService.updateDemoInfo(eo);
        return "ok";
    }

    @RequestMapping("/del")
    @ResponseBody
    public Object deleteDemoInfo(Long id){
        demoService.deleteDemoInfo(id);
        return "ok";
    }

    @RequestMapping("/getInfoById")
    @ResponseBody
    public Object getDemoInfoById(Long id){
        return demoService.getDemoInfoById(id);
    }
}
