/*
 * IPublicContentService.java         2015年12月15日 <br/>
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

package cn.lonsun.publicInfo.internal.service;

import cn.lonsun.content.vo.ContentChartQueryVO;
import cn.lonsun.content.vo.SortUpdateVO;
import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.publicInfo.internal.entity.PublicContentEO;
import cn.lonsun.publicInfo.vo.*;
import cn.lonsun.statistics.ContentChartVO;
import cn.lonsun.statistics.PublicListVO;
import cn.lonsun.statistics.StatisticsQueryVO;

import java.util.List;
import java.util.Map;

/**
 * 公开内容service <br/>
 *
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 * @date 2015年12月15日 <br/>
 */
public interface IPublicContentService extends IMockService<PublicContentEO> {

    Long saveEntity(PublicContentVO vo);

    String getIndexNum(Long organId);

    void updateCatIdByContentIds(Long organId, Long catId, Long targetOrganId, Long targetId, Long[] ids, Long[] contentIds, Integer isPublish);

    String saveEntities(PublicContentVO vo, Long contentId,Long type,String modelCode, String synOrganCatIds, String synColumnIsPublishs,Integer isColumnOpt) throws Exception;

    void delete(Long[] ids);

    void deleteAll(PublicContentQueryVO queryVO);

    Pagination getPagination(PublicContentQueryVO queryVO);

    PublicContentVO getPublicContent(Long id);

    PublicContentVO getPublicContentByBaseContentId(Long id);

    PublicContentVO getPublicContentWithCatNameByBaseContentId(Long id);

    PublicContentVO getPublicContent(PublicContentQueryVO queryVO);

    void updatePublicStatus(Long[] ids, Integer status);

    void createPublicIndex();// 信息公开建立索引

    List<PublicContentVO> getList(PublicContentQueryVO queryVO, Integer num);

    public List<ContentChartVO> getChartList(ContentChartQueryVO queryVO, String type);

    public Pagination getPublicPage(StatisticsQueryVO queryVO);

    public List<PublicListVO> getPublicList(StatisticsQueryVO vo);

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
    void updateSort(SortUpdateVO sortVo);

    Long saveWeServiceEntity(PublicContentVO contentVO);

    void updatePublicStatus(Long[] contentIds, Long organId, Long catId, Integer status);

    /**
     * 更新排序号
     *
     * @param id
     * @param sortNum
     */
    void updateSortNum(Long id, Long sortNum);

    List<Long> getCatalogIdByOrganId(Long organId);

    //根据目录获取信息公开每个单位的文章数量
    List<Map<String,Object>> getPublicContentStatisByCatalogAndOrganId(PublicContentQueryVO queryVO);
}