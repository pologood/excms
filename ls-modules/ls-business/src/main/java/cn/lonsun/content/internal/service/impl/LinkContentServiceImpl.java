/*
 * LinkContentServiceImpl.java         2015年11月21日 <br/>
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

package cn.lonsun.content.internal.service.impl;

import org.springframework.stereotype.Service;

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.service.ILinkContentService;
import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.util.FileUploadUtil;

/**
 * TODO <br/>
 * 
 * @date 2015年11月21日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
@Service("LinkContentServiceImpl")
public class LinkContentServiceImpl extends MockService<BaseContentEO> implements ILinkContentService {

    @Override
    public Long saveEntity(BaseContentEO contentEO) {
        Long id = contentEO.getId();
        if (null != id) {
            super.updateEntity(contentEO);
        } else {
            id = super.saveEntity(contentEO);
        }
        if (!AppUtil.isEmpty(contentEO.getImageLink())) {
            FileUploadUtil.saveFileCenterEO(contentEO.getImageLink());
        }
        // 更新缓存
        CacheHandler.saveOrUpdate(BaseContentEO.class, contentEO);
        return id;
    }
}