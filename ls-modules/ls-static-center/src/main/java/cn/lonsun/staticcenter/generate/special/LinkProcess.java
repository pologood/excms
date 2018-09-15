/*
 * LinkProcess.java         2015年9月11日 <br/>
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

package cn.lonsun.staticcenter.generate.special;

import org.springframework.stereotype.Component;

import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import cn.lonsun.staticcenter.generate.util.PathUtil;
import cn.lonsun.util.ReflectionUtils;

/**
 * 获取连接地址 <br/>
 *
 * @date 2015年9月11日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
@Component
public class LinkProcess implements Process {

    @Override
    public String getValue(Object obj) {
        Context context = ContextHolder.getContext();
        // 这里的link取的都是文章的访问路径
        Object contentId = ReflectionUtils.getValue(obj, "id");
        return PathUtil.getLinkPath(context.getColumnId(), (Long) contentId);
    }
}