/*
 * PublicCatalogServiceImpl.java         2016年10月21日 <br/>
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

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import cn.lonsun.cache.client.CacheGroup;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.publicInfo.internal.entity.OrganConfigEO;
import cn.lonsun.publicInfo.internal.entity.PublicCatalogEO;
import cn.lonsun.publicInfo.internal.entity.PublicCatalogOrganRelEO;
import cn.lonsun.publicInfo.internal.service.IPublicCatalogService;
import cn.lonsun.util.PublicCatalogUtil;
import cn.lonsun.webservice.PublicCatalogService;
import cn.lonsun.webservice.to.WebServiceTO;
import cn.lonsun.webservice.to.WebServiceTO.ErrorCode;

import com.alibaba.fastjson.JSON;

/**
 * 信息公开目录webservice接口 <br/>
 *
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 * @date 2016年10月21日 <br/>
 */
@Service("PublicCatalogServiceImpl_webservice")
public class PublicCatalogServiceImpl implements PublicCatalogService {

    @Resource
    private IPublicCatalogService publicCatalogService;

    @Override
    public WebServiceTO getPublicCatalogList(Long organId) {
        WebServiceTO to = new WebServiceTO();
        if (null == organId) {
            return to.error(ErrorCode.ArgumentsError, "organId不能为空！");
        }
        try {
            // 查询单位信息
            OrganConfigEO organConfigEO = CacheHandler.getEntity(OrganConfigEO.class, CacheGroup.CMS_ORGAN_ID, organId);
            if (null == organConfigEO || null == organConfigEO.getCatId()) {
                return to.error(ErrorCode.ArgumentsError, "单位配置信息不存在或没有配置信息公开目录！");
            }
            // 查询出所有目录信息
            List<PublicCatalogEO> publicCatalogList = publicCatalogService.getAllChildListByCatId(organConfigEO.getCatId());
            PublicCatalogUtil.filterCatalogList(publicCatalogList, organId, true);// 过滤目录列表
            PublicCatalogUtil.sortCatalog(publicCatalogList);
            return to.success(JSON.toJSONString(publicCatalogList), "操作成功！");
        } catch (Exception e) {
            return to.error(ErrorCode.SystemError, "系统异常，请稍后重试！");
        }
    }
}