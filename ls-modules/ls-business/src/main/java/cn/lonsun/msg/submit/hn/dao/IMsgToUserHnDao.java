package cn.lonsun.msg.submit.hn.dao;

import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.msg.submit.hn.entity.CmsMsgToUserHnEO;

/**
 * @author gu.fei
 * @version 2015-11-18 13:44
 */
public interface IMsgToUserHnDao extends IMockDao<CmsMsgToUserHnEO> {

    /**
     * 根据消息ID删除
     * @param msgId
     */
    void deleteByMsgId(Long msgId);
}
