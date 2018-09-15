/*
 * IMessageReceiveDao.java         2016年1月5日 <br/>
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

package cn.lonsun.message.internal.dao;

import cn.lonsun.core.base.dao.IBaseDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.message.internal.entity.MessageReceiveEO;
import cn.lonsun.message.vo.MessageQueryVO;

/**
 * TODO <br/>
 *
 * @date 2016年1月5日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
public interface IMessageReceiveDao extends IBaseDao<MessageReceiveEO> {

    /**
     * 获取消息分页列表
     *
     * @author fangtinghua
     * @param vo
     * @return
     */
    Pagination getPagination(MessageQueryVO vo);

    /**
     * 设置全部消息状态
     *
     * @author fangtinghua
     * @param userId
     * @param status
     * @return
     */
    void updateMessageStatus(Long userId, Long status);
}