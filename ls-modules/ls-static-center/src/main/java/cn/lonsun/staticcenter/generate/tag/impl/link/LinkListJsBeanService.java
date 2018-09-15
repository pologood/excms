/*
 * LinkListJsBeanService.java         2015年11月26日 <br/>
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

package cn.lonsun.staticcenter.generate.tag.impl.link;

import cn.lonsun.base.anno.DbInject;
import cn.lonsun.cache.client.CacheGroup;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.content.internal.dao.IBaseContentDao;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.site.contentModel.internal.entity.ContentModelEO;
import cn.lonsun.site.site.internal.entity.ColumnConfigEO;
import cn.lonsun.staticcenter.generate.GenerateConstant;
import cn.lonsun.staticcenter.generate.GenerateException;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import cn.lonsun.staticcenter.generate.util.AssertUtil;
import cn.lonsun.staticcenter.generate.util.MapUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 链接管理js生成 <br/>
 *
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 * @date 2015年11月26日 <br/>
 */
@Component
public class LinkListJsBeanService extends AbstractBeanService {

    @DbInject("baseContent")
    private IBaseContentDao contentDao;

    @Override
    public Object getObject(JSONObject paramObj) {
        // 合并map
        MapUtil.unionContextToJson(paramObj);
        Context context = ContextHolder.getContext();
        Long columnId = ObjectUtils.defaultIfNull(paramObj.getLong(GenerateConstant.ID), context.getColumnId());
        AssertUtil.isEmpty(columnId, "栏目id不能为空！");
        Long siteId = ObjectUtils.defaultIfNull(paramObj.getLong("siteId"), context.getSiteId());
        AssertUtil.isEmpty(siteId, "站点id不能为空！");
        // 查询结果
        Integer num = paramObj.getInteger(GenerateConstant.NUM);
        AssertUtil.isEmpty(num, "查询条数不能为空！");
        String order = paramObj.getString(GenerateConstant.ORDER);
        // 参数map
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("columnId", columnId);// 栏目id
        paramMap.put("siteId", siteId);// 站点id
        paramMap.put("isPublish", 1);// 已发布的
        paramMap.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
        paramMap.put("isJob", 0);// 非定时任务
        paramMap.put("nowTime", new Date());
        // 这里如果是定时任务，两个时间必须要有一个不等于空
        StringBuffer hql = new StringBuffer();
        hql.append(" FROM BaseContentEO WHERE columnId = :columnId and siteId = :siteId ");
        hql.append(" AND isPublish = :isPublish AND recordStatus = :recordStatus ");
        hql.append(" AND (isJob = :isJob OR (showStartTime IS NULL AND showEndTime > :nowTime) ");
        hql.append(" OR (showEndTime IS NULL AND showStartTime < :nowTime) OR (showStartTime < :nowTime AND showEndTime > :nowTime)) ");
        hql.append(" ORDER BY num ").append(StringUtils.isEmpty(order) ? "desc" : order);
        return contentDao.getEntities(hql.toString(), paramMap, num);
    }

    /**
     * 放入内容模型供页面判断使用
     *
     * @throws GenerateException
     * @see cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService#doProcess(java.lang.Object,
     * com.alibaba.fastjson.JSONObject)
     */
    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> doProcess(Object resultObj, JSONObject paramObj) throws GenerateException {
        Map<String, Object> map = super.doProcess(resultObj, paramObj);
        Long columnId = ObjectUtils.defaultIfNull(paramObj.getLong(GenerateConstant.ID), ContextHolder.getContext().getColumnId());
        // 获取栏目配置信息
        ColumnConfigEO columnConfigEO = CacheHandler.getEntity(ColumnConfigEO.class, CacheGroup.CMS_INDICATORID, columnId);
        AssertUtil.isEmpty(columnConfigEO, "栏目配置信息不存在！");
        // 获取到内容模型
        ContentModelEO contentModel = CacheHandler.getEntity(ContentModelEO.class, CacheGroup.CMS_CODE, columnConfigEO.getContentModelCode());
        map.put("contentModeObj", JSON.parseObject(contentModel.getContent()));
        // 替换空格为&nbsp; 替换回车为</br>
        List<BaseContentEO> contentList = (List<BaseContentEO>) resultObj;
        if (null != contentList && !contentList.isEmpty()) {
            for (BaseContentEO content : contentList) {
                String remark = content.getRemarks();
                if (StringUtils.isNotEmpty(remark)) {
                    content.setRemarks(remark.replaceAll("[\\t\\n\\r]", "</br>").replaceAll("\\s", "&nbsp;"));
                }
            }
        }
        return map;
    }

    @Override
    public String objToStr(String content, Object resultObj, JSONObject paramObj) throws GenerateException {
        String labelName = paramObj.getString(GenerateConstant.LABEL_NAME);
        Long columnId = ObjectUtils.defaultIfNull(paramObj.getLong(GenerateConstant.ID), ContextHolder.getContext().getColumnId());
        // 获取栏目配置信息
        ColumnConfigEO columnConfigEO = CacheHandler.getEntity(ColumnConfigEO.class, CacheGroup.CMS_INDICATORID, columnId);
        String vmTpl = columnConfigEO.getLinkCode();
        // 此写法是为了配合在后台进行直接生成时，没有标签内容也能解析
        if (StringUtils.isEmpty(labelName)) {
            paramObj.put(GenerateConstant.LABEL_NAME, StringUtils.isEmpty(vmTpl) ? "linkListJs" : vmTpl);
        }
        paramObj.put("columnId", columnConfigEO.getIndicatorId());
        paramObj.put("length", columnConfigEO.getTitleCount());
        paramObj.put("noteLength", columnConfigEO.getRemarksCount());
        return super.objToStr(content, resultObj, paramObj);
    }
}