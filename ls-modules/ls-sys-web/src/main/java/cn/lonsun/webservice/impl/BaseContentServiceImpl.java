/*
 * BaseContentServiceImpl.java         2017年1月5日 <br/>
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

package cn.lonsun.webservice.impl;

import cn.lonsun.activemq.MessageSenderUtil;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.service.IBaseContentService;
import cn.lonsun.content.internal.service.IContentPicService;
import cn.lonsun.message.internal.entity.MessageEnum;
import cn.lonsun.message.internal.entity.MessageStaticEO;
import cn.lonsun.site.site.internal.entity.ColumnMgrEO;
import cn.lonsun.webservice.BaseContentService;
import cn.lonsun.webservice.to.WebServiceTO;
import cn.lonsun.webservice.to.WebServiceTO.ErrorCode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by liuk on 2017年1月5日.
 */
@Service("BaseContentServiceImpl_webservice")
public class BaseContentServiceImpl implements BaseContentService {
    @Autowired
    private IContentPicService contentPicService;

    @Autowired
    private IBaseContentService baseContentService;

    @Override
    public WebServiceTO saveBaseContent(BaseContentEO baseContentEO) {
        WebServiceTO to = new WebServiceTO();
        // 检查必填项
        if (null == baseContentEO) {
            return to.error(ErrorCode.ArgumentsError, "数据不能为空");
        }
        Long siteId = baseContentEO.getSiteId();
        if (null == siteId) {
            return to.error(ErrorCode.ArgumentsError, "siteId不能为空");
        }
        Long columnId = baseContentEO.getColumnId();
        if (null == columnId) {
            return to.error(ErrorCode.ArgumentsError, "columnId不能为空");
        }
        String title = baseContentEO.getTitle();
        if (StringUtils.isEmpty(title)) {
            return to.error(ErrorCode.ArgumentsError, "title不能为空");
        }
        String article = baseContentEO.getArticle();
        if (StringUtils.isEmpty(article)) {
            return to.error(ErrorCode.ArgumentsError, "article不能为空");
        }
        ColumnMgrEO columnMgrEO = CacheHandler.getEntity(ColumnMgrEO.class, columnId);
        baseContentEO.setTypeCode(columnMgrEO.getColumnTypeCode());

        if (null == baseContentEO.getPublishDate()) {// 默认当前日期
            baseContentEO.setPublishDate(new Date());
        }
        Integer isPublish = baseContentEO.getIsPublish();
        if(null == isPublish){//默认未发布
            isPublish = 0;
        }
        baseContentEO.setEditor("推送");
        baseContentEO.setCreateUserId(1L);//写死为root发布

        try {
            if(BaseContentEO.TypeCode.pictureNews.toString().equals(baseContentEO.getTypeCode())){//图片新闻
                contentPicService.savePicNews(baseContentEO,article,null,null);
            }else if(BaseContentEO.TypeCode.articleNews.toString().equals(baseContentEO.getTypeCode())){
                baseContentService.saveArticleNews(baseContentEO, article, null, null, null, null);
            }

            // 发布
            if (isPublish == 1) {
                MessageStaticEO messageStaticEO = new MessageStaticEO();
                messageStaticEO.setSiteId(siteId);
                messageStaticEO.setColumnId(columnId);
                messageStaticEO.setContentIds(new Long[]{baseContentEO.getId()});
                messageStaticEO.setSource(MessageEnum.CONTENTINFO.value());
                messageStaticEO.setType(MessageEnum.PUBLISH.value());
                messageStaticEO.setScope(MessageEnum.CONTENT.value());
                messageStaticEO.setUserId(1L);//写死为root发布
                MessageSenderUtil.publishContent(messageStaticEO, 1);
            }
            return to.success(String.valueOf(baseContentEO.getId()), "添加成功！");
        } catch (Exception e) {
            e.printStackTrace();
            return to.error(ErrorCode.SystemError, "系统异常，请稍后重试！");
        }

    }
}