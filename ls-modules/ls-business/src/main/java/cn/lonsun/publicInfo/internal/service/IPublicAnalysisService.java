/*
 * IPublicAnalysisService.java         2016年9月6日 <br/>
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

import javax.servlet.http.HttpServletResponse;

import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.publicInfo.internal.entity.PublicContentEO;
import cn.lonsun.publicInfo.vo.PublicAnalysisQueryVO;

/**
 * TODO <br/>
 * 
 * @date 2016年9月6日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
public interface IPublicAnalysisService extends IMockService<PublicContentEO> {

    public enum PublicAnalysisType {
        organRanking, applyRanking, replyRanking, replyStatusRanking, catalogRanking
    }

    Object getRanking(PublicAnalysisQueryVO queryVO, PublicAnalysisType type);

    void exportRanking(PublicAnalysisQueryVO queryVO, PublicAnalysisType type, HttpServletResponse response);
}