package cn.lonsun.msg.submit.hn.service;

import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.msg.submit.hn.entity.CmsMsgToUserHnEO;

/**
 * @author gu.fei
 * @version 2015-11-18 13:44
 */
public interface IMsgToUserHnService extends IMockService<CmsMsgToUserHnEO> {

    /**
     * 根据消息ID删除
     * @param msgId
     */
    void deleteByMsgId(Long msgId);
}
