/*
 * PublicNavBeanService.java         2016年3月1日 <br/>
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

import cn.lonsun.cache.client.CacheGroup;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.publicInfo.internal.entity.OrganConfigEO;
import cn.lonsun.publicInfo.internal.entity.PublicCatalogEO;
import cn.lonsun.publicInfo.internal.entity.PublicCatalogOrganRelEO;
import cn.lonsun.publicInfo.internal.entity.PublicContentEO;
import cn.lonsun.publicInfo.internal.service.IPublicContentService;
import cn.lonsun.rbac.internal.entity.OrganEO;
import cn.lonsun.staticcenter.generate.GenerateConstant;
import cn.lonsun.staticcenter.generate.GenerateException;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import cn.lonsun.staticcenter.generate.util.AssertUtil;
import cn.lonsun.staticcenter.generate.util.MapUtil;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

/**
 * 信息公开导航页，只会存在动态情况 <br/>
 *
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 * @date 2016年3月1日 <br/>
 */
@Component
public class PublicNavBeanService extends AbstractBeanService {

    @Resource
    private IPublicContentService publicContentService;

    @Override
    public Object getObject(JSONObject paramObj) {
        Context context = ContextHolder.getContext();
        MapUtil.unionContextToJson(paramObj);// 合并map
        Long organId = ObjectUtils.defaultIfNull(paramObj.getLong(GenerateConstant.ORGAN_ID), context.getColumnId());
        AssertUtil.isEmpty(organId, "单位id不能为空！");
        OrganEO organEO = CacheHandler.getEntity(OrganEO.class, organId);
        AssertUtil.isEmpty(organEO, "单位信息不存在！");
        Long catId = paramObj.getLong("catId");//目录id
        String type = paramObj.getString(GenerateConstant.TYPE);// 类型
        String value = PublicConstant.PublicTypeEnum.getType(type);// 查询值
        boolean isLevel = paramObj.getBooleanValue("isLevel");//是否需要显示层级
        // 文章页特殊判断
        if (null != context.getContentId()) {
            Map<String, Object> paramMap = new HashMap<String, Object>();
            paramMap.put("contentId", context.getContentId());
            paramMap.put("siteId", context.getSiteId());// 站点
            paramMap.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());// 状态
            PublicContentEO publicContentEO = publicContentService.getEntity(PublicContentEO.class, paramMap);
            if (null == publicContentEO) {
                return null;
            }
            catId = publicContentEO.getCatId();
            value = publicContentEO.getType();
            if (PublicContentEO.Type.PUBLIC_CATALOG.toString().equals(value)) {// 如果等于依申请公开目录
                type = PublicConstant.PublicTypeEnum.DRIVING_PUBLIC.getKey();
            } else {
                type = PublicConstant.PublicTypeEnum.getTypeByValue(value);
            }
        }
        AssertUtil.isEmpty(value, "查询类型不匹配！");
        if (isLevel && null != catId && catId > 0L && PublicContentEO.Type.DRIVING_PUBLIC.toString().equals(value)) {// 如果指定目录
            OrganConfigEO organConfigEO = CacheHandler.getEntity(OrganConfigEO.class, CacheGroup.CMS_ORGAN_ID, organId);
            AssertUtil.isEmpty(organConfigEO, "单位配置信息不存在！");
            List<String> resultList = new ArrayList<String>();
            String target = ObjectUtils.defaultIfNull(paramObj.getString("target"), "_self");
            try{
                this.getParent(resultList, organId, organConfigEO.getCatId(), catId, target, type);
            }catch (GenerateException e){
                e.printStackTrace();
            }
            Collections.reverse(resultList);// 反转
            return StringUtils.join(resultList, " > ");
        }
        return organEO.getName() + paramObj.getString(PublicConstant.NAME + "_" + type);
    }

    @Override
    public String objToStr(String content, Object resultObj, JSONObject paramObj) {
        return resultObj.toString();
    }

    /**
     * 获取目录层级
     *
     * @param resultList
     * @param organId
     * @param parentId
     * @param catId
     * @param target
     * @param type
     */
    public void getParent(List<String> resultList, Long organId, Long parentId, Long catId, String target, String type) {
        if (null == parentId || parentId.equals(catId)) {
            return;
        }
        PublicCatalogEO catalogEO = CacheHandler.getEntity(PublicCatalogEO.class, catId);
        AssertUtil.isEmpty(catalogEO, "目录信息不存在！");
        PublicCatalogOrganRelEO relEO = CacheHandler.getEntity(PublicCatalogOrganRelEO.class, CacheGroup.CMS_JOIN_ID, organId, catId);
        String link = "<a href=\"/public/column/%s?type=%s&catId=%s&action=list\" target=\"%s\">%s</a>";
        resultList.add(String.format(link, organId, type, catId, target, null == relEO ? catalogEO.getName() : relEO.getName()));
        this.getParent(resultList, organId, parentId, catalogEO.getParentId(), target, type);
    }
}