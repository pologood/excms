/*
 * IPublicContentDao.java         2015年12月16日 <br/>
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

package cn.lonsun.publicInfo.internal.dao;

import cn.lonsun.content.vo.ContentChartQueryVO;
import cn.lonsun.content.vo.SortUpdateVO;
import cn.lonsun.content.vo.SortVO;
import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.publicInfo.internal.entity.PublicContentEO;
import cn.lonsun.publicInfo.vo.PublicContentQueryVO;
import cn.lonsun.publicInfo.vo.PublicContentRetrievalVO;
import cn.lonsun.publicInfo.vo.PublicContentVO;
import cn.lonsun.publicInfo.vo.PublicTjForDateVO;
import cn.lonsun.publicInfo.vo.PublicTjVO;
import cn.lonsun.statistics.ContentChartVO;
import cn.lonsun.statistics.StatisticsQueryVO;

import java.util.List;
import java.util.Map;

/**
 * TODO <br/>
 *
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 * @date 2015年12月16日 <br/>
 */
public interface IPublicContentDao extends IMockDao<PublicContentEO> {

    void updateCatIdByContentIds(Long organId, Long catId, Long targetOrganId, Long targetId, Long[] contentIds);

    void exchangeColumnId(Long columnId, Long catId, Long targetId, Long[] contentIds, Integer isPublish);

    void updateContentSort(Long organId, Long catId);

    Pagination getPagination(PublicContentQueryVO queryVO);

    PublicContentVO getPublicContent(Long id);

    PublicContentVO getPublicContentByBaseContentId(Long id);

    PublicContentVO getPublicContent(PublicContentQueryVO queryVO);

    SortVO getMaxNum(PublicContentVO queryVO);

    Pagination getAllListForPage(Long pageIndex, Integer pageSize);// 查询所有列表,分页的方式查询

    List<PublicContentVO> getList(PublicContentQueryVO queryVO, Integer num);

    public List<ContentChartVO> getChartList(ContentChartQueryVO queryVO, String type);

    public Long getTypeCount(StatisticsQueryVO queryVO, String type);

    public Pagination getRetrievalPagination(PublicContentRetrievalVO queryVO);

    // 手机端获取公开指南
    public Pagination getPublicGuide(Long siteId, Long organId, Long pageIndex, Integer pageSize);

    // 手机端获取公开年报
    public Pagination getPublicAnnualReport(Long siteId, Long organId, Long pageIndex, Integer pageSize);

    // 获取详细信息
    public PublicContentVO getPublicContentInfo(Long id);

    // 根据条件查询记录数量
    List<PublicContentVO> getCounts(PublicContentQueryVO queryVO, Integer limit);

    // 根据类型统计
    List<PublicTjVO> getPublicTjList(PublicContentQueryVO queryVO);

    // 根据目录统计
    List<PublicTjVO> getPublicTjByCatIdList(PublicContentQueryVO queryVO);

    // 根据按照时间段统计（本月、本季度、本年度）
    List<PublicTjForDateVO> getPublicTjListByDate(PublicContentQueryVO queryVO);

    // 列表排序
    public PublicContentEO getSort(SortUpdateVO sortVo);

    // 根据单位id、单位编码以及月份生成索引号
    String getMaxIndexNumByOrganId(Long organId, String codeAndMonth);

    List<Long> getCatalogIdByOrganId(Long organId);

    //根据目录获取信息公开每个单位的文章数量
    List<Map<String,Object>>  getPublicContentStatisByCatalogAndOrganId(PublicContentQueryVO queryVO);
}