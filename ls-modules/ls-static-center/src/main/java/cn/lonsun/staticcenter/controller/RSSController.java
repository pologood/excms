/*
 * RSSController.java         2016年8月3日 <br/>
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

package cn.lonsun.staticcenter.controller;

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.service.IBaseContentService;
import cn.lonsun.content.vo.ContentPageVO;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;

/**
 * rss订阅 <br/>
 *
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 * @date 2016年8月3日 <br/>
 */
@Controller
@RequestMapping(value = "/rss")
public class RSSController extends BaseController {

    @Resource
    private IBaseContentService baseContentService;

    @RequestMapping(value = "rss.xml")
    public String xml(Long siteId, ModelMap map, HttpServletResponse response) {
        if (null == siteId) {
            throw new BaseRunTimeException("站点id不能为空！");
        }
        IndicatorEO siteEO = CacheHandler.getEntity(IndicatorEO.class, siteId);
        if (null == siteEO) {
            throw new BaseRunTimeException("站点不存在！");
        }
        ContentPageVO pageVO = new ContentPageVO();
        pageVO.setSiteId(siteId);
        pageVO.setTypeCode(BaseContentEO.TypeCode.articleNews.toString());
        Pagination pagination = baseContentService.getPageAndContent(pageVO);
        List<BaseContentEO> list = (List<BaseContentEO>) pagination.getData();
        for (BaseContentEO eo : list) {
            eo.setArticle(StringEscapeUtils.unescapeHtml4(eo.getArticle()));
        }
        map.put("now", DateFormatUtils.format(new Date(), "yyyy-MM-dd hh:mm:ss"));
        map.put("siteEO", siteEO);
        map.put("pagination", pagination);
        response.setContentType("application/xml;charset=utf-8");
        return "rss/rss";
    }
}