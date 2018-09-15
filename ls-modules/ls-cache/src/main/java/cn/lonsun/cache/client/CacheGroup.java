/*
 * CacheGroup.java         2015年10月9日 <br/>
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

package cn.lonsun.cache.client;

/**
 * redis存储键值常亮 <br/>
 *
 * @date 2015年10月9日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
public class CacheGroup {
    public static final String CMS_ID = "id";// CMS主键id
    public static final String CMS_JOIN_ID = "join_id";// CMS联合主键
    public static final String CMS_SPLIT = ":";// 分隔符
    public static final String CMS_CACHE_CONFIG = "cms_cache_config"; // 所有缓存配置信息

    /**
     * CMS名称
     */
    public static final String CMS_NAME = "name";

    /**
     * CMS父id
     */
    public static final String CMS_PARENTID = "parentId";

    /**
     * CMS主键code
     */
    public static final String CMS_CODE = "code";

    /**
     * CMS类型
     */
    public static final String CMS_TYPE = "type";

    /**
     * CMS站点id，通用菜单、站点、栏目id信息
     */
    public static final String CMS_INDICATORID = "indicatorId";

    /**
     * CMS站点id
     */
    public static final String CMS_SITE_ID = "siteId";

    /**
     * CMS栏目id
     */
    public static final String CMS_COLUMN_ID = "columnId";

    /**
     * CMS内容模型id
     */
    public static final String CMS_MODEL_ID = "modelId";

    /**
     * 角色ID
     */
    public static final String CMS_ROLE_ID = "roleId";

    /**
     * url
     */
    public static final String CMS_URL = "url";

    /**
     * organId
     */
    public static final String CMS_ORGAN_ID = "organId";

    /**
     * 栏目类型的code值
     */
    public static final String CMS_COLUMN_TYPE_CODE = "column_type_code";

    /**
     * 栏目类型的code值，查询所有
     */
    public static final String CMS_ALL = "all";
}