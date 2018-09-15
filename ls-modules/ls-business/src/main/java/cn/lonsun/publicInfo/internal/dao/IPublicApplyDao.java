/*
 * IPublicApplyDao.java         2015年12月25日 <br/>
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

import java.util.List;

import cn.lonsun.content.vo.ContentChartQueryVO;
import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.publicInfo.internal.entity.PublicApplyEO;
import cn.lonsun.publicInfo.vo.PublicApplyQueryVO;
import cn.lonsun.publicInfo.vo.PublicApplyVO;
import cn.lonsun.publicInfo.vo.PublicContentQueryVO;
import cn.lonsun.publicInfo.vo.PublicTjVO;
import cn.lonsun.statistics.ContentChartVO;
import cn.lonsun.statistics.StatisticsQueryVO;

/**
 * TODO <br/>
 *
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 * @date 2015年12月25日 <br/>
 */
public interface IPublicApplyDao extends IMockDao<PublicApplyEO> {

    Pagination getPagination(PublicApplyQueryVO queryVO);

    PublicApplyVO getPublicApply(Long id);

    PublicApplyVO getPublicApplyByQueryCode(PublicApplyQueryVO queryVO);

    List<PublicTjVO> getPublicTjList(PublicContentQueryVO queryVO);

    List<PublicTjVO> getPublicTjByApplyStatus(PublicContentQueryVO queryVO);

    public List<ContentChartVO> getChartList(ContentChartQueryVO queryVO);

    public Long getTypeCount(StatisticsQueryVO queryVO);

    /**
     * 按照证件号码和查询密码查询依申请公开
     *
     * @param queryVO
     * @return
     */
    PublicApplyVO getPublicApplyByCardNum(PublicApplyQueryVO queryVO);
}