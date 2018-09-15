/*
 * IColumnNewsHeatDao.java         2016年4月5日 <br/>
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

package cn.lonsun.heatAnalysis.dao;

import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.vo.ContentPageVO;
import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.core.util.Pagination;

import java.util.List;

/**
 * TODO <br/>
 *
 * @date 2016年4月5日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
public interface IColumnNewsHeatDao extends IMockDao<BaseContentEO> {

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
     * 区别于getNewsHeatPage，此方法只根据pageSize取最新的条数
     * @param contentPageVO
     * @return
     * @author zhongjun
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