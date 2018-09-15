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
import cn.lonsun.content.internal.entity.GuestBookEO;
import cn.lonsun.content.internal.service.IBaseContentService;
import cn.lonsun.content.internal.service.IGuestBookService;
import cn.lonsun.content.vo.GuestBookEditVO;
import cn.lonsun.content.vo.VideoNewsVO;
import cn.lonsun.form.FormReturnVO;
import cn.lonsun.mongodb.service.ContentMongoServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 留言管理 <br/>
 *
 * @date 2016年8月26日 <br/>
 * @author liukun <br/>
 * @version v1.0 <br/>
 */
@Service("guestBookFormDataService")
public class GuestBookFormDataService extends AbstractFormDataService {

    @Resource
    private IBaseContentService baseContentService;
    @Resource
    private ContentMongoServiceImpl contentMongoService;
    @Resource
    private IGuestBookService guestBookService;

    @Override
    public FormReturnVO saveFormData(Map<String, Object> paramMap,DealStatus dealStatus) {
        GuestBookEditVO guestBookEditVO = new GuestBookEditVO();
        AppUtil.setFieldsValue(guestBookEditVO, paramMap, null, new String[]{"yyyy-MM-dd HH:mm:ss","yyyy-MM-dd"});

        Map<String,Object> map = new HashMap<String, Object>();
        map.put("baseContentId",guestBookEditVO.getBaseContentId());
        GuestBookEO eo = guestBookService.getEntity(GuestBookEO.class, map);
        BaseContentEO eo1 = baseContentService.getEntity(BaseContentEO.class, guestBookEditVO.getBaseContentId());
        eo.setGuestBookContent(guestBookEditVO.getGuestBookContent());
        eo.setPersonIp(guestBookEditVO.getPersonIp());
        eo.setPersonName(guestBookEditVO.getPersonName());
        eo.setAddDate(guestBookEditVO.getAddDate());
        eo.setIsPublic(guestBookEditVO.getIsPublic());
        eo.setIsPublicInfo(guestBookEditVO.getIsPublicInfo());
        if (guestBookEditVO.getRecType() != null) {
            if (guestBookEditVO.getRecType() == 0) {
                eo.setReceiveId(guestBookEditVO.getReceiveId());
            } else if (guestBookEditVO.getRecType() == 1) {
                eo.setReceiveUserCode(guestBookEditVO.getReceiveUserCode());
            }
        }
        if (!StringUtils.isEmpty(guestBookEditVO.getClassCode())) {
            eo.setClassCode(guestBookEditVO.getClassCode());
        }
        //回复信息
        eo.setReplyDate(guestBookEditVO.getReplyDate());
        eo.setDealStatus(guestBookEditVO.getDealStatus());
        eo.setResponseContent(guestBookEditVO.getResponseContent());
        if (null != guestBookEditVO.getRecType() && guestBookEditVO.getRecType() == 1) {
            eo.setReplyUserId(guestBookEditVO.getReplyUserId());
            eo.setReplyUserName(guestBookEditVO.getReplyUserName());
        }



        eo1.setTitle(guestBookEditVO.getTitle());

        guestBookService.updateEntity(eo);
        baseContentService.updateEntity(eo1);

        return super.buildReturnVO(guestBookEditVO.getBaseContentId(), guestBookEditVO.getTitle());
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