/*
 * ArticleNewsFormDataService.java         2016年8月1日 <br/>
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

package cn.lonsun.form.impl;

import cn.lonsun.base.DealStatus;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.entity.VideoNewsEO;
import cn.lonsun.content.internal.service.IBaseContentService;
import cn.lonsun.content.internal.service.IVideoNewsService;
import cn.lonsun.content.vo.VideoNewsVO;
import cn.lonsun.form.FormReturnVO;
import cn.lonsun.mongodb.service.ContentMongoServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Map;

/**
 * 视频新闻 <br/>
 *
 * @date 2016年8月1日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
@Service("videoNewsFormDataService")
public class VideoNewsFormDataService extends AbstractFormDataService {

    @Resource
    private IBaseContentService baseContentService;
    @Resource
    private ContentMongoServiceImpl contentMongoService;
    @Resource
    private IVideoNewsService videoNewsService;

    @Override
    public FormReturnVO saveFormData(Map<String, Object> paramMap,DealStatus dealStatus) {
        VideoNewsVO videoVO = new VideoNewsVO();
        AppUtil.setFieldsValue(videoVO, paramMap, null, new String[]{"yyyy-MM-dd HH:mm:ss","yyyy-MM-dd"});

        videoVO.setId(super.getMapValue(Long.class, "id", paramMap));
        videoVO.setArticle(super.getMapValue(String.class, "content", paramMap));
        videoVO.setTitle(super.getMapValue(String.class, "title", paramMap));
        videoVO.setTitleColor(super.getMapValue(String.class, "titleColor", paramMap));
        videoVO.setIsBold(super.getMapValue(Integer.class, "isBold", paramMap));
        videoVO.setIsTilt(super.getMapValue(Integer.class, "isTilt", paramMap));
        videoVO.setIsUnderline(super.getMapValue(Integer.class, "isUnderline", paramMap));
        videoVO.setAuthor(super.getMapValue(String.class, "author", paramMap));
        videoVO.setImageLink(super.getMapValue(String.class, "imageLink", paramMap));
        videoVO.setResources(super.getMapValue(String.class, "resources", paramMap));
        videoVO.setRemarks(super.getMapValue(String.class, "remarks", paramMap));

        BaseContentEO contentEO = baseContentService.getEntity(BaseContentEO.class,super.getMapValue(Long.class, "id", paramMap));
        videoVO.setPublishDate(contentEO.getPublishDate());
        videoVO.setIsTop(contentEO.getIsTop());
        videoVO.setIsNew(contentEO.getIsNew());
        videoVO.setIsPublish(contentEO.getIsPublish());
        videoVO.setIsJob(contentEO.getIsJob());
        videoVO.setJobIssueDate(contentEO.getJobIssueDate());
        videoVO.setColumnId(contentEO.getColumnId());
        videoVO.setSiteId(contentEO.getSiteId());


        // 保存文章
        VideoNewsEO eo = videoNewsService.saveVideo(videoVO);
        return super.buildReturnVO(videoVO.getId(), videoVO.getTitle());
    }

    @Override
    public FormReturnVO updateFormData(Long dataId, Map<String, Object> paramMap,DealStatus dealStatus) {
        paramMap.put("id", dataId);// 放入主键
        return this.saveFormData(paramMap,dealStatus);
    }

    @Override
    public FormReturnVO getFormData(Long dataId) {
        BaseContentEO baseContentEO = baseContentService.getEntity(BaseContentEO.class, dataId);
        if(null != baseContentEO){
            Map<String, Object> map = super.beanToMap(baseContentEO);
            return super.buildReturnVO(dataId, baseContentEO.getTitle(),map);
        }
        return null;
    }

    @Override
    public Boolean updateDealStatus(Long dataId, DealStatus dealStatus) {
        return null;
    }
}