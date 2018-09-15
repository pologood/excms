/*
 * ImageBeanService.java         2015年9月23日 <br/>
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

import cn.lonsun.base.anno.DbInject;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.internal.dao.IBaseContentDao;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.staticcenter.generate.GenerateConstant;
import cn.lonsun.staticcenter.generate.GenerateException;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import cn.lonsun.staticcenter.generate.util.MongoUtil;
import cn.lonsun.staticcenter.generate.util.PathUtil;
import cn.lonsun.util.ModelConfigUtil;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 文章新闻图片列表(专题专用) <br/>
 *
 * @author liuk <br/>
 * @version v1.0 <br/>
 * @date 2017年3月21日 <br/>
 */
@Component
public class DocImgNoteBeanService extends DocListBeanService {

    @DbInject("baseContent")
    private IBaseContentDao contentDao;

    /**
     * 查询文章列表
     */
    @Override
    public Object getObject(JSONObject paramObj) {
        Long[] ids = super.getQueryColumnIdByChild(paramObj, BaseContentEO.TypeCode.articleNews.toString());
        if (null == ids) {
            return null;
        }
        Context context = ContextHolder.getContext();
        Long columnId = context.getColumnId();
        String inIds = paramObj.getString(GenerateConstant.ID);
        if (StringUtils.isNotEmpty(inIds)) {// 当传入多栏目时，依第一个栏目为准
            columnId = Long.valueOf(StringUtils.split(inIds, ",")[0]);
        }

        // 站点ID
        Long siteId = paramObj.getLong("siteId");
        if (AppUtil.isEmpty(siteId)) {
            siteId = context.getSiteId();
        }

        Integer size = ids.length;
        Map<String, Object> map = new HashMap<String, Object>();
        Integer num = paramObj.getInteger(GenerateConstant.NUM);
        String where = paramObj.getString(GenerateConstant.WHERE);
        StringBuffer hql = new StringBuffer();
        hql.append("FROM BaseContentEO c WHERE c.columnId ").append(size == 1 ? " =:ids " : " in (:ids) ");
        hql.append(" AND c.siteId=:siteId AND c.isPublish=1 AND c.recordStatus =:recordStatus AND c.imageLink <> '' AND c.imageLink IS NOT NULL ");
        hql.append(StringUtils.isEmpty(where) ? "" : " AND " + where);
        hql.append(ModelConfigUtil.getOrderByHql(columnId, context.getSiteId(), BaseContentEO.TypeCode.articleNews.toString()));
        if (size == 1) {
            map.put("ids", ids[0]);
        } else {
            map.put("ids", ids);
        }
        map.put("siteId", siteId);
        map.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
        return contentDao.getEntities(hql.toString(), map, num);
    }

    /**
     * 预处理数据
     *
     * @throws GenerateException
     */
    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> doProcess(Object resultObj, JSONObject paramObj) throws GenerateException {
        // 预处理数据，处理文章链接问题
        List<BaseContentEO> list = (List<BaseContentEO>) resultObj;


        if (null != list && !list.isEmpty()) {
            // 处理文章链接
            for (BaseContentEO eo : list) {
                String path = PathUtil.getLinkPath(eo.getColumnId(), eo.getId());
                eo.setLink(path);

                String content = MongoUtil.queryById(eo.getId());
                eo.setArticle(content);
            }
        }
        return super.doProcess(resultObj, paramObj);
    }
}