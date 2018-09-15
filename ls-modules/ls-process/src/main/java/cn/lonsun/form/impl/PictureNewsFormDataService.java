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
import cn.lonsun.content.internal.service.IBaseContentService;
import cn.lonsun.content.internal.service.IContentPicService;
import cn.lonsun.content.vo.SynColumnVO;
import cn.lonsun.form.FormReturnVO;
import cn.lonsun.mongodb.service.ContentMongoServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 文章新闻 <br/>
 *
 * @date 2016年8月1日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
@Service("pictureNewsFormDataService")
public class PictureNewsFormDataService extends AbstractFormDataService {

    @Resource
    private IBaseContentService baseContentService;
    @Resource
    private IContentPicService contentPicService;
    @Resource
    private ContentMongoServiceImpl contentMongoService;

    @Override
    public FormReturnVO saveFormData(Map<String, Object> paramMap,DealStatus dealStatus) {
        BaseContentEO contentEO = new BaseContentEO();
        AppUtil.setFieldsValue(contentEO, paramMap, null, new String[]{"yyyy-MM-dd HH:mm:ss","yyyy-MM-dd"});
        String content = super.getMapValue(String.class, "content", paramMap);
        String picList = super.getMapValue(String.class, "picList", paramMap);
        Long[] synColumnIds = super.getMapValue(Long[].class, "synColumnIds", paramMap);
        // 保存图片新闻
        Long oldId=contentEO.getId();
        BaseContentEO eo = baseContentService.getEntity(BaseContentEO.class,oldId);
        contentEO.setSiteId(eo.getSiteId());
        contentEO.setColumnId(eo.getColumnId());
        contentPicService.savePicNews(contentEO,content, picList,synColumnIds);

        return super.buildReturnVO(oldId, contentEO.getTitle());
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