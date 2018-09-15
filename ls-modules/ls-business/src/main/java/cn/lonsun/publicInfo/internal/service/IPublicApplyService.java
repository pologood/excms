/*
 * IPublicApplyService.java         2015年12月22日 <br/>
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

import java.util.List;

import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.core.exception.BusinessException;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.publicInfo.internal.entity.PublicApplyEO;
import cn.lonsun.publicInfo.vo.PublicApplyQueryVO;
import cn.lonsun.publicInfo.vo.PublicApplyVO;
import cn.lonsun.publicInfo.vo.PublicContentQueryVO;
import cn.lonsun.publicInfo.vo.PublicTjVO;
import cn.lonsun.publicInfo.vo.PublicTotalVO;

/**
 * TODO <br/>
 * 
 * @date 2015年12月22日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
public interface IPublicApplyService extends IMockService<PublicApplyEO> {

    Long saveEntity(PublicApplyVO vo);

    void delete(Long[] ids);

    void deleteAll();

    Pagination getPagination(PublicApplyQueryVO queryVO) throws BusinessException;

    Pagination getPublicApplyInfo(PublicApplyQueryVO queryVO);

    PublicApplyVO getPublicApply(Long id);

    PublicTotalVO getPublicTotalVO(PublicContentQueryVO queryVO);

    void updatePublicStatus(Long[] ids, Integer status);

    List<PublicTjVO> getPublicTjList(PublicContentQueryVO queryVO);

    List<PublicTjVO> getPublicTjByApplyStatus(PublicContentQueryVO queryVO);
}