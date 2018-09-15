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
import cn.lonsun.content.onlineDeclaration.internal.entity.OnlineDeclarationEO;
import cn.lonsun.content.onlineDeclaration.internal.service.IOnlineDeclarationService;
import cn.lonsun.content.onlineDeclaration.vo.DeclaReplyVO;
import cn.lonsun.content.vo.GuestBookEditVO;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.form.FormReturnVO;
import cn.lonsun.mongodb.service.ContentMongoServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 留言管理 <br/>
 *
 * @date 2016年8月26日 <br/>
 * @author liukun <br/>
 * @version v1.0 <br/>
 */
@Service("onlineDeclarationFormDataService")
public class OnlineDeclarationFormDataService extends AbstractFormDataService {

    @Resource
    private IBaseContentService baseContentService;
    @Resource
    private ContentMongoServiceImpl contentMongoService;
    @Resource
    private IOnlineDeclarationService onlineDeclarationService;

    @Override
    public FormReturnVO saveFormData(Map<String, Object> paramMap,DealStatus dealStatus) {
        DeclaReplyVO declaReplyVO = new DeclaReplyVO();
        AppUtil.setFieldsValue(declaReplyVO, paramMap, null, new String[]{"yyyy-MM-dd HH:mm:ss","yyyy-MM-dd"});

        BaseContentEO contentEO = baseContentService.getEntity(BaseContentEO.class,super.getMapValue(Long.class, "id", paramMap));
        OnlineDeclarationEO eo=onlineDeclarationService.getEntity(OnlineDeclarationEO.class,declaReplyVO.getDeclarationId());
        if(eo==null){
            throw new BaseRunTimeException(TipsMode.Message.toString(), "参数出错");
        }
        eo.setDealStatus(declaReplyVO.getDealStatus());
        eo.setReplyContent(declaReplyVO.getReplyContent());
        eo.setReplyUnitName(declaReplyVO.getReplyUnitName());
        eo.setReplyDate(declaReplyVO.getReplyDate());
        onlineDeclarationService.updateEntity(eo);

        return super.buildReturnVO(contentEO.getId(), contentEO.getTitle());
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