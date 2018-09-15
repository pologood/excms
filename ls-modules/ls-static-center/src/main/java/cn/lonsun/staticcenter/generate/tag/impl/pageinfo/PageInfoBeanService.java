/*
 * LinkListBeanService.java         2015年11月25日 <br/>
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

package cn.lonsun.staticcenter.generate.tag.impl.pageinfo;

import cn.lonsun.base.anno.DbInject;
import cn.lonsun.content.internal.dao.IBaseContentDao;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.staticcenter.generate.GenerateConstant;
import cn.lonsun.staticcenter.generate.GenerateException;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import cn.lonsun.staticcenter.generate.util.MongoUtil;
import cn.lonsun.staticcenter.generate.util.PathUtil;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 单页面 <br/>
 *
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 * @date 2015年11月25日 <br/>
 */
@Component
public class PageInfoBeanService extends AbstractBeanService {

    @DbInject("baseContent")
    private IBaseContentDao contentDao;

    @Override
    public Object getObject(JSONObject paramObj) {
        Long columnId = paramObj.getLong(GenerateConstant.ID);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("siteId", ContextHolder.getContext().getSiteId());
        map.put("columnId", columnId);
        map.put("isPublish", 1);
        map.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
        return contentDao.getEntity(BaseContentEO.class, map);
    }

    @Override
    public Map<String, Object> doProcess(Object resultObj, JSONObject paramObj) throws GenerateException {
        // 预处理数据，处理文章内容
        BaseContentEO eo = (BaseContentEO) resultObj;
        String path = PathUtil.getLinkPath(eo.getColumnId(), null);
        // 根据id去mongodb读取文件内容
        String article = MongoUtil.queryById(eo.getId());
        eo.setArticle(StringUtils.replacePattern(article, "<([^>]*)>", ""));
        eo.setLink(path);
        return super.doProcess(resultObj, paramObj);
    }
}