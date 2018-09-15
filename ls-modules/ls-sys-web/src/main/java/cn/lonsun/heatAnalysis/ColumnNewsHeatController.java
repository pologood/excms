/*
 * ColumnNewsHeatController.java         2016年4月5日 <br/>
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

package cn.lonsun.heatAnalysis;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.vo.ContentPageVO;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.heatAnalysis.service.IColumnNewsHeatService;
import cn.lonsun.util.LoginPersonUtil;

/**
 * 栏目热度和文章热度分析 <br/>
 *
 * @date 2016年4月5日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
@Controller
@RequestMapping("/heatAnalysis")
public class ColumnNewsHeatController extends BaseController {

    @Resource
    private IColumnNewsHeatService columnNewsHeatService;

    /**
     * 首页
     *
     * @author fangtinghua
     * @return
     */
    @RequestMapping("/{module}/index")
    public String index(@PathVariable("module") String module) {
        return "/heatAnalysis/" + module + "/index";
    }

    /**
     * 获取栏目热度列表
     *
     * @author fangtinghua
     * @param contentPageVO
     * @return
     */
    @ResponseBody
    @RequestMapping("/column/getColumnHeatPage")
    public Object getColumnHeatPage(ContentPageVO contentPageVO) {
        contentPageVO.setSiteId(LoginPersonUtil.getSiteId());
        String[] typeCodes =
                { BaseContentEO.TypeCode.articleNews.toString(), BaseContentEO.TypeCode.pictureNews.toString(), BaseContentEO.TypeCode.videoNews.toString() };
        contentPageVO.setTypeCodes(typeCodes);
        return columnNewsHeatService.getColumnHeatPage(contentPageVO);
    }

    /**
     * 获取文章热度列表
     *
     * @author fangtinghua
     * @param contentPageVO
     * @return
     */
    @ResponseBody
    @RequestMapping("/news/getNewsHeatPage")
    public Object getNewsHeatPage(ContentPageVO contentPageVO) {
        contentPageVO.setSiteId(LoginPersonUtil.getSiteId());
        String[] typeCodes =
                { BaseContentEO.TypeCode.articleNews.toString(), BaseContentEO.TypeCode.pictureNews.toString(), BaseContentEO.TypeCode.videoNews.toString() };
        contentPageVO.setTypeCodes(typeCodes);
        return columnNewsHeatService.getNewsHeatPage(contentPageVO);
    }
}