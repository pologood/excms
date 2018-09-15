/*
 * GenerateUtil.java         2016年1月15日 <br/>
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

package cn.lonsun.staticcenter.util;

import cn.lonsun.site.template.internal.entity.TemplateConfEO;
import cn.lonsun.staticcenter.generate.GenerateException;
import cn.lonsun.staticcenter.generate.GenerateRecord;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import cn.lonsun.staticcenter.generate.util.AssertUtil;
import cn.lonsun.staticcenter.generate.util.ContextUtil;
import cn.lonsun.staticcenter.generate.util.MongoUtil;
import cn.lonsun.staticcenter.generate.util.RegexUtil;
import cn.lonsun.staticcenter.generate.util.VelocityUtil;
import cn.lonsun.util.HibernateHandler;
import cn.lonsun.util.HibernateSessionUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 生成工具类 <br/>
 *
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 * @date 2016年1月15日 <br/>
 */
public class GenerateUtil {

    /**
     * 生成
     *
     * @param context
     * @return
     * @author fangtinghua
     */
    public static String generate(final Context context) {
        // 执行结果
        return HibernateSessionUtil.execute(new HibernateHandler<String>() {

            /**
             * 只有当动态请求过来时，才会没有生成全局变量以及站点相关的信息设置
             *
             * @see cn.lonsun.util.HibernateHandler#execute()
             */
            @Override
            public String execute() {
                TemplateConfEO templateConfEO = context.getTemplateConfEO();
                AssertUtil.isEmpty(templateConfEO, "模板文件不存在.");// 判断模板文件
                // 是否动态请求
                if (StringUtils.isNotEmpty(context.getModule())) {
                    // 设置生成全局变量
                    context.setGenerateRecord(new GenerateRecord());
                    // 设置站点信息
                    ContextUtil.setContextUri();
                }
                // 设置上下文
                ContextHolder.setContext(context);
                // 获取模板文件内容
                String content = MongoUtil.queryCacheById(templateConfEO.getId());
                // 解析模板文件
                String result = RegexUtil.parseContent(content);
                // 配合专题设计，替换成正常的JS标签
                result = result.replaceAll(" type=\"text/js\"", "")
                        //去掉设计器相关JS
                        .replaceAll("<script[^>]*?data-js=\"remove\"[^>]*>[\\s\\S]*?</script>", "")
                        //去掉设计器相关的link链接
                        .replaceAll("<link[^>]*data-js=\"remove\"[^>]*>", "")
                        .replaceAll("data-(conf|label)=\"[\\s\\S]*?\"", "");

                // 最后经过velocity处理一下
                Map<String, Object> paramMap = new HashMap<String, Object>(context.getParamMap());
                paramMap.put("global_uri", context.getUri());
                paramMap.put("global_module", context.getModule());
                paramMap.put("global_siteId", context.getSiteId());
                paramMap.put("global_columnId", context.getColumnId());
                paramMap.put("global_contentId", context.getContentId());
                paramMap.put("global_from", context.getFrom());
                paramMap.put("global_userData", context.getUserData());
                return VelocityUtil.mergeString(result, paramMap);
            }

            /**
             * 当为静态生成时，需要把异常抛出，动态访问时，直接返回错误消息
             *
             * @see cn.lonsun.util.HibernateHandler#complete(java.lang.Object,
             *      java.lang.Throwable)
             */
            @Override
            public String complete(String result, Throwable exception) {
                if (null == exception) {
                    return result;
                }
                if (exception instanceof GenerateException) {
                    throw (GenerateException) exception;
                }
                throw new GenerateException("模板解析错误！", exception);
            }
        });
    }
}