/*
 * MessageReceiveServiceImpl.java         2015年12月1日 <br/>
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

package cn.lonsun.message.internal.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import cn.lonsun.cache.client.CacheGroup;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.core.base.service.impl.BaseService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.message.internal.dao.IMessageReceiveDao;
import cn.lonsun.message.internal.entity.MessageReceiveEO;
import cn.lonsun.message.internal.service.IMessageReceiveService;
import cn.lonsun.message.vo.MessageQueryVO;
import cn.lonsun.system.datadictionary.internal.entity.DataDictEO;
import cn.lonsun.system.datadictionary.internal.entity.DataDictItemEO;

/**
 * 消息查询 <br/>
 *
 * @date 2015年12月1日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
@Service
public class MessageReceiveServiceImpl extends BaseService<MessageReceiveEO> implements IMessageReceiveService {
    @Resource
    private IMessageReceiveDao messageReceiveDao;

    @Override
    @SuppressWarnings("unchecked")
    public Pagination getPagination(MessageQueryVO vo) {
        Pagination p = messageReceiveDao.getPagination(vo);
        List<MessageReceiveEO> data = (List<MessageReceiveEO>) p.getData();
        if (null != data && !data.isEmpty()) {
            DataDictEO dictEO = CacheHandler.getEntity(DataDictEO.class, CacheGroup.CMS_CODE, "column_type");
            List<DataDictItemEO> dictItemList = CacheHandler.getList(DataDictItemEO.class, CacheGroup.CMS_PARENTID, dictEO.getDictId());
            Map<String, DataDictItemEO> map = new HashMap<String, DataDictItemEO>();
            if (null != dictItemList && !dictItemList.isEmpty()) {
                for (DataDictItemEO item : dictItemList) {
                    map.put(item.getCode(), item);
                }
            }
            Date date = new Date();
            for (MessageReceiveEO messageReceiveEO : data) {
                if (map.containsKey(messageReceiveEO.getModeCode())) {
                    messageReceiveEO.setModeName(map.get(messageReceiveEO.getModeCode()).getName());
                } else {
                    messageReceiveEO.setModeName(messageReceiveEO.getModeCode());
                }
                messageReceiveEO.setDateDiff(this.getDateDiff(date, messageReceiveEO.getCreateDate()));
            }
        }
        return p;
    }

    @Override
    public void updateMessageStatus(Long[] ids, Long status) {
        if (null != ids && ids.length > 0) {
            for (Long id : ids) {
                MessageReceiveEO eo = super.getEntity(MessageReceiveEO.class, id);
                eo.setMessageStatus(status);
                super.updateEntity(eo);
            }
        }
    }

    @Override
    public void updateMessageStatus(Long userId, Long status) {
        messageReceiveDao.updateMessageStatus(userId, status);
    }

    /**
     * 获取时间间隔
     *
     * @author fangtinghua
     * @param start
     * @param end
     * @return
     */
    private String getDateDiff(Date start, Date end) {
        long nd = 1000 * 24 * 60 * 60;// 一天的毫秒数
        long nh = 1000 * 60 * 60;// 一小时的毫秒数
        long nm = 1000 * 60;// 一分钟的毫秒数
        long ns = 1000;// 一秒钟的毫秒数

        try {
            long diff = start.getTime() - end.getTime();
            long day = diff / nd;// 计算差多少天
            if (day > 0L) {
                return day + "天前";
            }
            long hour = diff % nd / nh;// 计算差多少小时
            if (hour > 0L) {
                return hour + "小时前";
            }
            long min = diff % nd % nh / nm;// 计算差多少分钟
            if (min > 0L) {
                return min + "分钟前";
            }
            long sec = diff % nd % nh % nm / ns;// 计算差多少秒
            if (sec > 0L) {
                return sec + "秒前";
            }
            return diff + "毫秒前";
        } catch (Throwable e) {
            return "";
        }
    }
}