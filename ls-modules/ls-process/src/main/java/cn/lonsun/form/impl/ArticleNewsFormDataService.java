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

import java.util.Map;

import javax.annotation.Resource;

import cn.lonsun.base.DealStatus;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.service.IBaseContentService;
import cn.lonsun.form.FormReturnVO;
import cn.lonsun.mongodb.entity.ContentMongoEO;
import cn.lonsun.mongodb.service.ContentMongoServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 文章新闻 <br/>
 *
 * @date 2016年8月1日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
@Service("articleNewsFormDataService")
public class ArticleNewsFormDataService extends AbstractFormDataService {

    @Resource
    private IBaseContentService baseContentService;
    @Resource
    private ContentMongoServiceImpl contentMongoService;

    @Override
    public FormReturnVO saveFormData(Map<String, Object> paramMap,DealStatus dealStatus) {
        BaseContentEO contentEO = new BaseContentEO();
        AppUtil.setFieldsValue(contentEO, paramMap, null, new String[]{"yyyy-MM-dd HH:mm:ss","yyyy-MM-dd"});
        String content = super.getMapValue(String.class, "content", paramMap);
        Long synMsgCatIds = super.getMapValue(Long.class, "synMsgCatIds", paramMap);
        String publicSynOrganCatIds = super.getMapValue(String.class, "publicSynOrganCatIds", paramMap);
        String publicSynOrganCatNames = super.getMapValue(String.class, "publicSynOrganCatNames", paramMap);
        Long[] synColumnIds = super.getMapValue(Long[].class, "synColumnIds", paramMap);

        BaseContentEO eo = baseContentService.getEntity(BaseContentEO.class,contentEO.getId());
        contentEO.setColumnId(eo.getColumnId());
        contentEO.setSiteId(eo.getSiteId());
        // 保存文章
        Long id = baseContentService.saveArticleNews(contentEO, content, synColumnIds, synMsgCatIds, publicSynOrganCatIds, publicSynOrganCatNames);
        return super.buildReturnVO(id, contentEO.getTitle());
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