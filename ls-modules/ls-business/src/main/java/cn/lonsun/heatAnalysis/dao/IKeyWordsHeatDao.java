/*
 * IKeyWordsHeatDao.java         2016年4月6日 <br/>
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

import cn.lonsun.content.vo.ContentPageVO;
import cn.lonsun.core.base.dao.IBaseDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.heatAnalysis.entity.KeyWordsHeatEO;

import java.util.List;

/**
 * TODO <br/>
 *
 * @date 2016年4月6日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
public interface IKeyWordsHeatDao extends IBaseDao<KeyWordsHeatEO> {

    /**
     * 获取搜索词列表
     *
     * @author fangtinghua
     * @param contentPageVO
     * @return
     */
    public Pagination getKeyWordsHeatPage(ContentPageVO contentPageVO);

    /**
     * 获取搜索词排序列表
     *
     * @author fangtinghua
     * @param contentPageVO
     * @return
     */
    public Pagination getKeyWordsSortPage(ContentPageVO contentPageVO);

    /**
     * 获取最小的排序号
     *
     * @author fangtinghua
     * @param siteId
     * @return
     */
    public Long getMinSortNum(Long siteId);

    /**
     * 更新所有排序
     *
     * @author fangtinghua
     * @param siteId
     */
    public void updateSortNumBySiteId(Long siteId);

    /**
     * 获取搜索热词列表
     * @param siteId
     *        站点ID
     * @param topCount
     *        获取条数
     * @param sort
     *        0:搜索次数降序 1:搜索次数升序
     * @return
     */
    public List<KeyWordsHeatEO> getKeyWordsHeatList(Long siteId, Integer topCount, Integer sort);
}