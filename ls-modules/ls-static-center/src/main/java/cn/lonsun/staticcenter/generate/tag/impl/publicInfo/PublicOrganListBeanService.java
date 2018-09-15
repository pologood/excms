/*
 * PublicOrganListBeanService.java         2016年3月15日 <br/>
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

package cn.lonsun.staticcenter.generate.tag.impl.publicInfo;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import cn.lonsun.cache.client.CacheGroup;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.publicInfo.internal.entity.OrganConfigEO;
import cn.lonsun.rbac.internal.entity.OrganEO;
import cn.lonsun.rbac.internal.service.IOrganService;
import cn.lonsun.site.site.internal.entity.SiteConfigEO;
import cn.lonsun.staticcenter.generate.GenerateConstant;
import cn.lonsun.staticcenter.generate.GenerateException;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import cn.lonsun.staticcenter.generate.util.AssertUtil;

import com.alibaba.fastjson.JSONObject;

/**
 * 查找依申请公开单位列表 <br/>
 *
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 * @date 2016年3月15日 <br/>
 */
@Component
public class PublicOrganListBeanService extends AbstractBeanService {

    @Resource
    private IOrganService organService;

    @Override
    public Object getObject(JSONObject paramObj) throws GenerateException {
        Context context = ContextHolder.getContext();
        Long siteId = ObjectUtils.defaultIfNull(paramObj.getLong(GenerateConstant.ID), context.getSiteId());
        AssertUtil.isEmpty(siteId, "站点id不能为空！");
        SiteConfigEO siteConfigEO = CacheHandler.getEntity(SiteConfigEO.class, CacheGroup.CMS_INDICATORID, siteId);
        if (StringUtils.isEmpty(siteConfigEO.getUnitIds())) {
            return Collections.emptyList();
        }
        List<OrganEO> list = organService.getPublicOrgans(Long.valueOf(siteConfigEO.getUnitIds()));
        if (null == list) {
            return Collections.emptyList();
        }
        String catIds = paramObj.getString("catIds");
        List<Long> catList = cn.lonsun.core.base.util.StringUtils.getListWithLong(catIds, ",");
        for (Iterator<OrganEO> it = list.iterator(); it.hasNext(); ) {// 删除没有单位目录的单位
            Long organId = it.next().getOrganId();
            OrganConfigEO organConfigEO = CacheHandler.getEntity(OrganConfigEO.class, CacheGroup.CMS_ORGAN_ID, organId);
            if (null == organConfigEO || null == organConfigEO.getCatId() || !organConfigEO.getIsEnable()) {
                it.remove();
            } else if (null != catList && !catList.contains(organConfigEO.getCatId())) {
                it.remove();
            }
        }
        return list;
    }
}