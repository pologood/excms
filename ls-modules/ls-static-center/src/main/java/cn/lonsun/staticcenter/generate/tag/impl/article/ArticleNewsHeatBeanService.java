/*
 * ArticleNewsHeatBeanService.java         2016年6月28日 <br/>
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

package cn.lonsun.staticcenter.generate.tag.impl.article;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import cn.lonsun.base.anno.DbInject;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import cn.lonsun.content.internal.dao.IBaseContentDao;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.staticcenter.generate.GenerateConstant;
import cn.lonsun.staticcenter.generate.GenerateException;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;

import com.alibaba.fastjson.JSONObject;

/**
 * 新闻点击数排行标签调用 <br/>
 * 
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 * @date 2016年6月28日 <br/>
 */
@Service
public class ArticleNewsHeatBeanService extends AbstractBeanService {

    @DbInject("baseContent")
    private IBaseContentDao contentDao;
    // 默认查询类型
    private String[] defaultCodes = { BaseContentEO.TypeCode.articleNews.toString(), BaseContentEO.TypeCode.pictureNews.toString(),
            BaseContentEO.TypeCode.videoNews.toString() };

    @Override
    public Object getObject(JSONObject paramObj) throws GenerateException {
        Context context = ContextHolder.getContext();
        Long siteId = paramObj.getLong("siteId");
        if (null == siteId) {
            if (null == (siteId = context.getSiteId())) {
                throw new GenerateException("站点id不能为空！");
            }
        }
        String typeCode = paramObj.getString("typeCode");
        String columnId = paramObj.getString("columnId");
        String excludeOrganId = paramObj.getString("excludeOrganId");
        String excludeColumnId = paramObj.getString("excludeColumnId");
        // 查询条数
        Integer num = paramObj.getInteger(GenerateConstant.NUM);
        StringBuffer hql = new StringBuffer();
        Map<String, Object> map = new HashMap<String, Object>();
        hql.append("select max(b.id) as id,b.createOrganId as createOrganId,count(b.id) as infoCount,");
        hql.append("max(oo.name) as organName from BaseContentEO b, OrganEO oo where b.createOrganId = oo.organId ");
        hql.append(" and b.siteId = :siteId and b.typeCode in (:typeCodes) and b.recordStatus = :recordStatus");
        if (StringUtils.isNotEmpty(excludeOrganId)) {// 排除单位
            hql.append(" and oo.organId not in (:excludeOrganId)");
            map.put("excludeOrganId", ConvertUtils.convert(StringUtils.split(excludeOrganId, ","), Long.class));
        }
        if (StringUtils.isNotEmpty(excludeColumnId)) {// 排除栏目
            hql.append(" and not exists (select 1 from IndicatorEO i where 1=1 ");
            hql.append(" and i.siteId = :siteId");
            hql.append(" and i.type in (:columnTypes) ");
            hql.append(" and i.recordStatus = :recordStatus");
            hql.append(" and b.columnId = i.indicatorId");
            hql.append(" and i.indicatorId in (:excludeColumnId))");
            map.put("columnTypes", new String[] { IndicatorEO.Type.CMS_Section.toString(), IndicatorEO.Type.COM_Section.toString() });
            map.put("excludeColumnId", ConvertUtils.convert(StringUtils.split(excludeColumnId, ","), Long.class));
        }
        if (StringUtils.isNotEmpty(columnId)) {// 如果栏目不为空
            Long[] columnIds = super.getQueryColumnIdByChild(columnId, typeCode);
            hql.append(" and b.columnId in (:columnId)");
            map.put("columnId", columnIds);
        }
        hql.append(" group by b.createOrganId order by infoCount desc");
        map.put("siteId", siteId);
        map.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
        if (StringUtils.isEmpty(typeCode)) {
            map.put("typeCodes", defaultCodes);
        } else {
            map.put("typeCodes", StringUtils.split(typeCode, ","));
        }
        return contentDao.getBeansByHql(hql.toString(), map, BaseContentEO.class, num);
    }
}