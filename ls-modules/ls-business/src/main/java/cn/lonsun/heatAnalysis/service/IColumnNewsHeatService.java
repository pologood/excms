/*
 * IColumnNewsHeatService.java         2016年4月5日 <br/>
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

package cn.lonsun.heatAnalysis.service;

import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.vo.ContentPageVO;
import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.core.util.Pagination;

import java.util.List;

/**
 * 栏目热度和文章热度分析 <br/>
 *
 * @date 2016年4月5日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
public interface IColumnNewsHeatService extends IMockService<BaseContentEO> {

    /**
     * 获取栏目热度列表
     *
     * @author fangtinghua
     * @param contentPageVO
     * @return
     */
    public Pagination getColumnHeatPage(ContentPageVO contentPageVO);

    /**
     * 文章热度分析
     *
     * @author fangtinghua
     * @param contentPageVO
     * @return
     */
    public Pagination getNewsHeatPage(ContentPageVO contentPageVO);


    /**
     * 文章热度分析 区别于getNewsHeatPage 没有count操作，只取第一页数据
     * @param contentPageVO
     * @return
     */
    public List<BaseContentEO> getNewsHeatList(ContentPageVO contentPageVO);

    /**
     * 文章部门点击排行
     *
     * @author fangtinghua
     * @param contentPageVO
     * @return
     */
    public Pagination getOrganHeatPage(ContentPageVO contentPageVO);
}