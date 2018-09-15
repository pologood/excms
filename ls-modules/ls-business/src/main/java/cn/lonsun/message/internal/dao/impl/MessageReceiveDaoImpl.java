/*
 * MessageReceiveDaoImpl.java         2016年1月5日 <br/>
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

package cn.lonsun.message.internal.dao.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import cn.lonsun.core.base.dao.impl.BaseDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.SqlUtil;
import cn.lonsun.message.internal.dao.IMessageReceiveDao;
import cn.lonsun.message.internal.entity.MessageReceiveEO;
import cn.lonsun.message.vo.MessageQueryVO;

/**
 * TODO <br/>
 *
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 * @date 2016年1月5日 <br/>
 */
@Repository
public class MessageReceiveDaoImpl extends BaseDao<MessageReceiveEO> implements IMessageReceiveDao {

    @Override
    public Pagination getPagination(MessageQueryVO vo) {
        StringBuffer hql = new StringBuffer();
        hql.append("from MessageReceiveEO where recUserId = ?");
        if (null != vo.getMessageStatus()) {
            hql.append(" and messageStatus = " + vo.getMessageStatus());
        }
        if (!StringUtils.isEmpty(vo.getModeCode())) {
            hql.append(" and modeCode = " + vo.getModeCode());
        }
        if (!StringUtils.isEmpty(vo.getTitle())) {
            hql.append(" and messageSystemEO.title like '%" + SqlUtil.prepareParam4Query(vo.getTitle()) + "%'");
        }
        hql.append(" order by createDate desc");
        return this.getPagination(vo.getPageIndex(), vo.getPageSize(), hql.toString(), new Object[] { vo.getUserId() });
    }

    @Override
    public void updateMessageStatus(Long userId, Long status) {
        StringBuffer hql = new StringBuffer();
        hql.append("update MessageReceiveEO set messageStatus = ? where recUserId = ?");
        this.executeUpdateByHql(hql.toString(), new Object[] { status, userId });
    }
}