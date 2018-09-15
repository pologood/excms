/*
 * ContentBeanServiceImpl.java         2015年9月8日 <br/>
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

package cn.lonsun.staticcenter.generate.tag.impl.article;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.service.IBaseContentService;
import cn.lonsun.staticcenter.generate.GenerateException;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import cn.lonsun.staticcenter.generate.util.MongoUtil;
import cn.lonsun.staticcenter.generate.util.RegexUtil;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 获取新闻内容 <br/>
 *
 * @date 2016年9月12日 <br/>
 * @author liuk <br/>
 * @version v1.0 <br/>
 */
@Component
public class ArticleNewsContentBeanService extends AbstractBeanService {

    @Autowired
    private IBaseContentService baseContentService;

    @Override
    public Object getObject(JSONObject paramObj) {
        Context context = ContextHolder.getContext();
        Long contentId = context.getContentId();// 根据文章id查询文章
        if(AppUtil.isEmpty(contentId)){
            contentId = paramObj.getLong("contentId");
        }
        BaseContentEO eo = baseContentService.getEntity(BaseContentEO.class,contentId);
        // 根据id去mongodb读取文件内容
        eo.setArticle(MongoUtil.queryById(eo.getId()));
        return eo;
    }

}