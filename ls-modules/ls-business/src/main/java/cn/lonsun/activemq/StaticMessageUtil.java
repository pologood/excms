/*
 * StaticMessageUtil.java         2016年3月14日 <br/>
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

package cn.lonsun.activemq;

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.message.internal.entity.MessageEnum;
import cn.lonsun.message.internal.entity.MessageStaticEO;
import cn.lonsun.rbac.internal.entity.OrganEO;
import cn.lonsun.statictask.internal.entity.StaticTaskEO;

/**
 * 静态消息设置属性工具类 <br/>
 * 
 * @date 2016年3月14日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
public class StaticMessageUtil {

    /**
     * 初始化数据
     * 
     * @author fangtinghua
     * @param staticTaskEO
     * @param eo
     */
    public static void initStaticEO(StaticTaskEO staticTaskEO, MessageStaticEO eo) {
        Long source = eo.getSource();// 来源 1.内容协同 2.信息公开
        Long columnId = eo.getColumnId();// 获取栏目id
        Long scope = eo.getScope();// 1.首页 2.栏目页 3.文章页
        String title = "";// 标题
        if (null != scope && scope > 0) {// 全站生成，则取站点名称
            if (MessageEnum.INDEX.value().equals(scope) || null == columnId) {
                staticTaskEO.setColumnId(eo.getSiteId());// 站点id
                title = getColumnTitle(eo.getSiteId(), MessageEnum.CONTENTINFO.value());
            } else {// 栏目id不为空
                staticTaskEO.setColumnId(columnId);// 当栏目id为空时，放入站点id
                title = getColumnTitle(columnId, source);
            }
        } else if (null != columnId) {// 栏目id不为空
            staticTaskEO.setColumnId(columnId);// 当栏目id为空时，放入站点id
            title = getColumnTitle(columnId, source);
        }
        staticTaskEO.setTitle(title);
        staticTaskEO.setCreateUserId(eo.getUserId());// 设置创建人
        staticTaskEO.setStatus(StaticTaskEO.INIT);
        staticTaskEO.setScope(scope);
        staticTaskEO.setType(eo.getType());
        staticTaskEO.setSource(eo.getSource());
        staticTaskEO.setSiteId(eo.getSiteId());
        staticTaskEO.setCount(0L);
        staticTaskEO.setDoneCount(0L);
        staticTaskEO.setFailCount(0L);
    }

    /**
     * 获取栏目标题
     * 
     * @author fangtinghua
     * @param columnId
     * @param source
     * @return
     */
    public static String getColumnTitle(Long columnId, Long source) {
        if (null == columnId || null == source) {
            return "";
        }
        if (MessageEnum.CONTENTINFO.value().equals(source)) {// 内容协同
            IndicatorEO eo = CacheHandler.getEntity(IndicatorEO.class, columnId);
            return null == eo ? "" : eo.getName();
        } else if (MessageEnum.PUBLICINFO.value().equals(source)) {// 信息公开
            OrganEO eo = CacheHandler.getEntity(OrganEO.class, columnId);
            return null == eo ? "" : eo.getName();
        }
        return "";
    }
}