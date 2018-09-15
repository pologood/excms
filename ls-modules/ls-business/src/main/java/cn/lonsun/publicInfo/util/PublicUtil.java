/*
 * PublicUtil.java         2016年7月15日 <br/>
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

package cn.lonsun.publicInfo.util;

import org.apache.commons.lang3.StringUtils;

import cn.lonsun.cache.client.CacheGroup;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.publicInfo.internal.entity.PublicCatalogEO;
import cn.lonsun.publicInfo.internal.entity.PublicCatalogOrganRelEO;
import cn.lonsun.publicInfo.internal.entity.PublicClassEO;

/**
 * 信息公开工具类 <br/>
 *
 * @date 2016年7月15日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
public class PublicUtil {

    /**
     * 获取目录名称
     *
     * @author fangtinghua
     * @param organId
     * @param catId
     */
    public static String getCatName(Long organId, Long catId) {
        PublicCatalogOrganRelEO relEO = CacheHandler.getEntity(PublicCatalogOrganRelEO.class, CacheGroup.CMS_JOIN_ID, organId, catId);
        if (null != relEO) {
            return relEO.getName();
        }
        PublicCatalogEO catalogEO = CacheHandler.getEntity(PublicCatalogEO.class, catId);
        return null == catalogEO ? StringUtils.EMPTY : catalogEO.getName();
    }

    /**
     * 获取分类名称
     *
     * @author fangtinghua
     * @param classIds
     * @return
     */
    public static String getClassName(String classIds) {
        if (StringUtils.isEmpty(classIds)) {
            return StringUtils.EMPTY;
        }
        int index = 0;
        String[] ids = StringUtils.split(classIds, ",");
        String[] idNames = new String[ids.length];
        for (String id : ids) {
            PublicClassEO classEO = CacheHandler.getEntity(PublicClassEO.class, id);
            idNames[index++] = null == classEO ? StringUtils.EMPTY : classEO.getName();
        }
        return StringUtils.join(idNames, ",");
    }
}