/*
 * DynamicController.java         2016年1月13日 <br/>
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

import cn.lonsun.core.util.CheckMobile;
import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.message.internal.entity.MessageEnum;
import cn.lonsun.staticcenter.generate.GenerateException;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import cn.lonsun.staticcenter.service.DynamicService;
import cn.lonsun.staticcenter.service.HtmlEnum;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 动态请求，排除资源文件目录 <br/>
 *
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 * @date 2016年1月13日 <br/>
 */
@Controller
@RequestMapping(value = "/wap")
public class WapDynamicController {

    private String page = "pageIndex";// 分页参数

    /**
     * 获取html内容
     *
     * @param module
     * @param action
     * @param id
     * @param request
     * @return
     * @throws GenerateException
     * @author fangtinghua
     */
    @ResponseBody
    @RequestMapping(value = "/{module:[\\w]+}/{action:[\\w]+}/{id:[\\d\\.]+}", produces = "text/html;charset=UTF-8")
    public Object getHtml(@PathVariable("module") String module, @PathVariable("action") String action, @PathVariable("id") Long id, HttpServletRequest request)
        throws GenerateException {
        if (!HtmlEnum.exitModule(module)) {
            throw new GenerateException("模块编码:\"" + module + "\",不存在.");
        }

        //获取UserAgent
        String UserAgent = request.getHeader("USER-AGENT");

        //检查访问方式是否为移动端
        Boolean isFromMobile = CheckMobile.check(UserAgent);

        // 获取service
        DynamicService service = SpringContextHolder.getBean(module + "DynamicService");

        // 参数路径
        int index = 0;
        Context context = new Context();
        StringBuffer path = new StringBuffer();
        // 获取参数，获取第一个参数即可
        Map<String, String> paramMap = new HashMap<String, String>();
        Map<String, String[]> map = request.getParameterMap();
        for (Entry<String, String[]> entry : map.entrySet()) {
            paramMap.put(entry.getKey(), entry.getValue()[0]);
            // 去除分页参数
            if (!page.equals(entry.getKey())) {
                //if ("siteId".equals(entry.getKey())) {// 站点id
                //    context.setSiteId(NumberUtils.toLong(entry.getValue()[0]));
                //}
                if (index++ > 0) {
                    path.append("&");
                }
                path.append(entry.getKey()).append("=").append(entry.getValue()[0]);
            }
        }

        // 放入分页参数
        if (paramMap.containsKey(page)) {
            String pageIndex = paramMap.get(page);
            if (NumberUtils.isNumber(pageIndex)) {
                context.setPageIndex(Long.valueOf(pageIndex));
            }
        }

        // 设置请求路径
        context.setPath(request.getRequestURI());
        if (!StringUtils.isEmpty(path.toString())) {
            context.setParam(path.toString());
            context.setPath(context.getPath() + "?" + path.toString());
        }

         //设置是否WAP访问
        context.setFrom(isFromMobile ? Context.From.WAP.toString() : Context.From.PC.toString());

        // 跟线程绑定
        ContextHolder.setContext(context.setSource(MessageEnum.CONTENTINFO.value()));// 默认内容管理
        return service.queryHtml(action, id, context.setModule(module).setAction(action).setParamMap(paramMap));
    }
}